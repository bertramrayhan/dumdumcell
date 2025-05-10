package pemilik.halamanTransaksiBeli;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class HalamanTransaksiBeliPController implements Initializable {

    @FXML
    private TextField txtSearchBar;
    @FXML
    private ImageView btnX;
    @FXML
    private AnchorPane PanelTambah;
    @FXML
    private TextField txtPotonganHarga;
    @FXML
    private Button btnBatal;
    @FXML
    private Button btnTambah;
    @FXML
    private ChoiceBox<?> cbxJenisDiskon1;
    @FXML
    private ChoiceBox<?> cbxJenisDiskon11;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
