package login;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    
    boolean showPassword = false;
    
    @FXML
    private void btnLogin(MouseEvent event) {
        String username = txtUsername.getText().trim();
        
        if(showPassword){
            txtPassword.setText(txtPasswordVisible.getText());
        }
        
        String password = txtPassword.getText().trim();
        
        try {
            String query = "select id_admin, role from admin where username=? and password=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                Session.setIdAdmin(result.getString("id_admin"));
                DumdumKasir.switchToBeranda();
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        txtUsername.requestFocus();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupRFIDListener(txtUsername);
        setupRFIDListener(txtPassword);
        
        txtPasswordVisible.setManaged(false);
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
    
    private void loginDenganRFID(){
        try {
            String query = "select id_admin, role from admin where kode_kartu=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, RFIDId);
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                Session.setIdAdmin(result.getString("id_admin"));
                DumdumKasir.switchToBeranda();
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            btnShowPassword.setImage(new Image(getClass().getResourceAsStream("/assets/icons/eye-off36px.png")));
        }else{
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length()); // Set kursor di akhir
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            btnShowPassword.setImage(new Image(getClass().getResourceAsStream("/assets/icons/eye36px.png")));
        }
    }
}