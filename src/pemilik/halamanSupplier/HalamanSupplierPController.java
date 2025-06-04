package pemilik.halamanSupplier;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import main.Koneksi;
import main.Session;

public class HalamanSupplierPController implements Initializable {

    @FXML Pane paneGelap;
    @FXML private TextField txtSearchBar;
    @FXML private ImageView btnX;
    @FXML private TableView<Supplier> tabelSupplier;
    @FXML private TableColumn<Supplier, String> colNamaSupplier, colNamaToko, colKontak, colAlamat;
    private ObservableList<Supplier> listSupplier = FXCollections.observableArrayList();
    
    //TAMBAH SUPPLIER
    @FXML private TextField txtNamaSupplierTambah, txtNamaTokoTambah, txtKontakTambah, txtAlamatTambah;
    @FXML private Button btnBatalTambahSupplier, btnIyaTambahSupplier;
    
    //EDIT SUPPLIER
    @FXML private AnchorPane paneEditSupplier;
    @FXML private TextField txtNamaSupplierEdit, txtNamaTokoEdit, txtKontakEdit, txtAlamatEdit;
    @FXML private Button btnBatalEditSupplier, btnIyaEditSupplier, btnEditSupplier;
    
    //HAPUS SUPPLIER
    @FXML private AnchorPane paneHapusSupplier;
    @FXML private Button btnHapusSupplier, btnBatalHapusSupplier, btnIyaHapusSupplier;

    private Supplier supplierTerpilih;
    private String idSupplierTerpilih;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTabelSupplier();
        getDataTabelSupplier();
        
        //TAMBAH SUPPLIER
        setKomponenTambahSupplier();
        
