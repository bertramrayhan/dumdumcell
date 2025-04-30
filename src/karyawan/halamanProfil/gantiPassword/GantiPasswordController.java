package karyawan.halamanProfil.gantiPassword;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Koneksi;
import main.Session;

public class GantiPasswordController implements Initializable {

    @FXML Button btnKonfirmasi, btnBatal;
    @FXML TextField txtPasswordLama, txtPasswordBaru, txtKonfirmasiPassword;
    @FXML StackPane pesanPane;
    @FXML Label lblPesan;
    private Stage dialogStage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Session.triggerOnEnter(this::gantiPassword, txtPasswordLama, txtPasswordBaru, txtKonfirmasiPassword);
    }    
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    @FXML
    private void gantiPassword(){
        String passwordLama = txtPasswordLama.getText();
        String passwordBaru = txtPasswordBaru.getText();
        String konfirmasiPassword = txtKonfirmasiPassword.getText();
        
        if(passwordLama.equals("") || passwordBaru.equals("") || konfirmasiPassword.equals("")){
            animasiPesanPane("Harap isi semua kolom");
            return;
        }else if(!passwordBaru.equals(konfirmasiPassword)){
            animasiPesanPane("Password baru dan konfirmasi password tidak cocok");
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
                    
                    pesanPane.setStyle("-fx-background-color: #388E3C; -fx-border-color: #1B5E20; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10;");
                    lblPesan.setStyle("-fx-text-fill: #E8F5E9;");
                    animasiPesanPane(() -> dialogStage.close(), "Password berhasil diperbarui");
                }else{
                    animasiPesanPane("Password lama yang Anda masukkan salah");
                }
            }
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void animasiPesanPane(String pesan){
        animasiPesanPane(null, pesan);
    }
    
    private void animasiPesanPane(Runnable onFinish, String pesan) {
        btnKonfirmasi.setDisable(true);
        pesanPane.requestFocus();
        lblPesan.setText(pesan);
        
        // Animasi fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), pesanPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Animasi naik ke tengah
        TranslateTransition naik = new TranslateTransition(Duration.millis(500), pesanPane);
        naik.setFromY(50);
        naik.setToY(-100);

        ParallelTransition naikSambilFade = new ParallelTransition(naik, fadeIn);
        
        // Pause biar pesan keliatan beberapa detik
        PauseTransition jeda = new PauseTransition(Duration.millis(1000));

        // Animasi turun kembali
        TranslateTransition turun = new TranslateTransition(Duration.millis(500), pesanPane);
        turun.setFromY(-100);
        turun.setToY(50);

        // Animasi fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), pesanPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        ParallelTransition turunSambilFade = new ParallelTransition(turun, fadeOut);
        
        // Gabung semua animasi dengan jeda tambahan
        SequentialTransition animasi = new SequentialTransition(naikSambilFade, jeda, turunSambilFade);
        animasi.setOnFinished(event -> {
        btnKonfirmasi.setDisable(false);
        if (onFinish != null) onFinish.run(); // Jalankan callback jika ada
        });
        animasi.play();
    }
    
    @FXML
    private void batalGantiPassword(){
        dialogStage.close();
    }
}
