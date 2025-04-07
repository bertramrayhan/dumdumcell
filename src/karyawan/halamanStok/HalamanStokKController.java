package karyawan.halamanStok;

import java.net.URL;
import java.util.ResourceBundle;
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

public class HalamanStokKController implements Initializable {
    @FXML TextField txtSearchBar;
    @FXML Button buttonX;
    @FXML ChoiceBox<String>SortBy;
    @FXML TableView TableStok;
    @FXML TableColumn<Stok, String>  colNamaBarang, colKategori, colHargaJual, colMerek, colExp, colBarcode;
    @FXML TableColumn<Stok, Integer> colStok;
    
    private final stokManager stokManager = new stokManager();
    
    @Override
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
        colMerek.setCellValueFactory(new PropertyValueFactory<>("merek"));
        colExp.setCellValueFactory(new PropertyValueFactory<>("exp"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
    }
     
    private void loadStok(String sortOption, String keyword) {
        ObservableList<Stok> listStok = stokManager.getAllStok(sortOption, keyword);
        TableStok.setItems(listStok);
    }
 }


 