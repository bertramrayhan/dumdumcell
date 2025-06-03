package pemilik.halamanProduk;

import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.converter.DefaultStringConverter;
import main.Koneksi;
import main.Pelengkap;
import main.Session;

public class HalamanProdukPController implements Initializable, Pelengkap {
    
    @FXML private Pane paneGelap;
    @FXML private Button btnTambahBarang, btnEditBarang, btnHapusBarang, btnKelolaKategori;
    @FXML private ImageView btnX;
    @FXML private TextField txtSearchBar;
    @FXML private ChoiceBox<String> sortBy;
    @FXML private TableView<Barang> tabelBarang;
    @FXML private TableColumn<Barang, String> colNamaBarang, colKategori, colMerek, colHargaJual, colStok, colExp, colBarcode;
    @FXML private TableRow<Barang> colTerakhir;
        
    private ObservableList<Barang> listBarang = FXCollections.observableArrayList();
    
    //TAMBAH BARANG
    @FXML private AnchorPane paneTambahBarang;
    @FXML private TextField txtNamaBarangTambah, txtNamaMerekTambah, txtBarcodeTambah, txtHargaJualTambah;
    @FXML private ChoiceBox<String> cbxKategoriTambah;
    @FXML private DatePicker dtPTanggalExpTambah;
    @FXML private Button btnBatalTambahBarang, btnIyaTambahBarang;
    
    //EDIT BARANG
    @FXML private AnchorPane paneEditBarang;
    @FXML private TextField txtNamaBarangEdit, txtNamaMerekEdit, txtBarcodeEdit, txtHargaJualEdit;
    @FXML private ChoiceBox<String> cbxKategoriEdit;
    @FXML private DatePicker dtPTanggalExpEdit;
    @FXML private Button btnBatalEditBarang, btnIyaEditBarang;
    Barang barangTerpilih;
    String idBarangTerpilih;
    
    //HAPUS BARANG
    @FXML private AnchorPane paneHapusBarang;
    @FXML private Button btnBatalHapusBarang, btnIyaHapusBarang;
    
    //KATEGORI
    @FXML private AnchorPane paneKelolaKategori;
    @FXML private Button btnTambahKategori;
    @FXML private Label btnTutup;
    @FXML private TableView<Kategori> tabelKategori;
    @FXML private TableColumn<Kategori, String> colNamaKategori;
    @FXML private TableColumn<Kategori, HBox> colAksi;
    private ObservableList<Kategori> listKategori = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTabelBarang();
        setSortBy();
        getDataTabelBarang();
        sortBy.setOnAction(event -> {
            getDataTabelBarang();
        });
        
        //TAMBAH BARANG
        setKomponenTambahBarang();
        
        //EDIT BARANG
        setKomponenEditBarang();
        
