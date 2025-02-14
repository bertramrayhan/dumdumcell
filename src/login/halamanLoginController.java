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
import javafx.scene.input.MouseEvent;
import main.Koneksi;

public class halamanLoginController implements Initializable {
    
    @FXML TextField txtUsername;
    @FXML PasswordField txtPassword;
    
    @FXML Button btnLogin;
        
    @FXML
    private void handleButtonAction(MouseEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        
        try {
            String query = "select id_admin, role from admin where username=? and password=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                System.out.println(result.getString("id_admin"));
                System.out.println(result.getString("role"));
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
