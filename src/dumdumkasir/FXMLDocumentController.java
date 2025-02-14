package dumdumkasir;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class FXMLDocumentController implements Initializable {
    
    @FXML TextField txtUsername;
    
    @FXML Button btnLogin;
        
    @FXML
    private void handleButtonAction(MouseEvent event) {
        System.out.println(txtUsername.getText());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