        //KELOLA KATEGORI
        setKomponenKelolaKategori();        
    }    

    @Override
    public void perbarui() {
        getDataTabelBarang();
    }
    
    @FXML
    private void getDataTabelBarang() {
        listBarang.clear();
        String keyword = txtSearchBar.getText().trim();
        
        String query = "SELECT b.id_barang, b.nama_barang, b.harga_jual, b.stok_utama, b.exp, b.merek, b.barcode, k.nama_kategori " +
                       "FROM barang b " +
                       "JOIN kategori k ON b.id_kategori = k.id_kategori\n" +
                       "WHERE b.is_deleted = FALSE ";
        
        boolean isAngka = keyword != null && keyword.matches("\\d+"); // Deteksi angka
        boolean isSearch = keyword != null && !keyword.trim().isEmpty();

        if (isSearch) {
            if (isAngka) {
                query += " AND (b.stok_utama = ? OR YEAR(b.exp) LIKE ? OR b.barcode LIKE ?)";
            } else {
                query += " AND (b.nama_barang LIKE ? OR k.nama_kategori LIKE ? OR b.merek LIKE ?)";
            }
        }

        query += getOrderBy();
        try (PreparedStatement statement = Koneksi.getCon().prepareStatement(query)) {
            if (isSearch) {
                if (isAngka) {
                    int angka = Integer.parseInt(keyword);
                    statement.setInt(1, angka); // stok
                    statement.setString(2, "%" + angka + "%"); // tahun exp
                    statement.setString(3, "%" + angka + "%");
                } else {
                    String searchKeyword = "%" + keyword + "%";
                    statement.setString(1, searchKeyword);
                    statement.setString(2, searchKeyword);
                    statement.setString(3, searchKeyword);
                }
            }

             ResultSet result = statement.executeQuery();
             while (result.next()) {
                String idBarang = result.getString("id_barang");
                String namaBarang = result.getString("nama_barang");
                String kategori = result.getString("nama_kategori");
                String hargaJual = Session.convertIntToRupiah(result.getInt("harga_jual"));
                String stok = result.getString("stok_utama");
                String exp = Session.convertTanggalIndo(result.getString("exp"));
                String merek = result.getString("merek");
                String barcode = result.getString("barcode");

                listBarang.add(new Barang(idBarang, namaBarang, kategori, merek, exp, hargaJual, barcode, stok));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        tabelBarang.setItems(listBarang);
    }
    
    @FXML
    private void clearSearchBar(){
        txtSearchBar.setText("");
        getDataTabelBarang();
    }
    
    private void setTabelBarang(){
        colNamaBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colHargaJual.setCellValueFactory(new PropertyValueFactory<>("hargaJual"));
        colMerek.setCellValueFactory(new PropertyValueFactory<>("merek"));
        colExp.setCellValueFactory(new PropertyValueFactory<>("exp"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
        LocalDate today = LocalDate.now();
        tabelBarang.setRowFactory(tv -> new TableRow<Barang>() {
            @Override
            protected void updateItem(Barang item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    String expString = item.getExp();
                    LocalDate expDate = LocalDate.parse(expString, dateFormatter);
                    
                    int stok = Integer.parseInt(item.getStok());
                    if (stok == 0 || expDate.isBefore(today)) {
                        setStyle("-fx-background-color: #ffcccc; -fx-text-fill: black;");
                    }
                    
                    setOnMousePressed(event -> {
                        if(colTerakhir != null){
                            String expColTerakhir = colTerakhir.getItem().getExp();
                            LocalDate expDateColTerakhir = LocalDate.parse(expColTerakhir, dateFormatter);
                            if(colTerakhir.getItem().getStok().matches("0+") || expDateColTerakhir.isBefore(today)){
                                colTerakhir.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: black;");
                            }
                        }
                        
                        if (stok == 0 || expDate.isBefore(today)) {
                            setStyle("-fx-background-color: #e57373; -fx-text-fill: black;");
                        }
                        colTerakhir = this;
                    });
                }
            }
        });
        
        tabelBarang.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !paneEditBarang.isVisible() && !paneTambahBarang.isVisible() && !paneHapusBarang.isVisible()) {
                btnEditBarang.setDisable(false);
                btnHapusBarang.setDisable(false);
            } else {
                btnEditBarang.setDisable(true);
                btnHapusBarang.setDisable(true);
            }
        });
    }
    
    private String getOrderBy() {
        String sortOption = sortBy.getValue();
        if (sortOption == null || sortOption.equals("Sort by")) {
            return ""; // Tidak ada sorting
        }
        switch (sortOption) {
                    case "Nama Barang (A-Z)":
                return " ORDER BY b.nama_barang ASC";
            case "Nama Barang (Z-A)":
                return " ORDER BY b.nama_barang DESC";
            case "Barang Hampir Expired":
                return " ORDER BY b.exp ASC";
            case "Kategori":
                return " ORDER BY k.nama_kategori ASC";
            case "Stok Terbanyak":
                return " ORDER BY b.stok_utama DESC";
            case "Stok Terdikit":
                return " ORDER BY b.stok_utama ASC";
            default:
                return "";
        }
    }
    
    private void setSortBy() {
        sortBy.getItems().addAll("Sort by","Nama Barang (A-Z)","Nama Barang (Z-A)","Barang Hampir Expired","Kategori","Stok Terbanyak",
            "Stok Terdikit");
         sortBy.setValue("Sort by");
    }
    
    public class Barang {
        private String idBarang, namaBarang, kategori, merek, hargaJual, exp, barcode, stok;

        public Barang(String idBarang, String namaBarang, String kategori, String merek, String exp, String hargaJual, 
                String barcode, String stok) {
            this.idBarang = idBarang;
            this.namaBarang = namaBarang;
            this.kategori = kategori;
            this.merek = merek;
            this.hargaJual = hargaJual;
            this.exp = exp;
            this.stok = stok;
            this.barcode = barcode;
        }

        public String getIdBarang() {return idBarang;}
        public String getNamaBarang() {return namaBarang;}
        public String getKategori() {return kategori;}
        public String getMerek() {return merek;}
        public String getHargaJual() {return hargaJual;}
        public String getExp() {return exp;}
        public String getStok() {return stok;}
        public String getBarcode() {return barcode;}
    }
    
    //TAMBAH BARANG
    private void setKomponenTambahBarang(){
        Session.triggerOnEnter(this::tambahBarang, txtNamaBarangTambah, txtNamaMerekTambah, txtBarcodeTambah, txtHargaJualTambah);
        dtPTanggalExpTambah.getEditor().setDisable(true);
        dtPTanggalExpTambah.getEditor().setOpacity(1);
        dtPTanggalExpTambah.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                // Blokir tanggal di masa depan
                if (!item.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
        dtPTanggalExpTambah.setValue(LocalDate.now().plusDays(1));

        Session.setTextFieldNumeric(13, txtBarcodeTambah);
        Session.setTextFieldNumeric(txtHargaJualTambah);
        
        try {
            String query = "SELECT nama_kategori FROM kategori";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            
            ResultSet result = statement.executeQuery();
            
            while(result.next()) {
                cbxKategoriTambah.getItems().add(result.getString("nama_kategori"));
            }
            cbxKategoriTambah.setValue(cbxKategoriTambah.getItems().get(0));
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void bukaTambahBarang(){
        Session.setShowPane(paneTambahBarang, paneGelap);
        Session.setMouseTransparentTrue(tabelBarang, btnTambahBarang, btnEditBarang, btnHapusBarang, btnKelolaKategori, txtSearchBar, btnX, sortBy);
    }
    
    @FXML
    private void tutupTambahBarang(){
        Session.setHidePane(paneTambahBarang, paneGelap);
        Session.setMouseTransparentFalse(tabelBarang, btnTambahBarang, btnEditBarang, btnHapusBarang, btnKelolaKategori, txtSearchBar, btnX, sortBy);
        txtNamaBarangTambah.setText("");
        txtNamaMerekTambah.setText("");
        txtBarcodeTambah.setText("");
        txtHargaJualTambah.setText("");
        cbxKategoriTambah.setValue(cbxKategoriTambah.getItems().get(0));
        dtPTanggalExpTambah.setValue(LocalDate.now().plusDays(1));
    }
    
    @FXML
    private void tambahBarang(){
        String namaBarang = txtNamaBarangTambah.getText().trim();
        String namaMerek = txtNamaMerekTambah.getText().trim();
        String barcodeBarang = txtBarcodeTambah.getText().trim();
        String hargaJual = txtHargaJualTambah.getText().trim();
        String expired = dtPTanggalExpTambah.getValue().toString();
        
        if(namaBarang.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nama Barang", btnIyaTambahBarang, btnBatalTambahBarang);
            return;
        }else if(namaMerek.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nama Merek", btnIyaTambahBarang, btnBatalTambahBarang);
            return;
        }else if(Session.cekDataSama("SELECT * FROM barang WHERE merek=? AND is_deleted = FALSE", namaMerek)){
            Session.animasiPanePesan(true, "Merek Barang Sudah Ada", btnIyaTambahBarang, btnBatalTambahBarang);
            return;
        }else if(barcodeBarang.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Barcode Barang", btnIyaTambahBarang, btnBatalTambahBarang);
            return;
        }else if(!(barcodeBarang.length() == 13)){
            Session.animasiPanePesan(true, "Masukkan Barcode dengan Panjang 13 Digit", btnIyaTambahBarang, btnBatalTambahBarang);
            return;
        }else if(Session.cekDataSama("SELECT * FROM barang WHERE barcode=? AND is_deleted = FALSE", barcodeBarang)){
            Session.animasiPanePesan(true, "Barcode Barang Sudah Ada", btnIyaTambahBarang, btnBatalTambahBarang);
            return;
        }else if(hargaJual.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Harga Jual Barang", btnIyaTambahBarang, btnBatalTambahBarang);
            return;
        }
        
        boolean adaBarangSamaDeleted = Session.cekDataSama("SELECT * FROM barang WHERE is_deleted=TRUE AND merek=? AND barcode=?", namaMerek, barcodeBarang);
        
        String idBarangBaru = Session.membuatIdBaru("barang", "id_barang", "brg", 4);
        String idKategori = getIdKategori(cbxKategoriTambah.getValue());
        try {
            String query;
            PreparedStatement statement = null;
            if(adaBarangSamaDeleted){
                query = "UPDATE barang SET nama_barang=?, merek=?, id_kategori=?, \n" +
                "Barcode=?, harga_jual=?, exp=?, stok_utama=0, stok_retur=0, is_deleted=FALSE\n" +
                "WHERE is_deleted=TRUE AND merek=? AND barcode=?";
                statement = Koneksi.getCon().prepareStatement(query);
                statement.setString(1, namaBarang);
                statement.setString(2, namaMerek);
                statement.setString(3, idKategori);
                statement.setString(4, barcodeBarang);
                statement.setString(5, hargaJual);
                statement.setString(6, expired);
                statement.setString(7, namaMerek);
                statement.setString(8, barcodeBarang);
            }else{
                query = "INSERT INTO barang (id_barang, nama_barang, id_kategori, merek, harga_jual, exp, barcode) \n" +
                "VALUES (?,?,?,?,?,?,?)";
                statement = Koneksi.getCon().prepareStatement(query);
                statement.setString(1, idBarangBaru);
                statement.setString(2, namaBarang);
                statement.setString(3, idKategori);
                statement.setString(4, namaMerek);
                statement.setString(5, hargaJual);
                statement.setString(6, expired);
                statement.setString(7, barcodeBarang);
            }            
            statement.executeUpdate();
            getDataTabelBarang();
            Session.animasiPanePesan(false, "Barang berhasil ditambahkan");
            tutupTambahBarang();
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
            
    private String getIdKategori(String kategori){
        String idKategori = "";
        
        try {
            String query = "SELECT id_kategori FROM kategori WHERE nama_kategori=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, kategori);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                idKategori = result.getString("id_kategori");
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idKategori;
    }
    
    //EDIT BARANG
    private void setKomponenEditBarang() {
        Session.triggerOnEnter(this::editBarang, txtNamaBarangEdit, txtNamaMerekEdit, txtBarcodeEdit, txtHargaJualEdit);
        dtPTanggalExpEdit.getEditor().setDisable(true);
        dtPTanggalExpEdit.getEditor().setOpacity(1);

        dtPTanggalExpEdit.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (!item.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
        dtPTanggalExpEdit.setValue(LocalDate.now());

        Session.setTextFieldNumeric(13, txtBarcodeEdit);
        Session.setTextFieldNumeric(txtHargaJualEdit);

        try {
            String query = "SELECT nama_kategori FROM kategori";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String kategori = result.getString("nama_kategori");
                cbxKategoriEdit.getItems().add(kategori);
            }

            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void bukaEditBarang(){
        Session.setShowPane(paneEditBarang, paneGelap);
        Session.setMouseTransparentTrue(tabelBarang, btnTambahBarang, btnEditBarang, btnHapusBarang, btnKelolaKategori, txtSearchBar, btnX, sortBy);
        
        int barisTerpilih = tabelBarang.getSelectionModel().getSelectedIndex();
        barangTerpilih = listBarang.get(barisTerpilih);
        idBarangTerpilih = barangTerpilih.getIdBarang();
        
        try {
            String query = "SELECT b.nama_barang, b.merek, k.nama_kategori, b.Barcode,\n" +
            "b.harga_jual, b.exp\n" +
            "FROM barang b\n" +
            "JOIN kategori k ON k.id_kategori = b.id_kategori\n" +
            "WHERE b.id_barang =?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idBarangTerpilih);
            ResultSet result = statement.executeQuery();
            
            if(result.next()) {
                txtNamaBarangEdit.setText(result.getString("nama_barang"));
                txtNamaMerekEdit.setText(result.getString("merek"));
                cbxKategoriEdit.setValue(result.getString("nama_kategori"));
                txtBarcodeEdit.setText(result.getString("barcode"));
                txtHargaJualEdit.setText(result.getString("harga_jual"));
                Date expDate = result.getDate("exp");
                LocalDate expLocalDate = expDate.toLocalDate();
                dtPTanggalExpEdit.setValue(expLocalDate);
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void tutupEditBarang(){
        Session.setHidePane(paneEditBarang, paneGelap);
        Session.setMouseTransparentFalse(tabelBarang, btnTambahBarang, btnEditBarang, btnHapusBarang, btnKelolaKategori, txtSearchBar, btnX, sortBy);
    }
    
    @FXML
    private void editBarang(){
        String namaBarang = txtNamaBarangEdit.getText().trim();
        String namaMerek = txtNamaMerekEdit.getText().trim();
        String barcodeBarang = txtBarcodeEdit.getText().trim();
        String hargaJual = txtHargaJualEdit.getText().trim();
        String expired = dtPTanggalExpEdit.getValue().toString();
                
        if(namaBarang.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nama Barang", btnIyaEditBarang, btnBatalEditBarang);
            return;
        }else if(namaMerek.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nama Merek", btnIyaEditBarang, btnBatalEditBarang);
            return;
        }else if(barcodeBarang.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Barcode Barang", btnIyaEditBarang, btnBatalEditBarang);
            return;
        }else if(!(barcodeBarang.length() == 13)){
            Session.animasiPanePesan(true, "Masukkan Barcode dengan Panjang 13 Digit", btnIyaEditBarang, btnBatalEditBarang);
            return;
        }else if(hargaJual.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Harga Jual Barang", btnIyaEditBarang, btnBatalEditBarang);
            return;
        }else if(!barangTerpilih.getBarcode().toLowerCase().equals(barcodeBarang.toLowerCase()) && Session.cekDataSama("SELECT * FROM barang WHERE is_deleted=FALSE AND barcode=?", barcodeBarang)){
            Session.animasiPanePesan(true, "Barcode Barang Sudah Ada", btnIyaEditBarang, btnBatalEditBarang);
            return;
        }
        
        String idKategori = getIdKategori(cbxKategoriEdit.getValue());
        try {
            String query = "UPDATE barang SET nama_barang=?, merek=?, id_kategori=?, \n" +
            "Barcode=?, harga_jual=?, exp=?\n" +
            "WHERE id_barang=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, namaBarang);
            statement.setString(2, namaMerek);
            statement.setString(3, idKategori);
            statement.setString(4, barcodeBarang);
            statement.setString(5, hargaJual);
            statement.setString(6, expired);
            statement.setString(7, idBarangTerpilih);
            statement.executeUpdate();
            
            getDataTabelBarang();
            Session.animasiPanePesan(false, "Barang berhasil diedit");
            tutupEditBarang();
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //HAPUS BARANG
    @FXML
    private void bukaHapusBarang(){
        Session.setShowPane(paneHapusBarang, paneGelap);
        Session.setMouseTransparentTrue(tabelBarang, btnTambahBarang, btnEditBarang, btnHapusBarang, btnKelolaKategori, txtSearchBar, btnX, sortBy);
        
        int barisTerpilih = tabelBarang.getSelectionModel().getSelectedIndex();
        Barang barangTerpilih = listBarang.get(barisTerpilih);
        idBarangTerpilih = barangTerpilih.getIdBarang();
    }
    
    @FXML
    private void tutupHapusBarang(){
        Session.setHidePane(paneHapusBarang, paneGelap);
        Session.setMouseTransparentFalse(tabelBarang, btnTambahBarang, btnEditBarang, btnHapusBarang, btnKelolaKategori, txtSearchBar, btnX, sortBy);
    }
    
    @FXML
    private void hapusBarang(){
        try {
            String query = "UPDATE barang SET is_deleted = TRUE WHERE id_barang=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idBarangTerpilih);
            statement.executeUpdate();
            
            getDataTabelBarang();
            Session.animasiPanePesan(false, "Barang berhasil dihapus");
            tutupHapusBarang();
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //KATEGORI
    public class Kategori{
        private String idKategori, namaKategori;
        private HBox aksi;
        public Kategori(String idKategori, String namaKategori, HBox aksi) {
            this.idKategori = idKategori;
            this.namaKategori = namaKategori;
            this.aksi = aksi;
        }
        public String getIdKategori() {return idKategori;}
        public String getNamaKategori() {return namaKategori;}
        public HBox getAksi() {return aksi;}
        public void setIdKategori(String idKategori) {this.idKategori = idKategori;}
        public void setNamaKategori(String namaKategori) {this.namaKategori = namaKategori;}
        public void setAksi(HBox aksi) {this.aksi = aksi;}
    }
    
    private void setKomponenKelolaKategori(){
        colNamaKategori.setCellValueFactory(new PropertyValueFactory<>("namaKategori"));
        colAksi.setCellValueFactory(new PropertyValueFactory<>("aksi"));
        
        tabelKategori.setEditable(true);
        
        colNamaKategori.setCellFactory(column -> {
            return new TextFieldTableCell<Kategori, String>(new DefaultStringConverter()) {
                private TextField textField;
                private String oldValue;

                @Override
                public void startEdit() {
                    oldValue = getItem(); // Store the original value before editing starts
                    super.startEdit();
                    textField = (TextField) getGraphic();

                    if (textField != null) {
                        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                            if (!isNowFocused) {
                                String input = textField.getText().trim();
                                commitEdit(input);
                            }
                        });
                    }
                }

                @Override
                public void commitEdit(String newValue) {
                    ObservableList<Kategori> listKategori = getTableView().getItems();
                    int currentIndex = getIndex();

                    if (currentIndex < 0 || currentIndex >= listKategori.size()) {
                        cancelEdit();
                        return;
                    }

                    Kategori kategori = listKategori.get(currentIndex);
                    
                    String trimmedNewValue = (newValue != null) ? newValue.trim() : "";

                    boolean isDuplicate = listKategori.stream()
                        .anyMatch(k -> k != kategori && k.getNamaKategori().equalsIgnoreCase(trimmedNewValue));

                    if (kategori.getIdKategori().equals("temp")) {
                        if (isDuplicate) {
                            Session.animasiPanePesan(true, "Nama kategori sudah digunakan");
                            listKategori.remove(kategori);
                            getTableView().refresh();
                            cancelEdit(); 
                            return;
                        } else if (trimmedNewValue.isEmpty()) {
                            Session.animasiPanePesan(true, "Nama kategori tidak boleh kosong");
                            listKategori.remove(kategori);
                            getTableView().refresh();
                            cancelEdit();
                            return;
                        }
                    } else {
                        if (isDuplicate) {
                            Session.animasiPanePesan(true, "Nama kategori sudah digunakan");
                            cancelEdit();
                            return;
                        }
                        if (trimmedNewValue.equals(kategori.getNamaKategori())) {
                            super.commitEdit(newValue); 
                            return; 
                        }
                        if (trimmedNewValue.isEmpty()) {
                            Session.animasiPanePesan(true, "Nama kategori tidak boleh kosong");
                            cancelEdit();
                            return;
                        }
                    }

                    super.commitEdit(newValue); 
                    kategori.setNamaKategori(trimmedNewValue); 

                    if (kategori.getIdKategori().equals("temp")) { // Persist new item
                        String idKategoriBaru = Session.membuatIdBaru("kategori", "id_kategori", "ktg", 2);
                        try {
                            String query = "INSERT INTO kategori (id_kategori, nama_kategori) VALUES (?,?)";
                            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);

                            statement.setString(1, idKategoriBaru);
                            statement.setString(2, trimmedNewValue);
                            statement.executeUpdate();

                            kategori.setIdKategori(idKategoriBaru);

                            setupActionButtons(kategori, idKategoriBaru);

                            Session.animasiPanePesan(false, "Kategori berhasil ditambahkan");
                            
                            statement.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Session.animasiPanePesan(true, "Gagal menambahkan kategori: " + e.getMessage());
                        }
                    } else { // Update existing item
                        try {
                            String query = "UPDATE kategori SET nama_kategori = ? WHERE id_kategori = ?";
                            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);

                            statement.setString(1, trimmedNewValue);
                            statement.setString(2, kategori.getIdKategori());
                            statement.executeUpdate();

                            Session.animasiPanePesan(false, "Kategori berhasil diperbarui");

                            statement.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Session.animasiPanePesan(true, "Gagal memperbarui kategori: " + e.getMessage());
                        }
                    }
                    getTableView().refresh(); 
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                     Kategori kategori = (Kategori) getTableRow().getItem(); // Get item safely
                     if (kategori != null && "temp".equals(kategori.getIdKategori())) {
                         getTableView().getItems().remove(kategori);
                         getTableView().refresh();
                     }
                     Session.setEnableButtons(btnTambahKategori); 
                }

                private void setupActionButtons(Kategori kategori, String kategoriId) {
                    TableView<Kategori> tableView = getTableView();
                    TableColumn<Kategori, String> namaKategoriColumn = findColumnByName("Nama Kategori"); // Example: Find column if needed

                    Button btnEdit = new Button();
                    btnEdit.setStyle(
                        "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-pref-width: 26px;" +
                        "-fx-pref-height: 26px;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0;"
                    );

                    Button btnHapus = new Button();
                    btnHapus.setStyle(
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

                    // Ensure icons are accessible
                    ImageView iconEdit = new ImageView(new Image(getClass().getResource("/assets/icons/edit16px.png").toExternalForm()));
                    iconEdit.setFitHeight(16);
                    iconEdit.setFitWidth(16);
                    btnEdit.setGraphic(iconEdit);

                    ImageView iconHapus = new ImageView(new Image(getClass().getResource("/assets/icons/delete16px.png").toExternalForm()));
                    iconHapus.setFitHeight(16);
                    iconHapus.setFitWidth(16);
                    btnHapus.setGraphic(iconHapus);

                    HBox aksiBox = new HBox(10, btnEdit, btnHapus);
                    aksiBox.setAlignment(Pos.CENTER);

                    // Edit button action
                    btnEdit.setOnAction(e -> {
                        int rowIndex = tableView.getItems().indexOf(kategori);
                        if (rowIndex != -1 && namaKategoriColumn != null) {
                            tableView.edit(rowIndex, namaKategoriColumn);
                            TablePosition pos = new TablePosition(tableView, rowIndex, namaKategoriColumn);
                            tableView.getFocusModel().focus(pos);
                            tableView.getSelectionModel().select(pos.getRow());
                        }
                    });

                    btnHapus.setOnAction(e -> {
                        try {
                            String query = "DELETE FROM kategori WHERE id_kategori=?";
                            PreparedStatement statementHapus = Koneksi.getCon().prepareStatement(query);

                            statementHapus.setString(1, kategoriId);
                            int affectedRows = statementHapus.executeUpdate();

                            if (affectedRows > 0) {
                                tableView.getItems().remove(kategori);
                                Session.animasiPanePesan(false, "Kategori Berhasil dihapus");
                            } else {
                                Session.animasiPanePesan(true, "Gagal menghapus kategori (tidak ditemukan).");
                            }
                            
                            statementHapus.close();
                        } catch (java.sql.SQLIntegrityConstraintViolationException constraintEx) {
                            Session.animasiPanePesan(true, "Kategori terkait suatu barang, tidak bisa dihapus.");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Session.animasiPanePesan(true, "Gagal menghapus kategori: " + ex.getMessage());
                        }
                    });

                    kategori.setAksi(aksiBox);
                }

                private TableColumn<Kategori, String> findColumnByName(String name) {
                     for (TableColumn<Kategori, ?> col : getTableView().getColumns()) {
                         if (col.getText().equals(name) && col instanceof TableColumn) {

                             try {
                                return (TableColumn<Kategori, String>) col;
                             } catch (ClassCastException e) { /* Handle appropriately */ }
                         }
                     }
                     return null; // Or throw an exception
                }
            };
        });
    }
    
    @FXML
    private void bukaKelolaKategori(){
        Session.setShowPane(paneKelolaKategori, paneGelap);
        Session.setMouseTransparentTrue(tabelBarang, btnTambahBarang, btnEditBarang, txtSearchBar, btnX, sortBy);
        
        try {
            String query = "SELECT id_kategori, nama_kategori FROM kategori";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            
            ResultSet result = statement.executeQuery();
            
            while(result.next()) {
                Button btnEdit = new Button();
                btnEdit.setStyle(
                    "-fx-background-color: #4CAF50;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 11px;" +
                    "-fx-pref-width: 26px;" +
                    "-fx-pref-height: 26px;" +
                    "-fx-background-radius: 5px;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 0;"
                );

                Button btnHapus = new Button();
                btnHapus.setStyle(
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

                ImageView iconEdit = new ImageView(new Image(getClass().getResource("/assets/icons/edit16px.png").toExternalForm()));
                iconEdit.setFitHeight(16);
                iconEdit.setFitWidth(16);
                btnEdit.setGraphic(iconEdit);

                ImageView iconHapus = new ImageView(new Image(getClass().getResource("/assets/icons/delete16px.png").toExternalForm()));
                iconHapus.setFitHeight(16);
                iconHapus.setFitWidth(16);
                btnHapus.setGraphic(iconHapus);

                HBox aksiBox = new HBox(10, btnEdit, btnHapus);
                aksiBox.setAlignment(Pos.CENTER);

                Kategori kategoriBaru = new Kategori(result.getString("id_kategori"), result.getString("nama_kategori"), aksiBox);
                
                btnEdit.setOnAction(e -> {
                    int rowIndex = tabelKategori.getItems().indexOf(kategoriBaru);
                    tabelKategori.edit(rowIndex, colNamaKategori);

                    TablePosition pos = new TablePosition(tabelKategori, rowIndex, colNamaKategori);
                    tabelKategori.getFocusModel().focus(pos);
                    tabelKategori.getSelectionModel().select(pos.getRow());
                });
                
                btnHapus.setOnAction(e -> {
                    try {
                        String queryHapus = "DELETE FROM kategori WHERE id_kategori=?";
                        PreparedStatement statementHapus = Koneksi.getCon().prepareStatement(queryHapus);
                        statementHapus.setString(1, kategoriBaru.getIdKategori());
                        statementHapus.executeUpdate();
                        
                        listKategori.remove(kategoriBaru);
                        Session.animasiPanePesan(false, "Kategori Berhasil dihapus");
                        
                        statementHapus.close();
                    } catch (Exception ex) {
                        Session.animasiPanePesan(true, "Kategori terkait suatu barang");
                    }
                });
                
                listKategori.add(kategoriBaru);
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        tabelKategori.setItems(listKategori);
    }
    
    @FXML
    private void tutupKelolaKategori(){
        Session.setHidePane(paneKelolaKategori, paneGelap);
        Session.setMouseTransparentFalse(tabelBarang, btnTambahBarang, btnEditBarang, txtSearchBar, btnX, sortBy);
        listKategori.clear();
        getDataTabelBarang();
    }
    
    @FXML
    private void tambahKategori() {
        Kategori kategoriBaru = new Kategori("temp", "", new HBox());
        listKategori.add(kategoriBaru);

        int rowIndex = listKategori.size() - 1;
        tabelKategori.scrollTo(kategoriBaru);
        tabelKategori.layout();

        tabelKategori.edit(rowIndex, colNamaKategori);

        TablePosition pos = new TablePosition(tabelKategori, rowIndex, colNamaKategori);
        tabelKategori.getFocusModel().focus(pos);
        tabelKategori.getSelectionModel().select(pos.getRow());
        
        Session.setDisableButtons(btnTambahKategori);
    }
}