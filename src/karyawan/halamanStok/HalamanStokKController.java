/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package karyawan.halamanStok;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author indra
 */
public class HalamanStokKController implements Initializable {
    @FXML private TextField txtSearchBar;
    @FXML private Button buttonX;
    @FXML private ChoiceBox sortBy;
    @FXML private TableView tableStok;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
