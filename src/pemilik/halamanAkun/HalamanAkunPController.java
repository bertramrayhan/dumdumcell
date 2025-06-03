package pemilik.halamanAkun;

import java.io.InputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import main.Koneksi;
import main.Session;

public class HalamanAkunPController implements Initializable {

    @FXML private TextField txtSearchBar;
    @FXML private ImageView btnX;
    @FXML private Button btnEditAkun, btnHapusAkun;
    @FXML private Pane paneGelap;
    
    @FXML private TableColumn<Akun, String> colNama, colRole, colUsername, colKodeKartu, colNomorTelepon, colAlamat;
    @FXML private TableView<Akun> tabelAkun;
    private ObservableList<Akun> listAkun = FXCollections.observableArrayList();
    
    //TAMBAH AKUN
    @FXML private TextField txtNamaTambah, txtUsernameTambah, txtPasswordVisibleTambah, txtKodeKartuTambah, txtNomorTeleponTambah, txtAlamatTambah;
    @FXML private PasswordField txtPasswordTambah;
    @FXML private ChoiceBox<String> cbxRoleTambah;
    @FXML private Button btnBatalTambahAkun, btnIyaTambahAkun;
    @FXML private ImageView btnShowPasswordTambah;
    
    //EDIT AKUN
    @FXML private AnchorPane paneEditAkun;
    @FXML private TextField txtNamaEdit, txtUsernameEdit, txtPasswordVisibleEdit, txtKodeKartuEdit, txtNomorTeleponEdit, txtAlamatEdit;
    @FXML private PasswordField txtPasswordEdit;
    @FXML private ChoiceBox<String> cbxRoleEdit;
    @FXML private Button btnBatalEditAkun, btnIyaEditAkun;
    @FXML private ImageView btnShowPasswordEdit;
    
    //HAPUS AKUN
    @FXML private AnchorPane paneHapusAkun;
    @FXML private Button btnIyaHapusAkun, btnBatalHapusAkun;
    
    private Akun akunTerpilih;
    private String idAkunTerpilih;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTabelAkun();
        getDataTabelAkun();
        
        //TAMBAH AKUN
        setKomponenTambahAkun();
        
