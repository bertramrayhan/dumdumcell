package login;

import java.net.URL;
import java.sql.CallableStatement;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import main.DumdumKasir;
import main.Koneksi;
import main.Session;

public class halamanLoginController implements Initializable {
    private String RFIDId = "";
    private long lastTime = 0;
    private final long RFID_THRESHOLD = 100;
    
    @FXML private Pane paneGelap;
    @FXML private AnchorPane panePilihanHalaman;
    @FXML private TextField txtUsername, txtPasswordVisible;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin, btnKaryawan, btnPemilik;
    @FXML private ImageView btnShowPassword;
    @FXML private StackPane panePesan;
    @FXML private Label lblPesan;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupRFIDListener(txtUsername);
        setupRFIDListener(txtPassword);
        
        txtPasswordVisible.setManaged(false);
        btnShowPassword.setOnMouseClicked(event -> {
            Session.togglePassword(txtPassword, txtPasswordVisible, btnShowPassword, "assets/icons/eye36px.png", "assets/icons/eye-off36px.png");
        });
        Session.inisialisasiPesan(panePesan, lblPesan);
    }  
    
    @FXML
    private void btnLogin() {
        String username = txtUsername.getText().trim();
        
        if(txtPasswordVisible.isVisible() == true){
            txtPassword.setText(txtPasswordVisible.getText().trim());
        }
        
        String password = txtPassword.getText().trim();
        
        try {
            String query = "SELECT id_admin, role FROM admin WHERE username=? AND password=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                String role = result.getString("role");
                Session.setIdAdmin(result.getString("id_admin"));
                
                query = "{CALL generate_log_saldo_shift_berdasarkan_waktu()}";
                CallableStatement statementProcedure = Koneksi.getCon().prepareCall(query);
                statementProcedure.executeQuery();
                
                if(role.equals("pemilik")){
                    bukaPanePilihanHalaman();
                }else{
                    goToHalamanKaryawan();
                }
            }else{
                Session.animasiPanePesan(true, "Username atau Password Salah", btnLogin);
                txtUsername.requestFocus();
                txtUsername.positionCaret(txtUsername.getText().length());
                txtPassword.clear();
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            Session.animasiPanePesan(true, "Username atau Password Salah", btnLogin);
            txtUsername.requestFocus();
            txtUsername.positionCaret(txtUsername.getText().length());
            txtPassword.clear();
        }
    }
    
    private void loginDenganRFID(){
        try {
            String query = "SELECT id_admin, role FROM admin WHERE kode_kartu=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, RFIDId);
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                String role = result.getString("role");
                Session.setIdAdmin(result.getString("id_admin"));
                DumdumKasir.switchToBeranda(role);
                
                query = "{CALL generate_log_saldo_shift_berdasarkan_waktu()}";
                CallableStatement statementProcedure = Koneksi.getCon().prepareCall(query);
                statementProcedure.executeQuery();
                
                if(role.equals("pemilik")){
                    bukaPanePilihanHalaman();
                }else{
                    goToHalamanKaryawan();
                }
            }else{
                Session.animasiPanePesan(true, "Kode Kartu Salah", btnLogin);
                txtUsername.requestFocus();
                txtUsername.positionCaret(txtUsername.getText().trim().length());
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            Session.animasiPanePesan(true, "Kode Kartu Salah", btnLogin);
            txtUsername.requestFocus();
            txtUsername.positionCaret(txtUsername.getText().trim().length());
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

            if (c == '\n' || c == '\r') { 
                RFIDId = RFIDId.replace("\n", "");
                RFIDId = RFIDId.replace("\r", "");
                if (RFIDId.length() >= 9) {
                    System.out.println("Scan RFID Terdeteksi: " + RFIDId);
                    loginDenganRFID();
                } else {
                    System.out.println("RFID tidak valid, panjang kurang dari 9 karakter.");
                }
                // Kosongkan RFIDId setelah pemrosesan
                RFIDId = ""; 
                field.clear();
            }
        });

        Session.triggerOnEnter(this::btnLogin, field);
    }
    
    @FXML
    private void bukaPanePilihanHalaman(){
        Session.setShowPane(panePilihanHalaman, paneGelap);
    }
    
    @FXML
    private void goToHalamanKaryawan(){
        try {
            DumdumKasir.switchToBeranda("karyawan");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void goToHalamanPemilik(){
        try {
            DumdumKasir.switchToBeranda("pemilik");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}