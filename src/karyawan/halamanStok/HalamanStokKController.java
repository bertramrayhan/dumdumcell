/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package karyawan.halamanStok;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.Koneksi;
import main.Session;

/**
 * FXML Controller class
 *
 * @author indra
 */
public class HalamanStokKController implements Initializable {
    @FXML TextField txtSearchBar;
    @FXML Button buttonX;
    @FXML ChoiceBox<String>SortBy;
    @FXML TableView TableStok;
    @FXML TableColumn<Stok, String>  colNamaBarang, colKategori, colHargaJual, colTipe, colMerek, colExp;
    @FXML TableColumn<Stok, Integer> colStok;
    
    private final stokManager stokManager = new stokManager();
    
    public void initialize(URL url, ResourceBundle rb) {
        setTableStok();
        setSortBy();
        loadStok(null, null); // Default load tanpa sort & search

        SortBy.valueProperty().addListener((obs, oldVal, newVal) -> loadStok(newVal, txtSearchBar.getText()));
        txtSearchBar.textProperty().addListener((obs, oldVal, newVal) -> loadStok(SortBy.getValue(), newVal));
        
        buttonX.setOnAction((ActionEvent event) -> {
        txtSearchBar.clear(); // Hapus teks pencarian
    });
    }    
    
    
    private void setSortBy() {
    SortBy.getItems().addAll(
        "Sort by",
        "Nama Barang (A-Z)",
        "Nama Barang (Z-A)",
        "Barang Hampir Expired",
        "Kategori",
        "Stok Terbanyak",
        "Stok Terdikit"
    );
     SortBy.setValue("Sort by"); // Set tulisan awal
}
     private void setTableStok(){
        colNamaBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colHargaJual.setCellValueFactory(new PropertyValueFactory<>("hargaJual"));
        colTipe.setCellValueFactory(new PropertyValueFactory<>("tipe"));
        colMerek.setCellValueFactory(new PropertyValueFactory<>("merek"));
        colExp.setCellValueFactory(new PropertyValueFactory<>("exp"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
    }
    private void loadStok(String sortOption, String keyword) {
        ObservableList<Stok> listStok = stokManager.getAllStok(sortOption, keyword);
        TableStok.setItems(listStok);
    }
 }


 