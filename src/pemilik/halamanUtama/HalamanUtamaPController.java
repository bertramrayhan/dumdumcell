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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.Session;

public class HalamanUtamaPController implements Initializable {
    @FXML private StackPane halamanUtama, panePesan;
    @FXML private Label lblPesan;
    
    @FXML private Pane indikatorBeranda, indikatorKas, indikatorSaldo, indikatorProduk, indikatorSupplier,indikatorDiskon;
    @FXML private Pane indikatorLaporan, indikatorBeli;
    
    @FXML private Button btnBeranda, btnKas, btnSaldo, btnProduk, btnSupplier, btnDiskon;
    @FXML private Button btnLaporan;
    
    public static Map<String, AnchorPane> penyimpananPanePemilik = new HashMap<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPane(Session.getPathBerandaP());
        Session.inisialisasiPesan(panePesan, lblPesan);
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
        animateIndikator(indikatorKas);
        loadPane(Session.getPathHalamanKasP());
    }
    
    @FXML
    void goToSaldo(){
        resetIndikator();
        animateIndikator(indikatorSaldo);
        loadPane(Session.getPathHalamanSaldoP());
    }
    
    @FXML
    void goToBeli(){
        resetIndikator();
        animateIndikator(indikatorBeli);
        loadPane(Session.getPathHalamanBeliP());
    }

    @FXML
    void goToProduk(){
        resetIndikator();
        animateIndikator(indikatorProduk);
        loadPane(Session.getPathHalamanProdukP());
    }
    
    @FXML
    void goToSupplier(){
        resetIndikator();
        animateIndikator(indikatorSupplier);
        loadPane(Session.getPathHalamanSupplierP());
    }

    @FXML
    void goToDiskon(){
        resetIndikator();
        animateIndikator(indikatorDiskon);
        loadPane(Session.getPathHalamanDiskonP());
    }

    @FXML
    void goToLaporan(){
        resetIndikator();
        animateIndikator(indikatorLaporan);
        loadPane(Session.getPathHalamanLaporanP());
    }
    
    private void resetIndikator() {
        indikatorBeranda.setScaleY(0);
        indikatorKas.setScaleY(0);
        indikatorSaldo.setScaleY(0);
        indikatorBeli.setScaleY(0);
        indikatorProduk.setScaleY(0);
        indikatorSupplier.setScaleY(0);
        indikatorDiskon.setScaleY(0);
        indikatorLaporan.setScaleY(0);
    }
    
    private void animateIndikator(Pane indikator) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), indikator);
        st.setFromY(0);
        st.setToY(1);
        st.play();
    }
}
