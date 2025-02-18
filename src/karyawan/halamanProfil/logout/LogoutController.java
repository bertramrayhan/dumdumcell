package karyawan.halamanProfil.logout;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LogoutController implements Initializable {

    @FXML
    private Button btnIya, btnTidak;

    private Stage dialogStage;
    private boolean logoutConfirmed = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bisa dipake kalau perlu inisialisasi tambahan
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isLogoutConfirmed() {
        return logoutConfirmed;
    }

    @FXML
    private void switchToLogin() {
        logoutConfirmed = true;
        dialogStage.close(); // Tutup dialog logout setelah pilih "Iya"
    }

    @FXML
    private void logoutCancel() {
        dialogStage.close(); // Tutup dialog kalau user pilih "Tidak"
    }
}
