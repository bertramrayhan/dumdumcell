package karyawan.halamanProfil;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import karyawan.halamanUtama.HalamanUtamaKController;
import main.DumdumKasir;
import main.Koneksi;
import main.Session;
import pemilik.halamanUtama.HalamanUtamaPController;

public class HalamanProfilKController implements Initializable {

    @FXML private Button btnLogout, btnGantiPassword;
    @FXML private Label lblNama, lblAlamat, lblNomorHP, lblUsername;
    @FXML private Pane paneGelap;
    
    //GANTI PASSWORD
    @FXML private AnchorPane paneGantiPassword;
    @FXML private Button btnKonfirmasi, btnBatal;
    @FXML private PasswordField txtPasswordLama, txtPasswordBaru, txtKonfirmasiPassword;
    @FXML private TextField txtPasswordLamaVisible, txtPasswordBaruVisible, txtKonfirmasiPasswordVisible;
    @FXML private CheckBox checkBoxTampilkanPassword;
    
    //LOGOUT
    @FXML private Button btnIya, btnTidak;
    @FXML private AnchorPane paneLogout;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isiDataKaryawan();
        Session.triggerOnEnter(this::gantiPassword, txtPasswordLama, txtPasswordBaru, txtKonfirmasiPassword);
    }    
    
    @FXML
    private void logout() {
        Session.setIdAdmin("");
        HalamanUtamaKController.penyimpananPaneKaryawan.clear();
        HalamanUtamaPController.penyimpananPanePemilik.clear();
        try {
            Stage loginStage = new Stage();
            Parent root = FXMLLoader.load(DumdumKasir.class.getResource(Session.getPathHalamanLogin()));

            Scene scene = new Scene(root);
            loginStage.setScene(scene);
            loginStage.setResizable(false);
            loginStage.centerOnScreen();
            loginStage.show();

            DumdumKasir.halamanUtama.close(); // Tutup halaman utama
            DumdumKasir.halamanUtama = loginStage; // Simpan halaman login sebagai utama

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void isiDataKaryawan(){
        try {
            String query = "SELECT nama, alamat, nomor_telepon, username FROM admin WHERE id_admin=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, Session.getIdAdmin());
            
            ResultSet result = statement.executeQuery();
            System.out.println(Session.getIdAdmin());
            if(result.next()){
                lblNama.setText(result.getString("nama"));
                lblAlamat.setText(result.getString("alamat"));
                lblNomorHP.setText(result.getString("nomor_telepon"));
                lblUsername.setText(result.getString("username"));
            }else{
                System.out.println("tidak ketemu");
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //GANTI PASSWORD
    @FXML
    private void bukaGantiPassword(){
        Session.setShowPane(paneGantiPassword, paneGelap);
    }
    
    @FXML
    private void tutupGantiPassword(){
        Session.setHidePane(paneGantiPassword, paneGelap);
        txtPasswordLama.clear();
        txtPasswordBaru.clear();
        txtKonfirmasiPassword.clear();
        txtPasswordLamaVisible.clear();
        txtPasswordBaruVisible.clear();
        txtKonfirmasiPasswordVisible.clear();
        checkBoxTampilkanPassword.setSelected(false);
        
        txtPasswordLama.setVisible(true);
        txtPasswordLama.setManaged(true);
        txtPasswordBaru.setVisible(true);
        txtPasswordBaru.setManaged(true);
        txtKonfirmasiPassword.setVisible(true);
        txtKonfirmasiPassword.setManaged(true);
        
        txtPasswordLamaVisible.setVisible(false);
        txtPasswordLamaVisible.setManaged(false);
        txtPasswordBaruVisible.setVisible(false);
        txtPasswordBaruVisible.setManaged(false);
        txtKonfirmasiPasswordVisible.setVisible(false);
        txtKonfirmasiPasswordVisible.setManaged(false);
    }
    
    @FXML
    private void tampilkanPassword(){
        Session.togglePassword(txtPasswordLama, txtPasswordLamaVisible);
        Session.togglePassword(txtPasswordBaru, txtPasswordBaruVisible);
        Session.togglePassword(txtKonfirmasiPassword, txtKonfirmasiPasswordVisible);
    }
    
    @FXML
    private void gantiPassword(){
        if(txtPasswordLamaVisible.isVisible() == true){
            txtPasswordLama.setText(txtPasswordLamaVisible.getText());
            txtPasswordBaru.setText(txtPasswordBaruVisible.getText());
            txtKonfirmasiPassword.setText(txtKonfirmasiPasswordVisible.getText());
        }
        
        String passwordLama = txtPasswordLama.getText();
        String passwordBaru = txtPasswordBaru.getText();
        String konfirmasiPassword = txtKonfirmasiPassword.getText();
        
        if(passwordLama.equals("") || passwordBaru.equals("") || konfirmasiPassword.equals("")){
            Session.animasiPanePesan(true, "Harap isi semua kolom", btnKonfirmasi);
            return;
        }else if(!passwordBaru.equals(konfirmasiPassword)){
            Session.animasiPanePesan(true, "Password baru dan konfirmasi password tidak cocok", btnKonfirmasi);
            return;
        }
        
        try {
            String query = "SELECT password FROM admin WHERE id_admin=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, Session.getIdAdmin());
            
            ResultSet result = statement.executeQuery();
            if(result.next()){
                if(passwordLama.equals(result.getString("password"))){
                    query = "UPDATE admin SET password=? WHERE id_admin=?";
                    statement = Koneksi.getCon().prepareStatement(query);
                    statement.setString(1, passwordBaru);
                    statement.setString(2, Session.getIdAdmin());
                    
                    statement.executeUpdate();

                    Session.animasiPanePesan(false, "Password berhasil diperbarui");
                    tutupGantiPassword();
                }else{
                    Session.animasiPanePesan(true, "Password Lama yang Anda Masukkan Salah", btnKonfirmasi);
                }
            }
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //LOGOUT
    @FXML
    private void bukaLogout(){
        Session.setShowPane(paneLogout, paneGelap);
    }
    
    @FXML
    private void tutupLogout(){
        Session.setHidePane(paneLogout, paneGelap);
    }
}
