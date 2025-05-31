package karyawan.halamanStok;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.Pelengkap;

public class HalamanStokKController implements Initializable, Pelengkap {
    @FXML TextField txtSearchBar;
    @FXML Button buttonX;
    @FXML ChoiceBox<String>SortBy;
    @FXML TableView TableStok;
    @FXML TableColumn<Stok, String>  colIdBarang, colNamaBarang, colKategori, colHargaJual, colMerek, colExp, colBarcode;
    @FXML TableColumn<Stok, Integer> colStok;
    @FXML TableRow<Stok> colTerakhir;
    
    private final stokManager stokManager = new stokManager();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTableStok();
        setSortBy();
        loadStok(null, null); // Default load tanpa sort & search

        SortBy.valueProperty().addListener((obs, oldVal, newVal) -> loadStok(newVal, txtSearchBar.getText().trim()));
        txtSearchBar.textProperty().addListener((obs, oldVal, newVal) -> loadStok(SortBy.getValue(), newVal));
        
        buttonX.setOnAction((ActionEvent event) -> {
            txtSearchBar.clear(); // Hapus teks pencarian
        });
    }    

    @Override
    public void perbarui() {
        loadStok(SortBy.getValue(), txtSearchBar.getText().trim());
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
        colIdBarang.setCellValueFactory(new PropertyValueFactory<>("idBarang"));
        colNamaBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colHargaJual.setCellValueFactory(new PropertyValueFactory<>("hargaJual"));
        colMerek.setCellValueFactory(new PropertyValueFactory<>("merek"));
        colExp.setCellValueFactory(new PropertyValueFactory<>("exp"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
        LocalDate today = LocalDate.now();
        TableStok.setRowFactory(tv -> new TableRow<Stok>() {
            @Override
            protected void updateItem(Stok item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    String expString = item.getExp();
                    LocalDate expDate = LocalDate.parse(expString, dateFormatter);
                    
                    int stok = item.getStok();
                    if (stok == 0 || expDate.isBefore(today)) {
                        setStyle("-fx-background-color: #ffcccc; -fx-text-fill: black;");
                    }
                    
                    setOnMousePressed(event -> {
                        if(colTerakhir != null){
                            String expColTerakhir = colTerakhir.getItem().getExp();
                            LocalDate expDateColTerakhir = LocalDate.parse(expColTerakhir, dateFormatter);
                            if(colTerakhir.getItem().getStok() == 0 || expDateColTerakhir.isBefore(today)){
                                colTerakhir.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: black;");
                            }
                        }
                        
                        if (stok == 0 || expDate.isBefore(today)) {
                            setStyle("-fx-background-color: #e57373; -fx-text-fill: black;");
                        }
                        colTerakhir = this;
                    });
                }
            }
        });
    }
     
    private void loadStok(String sortOption, String keyword) {
        ObservableList<Stok> listStok = stokManager.getAllStok(sortOption, keyword);
        TableStok.setItems(listStok);
    }
 }


 