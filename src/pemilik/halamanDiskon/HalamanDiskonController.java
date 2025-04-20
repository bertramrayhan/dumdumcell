/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package pemilik.halamanDiskon;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author indra
 */
public class HalamanDiskonController implements Initializable {

// TableView
@FXML private TableView<?> TableDiskon;

// TextField
@FXML private TextField txtSearchBar,TambahNamaDiskon,TambahPotonganHarga,TambahStatus,EditNamaDiskon,EditPotonganHArga,EditStatus;

// Button
@FXML private Button buttonX,btnEditDIskon,btnTambahDiskon,btnHapusDiskon,TambahBtnBatal,TambahBtnSimpan,EditBtnBatal,EditBtnSimpan,btnIyaHapusBarang,btnTidakHapusBarang;

// AnchorPane
@FXML private AnchorPane PanelTambah,PanelEdit,paneHapusDiskon;

// ChoiceBox
@FXML private ChoiceBox<?> TambahJenisDiskon,EditJenisDIskon;

// DatePicker
@FXML private DatePicker TambahTglMulai,TambahTglAkhir,EditTglMulai,EditTglAkhir;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void bukaEditDIskon(MouseEvent event) {
    }

    @FXML
    private void bukaHapusBarang(MouseEvent event) {
    }

    @FXML
    private void bukaTambahDiskon(MouseEvent event) {
    }

    @FXML
    private void hapusBarang(MouseEvent event) {
    }

    @FXML
    private void tutupHapusBarang(MouseEvent event) {
    }
    
}
