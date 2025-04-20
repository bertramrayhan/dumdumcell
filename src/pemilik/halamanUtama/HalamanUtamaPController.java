package pemilik.halamanUtama;

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

public class HalamanUtamaPController implements Initializable {
    @FXML private StackPane halamanUtama;
    
    @FXML private Pane indikatorBeranda, indikatorJual, indikatorAntarCabang, indikatorProduk, indikatorStok;
    @FXML private Pane indikatorDanLainLain, indikatorKas, indikatorSaldo, indikatorRekap, indikatorDiskon;
    
    @FXML private Button btnBeranda, btnKas, btnSaldo, btnProduk, btnDiskon;
    @FXML private Button btnPresensiPusat, btnPresensiCabang, btnLaporan;
    
    public static Map<String, AnchorPane> penyimpananPanePemilik = new HashMap<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPane(Session.getPathBerandaP());
    }    
    
    private void loadPane(String pathPane) {
        if (penyimpananPanePemilik.containsKey(pathPane)) {
            halamanUtama.getChildren().setAll(penyimpananPanePemilik.get(pathPane));
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pathPane));
            AnchorPane pane = loader.load();
            penyimpananPanePemilik.put(pathPane, pane); 
            halamanUtama.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void goToProfil(){
        resetIndikator();
        loadPane(Session.getPathHalamanProfilK());
    }
    
    @FXML
    void goToBeranda(){
        resetIndikator();
        animateIndikator(indikatorBeranda);
        loadPane(Session.getPathBerandaP());
    }
    
    @FXML
    void goToKas(){
        resetIndikator();
        animateIndikator(indikatorJual);
        loadPane(Session.getPathHalamanKasP());
    }
    
    @FXML
    void goToSaldo(){
        resetIndikator();
        animateIndikator(indikatorAntarCabang);
        loadPane(Session.getPathHalamanSaldoP());
    }

    @FXML
    void goToProduk(){
        resetIndikator();
        animateIndikator(indikatorProduk);
        loadPane(Session.getPathHalamanProdukP());
    }

    @FXML
    void goToDiskon(){
        resetIndikator();
        animateIndikator(indikatorKas);
        loadPane(Session.getPathHalamanDiskonP());
    }

    @FXML
    void goToPrensensiPusat(){
        resetIndikator();
        animateIndikator(indikatorSaldo);
        loadPane(Session.getPathHalamanPresensiPusatP());
    }

    @FXML
    void goToPresensiCabang(){
        resetIndikator();
        animateIndikator(indikatorRekap);
        loadPane(Session.getPathHalamanPresensiCabangP());
    }

    @FXML
    void goToLaporan(){
        resetIndikator();
        animateIndikator(indikatorDiskon);
        loadPane(Session.getPathHalamanLaporanP());
    }
    
    private void resetIndikator() {
        indikatorBeranda.setScaleY(0);
        indikatorStok.setScaleY(0);
        indikatorJual.setScaleY(0);
        indikatorAntarCabang.setScaleY(0);
        indikatorProduk.setScaleY(0);
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
