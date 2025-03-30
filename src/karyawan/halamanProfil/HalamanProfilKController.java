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
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import karyawan.halamanProfil.gantiPassword.GantiPasswordController;
import karyawan.halamanUtama.HalamanUtamaKController;
import share.logout.LogoutController;
import main.DumdumKasir;
import main.Koneksi;
import main.Session;
import pemilik.halamanUtama.HalamanUtamaPController;

public class HalamanProfilKController implements Initializable {

    @FXML Button btnLogout, btnGantiPassword;
    @FXML Label lblNama, lblAlamat, lblNomorHP, lblUsername;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isiDataKaryawan();
    }    
    
    @FXML
    private void logout(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/share/logout/logout.fxml"));
            Parent root = loader.load();

            Stage logoutStage = new Stage();
            logoutStage.initModality(Modality.APPLICATION_MODAL);
            logoutStage.initStyle(StageStyle.UNDECORATED);
            logoutStage.centerOnScreen();
            logoutStage.setScene(new Scene(root));

            LogoutController controller = loader.getController();
            controller.setDialogStage(logoutStage);
            
            logoutStage.showAndWait(); // Tunggu user klik "Iya" atau "Tidak"

            if (controller.isLogoutConfirmed()) {
                switchToLogin(); // Logout ke halaman login
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void gantiPassword(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/karyawan/halamanProfil/gantiPassword/gantiPassword.fxml"));
            Parent root = loader.load();

            Stage gantiPasswordStage = new Stage();
            gantiPasswordStage.initModality(Modality.APPLICATION_MODAL);
            gantiPasswordStage.initStyle(StageStyle.UNDECORATED);
            gantiPasswordStage.centerOnScreen();
            gantiPasswordStage.setScene(new Scene(root));

            GantiPasswordController controller = loader.getController();
            controller.setDialogStage(gantiPasswordStage);
            
            gantiPasswordStage.showAndWait(); // Tunggu user klik "Iya" atau "Tidak"
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void switchToLogin() {
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
}