        //EDIT AKUN
        setKomponenEditAkun();
    }    
    
    public class Akun{
        private String idAkun, nama, role, username, kodeKartu, nomorTelepon, password, alamat;

        public Akun(String idAkun, String nama, String role, String username, String kodeKartu, String nomorTelepon, String password, String alamat) {
            this.idAkun = idAkun;this.nama = nama;this.role = role;this.username = username;this.kodeKartu = kodeKartu;this.nomorTelepon = nomorTelepon;this.password = password;this.alamat = alamat;
        }
        public String getIdAkun() {return idAkun;}
        public String getNama() {return nama;}
        public String getRole() {return role;}
        public String getUsername() {return username;}
        public String getKodeKartu() {return kodeKartu;}
        public String getNomorTelepon() {return nomorTelepon;}
        public String getPassword() {return password;}
        public String getAlamat() {return alamat;}
    }
    
    private void setTabelAkun(){
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colKodeKartu.setCellValueFactory(new PropertyValueFactory<>("kodeKartu"));
        colNomorTelepon.setCellValueFactory(new PropertyValueFactory<>("nomorTelepon"));
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        
        tabelAkun.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null  && !paneHapusAkun.isVisible()) { //&& !paneEditAkun.isVisible()
                btnEditAkun.setDisable(false);
                btnHapusAkun.setDisable(false);
            } else {
                btnEditAkun.setDisable(true);
                btnHapusAkun.setDisable(true);
            }
        });
        
        tabelAkun.setOnMouseClicked(event -> {
            Akun supplier = tabelAkun.getSelectionModel().getSelectedItem();
            if (supplier != null) {
                akunTerpilih = supplier;
                idAkunTerpilih = supplier.getIdAkun();
            }
        });
    }
    
    @FXML
    private void getDataTabelAkun(){
        listAkun.clear();
        String keyword = txtSearchBar.getText().trim();
        
        String query = "SELECT * FROM admin WHERE is_deleted=FALSE";
        
        boolean isSearch = keyword != null && !keyword.trim().isEmpty();

        if (isSearch) {
            query += " AND (nama LIKE ? OR nomor_telepon LIKE ? OR alamat LIKE ? OR role LIKE ? OR username LIKE ? OR kode_kartu LIKE ?)";
        }

        try (PreparedStatement statement = Koneksi.getCon().prepareStatement(query)) {
            if (isSearch) {
                String searchKeyword = "%" + keyword + "%";
                statement.setString(1, searchKeyword);
                statement.setString(2, searchKeyword);
                statement.setString(3, searchKeyword);
                statement.setString(4, searchKeyword);
                statement.setString(5, searchKeyword);
                statement.setString(6, searchKeyword);
            }

             ResultSet result = statement.executeQuery();
             while (result.next()) {
                String idAkun = result.getString("id_admin");
                String nama = result.getString("nama");
                String role = result.getString("role");
                role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
                String username = result.getString("username");
                String kodeKartu = result.getString("kode_kartu");
                String nomorTelepon = result.getString("nomor_telepon");
                String alamat = result.getString("alamat");
                String password = result.getString("password");

                listAkun.add(new Akun(idAkun, nama, role, username, kodeKartu, nomorTelepon, password, alamat));
            }
            
             result.close();
             statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabelAkun.setItems(listAkun);
    }
    
    @FXML
    private void clearSearchBar(){
        txtSearchBar.setText("");
        getDataTabelAkun();
    }
    
    //TAMBAH AKUN
    private void setKomponenTambahAkun(){
        cbxRoleTambah.getItems().addAll("Karyawan", "Pemilik");
        cbxRoleTambah.setValue("Karyawan");
        
        Session.setTextFieldNumeric(txtKodeKartuTambah);
        Session.setTextFieldNumeric(13, txtNomorTeleponTambah);
        
        txtPasswordVisibleTambah.setManaged(false);
        btnShowPasswordTambah.setOnMouseClicked(event -> {
            Session.togglePassword(txtPasswordTambah, txtPasswordVisibleTambah, btnShowPasswordTambah, "assets/icons/eye36px.png", "assets/icons/eye-off36px.png");
        });
        
        Session.triggerOnEnter(this::tambahAkun, txtNamaTambah, txtUsernameTambah, txtPasswordTambah, txtPasswordVisibleTambah, txtKodeKartuTambah, txtNomorTeleponTambah);
    }
    
    @FXML
    private void clearTambahAkun(){
        txtNamaTambah.clear();
        txtUsernameTambah.clear();
        txtPasswordTambah.clear();
        txtPasswordVisibleTambah.clear();
        txtKodeKartuTambah.clear();
        txtNomorTeleponTambah.clear();
        txtAlamatTambah.clear();
    }
    
    @FXML
    private void tambahAkun(){
        String nama = txtNamaTambah.getText().trim();
        String role = cbxRoleTambah.getValue().toLowerCase();
        String username = txtUsernameTambah.getText().trim();
        if(txtPasswordVisibleTambah.isVisible() == true){
            txtPasswordTambah.setText(txtPasswordVisibleTambah.getText());
        }
        String password = txtPasswordTambah.getText().trim();
        String kodeKartu = txtKodeKartuTambah.getText().trim();
        String nomorTelepon = txtNomorTeleponTambah.getText().trim();
        String alamat = txtAlamatTambah.getText().trim();
        
        if(nama.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nama", btnIyaTambahAkun);
            return;
        }else if(username.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Username", btnIyaTambahAkun);
            return;
        }else if(password.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Password", btnIyaTambahAkun);
            return;
        }else if(kodeKartu.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Kode Kartu", btnIyaTambahAkun);
            return;
        }else if(nomorTelepon.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nomor Telepon", btnIyaTambahAkun);
            return;
        }else if(alamat.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Alamat", btnIyaTambahAkun);
            return;
        }else if(nomorTelepon.length() < 12){
            Session.animasiPanePesan(true, "Panjang Kontak minimal 12 digit", btnIyaTambahAkun);
            return;
        }else if(nomorTelepon.length() > 13){
            Session.animasiPanePesan(true, "Panjang Kontak maksimal 13 digit", btnIyaTambahAkun);
            return;
        }else if(kodeKartu.length() < 9){
            Session.animasiPanePesan(true, "Kode Kartu minimal 9 digit", btnIyaTambahAkun);
            return;
        }else if(Session.cekDataSama("SELECT * FROM admin WHERE nama=? AND is_deleted=FALSE", nama)){
            Session.animasiPanePesan(true, "Nama sudah ada", btnIyaTambahAkun);
            return;
        }else if(Session.cekDataSama("SELECT * FROM admin WHERE username=? AND is_deleted=FALSE", username)){
            Session.animasiPanePesan(true, "Username sudah ada", btnIyaTambahAkun);
            return;
        }else if(Session.cekDataSama("SELECT * FROM admin WHERE kode_kartu=? AND is_deleted=FALSE", kodeKartu)){
            Session.animasiPanePesan(true, "Kode Kartu sudah ada", btnIyaTambahAkun);
            return;
        }else if(Session.cekDataSama("SELECT * FROM admin WHERE nomor_telepon=? AND is_deleted=FALSE", nomorTelepon)){
            Session.animasiPanePesan(true, "Nomor Telepon sudah ada", btnIyaTambahAkun);
            return;
        }
        
        boolean adaBarangSamaDeleted = Session.cekDataSama("SELECT * FROM admin WHERE is_deleted=TRUE AND nama=?", nama);
        
        String idAdminBaru = Session.membuatIdBaru("admin", "id_admin", "adm", 2);
        try {
            String query;
            PreparedStatement statement = null;
            if(adaBarangSamaDeleted){
                query = "UPDATE admin SET nomor_telepon=?, alamat=?, role=?,\n" +
                "username=?, admin.password=?, kode_kartu=?, is_deleted=FALSE\n" +
                "WHERE nama=? AND is_deleted=TRUE";
                statement = Koneksi.getCon().prepareStatement(query);
                statement.setString(1, nomorTelepon);
                statement.setString(2, alamat);
                statement.setString(3, role);
                statement.setString(4, username);
                statement.setString(5, password);
                statement.setString(6, kodeKartu);
                statement.setString(7, nama);
            }else{
                query = "INSERT INTO admin VALUES (?,?,?,?,?,?,?,?,FALSE)";
                statement = Koneksi.getCon().prepareStatement(query);
                statement.setString(1, idAdminBaru);
                statement.setString(2, nama);
                statement.setString(3, nomorTelepon);
                statement.setString(4, alamat);
                statement.setString(5, role);
                statement.setString(6, username);
                statement.setString(7, password);
                statement.setString(8, kodeKartu);
            }
            statement.executeUpdate();
            
            getDataTabelAkun();
            Session.animasiPanePesan(false, "Akun berhasil ditambahkan");
            clearTambahAkun();
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //EDIT AKUN
    private void setKomponenEditAkun(){
        cbxRoleEdit.getItems().addAll("Karyawan", "Pemilik");
        
        Session.setTextFieldNumeric(txtKodeKartuEdit);
        Session.setTextFieldNumeric(13, txtNomorTeleponEdit);
        
        txtPasswordVisibleEdit.setManaged(false);
        btnShowPasswordEdit.setOnMouseClicked(event -> {
            Session.togglePassword(txtPasswordEdit, txtPasswordVisibleEdit, btnShowPasswordEdit, "assets/icons/eye36px.png", "assets/icons/eye-off36px.png");
        });
        
        Session.triggerOnEnter(this::editAkun, txtNamaEdit, txtUsernameEdit, txtPasswordEdit, txtPasswordVisibleEdit, txtKodeKartuEdit, txtNomorTeleponEdit);
    }
    
    @FXML
    private void bukaEditAkun(){
        Session.setShowPane(paneEditAkun, paneGelap);
        Session.setMouseTransparentTrue(txtNamaTambah, cbxRoleTambah, txtKodeKartuTambah, txtNomorTeleponTambah, tabelAkun);
        
        txtNamaEdit.setText(akunTerpilih.getNama());
        cbxRoleEdit.setValue(akunTerpilih.getRole());
        txtUsernameEdit.setText(akunTerpilih.getUsername());
        txtPasswordEdit.setText(akunTerpilih.getPassword());
        txtKodeKartuEdit.setText(akunTerpilih.getKodeKartu());
        txtNomorTeleponEdit.setText(akunTerpilih.getNomorTelepon());
        txtAlamatEdit.setText(akunTerpilih.getAlamat());
    }
    
    @FXML
    private void tutupEditAkun(){
        Session.setHidePane(paneEditAkun, paneGelap);
        Session.setMouseTransparentFalse(txtNamaTambah, cbxRoleTambah, txtKodeKartuTambah, txtNomorTeleponTambah, tabelAkun);
        
        txtPasswordEdit.setVisible(true);
        txtPasswordEdit.setManaged(true);

        txtPasswordVisibleEdit.setVisible(false);
        txtPasswordVisibleEdit.setManaged(false);
        
        InputStream iconStream = ClassLoader.getSystemResourceAsStream("assets/icons/eye-off36px.png");
        Image icon = new Image(iconStream);
        btnShowPasswordEdit.setImage(icon);
    }
    
    @FXML
    private void editAkun(){
        String role = cbxRoleEdit.getValue().toLowerCase();
        String username = txtUsernameEdit.getText().trim();
        if(txtPasswordVisibleEdit.isVisible() == true){
            txtPasswordEdit.setText(txtPasswordVisibleEdit.getText());
        }
        String password = txtPasswordEdit.getText().trim();
        String kodeKartu = txtKodeKartuEdit.getText().trim();
        String nomorTelepon = txtNomorTeleponEdit.getText().trim();
        String alamat = txtAlamatEdit.getText().trim();
        
        if(username.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Username", btnIyaEditAkun);
            return;
        }else if(password.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Password", btnIyaEditAkun);
            return;
        }else if(kodeKartu.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Kode Kartu", btnIyaEditAkun);
            return;
        }else if(nomorTelepon.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nomor Telepon", btnIyaEditAkun);
            return;
        }else if(alamat.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Alamat", btnIyaEditAkun);
            return;
        }else if(nomorTelepon.length() < 12){
            Session.animasiPanePesan(true, "Panjang Kontak minimal 12 digit", btnIyaEditAkun);
            return;
        }else if(nomorTelepon.length() > 13){
            Session.animasiPanePesan(true, "Panjang Kontak maksimal 13 digit", btnIyaEditAkun);
            return;
        }else if(kodeKartu.length() < 9){
            Session.animasiPanePesan(true, "Kode Kartu minimal 9 digit", btnIyaEditAkun);
            return;
        }else if(kodeKartu.length() > 12){
            Session.animasiPanePesan(true, "Kode Kartu maksimal 12 digit", btnIyaEditAkun);
            return;
        }else if(!akunTerpilih.getUsername().toLowerCase().equals(username.toLowerCase()) && Session.cekDataSama("SELECT * FROM admin WHERE username=? AND is_deleted=FALSE", username)){
            Session.animasiPanePesan(true, "Username sudah ada", btnIyaEditAkun);
            return;
        }else if(!akunTerpilih.getKodeKartu().toLowerCase().equals(kodeKartu.toLowerCase()) && Session.cekDataSama("SELECT * FROM admin WHERE kode_kartu=? AND is_deleted=FALSE", kodeKartu)){
            Session.animasiPanePesan(true, "Kode Kartu sudah ada", btnIyaEditAkun);
            return;
        }else if(!akunTerpilih.getNomorTelepon().toLowerCase().equals(nomorTelepon.toLowerCase()) && Session.cekDataSama("SELECT * FROM admin WHERE nomor_telepon=? AND is_deleted=FALSE", nomorTelepon)){
            Session.animasiPanePesan(true, "Nomor Telepon sudah ada", btnIyaEditAkun);
            return;
        }
                
        try {
            String query = "UPDATE admin SET nomor_telepon=?, alamat=?, role=?,\n" +
                "username=?, admin.password=?, kode_kartu=?\n" +
                "WHERE id_admin=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, nomorTelepon);
            statement.setString(2, alamat);
            statement.setString(3, role);
            statement.setString(4, username);
            statement.setString(5, password);
            statement.setString(6, kodeKartu);
            statement.setString(7, idAkunTerpilih);
            statement.executeUpdate();
            
            getDataTabelAkun();
            Session.animasiPanePesan(false, "Akun berhasil diperbarui");
            tutupEditAkun();
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //HAPUS AKUN
    @FXML
    private void bukaHapusAkun(){
        Session.setShowPane(paneHapusAkun, paneGelap);
        Session.setMouseTransparentTrue(txtNamaTambah, cbxRoleTambah, txtUsernameTambah, txtPasswordTambah, txtPasswordVisibleTambah, txtKodeKartuTambah, txtNomorTeleponTambah, txtAlamatTambah, btnBatalTambahAkun, btnIyaTambahAkun, btnHapusAkun, txtSearchBar, tabelAkun);
    }
    
    @FXML
    private void tutupHapusAkun(){
        Session.setHidePane(paneHapusAkun, paneGelap);
        Session.setMouseTransparentFalse(txtNamaTambah, cbxRoleTambah, txtUsernameTambah, txtPasswordTambah, txtPasswordVisibleTambah, txtKodeKartuTambah, txtNomorTeleponTambah, txtAlamatTambah, btnBatalTambahAkun, btnIyaTambahAkun, btnHapusAkun, txtSearchBar, tabelAkun);
    }
    
    @FXML
    private void hapusAkun(){
        try {
            String query = "UPDATE admin SET is_deleted = TRUE WHERE id_admin=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idAkunTerpilih);
            statement.executeUpdate();
            
            getDataTabelAkun();
            Session.animasiPanePesan(false, "Akun berhasil dihapus");
            tutupHapusAkun();
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}