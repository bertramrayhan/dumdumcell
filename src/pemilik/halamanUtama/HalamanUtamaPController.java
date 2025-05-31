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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.Pelengkap;
import main.Session;

public class HalamanUtamaPController implements Initializable {
    @FXML private StackPane halamanUtama, panePesan;
    @FXML private Label lblPesan;
    
    @FXML private Pane indikatorBeranda, indikatorProduk, indikatorSupplier,indikatorDiskon;
    @FXML private Pane indikatorLaporan, indikatorBeli, indikatorKartuStok;
    
    @FXML private Button btnBeranda, btnProduk, btnSupplier, btnDiskon;
    @FXML private Button btnLaporan, btnKartuStok;
    
    public static Map<String, AnchorPane> penyimpananPanePemilik = new HashMap<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPane(Session.getPathBerandaP());
        Session.inisialisasiPesan(panePesan, lblPesan);
    }    
    
    private void loadPane(String pathPane) {
        if (penyimpananPanePemilik.containsKey(pathPane)) {
            Node cachedPane = penyimpananPanePemilik.get(pathPane);

            // Ambil controller dari UserData dan refresh
            Object controller = cachedPane.getUserData();
            if (controller instanceof Pelengkap) {
                ((Pelengkap) controller).perbarui();
                System.out.println("refresh (from cache)");
            }

            halamanUtama.getChildren().setAll(cachedPane);
            //showWithSlideAndFade(cachedPane);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pathPane));
            AnchorPane pane = loader.load();

            // Ambil dan simpan controller ke UserData!
            Object controller = loader.getController();
            pane.setUserData(controller);

            penyimpananPanePemilik.put(pathPane, pane);
            halamanUtama.getChildren().setAll(pane);
            //showWithSlideAndFade(pane);
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
    void goToKartuStok(){
        resetIndikator();
        animateIndikator(indikatorKartuStok);
        loadPane(Session.getPathHalamanKartuStokP());
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
        indikatorBeli.setScaleY(0);
        indikatorProduk.setScaleY(0);
        indikatorSupplier.setScaleY(0);
        indikatorDiskon.setScaleY(0);
        indikatorLaporan.setScaleY(0);
        indikatorKartuStok.setScaleY(0);
    }
    
    private void animateIndikator(Pane indikator) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), indikator);
        st.setFromY(0);
        st.setToY(1);
        st.play();
    }
}
