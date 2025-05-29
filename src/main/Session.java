package main;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Session {
    private static final DateTimeFormatter formatTanggal = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
    private static final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    
    private static SequentialTransition animasiSedangBerjalan;
    private static String pesanTerakhir = "";
    private static long waktuTerakhir = 0;
    private static StackPane panePesan;
    private static Label lblPesan;
    
    private static String idAdmin = "";
    //SHARE
    private static final String pathHalamanLogin = "/login/halamanLogin.fxml";
    private static final String pathHalamanKas = "/login/halamanLogin.fxml";
    private static final String pathHalamanSaldo = "/login/halamanLogin.fxml";
    
    //KARYAWAN
    private static final String pathHalamanUtamaK = "/karyawan/halamanUtama/halamanUtamaK.fxml";
    private static final String pathBerandaK = "/karyawan/beranda/halamanBerandaK.fxml";
    private static final String pathHalamanProfilK = "/karyawan/halamanProfil/halamanProfilK.fxml";
    private static final String pathHalamanJualK = "/karyawan/halamanJual/halamanJualK.fxml";
    private static final String pathHalamanSaldoK = "/karyawan/halamanSaldo/halamanSaldoK.fxml";
    private static final String pathHalamanStokK =  "/karyawan/halamanStok/halamanStokK.fxml";
    private static final String pathHalamanTransaksiAntarCabangK = "/karyawan/halamanTransaksiAntarCabang/halamanTransaksiAntarCabangK.fxml";
    private static final String pathHalamanTransaksiReturK =  "/karyawan/halamanTransaksiRetur/halamanTransaksiReturK.fxml";
    private static final String pathHalamanKasK =  "/karyawan/halamanKas/halamanKasK.fxml";
    private static final String pathHalamanTopupSaldoK = "/karyawan/halamanTopupSaldo/halamanTopupSaldoK.fxml";
    private static final String pathHalamanTransaksiLainLainK = "/karyawan/halamanTransaksiLainLain/halamanTransaksiLainLainK.fxml";
    //PEMILIK
    private static final String pathHalamanUtamaP = "/pemilik/halamanUtama/halamanUtamaP.fxml";
    private static final String pathBerandaP = "/pemilik/beranda/halamanBerandaP.fxml";
    private static final String pathHalamanKasP = "/karyawan/halamanKas/halamanKasK.fxml";
    private static final String pathHalamanSaldoP = "/karyawan/halmanSaldo/halamanSaldo.fxml";
    private static final String pathHalamanBeliP = "/pemilik/halamanTransaksiBeli/halamanTransaksiBeliP.fxml";
    private static final String pathHalamanProdukP = "/pemilik/halamanProduk/halamanProdukP.fxml";
    private static final String pathHalamanSupplierP = "/pemilik/halamanSupplier/halamanSupplierP.fxml";
    private static final String pathHalamanDiskonP = "/pemilik/halamanDiskon/halamanDiskonP.fxml";
    private static final String pathHalamanLaporanP = "/pemilik/halamanLaporan/halamanLaporanPenjualan.fxml";
    
    public static void setIdAdmin(String idAdmin) {Session.idAdmin = idAdmin;}
    public static String getIdAdmin() {return idAdmin;}
    public static String getPathHalamanLogin() {return pathHalamanLogin;}
    public static String getPathHalamanUtamaK() {return pathHalamanUtamaK;}
    public static String getPathBerandaK() {return pathBerandaK;}
    public static String getPathHalamanProfilK() {return pathHalamanProfilK;}
    public static String getPathHalamanJualK() {return pathHalamanJualK;}
    public static String getPathHalamanSaldoK() {return pathHalamanSaldoK;}
    public static String getPathHalamanStokK() {return pathHalamanStokK;}
    public static String getPathHalamanUtamaP() {return pathHalamanUtamaP;}    
    public static String getPathBerandaP() {return pathBerandaP;}
    public static String getPathHalamanProdukP() {return pathHalamanProdukP;}
    public static String getPathHalamanBeliP() {return pathHalamanBeliP;}
    public static String getPathHalamanSupplierP() {return pathHalamanSupplierP;}
    public static String getPathHalamanTransaksiAntarCabangK() { return pathHalamanTransaksiAntarCabangK;};
    public static String getPathHalamanTransaksiReturK() { return pathHalamanTransaksiReturK;};
    public static String getPathHalamanKas() { return pathHalamanKasK;};
    public static String getPathHalamanTopupSaldoK() { return pathHalamanTopupSaldoK;};
    public static String getPathHalamanTransaksiLainLainK(){ return pathHalamanTransaksiLainLainK;}; 
    public static String getPathHalamanKasK() {return pathHalamanKasK;}
    public static String getPathHalamanKasP() {return pathHalamanKasP;}
    public static String getPathHalamanSaldoP() {return pathHalamanSaldoP;}
    public static String getPathHalamanDiskonP() {return pathHalamanDiskonP;}
    public static String getPathHalamanLaporanP() {return pathHalamanLaporanP;}
    
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

    public static String getBulan(int bulan) {
        String[] bulanIndo = {
            "Januari", "Februari", "Maret", "April", "Mei", "Juni", 
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        };

        // Pastikan angka bulan valid (1-12)
        if (bulan >= 1 && bulan <= 12) {
            return bulanIndo[bulan - 1];
        } else {
            return "Bulan tidak valid"; // Jika input di luar range 1-12
        }
    }
    
    public static void setTextFieldNumeric(TextField... textFields) {
        Pattern pattern = Pattern.compile("\\d*"); // Hanya angka
        UnaryOperator<TextFormatter.Change> filter = change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        };

        for (TextField tf : textFields) {
            tf.setTextFormatter(new TextFormatter<>(filter));
        }
    }
    
    public static void setTextFieldNumeric(int maxLength, TextField... textFields) {
        Pattern pattern = Pattern.compile("\\d*"); // hanya angka

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (pattern.matcher(newText).matches() && newText.length() <= maxLength) {
                return change;
            }
            return null;
        };

        for (TextField tf : textFields) {
            tf.setTextFormatter(new TextFormatter<>(filter));
        }
    }
    
    public static void inisialisasiPesan(StackPane pane, Label label){
        panePesan = pane;
        lblPesan = label;
    }
        
    public static void animasiPanePesan(boolean isGagal, String pesan, Button ... buttons) {
        long sekarang = System.currentTimeMillis();
        if(pesan.contains(pesanTerakhir) && (sekarang - waktuTerakhir < 2000)){
            return;
        }
        
        if (animasiSedangBerjalan != null) {
            animasiSedangBerjalan.stop(); // Hentikan animasi sebelumnya
            panePesan.setOpacity(0); // Reset tampilan
            panePesan.setTranslateY(50);
        }

        pesanTerakhir = pesan;
        waktuTerakhir = sekarang;
        
        if (isGagal) {
            panePesan.setStyle("-fx-background-color: #D32F2F; -fx-border-color: #B71C1C; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10;");
            lblPesan.setStyle("-fx-text-fill: #ffebee;");
        } else {
            panePesan.setStyle("-fx-background-color: #388E3C; -fx-border-color: #1B5E20; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10;");
            lblPesan.setStyle("-fx-text-fill: #E8F5E9;");
        }

        lblPesan.setText(pesan);
        setDisableButtons(buttons);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), panePesan);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition naik = new TranslateTransition(Duration.millis(500), panePesan);
        naik.setFromY(50);
        naik.setToY(-100);

        ParallelTransition naikSambilFade = new ParallelTransition(naik, fadeIn);

        PauseTransition jeda = new PauseTransition(Duration.millis(1000));

        TranslateTransition turun = new TranslateTransition(Duration.millis(500), panePesan);
        turun.setFromY(-100);
        turun.setToY(50);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), panePesan);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        ParallelTransition turunSambilFade = new ParallelTransition(turun, fadeOut);

        animasiSedangBerjalan = new SequentialTransition(naikSambilFade, jeda, turunSambilFade);
        animasiSedangBerjalan.setOnFinished(event -> {
            setEnableButtons(buttons);
            animasiSedangBerjalan = null; // reset biar bisa deteksi animasi baru lagi nanti
        });
        animasiSedangBerjalan.play();
    }
    
    public static void togglePassword(PasswordField txtPassword, TextField txtPasswordVisible, ImageView btnShowPassword,
                                  String eyeIconPath, String eyeOffIconPath) {
        boolean isShowing = txtPasswordVisible.isVisible();

        String iconToLoad = isShowing ? eyeOffIconPath : eyeIconPath;
        InputStream iconStream = ClassLoader.getSystemResourceAsStream(iconToLoad);

        if (iconStream == null) {
            return;
        }

        Image icon = new Image(iconStream);

        if (!isShowing) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());

            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());

            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
        }

        btnShowPassword.setImage(icon);
    }
    
    public static void togglePassword(PasswordField txtPassword, TextField txtPasswordVisible) {
        boolean isShowing = txtPasswordVisible.isVisible();

        if (!isShowing) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());

            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());

            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
        }
    }
    
    public static void setEnableButtons(Button ... buttons){
        for(Button btn : buttons){
            btn.setDisable(false);
        }
    }
    
    public static void setDisableButtons(Button ... buttons){
        for(Button btn : buttons){
            btn.setDisable(true);
        }
    }
    
    public static void setMouseTransparentTrue(Node... nodes) {
        for (Node node : nodes) {
            node.setMouseTransparent(true);
        }
    }

    public static void setMouseTransparentFalse(Node... nodes) {
        for (Node node : nodes) {
            node.setMouseTransparent(false);
        }
    }
    
    public static void setShowPane(AnchorPane pane){
        pane.setVisible(true);
        pane.setMouseTransparent(false);
    }
    
    public static void setHidePane(AnchorPane pane){
        pane.setVisible(false);
        pane.setMouseTransparent(true);
    }
    
    public static void setShowPane(AnchorPane pane, Pane paneGelap){
        pane.setVisible(true);
        pane.setMouseTransparent(false);
        paneGelap.setVisible(true);

        // Set initial scale kecil
        pane.setScaleX(0.8);
        pane.setScaleY(0.8);

        // Animasi scale up
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(300), pane);
        scaleUp.setFromX(0.8);
        scaleUp.setFromY(0.8);
        scaleUp.setToX(1);
        scaleUp.setToY(1);
        scaleUp.play();
    }

    public static void setHidePane(AnchorPane pane, Pane paneGelap){
        // Animasi scale down dulu, setelah selesai baru disembunyiin
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(300), pane);
        scaleDown.setFromX(1);
        scaleDown.setFromY(1);
        scaleDown.setToX(0.8);
        scaleDown.setToY(0.8);

        scaleDown.setOnFinished(e -> {
            pane.setVisible(false);
            pane.setMouseTransparent(true);
            paneGelap.setVisible(false);
        });

        scaleDown.play();
    }
    
    public static void triggerOnEnter(Runnable action, TextInputControl... inputs) {
        for (TextInputControl input : inputs) {
            input.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    action.run();
                }
            });
        }
    }
    
    public static boolean cekDataSama(String query, String ... namaData){
        boolean sama = false;
        
        try {
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            int counter = 1;
            for(String data : namaData){
                System.out.println(Arrays.toString(namaData));
                statement.setString(counter, data);
                counter++;
            }
            ResultSet result = statement.executeQuery();
            
            if(result.next()) {
                sama = true;
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return sama;
    }
    
    public static String membuatIdBaru(String namaTabel, String namaKolom, String prefix, int jumlahDigit) {
        String newId = prefix + String.format("%0" + jumlahDigit + "d", 1);

        try {
            String query = String.format("SELECT %s FROM %s ORDER BY %s DESC LIMIT 1", namaKolom, namaTabel, namaKolom);
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String lastId = result.getString(namaKolom);
                int oldNumber = Integer.parseInt(lastId.substring(prefix.length()));
                int newNumber = oldNumber + 1;
                newId = prefix + String.format("%0" + jumlahDigit + "d", newNumber);
            }

            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newId;
    }
    
    public static String getId(String namaTabel, String namaKolomId, String namaKolomKriteria, String nilaiKriteria) {
        String id = null;

        try {
            String query = String.format("SELECT %s FROM %s WHERE %s = ?", namaKolomId, namaTabel, namaKolomKriteria);
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, nilaiKriteria);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                id = result.getString(namaKolomId);
            } else {
                throw new RuntimeException("Data dengan " + namaKolomKriteria + " = '" + nilaiKriteria + "' tidak ditemukan di tabel " + namaTabel);
            }

            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Terjadi kesalahan saat mengambil ID: " + e.getMessage());
        }

        return id;
    }
}
