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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import main.DumdumKasir;
import main.Koneksi;
import main.Session;

public class halamanLoginController implements Initializable {
    private StringBuilder currentInput = new StringBuilder();
    private long lastInputTime = 0;
    private boolean isPotentiallyRFID = false;
    private final long RFID_THRESHOLD = 80; // Threshold waktu (ms), mungkin perlu disesuaikan
    private final int MIN_RFID_LENGTH = 9; // Panjang minimal ID RFID

    // Variabel untuk menyimpan ID RFID yang terdeteksi
    private String RFIDId = "";
    
    @FXML private AnchorPane paneLogin;
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
        setupTextField(txtUsername);
        setupTextField(txtPassword);
        
        txtPasswordVisible.setManaged(false);
        btnShowPassword.setOnMouseClicked(event -> {
            Session.togglePassword(txtPassword, txtPasswordVisible, btnShowPassword, "assets/icons/eye36px.png", "assets/icons/eye-off36px.png");
        });
        Session.inisialisasiPesan(panePesan, lblPesan);
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
                txtUsername.positionCaret(txtUsername.getText().length());
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            Session.animasiPanePesan(true, "Kode Kartu Salah", btnLogin);
            txtUsername.requestFocus();
            txtUsername.positionCaret(txtUsername.getText().length());
        }
    }
    
    private void setupTextField(TextField field) {
        field.setOnKeyTyped(event -> {
            long currentTime = System.currentTimeMillis();
            char c = event.getCharacter().charAt(0);

            // Cek jika ada jeda terlalu lama, reset buffer dan status
            if (lastInputTime != 0 && (currentTime - lastInputTime) > RFID_THRESHOLD) {
                System.out.println("Jeda terdeteksi, reset buffer.");
                currentInput.setLength(0); // Reset buffer
                isPotentiallyRFID = false; // Anggap manual jika ada jeda
            } else {
                // Jika tidak ada jeda panjang, anggap bagian dari sekuens cepat (RFID)
                // Hanya set true jika buffer belum kosong (menghindari 1 char dianggap RFID)
                if (currentInput.length() > 0) {
                     isPotentiallyRFID = true;
                }
            }

            // Tambahkan karakter ke buffer (kecuali jika itu newline/CR)
            if (c != '\n' && c != '\r') {
                currentInput.append(c);
            }
            lastInputTime = currentTime;

            // Proses jika karakter adalah Enter
            if (c == '\n' || c == '\r') {
                String finalInput = currentInput.toString();
                System.out.println("Enter terdeteksi. Input: '" + finalInput + "', Potensi RFID: " + isPotentiallyRFID);

                // Cek apakah ini adalah RFID
                if (isPotentiallyRFID && finalInput.length() >= MIN_RFID_LENGTH) {
                    System.out.println("-> Dianggap sebagai RFID.");
                    RFIDId = finalInput; // Simpan ID RFID
                    loginDenganRFID();
                } else {
                    // Jika tidak dianggap RFID (terlalu pendek atau ada jeda sebelumnya)
                    // Anggap sebagai input manual yang diakhiri Enter
                    System.out.println("-> Dianggap sebagai Manual Enter.");
                    // Kita panggil btnLogin, tapi field mungkin sudah kosong
                    // btnLogin perlu mengambil nilai field saat itu
                    btnLogin();
                }

                // Reset state setelah Enter diproses
                currentInput.setLength(0);
                isPotentiallyRFID = false;
                field.clear(); // Kosongkan field setelah diproses
                event.consume(); // Hentikan event agar tidak diproses lebih lanjut
            }
        });
    }
    
    @FXML
    private void bukaPanePilihanHalaman(){
        Session.setShowPane(panePilihanHalaman, paneGelap);
        Session.setMouseTransparentTrue(paneLogin);
        paneGelap.requestFocus();
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