package karyawan.halamanJual;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import main.Koneksi;
import main.Session;

public class HalamanJualKController implements Initializable {
    @FXML Label lblTanggal, lblJam, lblKasir, lblSubtotal, lblDiskon, lblTotal, lblKembalian;
    @FXML TextField txtBarcode, txtBarcodeQty, txtBayar;
    @FXML Button btnTambahProdukBarcode, btnTambahProdukManual, btnBatalTransaksi, btnKonfirmasiTransaksi;
    @FXML TextArea txtACatatan;
    @FXML ChoiceBox<String> cbxCaraBayar;
    private final DateTimeFormatter formatWaktu = DateTimeFormatter.ofPattern("HH:mm");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setWaktuDanTanggal();
        setKasir();
        setCbxCaraBayar();
    }    
    
    private void setKasir(){
        try {
            String query = "SELECT username FROM admin WHERE id_admin=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, Session.getIdAdmin());
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                lblKasir.setText(result.getString("username"));
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setCbxCaraBayar(){
        cbxCaraBayar.getItems().addAll("Tunai", "Transfer");
        cbxCaraBayar.setValue("Tunai");
    }
    
    private void setWaktuDanTanggal() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime now = LocalTime.now();
            lblJam.setText(now.format(formatWaktu));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        lblTanggal.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}
