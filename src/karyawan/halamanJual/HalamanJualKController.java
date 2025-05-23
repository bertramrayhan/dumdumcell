package karyawan.halamanJual;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import main.Koneksi;
import main.Session;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class HalamanJualKController implements Initializable {
    @FXML private AnchorPane paneCetakStruk;
    @FXML private StackPane panePesan;
    @FXML private Label lblTanggal, lblJam, lblKasir, lblSubtotal, lblTotal, lblKembalian, lblPesan;
    @FXML private TextField txtBarcode, txtBarcodeQty, txtBayar, txtManualQty;
    @FXML private Button btnTambahProdukBarcode, btnTambahProdukManual, btnBatalTransaksi, btnKonfirmasiTransaksi, btnTutupCetakStruk, btnIyaCetakStruk;
    @FXML private TextArea txtACatatan;
    @FXML private ChoiceBox<String> cbxCaraBayar, cbxKategori, cbxProduk, cbxDiskon;
    @FXML private TableView<Barang> tabelBarang;
    @FXML private TableColumn<Barang, String> colBarcode, colBarang, colHarga, colSubtotal;
    @FXML private TableColumn<Barang, Integer> colQty;
    @FXML private TableColumn<Barang, Button> colBatal;
    static ObservableList<Barang> listBarang = FXCollections.observableArrayList();
    
    private String idTransaksiBaru = "";
    
    //RIWAYAT TRANSAKSI
    @FXML private Tab tabRiwayatTransaksi;
    @FXML private Label lblTotalPenjualanBarang, lblTotalPenjualanSaldo, lblTotalPenjualan;
    @FXML private ChoiceBox<String> cbxShift;
    @FXML private DatePicker dtPTanggalAwal, dtPTanggalAkhir;
    @FXML private Button btnDetail, btnUnduh;
    @FXML private ImageView imgDetail, imgUnduh;
    @FXML private TableView<Transaksi> tabelTransaksi;
    @FXML private TableColumn<Transaksi, String> colKaryawan, colTanggal, colWaktu, colJenisPembayaran, colDiskon, colTotalPembelian, colKembalian;
    static ObservableList<Transaksi> listTransaksi = FXCollections.observableArrayList();
    
    private final DateTimeFormatter formatWaktu = DateTimeFormatter.ofPattern("HH:mm");
    
    static String idTransaksiTerpilih = "";
    
    private void setTextFieldNumeric(){
        Session.setTextFieldNumeric(txtBayar);
        Session.setTextFieldNumeric(txtBarcode, 13);
        Session.setTextFieldNumeric(txtBarcodeQty);
        Session.setTextFieldNumeric(txtManualQty);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTextFieldNumeric();
        setTabelBarang();
        setWaktuDanTanggal();
        setKasir();
        setCbxTransaksi();
        cbxKategori.setOnAction(event -> {
            setCbxProduk();
        });
        cbxDiskon.setOnAction(event -> {
            setTotal();
        });
        Session.triggerOnEnter(this::konfirmasiTransaksi, txtBayar);
        
        //RIWAYAT TRANSAKSI
        setKomponenRiwayatTransaksi();
        setDatePicker();
        setTabelTransaksi();
        getSemuaTransaksi();
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
            if(txtBarcode.getText().trim().isEmpty()){
                Session.animasiPanePesan(true, panePesan, lblPesan, "Masukkan barcode produk", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                return;
            }else if (txtBarcodeQty.getText().isEmpty() || txtBarcodeQty.getText().equals("0")) {
                Session.animasiPanePesan(true, panePesan, lblPesan, "Masukkan jumlah produk yang dipesan", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                return;
            } else {
                String inpBarcode = txtBarcode.getText().trim();
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
                String query = "SELECT b.id_barang, b.barcode, b.merek, b.harga_jual\n" +
                               "FROM barang b " +
                               "WHERE " + (isBarcode ? "b.barcode = ? " : "b.merek = ? ") +
                               "AND b.exp > DATE(NOW())";
                PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
                statement.setString(1, identifier);

                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    String idBarang = result.getString("id_barang");
                    String barcode = result.getString("barcode");
                    String merek = result.getString("merek");
                    int harga = result.getInt("harga_jual");
                    Button batal = new Button();
                    batal.setStyle(
                        "-fx-background-color: #F44336;" +
                        "-fx-text-fill: white;" +               
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-pref-width: 26px;" +
                        "-fx-pref-height: 26px;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0;"
                    );

                    ImageView iconHapus = new ImageView(new Image(getClass().getResource("/assets/icons/delete16px.png").toExternalForm()));
                    iconHapus.setFitHeight(16);
                    iconHapus.setFitWidth(16);
                    batal.setGraphic(iconHapus);
                    
                    Barang barang = new Barang(idBarang, barcode, merek, harga, qty, batal);

                    batal.setOnAction(e -> {
                        listBarang.remove(barang);
                        setTotal();
                    });
                    listBarang.add(barang);
                }else{
                    Session.animasiPanePesan(true, panePesan, lblPesan, "Produk sudah expired", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
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
        setTotal();
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

        idTransaksiBaru = Session.membuatIdBaru("transaksi_jual", "id_transaksi_jual", "jual", 4);

        try {
            int subtotal = Session.convertRupiahToInt(lblSubtotal.getText());
            int total = Session.convertRupiahToInt(lblTotal.getText());

            String query = "INSERT INTO transaksi_jual VALUES (?,?,NOW(),?,?,?,?,?,?)";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idTransaksiBaru);
            statement.setString(2, Session.getIdAdmin());
            statement.setString(3, cbxCaraBayar.getValue());
            statement.setInt(4, subtotal);
            statement.setInt(5, total);
            statement.setString(6, getIdDiskon(cbxDiskon.getValue()));
            statement.setInt(7, Session.convertRupiahToInt(lblKembalian.getText()));
            statement.setString(8, txtACatatan.getText().trim());

            statement.executeUpdate();

            for (Barang barang : listBarang) {
                query = "INSERT INTO detail_transaksi_jual VALUES (?,?,?,?)";
                statement = Koneksi.getCon().prepareStatement(query);
                statement.setString(1, idTransaksiBaru);
                statement.setString(2, barang.getIdBarang());
                statement.setInt(3, barang.getQty());
                statement.setInt(4, barang.getQty() * Session.convertRupiahToInt(barang.getHarga()));
                statement.executeUpdate();
            }

            statement.close();
            refresh();
            Session.animasiPanePesan(false, panePesan, lblPesan, "Transaksi berhasil!", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        bukaCetakStruk();
    }
    
    @FXML
    public void refresh(){
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
    
    private void setCbxTransaksi(){
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
            "WHERE id_kategori = (SELECT id_kategori FROM kategori WHERE nama_kategori=?) " +
            "AND barang.exp > DATE(NOW())";
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
            query = "SELECT nama_diskon FROM diskon WHERE status='aktif'";
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
    
    private void setCbxProduk(){
        cbxProduk.getItems().clear();
        cbxProduk.setValue(null);
        try {
            //combo box produk
            String query = "SELECT merek FROM barang \n" +
            "WHERE id_kategori = (SELECT id_kategori FROM kategori WHERE nama_kategori=?) " +
            "AND barang.exp > DATE(NOW())";
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
        
        for(Barang barang : listBarang){
            subtotal += Session.convertRupiahToInt(barang.getSubtotal());
        }
        lblSubtotal.setText(Session.convertIntToRupiah(subtotal));
        
        int diskon = getHargaDiskon(cbxDiskon.getValue());
        int total = subtotal - diskon >= 0 ? subtotal - diskon : 0;
                
        lblTotal.setText(Session.convertIntToRupiah(total));
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
                query = "SELECT stok_utama FROM barang WHERE merek=?";
            }else{
                query = "SELECT stok_utama FROM barang WHERE barcode=?";
            }
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, barcode.isEmpty() ? merek : barcode);

            ResultSet result = statement.executeQuery();
            if(result.next()){
                if(result.getInt("stok_utama") >= qty){
                    cukup = true;
                }else{
                    Session.animasiPanePesan(true, panePesan, lblPesan, "Stok tidak cukup untuk " + merek, btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                }
            }else{
                Session.animasiPanePesan(true, panePesan, lblPesan, "Produk tidak ditemukan", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
            }
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cukup;
    }
        
    private int getHargaDiskon(String namaDiskon){
        if(namaDiskon.isEmpty()){
            return 0;
        }
        int diskon = 0;
        
        try {
            String query = "SELECT jenis_diskon, harga_diskon FROM diskon WHERE nama_diskon=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, namaDiskon);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                if(result.getString("jenis_diskon").equals("Persentase")){
                    diskon = Session.convertRupiahToInt(lblSubtotal.getText()) * result.getInt("harga_diskon") / 100;
                }else{
                    diskon = result.getInt("harga_diskon");
                }
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
    
    private void bukaCetakStruk(){
        Session.setShowPane(paneCetakStruk);
        tabRiwayatTransaksi.setDisable(true);
        
    }
    
    @FXML
    private void tutupCetakStruk(){
        Session.setHidePane(paneCetakStruk);
        tabRiwayatTransaksi.setDisable(false);
    }
    
    @FXML
    private void cetakStruk(){
        String reportPath = "src/main/struk.jasper";
                   
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id_transaksi_jual", idTransaksiBaru);

        try {
            JasperPrint print = JasperFillManager.fillReport(reportPath, parameter, Koneksi.getCon());
            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        tutupCetakStruk();
    }
        
    //RIWAYAT TRANSAKSI
    public class Transaksi{
        String idTransaksi, karyawan, tanggal, waktu, jenisPembayaran, diskon, totalPembelian, kembalian;

        public Transaksi(String idTransaksi, String karyawan, String tanggal, String waktu, String jenisPembayaran, String diskon, 
                String totalPembelian, String kembalian) {
            this.idTransaksi = idTransaksi;
            this.karyawan = karyawan;
            this.tanggal = tanggal;
            this.waktu = waktu;
            this.jenisPembayaran = jenisPembayaran;
            this.diskon = diskon;
            this.totalPembelian = totalPembelian;
            this.kembalian = kembalian;
        }
        public String getIdTransaksi() {return idTransaksi;}
        public String getKaryawan() {return karyawan;}
        public String getTanggal() {return tanggal;}
        public String getWaktu() {return waktu;}
        public String getJenisPembayaran() {return jenisPembayaran;}
        public String getDiskon() {return diskon;}
        public String getTotalPembelian() {return totalPembelian;}
        public String getKembalian() {return kembalian;}
        @Override
        public String toString() {
            return String.format(
            "Transaksi [Karyawan: %s, Tanggal: %s, Waktu: %s, Jenis Pembayaran: %s, Diskon: %s, Total Pembelian: %s, Kembalian: %s]",
            karyawan, tanggal, waktu, jenisPembayaran, diskon, totalPembelian, kembalian);
        }
    }
    
    private void setKomponenRiwayatTransaksi(){
        cbxShift.getItems().addAll("Pagi", "Malam");
        cbxShift.setValue("Pagi");
        cbxShift.setOnAction(event -> {
            getSemuaTransaksi();
        });
        
        btnDetail.setDisable(true);
        btnUnduh.setDisable(true);
        imgDetail.setOpacity(0.5F);
        imgUnduh.setOpacity(0.5F);
        
        tabelTransaksi.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                btnDetail.setDisable(false);
                btnUnduh.setDisable(false);
                imgDetail.setOpacity(1F);
                imgUnduh.setOpacity(1F);
            } else {
                btnDetail.setDisable(true);
                btnUnduh.setDisable(true);
                imgDetail.setOpacity(0.5F);
                imgUnduh.setOpacity(0.5F);
            }
        });
    }
    
    private void setDatePicker(){
        dtPTanggalAwal.getEditor().setDisable(true);
        dtPTanggalAwal.getEditor().setOpacity(1);
        
        dtPTanggalAkhir.getEditor().setDisable(true);
        dtPTanggalAkhir.getEditor().setOpacity(1);

        dtPTanggalAwal.setValue(LocalDate.now());
        dtPTanggalAkhir.setValue(LocalDate.now());

        // Setup DatePicker Tanggal Awal
        dtPTanggalAwal.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                // Blokir tanggal di masa depan
                if (item.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Warna merah muda buat disabled
                }

                // Blokir kalau tanggal awal lebih dari tanggal akhir
                if (dtPTanggalAkhir.getValue() != null && item.isAfter(dtPTanggalAkhir.getValue())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Setup DatePicker Tanggal Akhir
        dtPTanggalAkhir.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                // Blokir tanggal di masa depan
                if (item.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }

                // Blokir kalau tanggal akhir kurang dari tanggal awal
                if (dtPTanggalAwal.getValue() != null && item.isBefore(dtPTanggalAwal.getValue())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Listener untuk update DatePicker saat ada perubahan nilai
        dtPTanggalAwal.valueProperty().addListener((obs, oldValue, newValue) -> {
            dtPTanggalAkhir.setDayCellFactory(dp -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item.isAfter(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }

                    if (dtPTanggalAwal.getValue() != null && item.isBefore(dtPTanggalAwal.getValue())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });
        });

        dtPTanggalAkhir.valueProperty().addListener((obs, oldValue, newValue) -> {
            dtPTanggalAwal.setDayCellFactory(dp -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item.isAfter(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }

                    if (dtPTanggalAkhir.getValue() != null && item.isAfter(dtPTanggalAkhir.getValue())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });
        });
    }
    
    private void setTabelTransaksi(){
        colKaryawan.setCellValueFactory(new PropertyValueFactory<>("karyawan"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colWaktu.setCellValueFactory(new PropertyValueFactory<>("waktu"));
        colJenisPembayaran.setCellValueFactory(new PropertyValueFactory<>("jenisPembayaran"));
        colDiskon.setCellValueFactory(new PropertyValueFactory<>("diskon"));
        colTotalPembelian.setCellValueFactory(new PropertyValueFactory<>("totalPembelian"));
        colKembalian.setCellValueFactory(new PropertyValueFactory<>("kembalian"));
    }
    
    @FXML
    private void getSemuaTransaksi(){
        String shift = cbxShift.getValue();
        String tanggalAwal = dtPTanggalAwal.getValue().toString();
        String tanggalAkhir = dtPTanggalAkhir.getValue().toString();
        
        if(tanggalAwal.isEmpty() || tanggalAkhir.isEmpty()){
            return;
        }
        
        String[] waktuShift = getWaktuShift(shift);
        listTransaksi.clear();
        try {
            String query = "SELECT tj.id_transaksi_jual AS id_transaksi, adm.username AS username, DATE(tj.tanggal_transaksi_jual) AS tanggal, \n" +
            "TIME(tj.tanggal_transaksi_jual) AS waktu, tj.cara_bayar AS jenis_pembayaran,\n" +
            "dsk.nama_diskon AS diskon, tj.total_transaksi_jual AS total_pembelian,\n" +
            "tj.kembalian AS kembalian\n" +
            "FROM transaksi_jual tj\n" +
            "JOIN admin adm ON adm.id_admin = tj.id_admin\n" +
            "LEFT JOIN diskon dsk ON dsk.id_diskon = tj.id_diskon\n" +
            "WHERE DATE(tanggal_transaksi_jual) BETWEEN ? AND ?\n" +
            "AND TIME(tj.tanggal_transaksi_jual) BETWEEN ? AND ?\n" +
            "ORDER BY tanggal_transaksi_jual ASC";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, tanggalAwal);
            statement.setString(2, tanggalAkhir);
            statement.setString(3, waktuShift[0]);
            statement.setString(4, waktuShift[1]);
            ResultSet result = statement.executeQuery();
            
            int totalPenjualanBarang = 0;
            int totalPenjualanSaldo = 0;
            int totalPenjualan = 0;
            
            while(result.next()) {
                String idTransaksi = result.getString("id_transaksi");
                String karyawan = result.getString("username");
                String tanggal = Session.convertTanggalIndo(result.getString("tanggal"));
                String waktu = result.getString("waktu");
                String jenisPembayaran = result.getString("jenis_pembayaran");
                String diskon = result.getString("diskon");
                int totalPembelian = result.getInt("total_pembelian");
                String kembalian = Session.convertIntToRupiah(result.getInt("kembalian"));
                
                Transaksi trs = new Transaksi(idTransaksi, karyawan, tanggal, waktu, jenisPembayaran, diskon, 
                Session.convertIntToRupiah(totalPembelian), kembalian);
                listTransaksi.add(trs);
                
                totalPenjualanBarang += totalPembelian;
            }
                        
            //MENGHITUNG TOTAL SALDO
            query = "SELECT SUM(total_topup) AS total_topup\n" +
            "FROM topup_saldo_pelanggan\n" +
            "WHERE DATE(tanggal) BETWEEN ? AND ?\n" +
            "AND TIME(tanggal) BETWEEN ? AND ?";
            statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, tanggalAwal);
            statement.setString(2, tanggalAkhir);
            statement.setString(3, waktuShift[0]);
            statement.setString(4, waktuShift[1]);
            result = statement.executeQuery();
            
            if(result.next()){
                totalPenjualanSaldo = result.getInt("total_topup");
            }
            
            totalPenjualan = totalPenjualanBarang + totalPenjualanSaldo;
            lblTotalPenjualanBarang.setText(Session.convertIntToRupiah(totalPenjualanBarang));
            lblTotalPenjualanSaldo.setText(Session.convertIntToRupiah(totalPenjualanSaldo));
            
            //MENGHITUNG TOTAL TRANSAKSI LAIN
            query = "SELECT SUM(\n" +
            "    CASE\n" +
            "        WHEN jenis_transaksi = 'Pemasukan' THEN nominal\n" +
            "        WHEN jenis_transaksi = 'Pengeluaran' THEN -nominal\n" +
            "        ELSE 0\n" +
            "    END\n" +
            ") AS total_transaksi_lain\n" +
            "FROM transaksi_lain\n" +
            "WHERE DATE(tanggal_transaksi) BETWEEN ? AND ?\n" +
            "AND TIME(tanggal_transaksi) BETWEEN ? AND ?";
            statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, tanggalAwal);
            statement.setString(2, tanggalAkhir);
            statement.setString(3, waktuShift[0]);
            statement.setString(4, waktuShift[1]);
            result = statement.executeQuery();
            
            if(result.next()){
                totalPenjualan += result.getInt("total_transaksi_lain");
            }
            
            lblTotalPenjualan.setText(Session.convertIntToRupiah(totalPenjualan));
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        tabelTransaksi.setItems(listTransaksi);
        tabelTransaksi.refresh();
    }
    
    @FXML
    private void bukaDetailTransaksi(){
        int barisTerpilih = tabelTransaksi.getSelectionModel().getSelectedIndex();
        Transaksi transaksiTerpilih = listTransaksi.get(barisTerpilih);
        idTransaksiTerpilih = transaksiTerpilih.getIdTransaksi();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/karyawan/halamanJual/detail.fxml"));
            Parent root = loader.load();

            Stage detailTransaksiStage = new Stage();
            detailTransaksiStage.initModality(Modality.APPLICATION_MODAL);
            detailTransaksiStage.initStyle(StageStyle.UNDECORATED);
            detailTransaksiStage.centerOnScreen();
            detailTransaksiStage.setScene(new Scene(root));

            DetailController controller = loader.getController();
            controller.setDetailTransaksiStage(detailTransaksiStage);
            
            detailTransaksiStage.showAndWait(); // Tunggu user klik "Iya" atau "Tidak"
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void cetakStrukTransaksi() {
        int barisTerpilih = tabelTransaksi.getSelectionModel().getSelectedIndex();
        Transaksi transaksiTerpilih = listTransaksi.get(barisTerpilih);
        idTransaksiTerpilih = transaksiTerpilih.getIdTransaksi();
        String reportPath = "src/main/struk.jasper";

        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id_transaksi_jual", idTransaksiTerpilih);

        try {
            JasperPrint print = JasperFillManager.fillReport(reportPath, parameter, Koneksi.getCon());

            // Menambahkan pengaturan DPI
            System.out.println(print.getPageHeight());
            System.out.println(print.getPageWidth());
//            print.setPageHeight((int) (print.getPageHeight() * 203 / 72)); // Mengatur tinggi halaman
//            print.setPageWidth((int) (print.getPageWidth() * 203 / 72));   // Mengatur lebar halaman

            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] getWaktuShift(String shift){
        String[] waktuShift = new String[2];
        
        try {
            String query = "SELECT jam_masuk, jam_selesai FROM shift WHERE nama_shift=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, shift);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                waktuShift[0] = result.getString("jam_masuk");
                waktuShift[1] = result.getString("jam_selesai");
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return waktuShift;
    }
}