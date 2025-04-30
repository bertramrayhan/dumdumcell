package login;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
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
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupRFIDListener(txtUsername);
        setupRFIDListener(txtPassword);
        
        txtPasswordVisible.setManaged(false);
        btnShowPassword.setOnMouseClicked(event -> {
            Session.togglePassword(txtPassword, txtPasswordVisible, btnShowPassword, "assets/icons/eye36px.png", "assets/icons/eye-off36px.png");
        });
    }  
    
    @FXML
    private void btnLogin() {
        String username = txtUsername.getText();
        
        if(txtPasswordVisible.isVisible() == true){
            txtPassword.setText(txtPasswordVisible.getText());
        }
        
        String password = txtPassword.getText();
        
        try {
            String query = "SELECT id_admin, role FROM admin WHERE username=? AND password=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                if(result.getString("role").equals("karyawan")){
                    absensi();
                }
                Session.setIdAdmin(result.getString("id_admin"));
                DumdumKasir.switchToBeranda(result.getString("role"));
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Session.animasiPanePesan(true, panePesan, lblPesan, "Username atau Password Salah", btnLogin);
        txtUsername.requestFocus();
        txtUsername.positionCaret(txtUsername.getText().length());
        txtPassword.clear();
    }
    
    private void loginDenganRFID(){
        try {
            String query = "SELECT id_admin, role FROM admin WHERE kode_kartu=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, RFIDId);
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                if(result.getString("role").equals("karyawan")){
                    absensi();
                }
                Session.setIdAdmin(result.getString("id_admin"));
                DumdumKasir.switchToBeranda(result.getString("role"));
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Session.animasiPanePesan(true, panePesan, lblPesan, "Username atau Password Salah", btnLogin);
        txtUsername.requestFocus();
        txtUsername.positionCaret(txtUsername.getText().length());
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
                if (lastTime != 0 && (currentTime - lastTime) > RFID_THRESHOLD) {
                    if (RFIDId.length() >= 9) {
                        System.out.println("Scan RFID Terdeteksi: " + RFIDId);
                        RFIDId = RFIDId.replace("\n", "").replace("\r", "");
                        loginDenganRFID();
                    }
                    RFIDId = ""; 
                    field.clear();
                }

            }
            
        });
        
        Session.triggerOnEnter(this::btnLogin, field);
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
}