package karyawan.halamanUtama;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.Pelengkap;
import main.Session;

public class HalamanUtamaKController implements Initializable {    
    @FXML private StackPane halamanUtama;
    
    @FXML private Pane indikatorBeranda, indikatorJual, indikatorAntarCabang, indikatorRetur, indikatorStok;
    @FXML private Pane indikatorDanLainLain, indikatorKas, indikatorSaldo, indikatorRekap;
    
    @FXML private Button btnBeranda, btnJual, btnAntarCabang, btnRetur, btnStok;
    @FXML private Button btnDanLainLain, btnKas, btnSaldo, btnRekap;

    public static Map<String, AnchorPane> penyimpananPaneKaryawan = new HashMap<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPane(Session.getPathBerandaK());
    }    
    
    private void loadPane(String pathPane) {
        if (penyimpananPaneKaryawan.containsKey(pathPane)) {
            Node cachedPane = penyimpananPaneKaryawan.get(pathPane);

            // Ambil controller dari UserData dan refresh
            Object controller = cachedPane.getUserData();
            if (controller instanceof Pelengkap) {
                ((Pelengkap) controller).refresh();
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

            // Panggil refresh pertama kali juga
            if (controller instanceof Pelengkap) {
                ((Pelengkap) controller).refresh();
                System.out.println("refresh (first load)");
            }
            penyimpananPaneKaryawan.put(pathPane, pane);
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
        loadPane(Session.getPathBerandaK());
    }
    
    @FXML
    void goToStok(){
        resetIndikator();
        animateIndikator(indikatorStok);
        loadPane(Session.getPathHalamanStokK());
    }
    
    @FXML
    void goToJual(){
        resetIndikator();
        animateIndikator(indikatorJual);
        loadPane(Session.getPathHalamanJualK());
    }
    
    @FXML
    void goToAntarCabang(){
        resetIndikator();
        animateIndikator(indikatorAntarCabang);
        loadPane(Session.getPathHalamanTransaksiAntarCabang());
    }

    @FXML
    void goToRetur(){
        resetIndikator();
        animateIndikator(indikatorRetur);
        loadPane(Session.getPathHalamanTransaksiRetur());
    }

    @FXML
    void goToKas(){
        resetIndikator();
        animateIndikator(indikatorKas);
        loadPane(Session.getPathHalamanKas());
    }

    @FXML
    void goToSaldo(){
        resetIndikator();
        animateIndikator(indikatorSaldo);
        loadPane(Session.getPathHalamanSaldoK());
    }

    @FXML
    void goToRekap(){
        resetIndikator();
        animateIndikator(indikatorRekap);
        loadPane(Session.getPathHalamanRekap());
    }
    
    @FXML
    void goToDanLainLain(){
        resetIndikator();
        animateIndikator(indikatorDanLainLain);
        loadPane(Session.getpathHalamanTransaksiDLL());
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
        indikatorDanLainLain.setScaleY(0);
    }
    
    private void animateIndikator(Pane indikator) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), indikator);
        st.setFromY(0);
        st.setToY(1);
        st.play();
    }
    
    private void showWithSlideAndFade(Node node) {
        node.setOpacity(0);
        node.setTranslateX(halamanUtama.getWidth());

        FadeTransition fade = new FadeTransition(Duration.millis(300), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(300), node);
        slide.setFromX(halamanUtama.getWidth());
        slide.setToX(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);

        ParallelTransition combo = new ParallelTransition(fade, slide);
        combo.play();
    }
}
