package main;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Session {
    private static final DateTimeFormatter formatTanggal = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));

    public static String getPathHalamanSaldo() {
        return pathHalamanSaldo;
    }
    private static final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    
    private static String idAdmin = "";
    private static final String pathHalamanLogin = "/login/halamanLogin.fxml";
    
    private static final String pathHalamanUtama = "/karyawan/halamanUtama/halamanUtamaK.fxml";
    private static final String pathBeranda = "/karyawan/beranda/halamanBerandaK.fxml";
    private static final String pathHalamanProfil = "/karyawan/halamanProfil/halamanProfilK.fxml";
    private static final String pathHalamanJual = "/karyawan/halamanJual/halamanJualK.fxml";
    private static final String pathHalamanSaldo = "/karyawan/halamanSaldo/halamanSaldoK.fxml";
    private static final String pathHalamanStok =  "/karyawan/halamanStok/halamanStokK.fxml";

    public static void setIdAdmin(String idAdmin) {
        Session.idAdmin = idAdmin;
    }

    public static String getIdAdmin() {
        return idAdmin;
    }

    public static String getPathHalamanLogin() {
        return pathHalamanLogin;
    }

    public static String getPathHalamanUtama() {
        return pathHalamanUtama;
    }

    public static String getPathBeranda() {
        return pathBeranda;
    }

    public static String getPathHalamanProfil() {
        return pathHalamanProfil;
    }

    public static String getPathHalamanJual() {
        return pathHalamanJual;
    }
    public static String getPathHalamanStok(){
        return pathHalamanStok;
    }

    public static String convertTanggalIndo(String tanggal){
        LocalDate tglExp = LocalDate.parse(tanggal, DateTimeFormatter.ISO_LOCAL_DATE);

        return tglExp.format(formatTanggal);
    }
    
    public static String convertIntToRupiah(int harga){
        return formatRupiah.format(harga);
    }
    
    public static int convertRupiahToInt(String rupiah){
        int harga = 0;
        try {
            harga = formatRupiah.parse(rupiah).intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return harga;
    }

    public static void setTextFieldNumeric(TextField textField) {
        Pattern pattern = Pattern.compile("\\d*"); // Hanya angka
        UnaryOperator<TextFormatter.Change> filter = change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        };
        textField.setTextFormatter(new TextFormatter<>(filter));
    }
    
    public static void animasiPanePesan(boolean isGagal, Pane panePesan, Label lblPesan, String pesan, Button ... buttons) {
        if(isGagal){
            panePesan.setStyle("-fx-background-color: #D32F2F; -fx-border-color: #B71C1C; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10;");
            lblPesan.setStyle("-fx-text-fill: #ffebee;");
        }else{
            panePesan.setStyle("-fx-background-color: #388E3C; -fx-border-color: #1B5E20; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10;");
            lblPesan.setStyle("-fx-text-fill: #E8F5E9;");
        }
        
        lblPesan.setText(pesan);
        for(Button btn : buttons){
            btn.setDisable(true);
        }
        
        // Animasi fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), panePesan);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Animasi naik ke tengah
        TranslateTransition naik = new TranslateTransition(Duration.millis(500), panePesan);
        naik.setFromY(50);
        naik.setToY(-100);

        ParallelTransition naikSambilFade = new ParallelTransition(naik, fadeIn);
        
        // Pause biar pesan keliatan beberapa detik
        PauseTransition jeda = new PauseTransition(Duration.millis(1000));

        // Animasi turun kembali
        TranslateTransition turun = new TranslateTransition(Duration.millis(500), panePesan);
        turun.setFromY(-100);
        turun.setToY(50);

        // Animasi fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), panePesan);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        ParallelTransition turunSambilFade = new ParallelTransition(turun, fadeOut);
        
        // Gabung semua animasi dengan jeda tambahan
        SequentialTransition animasi = new SequentialTransition(naikSambilFade, jeda, turunSambilFade);
        animasi.setOnFinished(event -> {
            for(Button btn : buttons){
                btn.setDisable(false);
            }
        });
        animasi.play();
    }
}
