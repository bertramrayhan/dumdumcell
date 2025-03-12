package karyawan.halamanJual;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import main.Koneksi;
import main.Session;

public class HalamanJualKController implements Initializable {
    @FXML StackPane panePesan;
    @FXML Label lblTanggal, lblJam, lblKasir, lblSubtotal, lblTotal, lblKembalian, lblPesan;
    @FXML TextField txtBarcode, txtBarcodeQty, txtBayar, txtManualQty;
    @FXML Button btnTambahProdukBarcode, btnTambahProdukManual, btnBatalTransaksi, btnKonfirmasiTransaksi;
    @FXML TextArea txtACatatan;
    @FXML ChoiceBox<String> cbxCaraBayar, cbxKategori, cbxProduk, cbxDiskon;
    @FXML TableView tabelBarang;
    @FXML TableColumn<Barang, String> colBarcode, colBarang, colHarga, colSubtotal;
    @FXML TableColumn<Barang, Integer> colQty;
    @FXML TableColumn<Barang, Button> colBatal;
    static ObservableList<Barang> listBarang = FXCollections.observableArrayList();
    
    private final DateTimeFormatter formatWaktu = DateTimeFormatter.ofPattern("HH:mm");
    
    private void setTextFieldNumeric(){
        Session.setTextFieldNumeric(txtBayar);
        Session.setTextFieldNumeric(txtBarcodeQty);
        Session.setTextFieldNumeric(txtManualQty);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTextFieldNumeric();
        setTabelBarang();
        setWaktuDanTanggal();
        setKasir();
        setCbx();
        cbxKategori.setOnAction(event -> {
            setCbxProduk();
        });
        cbxDiskon.setOnAction(event -> {
            setTotal();
        });
    }    
    
