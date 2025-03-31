package pemilik.beranda;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import main.Koneksi;
import main.Session;

public class HalamanBerandaPController implements Initializable {
    @FXML private LineChart<String, Number> chartPenjualan; 
    @FXML private BarChart<String, Number> chartBarangTerlaris; 
    //@FXML private ScrollPane scrollPaneChartPenjualan;
    
    @FXML private CategoryAxis axisPeriode, axisNamaBarang; 
    @FXML private NumberAxis axisTotalPenjualan;
    @FXML private Label lblTotalBarangTerjual, lblJumlahBarang, lblJumlahBarangHampirExp, lblWaktu, lblJumlahTransaksi, lblTotalOmzet, 
    lblRataRataPembelian;
    @FXML private ChoiceBox<String> cbxSortByTotalPenjualan;
    
    private final DateTimeFormatter formatWaktu = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setCbxDanChart();
        mulaiWaktu();
        setBarang();
        setTransaksi();
        setGrafikPenjualan();
        setGrafikBarangTerlaris();
    }    
    
    private void mulaiWaktu() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime now = LocalTime.now();
            lblWaktu.setText(now.format(formatWaktu));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void setBarang(){
        try {
            String query = "SELECT COALESCE(total_barang, 0) as total_barang, \n" +
            "       COALESCE(jumlah_barang, 0) as jumlah_barang, \n" +
            "       COALESCE(jumlah_barang_hampir_expired, 0) as jumlah_barang_hampir_expired \n" +
            "FROM ( \n" +
            "    SELECT  \n" +
            "        (SELECT SUM(dtj.jumlah_barang)  \n" +
            "         FROM detail_transaksi_jual dtj  \n" +
            "         JOIN transaksi_jual tj  \n" +
            "         ON tj.id_transaksi_jual = dtj.id_transaksi_jual  \n" +
            "         WHERE DATE(tj.tanggal_transaksi_jual) = CURRENT_DATE) AS total_barang, \n" +
            "         \n" +
            "        (SELECT SUM(stok) FROM barang) AS jumlah_barang, \n" +
            "\n" +
            "        (SELECT COUNT(*)  \n" +
            "         FROM barang  \n" +
            "         WHERE exp IS NOT NULL  \n" +
            "         AND exp <= DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY)) AS jumlah_barang_hampir_expired \n" +
            ") AS subquery;";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                lblTotalBarangTerjual.setText(result.getString("total_barang"));
                lblJumlahBarang.setText(result.getString("jumlah_barang"));
                lblJumlahBarangHampirExp.setText(result.getString("jumlah_barang_hampir_expired"));
            } else {
                lblTotalBarangTerjual.setText("0");
                lblJumlahBarang.setText("0");
                lblJumlahBarangHampirExp.setText("0");
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setTransaksi(){
        try {
            String query = "SELECT COUNT(*) AS jumlah_transaksi, SUM(total_transaksi_jual) AS total_omzet,\n" +
            "AVG(total_transaksi_jual) AS rata_rata_pembelian\n" +
            "FROM transaksi_jual\n" +
            "WHERE DATE(tanggal_transaksi_jual) = CURDATE()";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                lblJumlahTransaksi.setText(result.getString("jumlah_transaksi"));
                lblTotalOmzet.setText(Session.convertIntToRupiah(result.getInt("total_omzet")));
                lblRataRataPembelian.setText(Session.convertIntToRupiah(result.getInt("rata_rata_pembelian")));
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void setGrafikPenjualan(){
        XYChart.Series<String, Number> dataPenjualan = new XYChart.Series<>();
        dataPenjualan.setName("penjualan");
        
        try {
            String query = getQueryBerdasarkanSortBy(cbxSortByTotalPenjualan.getValue());
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            ResultSet result = statement.executeQuery();
            
            while(result.next()) {
                String periode = cbxSortByTotalPenjualan.getValue().equals("Bulan") ? 
                        Session.getBulan(result.getInt("periode")) : result.getString("periode");
                int totalPenjualan = result.getInt("total_penjualan");
                dataPenjualan.getData().add(new XYChart.Data<>(periode, totalPenjualan));
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        chartPenjualan.getData().setAll(dataPenjualan);
    }
    
    private void setGrafikBarangTerlaris() {
        XYChart.Series<String, Number> dataBarangTerlaris = new XYChart.Series<>();
        dataBarangTerlaris.setName("Barang Terlaris");

        try {
            String query = "SELECT brg.nama_barang AS nama_barang, SUM(dtj.jumlah_barang) AS jumlah_barang \n" +
            "FROM detail_transaksi_jual dtj\n" +
            "JOIN barang brg ON brg.id_barang = dtj.id_barang\n" +
            "GROUP BY brg.nama_barang\n" +
            "ORDER BY dtj.jumlah_barang DESC";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String namaBarang = result.getString("nama_barang");
                int totalTerjual = result.getInt("jumlah_barang");

                dataBarangTerlaris.getData().add(new XYChart.Data<>(namaBarang, totalTerjual));
            }

            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        chartBarangTerlaris.getData().setAll(dataBarangTerlaris);
    }
    
    private void setCbxDanChart(){
        cbxSortByTotalPenjualan.getItems().addAll("Tanggal", "Bulan", "Tahun");
        cbxSortByTotalPenjualan.setValue("Tanggal");
        cbxSortByTotalPenjualan.setOnAction((ActionEvent event) -> {
            setGrafikPenjualan();
        });
        
        axisTotalPenjualan.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
            @Override
            public String toString(Number value) {
                return Session.convertIntToRupiah(value.intValue());
            }

            @Override
            public Number fromString(String string) {
                return null; // Gak perlu dari string, jadi return null aja
            }
        });
        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : chartBarangTerlaris.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        Tooltip tooltip = new Tooltip("Terjual: " + data.getYValue());

                        // Atur biar tooltip langsung muncul tanpa delay
                        tooltip.setShowDelay(Duration.ZERO);
                        tooltip.setHideDelay(Duration.ZERO);
                        tooltip.setShowDuration(Duration.INDEFINITE); // Biar nggak ilang sendiri

                        Tooltip.install(data.getNode(), tooltip);
                    }
                }
            }
        });
        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : chartPenjualan.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        Tooltip tooltip = new Tooltip("Total: " + Session.convertIntToRupiah(data.getYValue().intValue()));

                        // Atur biar tooltip langsung muncul tanpa delay
                        tooltip.setShowDelay(Duration.ZERO);
                        tooltip.setHideDelay(Duration.ZERO);
                        tooltip.setShowDuration(Duration.INDEFINITE); // Biar nggak ilang sendiri

                        Tooltip.install(data.getNode(), tooltip);
                    }
                }
            }
        });
        //scrollPaneChartPenjualan.setContent(chartPenjualan);
        //scrollPaneChartPenjualan.setFitToWidth(false);
        //scrollPaneChartPenjualan.setFitToHeight(true); 
        axisPeriode.setTickLabelRotation(45);
        axisPeriode.setStyle("-fx-tick-label-font-size: 10px;");
        axisNamaBarang.setTickLabelRotation(45);
    }
    
    private String getQueryBerdasarkanSortBy(String period) {
        String query = "";
        switch (period) {
            case "Tanggal":
                query = "SELECT DATE(tanggal_transaksi_jual) AS periode,\n" +
                "SUM(total_transaksi_jual) AS total_penjualan\n" +
                "FROM transaksi_jual\n" +
                "WHERE MONTH(tanggal_transaksi_jual) = MONTH(CURDATE())\n" +
                "GROUP BY DATE(tanggal_transaksi_jual)\n" +
                "ORDER BY DATE(tanggal_transaksi_jual) ASC";
                break;
            case "Bulan":
                query = "SELECT MONTH(tanggal_transaksi_jual) AS periode, SUM(total_transaksi_jual) AS total_penjualan " +
                        "FROM transaksi_jual GROUP BY MONTH(tanggal_transaksi_jual) ORDER BY MONTH(tanggal_transaksi_jual)";
                break;
            case "Tahun":
                query = "SELECT YEAR(tanggal_transaksi_jual) AS periode, SUM(total_transaksi_jual) AS total_penjualan " +
                        "FROM transaksi_jual GROUP BY YEAR(tanggal_transaksi_jual) ORDER BY YEAR(tanggal_transaksi_jual)";
                break;
        }
        return query;
    }
}