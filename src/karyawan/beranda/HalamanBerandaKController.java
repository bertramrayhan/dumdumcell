package karyawan.beranda;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import main.Barang;

public class HalamanBerandaKController implements Initializable {
    @FXML private TableView<Barang> tabelBarangExpired;
    @FXML private TableView<Promo> tabelPromo;
    @FXML private TableColumn<Barang, String> namaBarangCol, hargaCol, totalBarangCol, expCol;
    @FXML private TableColumn<Promo, String> promoCol, potonganHargaCol;
    
    @FXML private Label lblWaktu;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    
    private ObservableList<Barang> barangList = FXCollections.observableArrayList();
    private ObservableList<Promo> promoList = FXCollections.observableArrayList();
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mulaiWaktu();
        setTabelBarangExpired();
        setTabelPromo();
    }
    
    private void mulaiWaktu() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime now = LocalTime.now();
            lblWaktu.setText(now.format(formatter));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void setTabelBarangExpired(){
        namaBarangCol.setCellValueFactory(new PropertyValueFactory<>("nama"));
        hargaCol.setCellValueFactory(new PropertyValueFactory<>("harga"));
        totalBarangCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        expCol.setCellValueFactory(new PropertyValueFactory<>("expired"));
        
        barangList.add(new Barang("es krim", "Rp7.000", "10", "23 Maret 2025"));
        barangList.add(new Barang("rokok", "Rp15.000", "30", "23 September 2025"));
        tabelBarangExpired.setItems(barangList);
    }
    
    private void setTabelPromo(){
        promoCol.setCellValueFactory(new PropertyValueFactory<>("promo"));
        potonganHargaCol.setCellValueFactory(new PropertyValueFactory<>("potonganHarga"));
        
        promoList.add(new Promo("12.12", "Rp12.000"));
        tabelPromo.setItems(promoList);
    }
}