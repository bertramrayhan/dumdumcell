package karyawan.halamanUtama;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.Session;

public class HalamanUtamaKController implements Initializable {
    String pathNow = Session.getPathBeranda();
    
    @FXML private StackPane halamanUtama;
    
    @FXML private Pane indikatorBeranda, indikatorJual, indikatorAntarCabang, indikatorRetur, indikatorStok;
    @FXML private Pane indikatorDanLainLain, indikatorKas, indikatorSaldo, indikatorRekap, indikatorDiskon;
    
    @FXML private Button btnBeranda, btnJual, btnAntarCabang, btnRetur, btnStok;
    @FXML private Button btnDanLainLain, btnKas, btnSaldo, btnRekap, btnDiskon;

    public static Map<String, AnchorPane> penyimpananPaneKaryawan = new HashMap<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPane(Session.getPathBeranda());
    }    
    
    private void loadPane(String pathPane) {
        if (penyimpananPaneKaryawan.containsKey(pathPane)) {
            halamanUtama.getChildren().setAll(penyimpananPaneKaryawan.get(pathPane));
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pathPane));
            AnchorPane pane = loader.load();
            penyimpananPaneKaryawan.put(pathPane, pane); // Simpan ke cache biar nggak load lagi
            halamanUtama.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //untuk reload ulang pane. kalo dibutuhkan, pakai.
//    public void reloadPane(String pathPane) {
//        penyimpananPaneKaryawan.remove(pathPane); // Hapus cache biar nanti di-load ulang
//        loadPane(pathPane);
//    }

    
    @FXML
    void goToProfil(){
        resetIndikator();
        loadPane(Session.getPathHalamanProfil());
    }
    
    @FXML
    void goToBeranda(){
        resetIndikator();
        animateIndikator(indikatorBeranda);
        loadPane(Session.getPathBeranda());
    }
    
    @FXML
    void goToStok(){
        resetIndikator();
        animateIndikator(indikatorStok);
        //loadPane(Session.getPathHalamanStok());
    }
    
    @FXML
    void goToJual(){
        resetIndikator();
        animateIndikator(indikatorJual);
        loadPane(Session.getPathHalamanJual());
    }
    
    @FXML
    void goToAntarCabang(){
        resetIndikator();
        animateIndikator(indikatorAntarCabang);
        //loadPane(Session.getPathHalamanAntarCabang());
    }

    @FXML
    void goToRetur(){
        resetIndikator();
        animateIndikator(indikatorRetur);
        //loadPane(Session.getPathHalamanRetur());
    }

    @FXML
    void goToKas(){
        resetIndikator();
        animateIndikator(indikatorKas);
        //loadPane(Session.getPathHalamanKas());
    }

    @FXML
    void goToSaldo(){
        resetIndikator();
        animateIndikator(indikatorSaldo);
        //loadPane(Session.getPathHalamanSaldo());
    }

    @FXML
    void goToRekap(){
        resetIndikator();
        animateIndikator(indikatorRekap);
        //loadPane(Session.getPathHalamanRekap());
    }

    @FXML
    void goToDiskon(){
        resetIndikator();
        animateIndikator(indikatorDiskon);
        //loadPane(Session.getPathHalamanDiskon());
    }
    
    @FXML
    void goToDanLainLain(){
        resetIndikator();
        animateIndikator(indikatorDanLainLain);
        //loadPane(Session.getPathHalamanDiskon());
    }
    
    private void resetIndikator() {
        indikatorBeranda.setScaleY(0);
        indikatorStok.setScaleY(0);
        indikatorJual.setScaleY(0);
        indikatorAntarCabang.setScaleY(0);
        indikatorRetur.setScaleY(0);
        indikatorKas.setScaleY(0);
        indikatorSaldo.setScaleY(0);
        indikatorRekap.setScaleY(0);
        indikatorDiskon.setScaleY(0);
        indikatorDanLainLain.setScaleY(0);
    }
    
    private void animateIndikator(Pane indikator) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), indikator);
        st.setFromY(0);
        st.setToY(1);
        st.play();
    }
}