        //EDIT SUPPLIER
        setKomponenEditSupplier();
    }    
    
    public class Supplier{
        String idSupplier, namaSupplier, namaToko, kontak, alamat;
        public Supplier(String idSupplier, String namaSupplier, String namaToko, String kontak, String alamat) {
            this.idSupplier = idSupplier;
            this.namaSupplier = namaSupplier;
            this.namaToko = namaToko;
            this.kontak = kontak;
            this.alamat = alamat;
        }
        public String getIdSupplier() {return idSupplier;}
        public String getNamaSupplier() {return namaSupplier;}
        public String getNamaToko() {return namaToko;}
        public String getKontak() {return kontak;}
        public String getAlamat() {return alamat;}
    }
    
    private void setTabelSupplier(){
        colNamaSupplier.setCellValueFactory(new PropertyValueFactory<>("namaSupplier"));
        colNamaToko.setCellValueFactory(new PropertyValueFactory<>("namaToko"));
        colKontak.setCellValueFactory(new PropertyValueFactory<>("kontak"));
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        
        tabelSupplier.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !paneEditSupplier.isVisible() && !paneHapusSupplier.isVisible()) {
                btnEditSupplier.setDisable(false);
                btnHapusSupplier.setDisable(false);
            } else {
                btnEditSupplier.setDisable(true);
                btnHapusSupplier.setDisable(true);
            }
        });
        
        tabelSupplier.setOnMouseClicked(event -> {
            Supplier supplier = tabelSupplier.getSelectionModel().getSelectedItem();
            if (supplier != null) {
                supplierTerpilih = supplier;
                idSupplierTerpilih = supplier.getIdSupplier();
            }
        });
    }
    
    @FXML
    private void getDataTabelSupplier() {
        listSupplier.clear();
        String keyword = txtSearchBar.getText().trim();
        
        String query = "SELECT * FROM supplier";
        
        boolean isSearch = keyword != null && !keyword.trim().isEmpty();

        if (isSearch) {
            query += " WHERE (nama_supplier LIKE ? OR nama_toko LIKE ? OR kontak LIKE ? OR alamat LIKE ?)";
        }

        try (PreparedStatement statement = Koneksi.getCon().prepareStatement(query)) {
            if (isSearch) {
                String searchKeyword = "%" + keyword + "%";
                statement.setString(1, searchKeyword);
                statement.setString(2, searchKeyword);
                statement.setString(3, searchKeyword);
                statement.setString(4, searchKeyword);
            }

             ResultSet result = statement.executeQuery();
             while (result.next()) {
                String idSupplier = result.getString("id_supplier");
                String namaSupplier = result.getString("nama_supplier");
                String namaToko = result.getString("nama_toko");
                String kontak = result.getString("kontak");
                String alamat = result.getString("alamat");

                listSupplier.add(new Supplier(idSupplier, namaSupplier, namaToko, kontak, alamat));
            }
            
             result.close();
             statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabelSupplier.setItems(listSupplier);
    }
    
    @FXML
    private void clearSearchBar(){
        txtSearchBar.setText("");
        getDataTabelSupplier();
    }
    
    //TAMBAH SUPPLIER
    private void setKomponenTambahSupplier(){
        Session.triggerOnEnter(this::tambahSupplier, txtNamaSupplierTambah, txtNamaTokoTambah, txtKontakTambah, txtAlamatTambah);
        Session.setTextFieldNumeric(13, txtKontakTambah);
    }
    
    @FXML
    private void clearTambahSupplier(){
        txtNamaSupplierTambah.setText("");
        txtNamaTokoTambah.setText("");
        txtKontakTambah.setText("");
        txtAlamatTambah.setText("");
    }
    
    @FXML
    private void tambahSupplier(){
        String namaSupplier = txtNamaSupplierTambah.getText().trim();
        String namaToko = txtNamaTokoTambah.getText().trim();
        String kontak = txtKontakTambah.getText().trim();
        String alamat = txtAlamatTambah.getText().trim();
        
        if(namaSupplier.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nama Supplier", btnIyaTambahSupplier);
            return;
        }else if(namaToko.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nama Toko", btnIyaTambahSupplier);
            return;
        }else if(kontak.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Kontak", btnIyaTambahSupplier);
            return;
        }else if(alamat.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Alamat", btnIyaTambahSupplier);
            return;
        }else if(kontak.length() < 12){
            Session.animasiPanePesan(true, "Panjang Kontak minimal 12 digit", btnIyaTambahSupplier);
            return;
        }else if(kontak.length() > 13){
            Session.animasiPanePesan(true, "Panjang Kontak maksimal 13 digit", btnIyaTambahSupplier);
            return;
        }else if(Session.cekDataSama("SELECT * FROM supplier WHERE nama_supplier=?", namaSupplier)){
            Session.animasiPanePesan(true, "Nama Supplier sudah ada", btnIyaTambahSupplier);
            return;
        }else if(Session.cekDataSama("SELECT * FROM supplier WHERE nama_toko=?", namaToko)){
            Session.animasiPanePesan(true, "Nama Toko sudah ada", btnIyaTambahSupplier);
            return;
        }else if(Session.cekDataSama("SELECT * FROM supplier WHERE kontak=?", namaToko)){
            Session.animasiPanePesan(true, "Kontak sudah ada", btnIyaTambahSupplier);
            return;
        }
        
        String idSupplierBaru = Session.membuatIdBaru("supplier", "id_supplier", "spl", 2);
        try {
            String query = "INSERT INTO supplier VALUES (?,?,?,?,?)";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idSupplierBaru);
            statement.setString(2, namaSupplier);
            statement.setString(3, namaToko);
            statement.setString(4, kontak);
            statement.setString(5, alamat);
            statement.executeUpdate();
            
            getDataTabelSupplier();
            Session.animasiPanePesan(false, "Supplier berhasil ditambahkan");
            clearTambahSupplier();
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
            
    //EDIT SUPPLIER
    private void setKomponenEditSupplier(){
        Session.triggerOnEnter(this::editSupplier, txtNamaSupplierEdit, txtNamaTokoEdit, txtKontakEdit, txtAlamatEdit);
        Session.setTextFieldNumeric(13, txtKontakEdit);
    }
    
    @FXML
    private void bukaEditSupplier(){
        Session.setShowPane(paneEditSupplier, paneGelap);
        Session.setMouseTransparentTrue(txtNamaSupplierTambah, txtAlamatTambah, btnIyaTambahSupplier, txtSearchBar, tabelSupplier);
        
        txtNamaSupplierEdit.setText(supplierTerpilih.getNamaSupplier());
        txtNamaTokoEdit.setText(supplierTerpilih.getNamaToko());
        txtKontakEdit.setText(supplierTerpilih.getKontak());
        txtAlamatEdit.setText(supplierTerpilih.getAlamat());
    }
    
    @FXML
    private void tutupEditSupplier(){
        Session.setHidePane(paneEditSupplier, paneGelap);
        Session.setMouseTransparentFalse(txtNamaSupplierTambah, txtAlamatTambah, btnIyaTambahSupplier, txtSearchBar, tabelSupplier);
    }
    
    @FXML
    private void editSupplier(){
        String namaSupplier = txtNamaSupplierEdit.getText().trim().trim();
        String namaToko = txtNamaTokoEdit.getText().trim().trim();
        String kontak = txtKontakEdit.getText().trim().trim();
        String alamat = txtAlamatEdit.getText().trim().trim();
        
        if(namaSupplier.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nama Supplier", btnIyaEditSupplier);
            return;
        }else if(namaToko.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nama Toko", btnIyaEditSupplier);
            return;
        }else if(kontak.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Kontak", btnIyaEditSupplier);
            return;
        }else if(alamat.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Alamat", btnIyaEditSupplier);
            return;
        }else if(kontak.length() < 12){
            Session.animasiPanePesan(true, "Panjang Kontak minimal 12 digit", btnIyaEditSupplier);
            return;
        }else if(kontak.length() > 13){
            Session.animasiPanePesan(true, "Panjang Kontak maksimal 13 digit", btnIyaEditSupplier);
            return;
        }else if(!supplierTerpilih.getNamaSupplier().toLowerCase().equals(namaSupplier.toLowerCase()) && Session.cekDataSama("SELECT * FROM supplier WHERE nama_supplier=?", namaSupplier)){
            Session.animasiPanePesan(true, "Nama Supplier sudah ada", btnIyaEditSupplier);
            return;
        }else if(!supplierTerpilih.getNamaToko().toLowerCase().equals(namaToko.toLowerCase()) && Session.cekDataSama("SELECT * FROM supplier WHERE nama_toko=?", namaToko)){
            Session.animasiPanePesan(true, "Nama Toko sudah ada", btnIyaEditSupplier);
            return;
        } 
        
        try {
            String query = "UPDATE `supplier` SET nama_supplier= ?, nama_toko = ?,\n" +
            "kontak = ?, alamat = ?\n" +
            "WHERE id_supplier = ?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, namaSupplier);
            statement.setString(2, namaToko);
            statement.setString(3, kontak);
            statement.setString(4, alamat);
            statement.setString(5, idSupplierTerpilih);
            statement.executeUpdate();
            
            getDataTabelSupplier();
            Session.animasiPanePesan(false, "Supplier berhasil diperbarui");
            tutupEditSupplier();
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //HAPUS SUPPLIER
    @FXML
    private void bukaHapusSupplier(){
        Session.setShowPane(paneHapusSupplier, paneGelap);
        Session.setMouseTransparentTrue(txtNamaSupplierTambah, txtAlamatTambah, txtNamaTokoTambah, txtKontakTambah, btnBatalTambahSupplier, btnIyaTambahSupplier, txtSearchBar, btnHapusSupplier, tabelSupplier);
    }
    
    @FXML
    private void tutupHapusSupplier(){
        Session.setHidePane(paneHapusSupplier, paneGelap);
        Session.setMouseTransparentFalse(txtNamaSupplierTambah, txtAlamatTambah, txtNamaTokoTambah, txtKontakTambah, btnBatalTambahSupplier, btnIyaTambahSupplier, txtSearchBar, btnHapusSupplier, tabelSupplier);
    }
    
    @FXML
    private void hapusSupplier(){
        try {
            String query = "DELETE FROM supplier WHERE id_supplier=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idSupplierTerpilih);
            statement.executeUpdate();
            
            getDataTabelSupplier();
            Session.animasiPanePesan(false, "Supplier berhasil dihapus");
            tutupHapusSupplier();
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