    @FXML
    private void tambahBarisTabelBarang(MouseEvent event) {
        Button btn = (Button) event.getSource();
        if (btn.getId().equals("btnTambahProdukManual")) {
            if (txtManualQty.getText().isEmpty() || txtManualQty.getText().equals("0")) {
                Session.animasiPanePesan(true, panePesan, lblPesan, "Masukkan jumlah produk yang dipesan", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                return;
            }else if(cbxProduk.getValue() == null){
                Session.animasiPanePesan(true, panePesan, lblPesan, "Produk tidak ditemukan", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                return;
            }else{
                String namaProduk = cbxProduk.getValue();
                int qty = Integer.parseInt(txtManualQty.getText());
                tambahProduk(namaProduk, qty, false); // false indicates manual entry
            }
        } else if (btn.getId().equals("btnTambahProdukBarcode")) {
            if(txtBarcode.getText().isEmpty()){
                Session.animasiPanePesan(true, panePesan, lblPesan, "Masukkan barcode produk", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                return;
            }else if (txtBarcodeQty.getText().isEmpty() || txtBarcodeQty.getText().equals("0")) {
                Session.animasiPanePesan(true, panePesan, lblPesan, "Masukkan jumlah produk yang dipesan", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                return;
            } else {
                String inpBarcode = txtBarcode.getText();
                int qty = Integer.parseInt(txtBarcodeQty.getText());
                tambahProduk(inpBarcode, qty, true); // true indicates barcode entry
            }
        }

        tabelBarang.refresh();
        tabelBarang.setItems(listBarang);
    }

    private void tambahProduk(String identifier, int qty, boolean isBarcode) {
        try {
            boolean produkAda = false;
            for (Barang barang : listBarang) {
                if (isBarcode) {
                    if (barang.getBarcode().equals(identifier)) {
                        if (cekStokBarangCukup("", identifier, barang.getQty() + qty)) {
                            barang.setQty(barang.getQty() + qty);
                            setTotal();
                        } else {
                            Session.animasiPanePesan(true, panePesan, lblPesan, "Stok tidak cukup untuk " + barang.getBarang(), btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                        }
                        produkAda = true;
                        break;
                    }
                } else {
                    if (barang.getBarang().equals(identifier)) {
                        if (cekStokBarangCukup(identifier, "", barang.getQty() + qty)) {
                            barang.setQty(barang.getQty() + qty);
                            setTotal();
                        } else {
                            Session.animasiPanePesan(true, panePesan, lblPesan, "Stok tidak cukup untuk " + barang.getBarang(), btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                        }
                        produkAda = true;
                        break;
                    }
                }
            }

            if (!produkAda && cekStokBarangCukup(isBarcode ? "" : identifier, isBarcode ? identifier : "", qty)) {
                String query = "SELECT b.barcode, b.merek, b.harga_jual\n" +
                               "FROM barang b " +
                               "WHERE " + (isBarcode ? "b.barcode = ?" : "b.merek = ?");
                PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
                statement.setString(1, identifier);

                ResultSet result = statement.executeQuery();

                if (result.next() && !result.getString("barcode").isEmpty()) {
                    String barcode = result.getString("barcode");
                    String merek = result.getString("merek");
                    int harga = result.getInt("harga_jual");
                    Button batal = new Button("X");
                    batal.setStyle(
                        "-fx-background-color: red; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-background-radius: 0; " +
                        "-fx-padding: 4px;" +
                        "-fx-cursor: hand;"
                    );

                    Barang barang = new Barang(barcode, merek, harga, qty, batal);

                    batal.setOnAction(e -> {
                        listBarang.remove(barang);
                        setTotal();
                    });
                    listBarang.add(barang);
                    setTotal();
                }else{
                    Session.animasiPanePesan(true, panePesan, lblPesan, "Produk tidak ditemukan", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                }

                result.close();
                statement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Session.animasiPanePesan(true, panePesan, lblPesan, "Terjadi kesalahan: " + e.getMessage(), btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
        }

        if (isBarcode) {
            txtBarcode.setText("");
            txtBarcodeQty.setText("");
        } else {
            txtManualQty.setText("");
        }
    }
    
    @FXML
    private void konfirmasiTransaksi() {
        if (listBarang.isEmpty()) {
            Session.animasiPanePesan(true, panePesan, lblPesan, "Tabel pesan masih kosong", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
            return;
        } else if (lblKembalian.getText().contains("-")) {
            Session.animasiPanePesan(true, panePesan, lblPesan, "Pembayaran kurang", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
            return;
        }

        String idTransaksi = getNewIdTransaksi();

        try {
            // Check if lblTotal is empty and set default value
            int total = lblTotal.getText().isEmpty() ? 0 : Session.convertRupiahToInt(lblTotal.getText());

            String query = "INSERT INTO transaksi_jual VALUES (?,?,NOW(),?,?,?,?,?)";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idTransaksi);
            statement.setString(2, Session.getIdAdmin());
            statement.setString(3, cbxCaraBayar.getValue().toLowerCase());
            statement.setInt(4, total); // Use the total variable here
            statement.setString(5, getIdDiskon(cbxDiskon.getValue()));
            statement.setInt(6, Session.convertRupiahToInt(lblKembalian.getText()));
            statement.setString(7, txtACatatan.getText());

            statement.executeUpdate();

            for (Barang barang : listBarang) {
                query = "INSERT INTO detail_transaksi_jual VALUES (?,?,?,?)";
                statement = Koneksi.getCon().prepareStatement(query);
                statement.setString(1, idTransaksi);
                statement.setString(2, getIdBarang(barang.getBarang()));
                statement.setInt(3, barang.getQty());
                statement.setInt(4, barang.getQty() * Session.convertRupiahToInt(barang.getHarga()));
                statement.executeUpdate();
            }

            statement.close();
            resetSemua();
            Session.animasiPanePesan(false, panePesan, lblPesan, "Transaksi berhasil!", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void resetSemua(){
        txtManualQty.setText("");
        txtBarcodeQty.setText("");
        txtBarcode.setText("");
        txtACatatan.setText("");
        txtBayar.setText("");
        cbxDiskon.setValue("");
        listBarang.clear();
        tabelBarang.setItems(listBarang);
        tabelBarang.refresh();
        setKembalian();
        setTotal();
    }
    
    private void setKasir(){
        try {
            String query = "SELECT username FROM admin WHERE id_admin=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, Session.getIdAdmin());
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                lblKasir.setText(result.getString("username"));
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setCbx(){
        //combo box cara bayar
        cbxCaraBayar.getItems().addAll("Tunai", "Transfer");
        cbxCaraBayar.setValue("Tunai");

        try {
            //combo box kategori
            String query = "SELECT nama_kategori FROM kategori";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);

            ResultSet result = statement.executeQuery();
            while(result.next()){
                cbxKategori.getItems().add(result.getString("nama_kategori"));
            }
            
            if (!cbxKategori.getItems().isEmpty()) {
                cbxKategori.setValue(cbxKategori.getItems().get(0));
            }
            
            //combo box produk
            query = "SELECT merek FROM barang \n" +
            "WHERE id_kategori = (SELECT id_kategori FROM kategori WHERE nama_kategori=?)";
            statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, cbxKategori.getItems().get(0));
            
            result = statement.executeQuery();
            
            while(result.next()){
                cbxProduk.getItems().add(result.getString("merek"));
            }
            
            if (!cbxProduk.getItems().isEmpty()) {
                cbxProduk.setValue(cbxProduk.getItems().get(0));
            }
            
            //combo box diskon
            query = "SELECT nama_diskon FROM diskon";
            statement = Koneksi.getCon().prepareStatement(query);
            
            result = statement.executeQuery();
            
            cbxDiskon.getItems().add("");
            while(result.next()){
                cbxDiskon.getItems().add(result.getString("nama_diskon"));
            }
            
            if (!cbxDiskon.getItems().isEmpty()) {
                cbxDiskon.setValue(cbxDiskon.getItems().get(0));
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void setCbxProduk(){
        cbxProduk.getItems().clear();
        cbxProduk.setValue(null);
        try {
            //combo box produk
            String query = "SELECT merek FROM barang \n" +
            "WHERE id_kategori = (SELECT id_kategori FROM kategori WHERE nama_kategori=?)";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, cbxKategori.getValue());
            
            ResultSet result = statement.executeQuery();
            
            while(result.next()){
                cbxProduk.getItems().add(result.getString("merek"));
            }
            
            if (!cbxProduk.getItems().isEmpty()) {
                cbxProduk.setValue(cbxProduk.getItems().get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setWaktuDanTanggal() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime now = LocalTime.now();
            lblJam.setText(now.format(formatWaktu));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        lblTanggal.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
    
    private void setTabelBarang(){
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colBarang.setCellValueFactory(new PropertyValueFactory<>("barang"));
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colBatal.setCellValueFactory(new PropertyValueFactory<>("batal"));

        tabelBarang.setEditable(true);

        colQty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colQty.setOnEditCommit(event -> {
            Barang barang = event.getRowValue(); 
            if(cekStokBarangCukup(barang.getBarang(), "", event.getNewValue())){
                barang.setQty(event.getNewValue());
                setTotal();
            }
            tabelBarang.refresh(); 
        });
    }
    
    private void setTotal(){
        int subtotal = 0;
        int diskon = getHargaDiskon(cbxDiskon.getValue());
        
        for(Barang barang : listBarang){
            subtotal += Session.convertRupiahToInt(barang.getSubtotal());
        }
        lblSubtotal.setText(Session.convertIntToRupiah(subtotal));
        lblTotal.setText(Session.convertIntToRupiah(subtotal - diskon));
        setKembalian();
    }
    
    @FXML
    private void setKembalian(){
        int bayar = txtBayar.getText().isEmpty() ? 0 : Integer.parseInt(txtBayar.getText());
        int total = Session.convertRupiahToInt(lblTotal.getText());
        
        lblKembalian.setText(Session.convertIntToRupiah(bayar - total));
    }
    
    private boolean cekStokBarangCukup(String merek, String barcode, int qty){
        boolean cukup = false;
        try {
            String query = "";
            if(barcode.isEmpty()){
                query = "SELECT stok FROM barang WHERE merek=?";
            }else{
                query = "SELECT stok FROM barang WHERE barcode=?";
            }
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, barcode.isEmpty() ? merek : barcode);

            ResultSet result = statement.executeQuery();
            if(result.next()){
                if(result.getInt("stok") >= qty){
                    cukup = true;
                }else{
                    Session.animasiPanePesan(true, panePesan, lblPesan, "Stok tidak cukup untuk " + merek, btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                }
            }
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cukup;
    }
    
    private String getNewIdTransaksi(){
        String transaksiId = "jual0001";
        
        try {
            String query = "SELECT id_transaksi_jual FROM transaksi_jual ORDER BY id_transaksi_jual DESC LIMIT 1";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                String idLama = result.getString("id_transaksi_jual");
                int nomorBaru = Integer.parseInt(idLama.substring(4));
                transaksiId = String.format("jual%04d", nomorBaru + 1);
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return transaksiId;
    }
    
    private String getIdBarang(String merek){
        String idBarang = "";
        
        try {
            String query = "SELECT id_barang FROM barang WHERE merek=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, merek);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                idBarang = result.getString("id_barang");
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return idBarang;
    }
    
    private int getHargaDiskon(String namaDiskon){
        if(namaDiskon.isEmpty()){
            return 0;
        }
        int diskon = 0;
        
        try {
            String query = "SELECT harga_diskon FROM diskon WHERE nama_diskon=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, namaDiskon);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                diskon = result.getInt("harga_diskon");
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return diskon;
    }
    
    private String getIdDiskon(String namaDiskon){
        if(namaDiskon.isEmpty()){
            return null;
        }
        String idDiskon = "";
        
        try {
            String query = "SELECT id_diskon FROM diskon WHERE nama_diskon=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, namaDiskon);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                idDiskon = result.getString("id_diskon");
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return idDiskon;
    }
}