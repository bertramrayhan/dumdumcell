package karyawan.beranda;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import main.Koneksi;
import main.Pelengkap;
import main.Session;

public class HalamanBerandaKController implements Initializable, Pelengkap{
    @FXML private TableView<Barang> tabelBarangExpired;
    @FXML private TableView<Promo> tabelPromo;
    @FXML private TableColumn<Barang, String> namaBarangCol, totalBarangCol, expCol;
    @FXML private TableColumn<Promo, String> promoCol, potonganHargaCol;
    
    @FXML private Label lblWaktu, lblTotalBarangTerjual, lblJumlahBarang, lblJumlahBarangHampirExp, lblTotalRevenueShiftIni;
    private final DateTimeFormatter formatWaktu = DateTimeFormatter.ofPattern("HH:mm:ss");

    
    private ObservableList<Barang> listBarang = FXCollections.observableArrayList();
    private ObservableList<Promo> listPromo = FXCollections.observableArrayList();
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getInfoBarang();
        getTotalRevenue();
        setKomponen();
        mulaiWaktu();
        getDataTabelBarangExpired();
        getDataTabelPromo();
    }
    
    private void mulaiWaktu() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime now = LocalTime.now();
            lblWaktu.setText(now.format(formatWaktu));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @Override
    public void perbarui() {
        getInfoBarang();
        getTotalRevenue();
    }
    
    private void setKomponen(){
        namaBarangCol.setCellValueFactory(new PropertyValueFactory<>("nama"));
        totalBarangCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        expCol.setCellValueFactory(new PropertyValueFactory<>("expired"));   
        
        promoCol.setCellValueFactory(new PropertyValueFactory<>("promo"));
        potonganHargaCol.setCellValueFactory(new PropertyValueFactory<>("potonganHarga"));
    }
    
    private void getInfoBarang(){
        try {
            String query = "SELECT COALESCE(total_barang, 0) as total_barang, \n" +
            "       COALESCE(jumlah_barang, 0) as jumlah_barang, \n" +
            "       COALESCE(jumlah_barang_hampir_expired, 0) as jumlah_barang_hampir_expired \n" +
            "FROM ( \n" +
            "    SELECT  \n" +
            "        (SELECT SUM(dtj.jumlah_barang)  \n" +
            "         FROM detail_transaksi_jual dtj  \n" +
            "         JOIN transaksi_jual tj  \n" +
            "         ON tj.id_transaksi_jual = dtj.id_transaksi_jual  \n" +
            "         WHERE DATE(tj.tanggal_transaksi_jual) = CURRENT_DATE) AS total_barang, \n" +
            "         \n" +
            "        (SELECT SUM(stok_utama) FROM barang WHERE is_deleted=FALSE) AS jumlah_barang, \n" +
            "\n" +
            "        (SELECT COUNT(*)  \n" +
            "         FROM barang  \n" +
            "         WHERE exp IS NOT NULL  \n" +
            "         AND exp <= DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY) AND is_deleted=FALSE) AS jumlah_barang_hampir_expired \n" +
            ") AS subquery;";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                lblTotalBarangTerjual.setText(result.getString("total_barang"));
                lblJumlahBarang.setText(result.getString("jumlah_barang"));
                lblJumlahBarangHampirExp.setText(result.getString("jumlah_barang_hampir_expired"));
            } else {
                lblTotalBarangTerjual.setText("0");
                lblJumlahBarang.setText("0");
                lblJumlahBarangHampirExp.setText("0");
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void getTotalRevenue(){
        String[] waktuShift = getWaktuShiftByNow();
        int totalRevenue = 0;
        
        try {
            //TRANSAKSI JUAL
            String query = "SELECT SUM(total_transaksi_jual) AS total_transaksi_jual FROM transaksi_jual\n" +
            "WHERE DATE(tanggal_transaksi_jual) = DATE(NOW()) AND cara_bayar = \"Tunai\"\n" +
            "AND TIME(tanggal_transaksi_jual) BETWEEN ? AND ?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, waktuShift[0]);
            statement.setString(2, waktuShift[1]);
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()) {
                totalRevenue += result.getInt("total_transaksi_jual");
            }
            
            //TOPUP SALDO
            query = "SELECT SUM(harga_jual_saldo) AS harga_jual_saldo \n" +
            "FROM topup_saldo_pelanggan \n" +
            "WHERE DATE(tanggal) = DATE(NOW())\n" +
            "AND TIME(tanggal) BETWEEN ? AND ?  \n" +
            "AND (nama_rekening IS NULL OR nama_rekening = '')";
            statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, waktuShift[0]);
            statement.setString(2, waktuShift[1]);
            
            result = statement.executeQuery();
            
            if(result.next()) {
                totalRevenue += result.getInt("harga_jual_saldo");
            }
            
            //TRANSAKSI LAIN-LAIN
            query = "SELECT SUM(\n" +
            "CASE\n" +
            "	WHEN jenis_transaksi = 'Pemasukan' THEN nominal\n" +
            "	WHEN jenis_transaksi = 'Pengeluaran' THEN -nominal\n" +
            "	ELSE 0\n" +
            "END) AS total_transaksi_lain\n" +
            "FROM transaksi_lain\n" +
            "WHERE DATE(tanggal_transaksi) = DATE(NOW()) \n" +
            "AND TIME(tanggal_transaksi) BETWEEN ? AND ?";
            statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, waktuShift[0]);
            statement.setString(2, waktuShift[1]);
            
            result = statement.executeQuery();
            
            if(result.next()) {
                totalRevenue += result.getInt("total_transaksi_lain");
            }
            
            //TRANSAKSI ANTAR CABANG
            query = "SELECT SUM(\n" +
            "CASE\n" +
            "	WHEN jenis_transaksi = 'Antar Cabang +' THEN nominal\n" +
            "	WHEN jenis_transaksi = 'Antar Cabang -' THEN -nominal\n" +
            "	ELSE 0\n" +
            "END) AS total_transaksi_antar_cabang\n" +
            "FROM transaksi_antar_cabang\n" +
            "WHERE DATE(tanggal_transaksi) = DATE(NOW()) \n" +
            "AND TIME(tanggal_transaksi) BETWEEN ? AND ?";
            statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, waktuShift[0]);
            statement.setString(2, waktuShift[1]);
            
            result = statement.executeQuery();
            
            if(result.next()) {
                totalRevenue += result.getInt("total_transaksi_antar_cabang");
            }
            
            lblTotalRevenueShiftIni.setText(Session.convertIntToRupiah(totalRevenue));
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void getDataTabelBarangExpired(){
        listBarang.clear();
        try {
            String query = "SELECT merek, stok_utama, exp \n" +
            "FROM barang\n" +
            "WHERE exp IS NOT NULL\n" +
            "AND exp <= DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY) AND is_deleted=FALSE AND stok_utama > 0";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            
            ResultSet result = statement.executeQuery();
            
            while(result.next()){
                String namaBarang = result.getString("merek");
                String stok = result.getString("stok_utama");
                String tglExp = Session.convertTanggalIndo(result.getString("exp"));
                
                listBarang.add(new Barang(namaBarang, stok, tglExp));
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        tabelBarangExpired.setItems(listBarang);
    }
    
    private void getDataTabelPromo(){      
        listPromo.clear();
        try {
            String query = "SELECT nama_diskon, jenis_diskon, harga_diskon FROM diskon WHERE status='aktif'";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            
            ResultSet result = statement.executeQuery();
            
            while(result.next()){
                String namaDiskon = result.getString("nama_diskon");
                String hargaDiskon = result.getString("jenis_diskon").equals("Nominal") ? 
                        Session.convertIntToRupiah(result.getInt("harga_diskon")) : result.getString("harga_diskon") + "%";
                
                listPromo.add(new Promo(namaDiskon, hargaDiskon));
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        tabelPromo.setItems(listPromo);
    }
    
    public String[] getWaktuShiftByNow() {
        String[] waktuShift = new String[2];
        LocalTime now = LocalTime.now();

        LocalTime pagiMulai = LocalTime.parse("07:30:00");
        LocalTime pagiSelesai = LocalTime.parse("15:30:00");
        LocalTime malamMulai = LocalTime.parse("15:30:01");
        LocalTime malamSelesai = LocalTime.parse("23:30:00");

        if (!now.isBefore(pagiMulai) && !now.isAfter(pagiSelesai)) {
            // shift pagi
            waktuShift[0] = pagiMulai.toString();
            waktuShift[1] = pagiSelesai.toString();
        } else if (!now.isBefore(malamMulai) && !now.isAfter(malamSelesai)) {
            // shift malam
            waktuShift[0] = malamMulai.toString();
            waktuShift[1] = malamSelesai.toString();
        } else {
            waktuShift[0] = null;
            waktuShift[1] = null;
        }
        return waktuShift;
    }
}

