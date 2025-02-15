package karyawan.halamanUtama;

import java.io.IOException;
import java.net.URL;
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

public class HalamanUtamaKController implements Initializable {
    final String pathBeranda = "/karyawan/beranda/halamanBerandaK.fxml";
    String pathNow = pathBeranda;
    
    @FXML private StackPane halamanUtama;
    
    @FXML private Pane indikatorBeranda, indikatorStok;
    
    @FXML private Button btnBeranda, btnStok;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPane(pathBeranda);
    }    
    
    private void loadPane(String pathPane) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pathPane));
            AnchorPane pane = loader.load();

            halamanUtama.getChildren().setAll(pane); // Ganti isi StackPane
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void goToBeranda(){
        resetIndikator();
        animateIndikator(indikatorBeranda);
    }
    
    @FXML
    void goToStok(){
        resetIndikator();
        animateIndikator(indikatorStok);
    }
    
    private void resetIndikator() {
        indikatorBeranda.setScaleY(0);
        indikatorStok.setScaleY(0);
    }
    
    private void animateIndikator(Pane indikator) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), indikator);
        st.setFromY(0);
        st.setToY(1);
        st.play();
    }
}
