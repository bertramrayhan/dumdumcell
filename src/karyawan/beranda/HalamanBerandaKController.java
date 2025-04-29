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
    
    @FXML private Label lblWaktu, lblStatusDatang, lblShift, lblJamKerja, lblTotalBarangTerjual, lblJumlahBarang, lblJumlahBarangHampirExp;
    private final DateTimeFormatter formatWaktu = DateTimeFormatter.ofPattern("HH:mm:ss");

    
    private ObservableList<Barang> listBarang = FXCollections.observableArrayList();
    private ObservableList<Promo> listPromo = FXCollections.observableArrayList();
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setKomponen();
        setStatusDatang();
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
    public void refresh() {
        getInfoBarang();
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
            "        (SELECT SUM(stok) FROM barang) AS jumlah_barang, \n" +
            "\n" +
            "        (SELECT COUNT(*)  \n" +
            "         FROM barang  \n" +
            "         WHERE exp IS NOT NULL  \n" +
            "         AND exp <= DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY)) AS jumlah_barang_hampir_expired \n" +
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
    
    private void setStatusDatang(){
        try {
            String query = "SELECT sk.durasi_terlambat, s.nama_shift, \n" +
            "TIME_FORMAT(s.jam_masuk, '%H.%i') AS jam_masuk, \n" +
            "TIME_FORMAT(s.jam_selesai, '%H.%i') AS jam_selesai\n" +
            "FROM shift_karyawan sk\n" +
            "JOIN jadwal_shift js ON js.id_jadwal = sk.id_jadwal\n" +
            "JOIN shift s ON js.id_shift = s.id_shift\n" +
            "WHERE sk.id_admin =? \n" +
            "AND js.tanggal = CURRENT_DATE\n" +
            "AND CURRENT_TIME BETWEEN s.jam_masuk AND s.jam_selesai";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, Session.getIdAdmin());
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                lblShift.setText("Shift : " + result.getString("nama_shift"));
                lblJamKerja.setText(String.format("Jam Kerja : %s - %s", result.getString("jam_masuk"), result.getString("jam_selesai")));
                
                if(result.getInt("durasi_terlambat") == 0){
                    lblStatusDatang.setText("Status : Tepat Waktu");
                }else{
                    lblStatusDatang.setText("Status : Terlambat");
                }
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void getDataTabelBarangExpired(){
        listBarang.clear();
        try {
            String query = "SELECT merek, stok, exp \n" +
            "FROM barang\n" +
            "WHERE exp IS NOT NULL\n" +
            "AND exp <= DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY)";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            
            ResultSet result = statement.executeQuery();
            
            while(result.next()){
                String namaBarang = result.getString("merek");
                String stok = result.getString("stok");
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
}