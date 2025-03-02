package login;

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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.DumdumKasir;
import main.Koneksi;
import main.Session;

public class halamanLoginController implements Initializable {
    private String RFIDId = "";
    private long lastTime = 0;
    private final long RFID_THRESHOLD = 100;
    
    @FXML TextField txtUsername, txtPasswordVisible;
    @FXML PasswordField txtPassword;
    @FXML Button btnLogin;
    @FXML ImageView btnShowPassword;
    @FXML StackPane panePesan;
    @FXML Label lblPesan;
    
    boolean showPassword = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupRFIDListener(txtUsername);
        setupRFIDListener(txtPassword);
        
        txtPasswordVisible.setManaged(false);
    }  
    
    @FXML
    private void btnLogin(MouseEvent event) {
        String username = txtUsername.getText().trim();
        
        if(showPassword){
            txtPassword.setText(txtPasswordVisible.getText());
        }
        
        String password = txtPassword.getText().trim();
        
        try {
            String query = "SELECT id_admin, role FROM admin WHERE username=? AND password=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                Session.setIdAdmin(result.getString("id_admin"));
                absensi();
                DumdumKasir.switchToBeranda();
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        animasiPanePesan();
        txtUsername.requestFocus();
        txtUsername.positionCaret(txtUsername.getText().length());
    }
    
    private void loginDenganRFID(){
        try {
            String query = "SELECT id_admin, role FROM admin WHERE kode_kartu=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, RFIDId);
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                Session.setIdAdmin(result.getString("id_admin"));
                absensi();
                DumdumKasir.switchToBeranda();
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        animasiPanePesan();
        txtUsername.requestFocus();
        txtUsername.positionCaret(txtUsername.getText().length());
    }
    
    private void absensi(){
        try {
            String query = "select jam_masuk FROM shift_karyawan WHERE id_admin=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, Session.getIdAdmin());
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
             if(result.getString("jam_masuk") == null){
                 query = "UPDATE shift_karyawan SET jam_masuk=CURRENT_TIME";
                 statement = Koneksi.getCon().prepareStatement(query);
                 statement.executeUpdate();
             }   
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setupRFIDListener(TextField field) {
        field.setOnKeyTyped(event -> {
            long currentTime = System.currentTimeMillis();
            char c = event.getCharacter().charAt(0);

            // Reset RFIDId kalau inputnya lambat
            if (lastTime != 0 && (currentTime - lastTime) > RFID_THRESHOLD) {
                RFIDId = "";
            }

            RFIDId += c;
            lastTime = currentTime;
            
            if (c == '\n' || c == '\r') { // RFID biasanya diakhiri Enter
                if (RFIDId.length() >= 9) {
                    System.out.println("Scan RFID Terdeteksi: " + RFIDId);
                    RFIDId = RFIDId.replace("\n", "").replace("\r", "");
                    loginDenganRFID();
                }
                RFIDId = ""; 
                field.clear();
            }
            
        });
    }
    
    @FXML
    private void showOrHidePassword(MouseEvent evt){
        showPassword = !showPassword;

        if(showPassword){
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length()); // Set kursor di akhir
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            btnShowPassword.setImage(new Image(getClass().getResourceAsStream("/assets/icons/eye36px.png")));
        }else{
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length()); // Set kursor di akhir
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            btnShowPassword.setImage(new Image(getClass().getResourceAsStream("/assets/icons/eye-off36px.png")));
        }
    }
    
    private void animasiPanePesan() {
        btnLogin.setDisable(true);
        
        // Animasi fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), panePesan);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Animasi naik ke tengah
        TranslateTransition naik = new TranslateTransition(Duration.millis(500), panePesan);
        naik.setFromY(50);
        naik.setToY(-100);

        ParallelTransition naikSambilFade = new ParallelTransition(naik, fadeIn);
        
        // Pause biar pesan keliatan beberapa detik
        PauseTransition jeda = new PauseTransition(Duration.millis(1000));

        // Animasi turun kembali
        TranslateTransition turun = new TranslateTransition(Duration.millis(500), panePesan);
        turun.setFromY(-100);
        turun.setToY(50);

        // Animasi fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), panePesan);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        ParallelTransition turunSambilFade = new ParallelTransition(turun, fadeOut);
        
        // Gabung semua animasi dengan jeda tambahan
        SequentialTransition animasi = new SequentialTransition(naikSambilFade, jeda, turunSambilFade);
        animasi.setOnFinished(event -> {
        btnLogin.setDisable(false);
        });
        animasi.play();
    }
}