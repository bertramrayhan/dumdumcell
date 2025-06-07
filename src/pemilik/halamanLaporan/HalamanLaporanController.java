package pemilik.halamanLaporan;

import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import main.Koneksi;
import main.Pelengkap;
import main.Session;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.view.JasperViewer;

public class HalamanLaporanController implements Initializable, Pelengkap {

    @FXML private Button btnPDF, btnEXCEL, btnREFRESH;
    @FXML private DatePicker dtPTanggalAwal, dtPTanggalAkhir;
    @FXML private Label txtLaporan;
    
    private String jenisLaporan = "Laporan Pembelian";
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setKomponen();
    }    
    
    private void setKomponen(){
        LocalDate hariIni = LocalDate.now();
        dtPTanggalAwal.getEditor().setDisable(true);
        dtPTanggalAwal.getEditor().setOpacity(1);
        dtPTanggalAkhir.getEditor().setDisable(true);
        dtPTanggalAkhir.getEditor().setOpacity(1);

        // Listener untuk dtPTanggalAwal
        dtPTanggalAwal.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dtPTanggalAkhir.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        boolean isDisabled = empty || item.isBefore(newDate) || item.isAfter(hariIni);
                        setDisable(isDisabled);
                        if (isDisabled) {
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                });
            }
        });

        // Listener untuk dtPTanggalAkhir
        dtPTanggalAkhir.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dtPTanggalAwal.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        boolean isDisabled = empty || item.isAfter(newDate) || item.isAfter(hariIni);
                        setDisable(isDisabled);
                        if (isDisabled) {
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                });
            }
        });

        dtPTanggalAwal.setValue(hariIni);
        dtPTanggalAkhir.setValue(hariIni);
    }

    @Override
    public void perbarui() {
        dtPTanggalAwal.setValue(LocalDate.now());
        dtPTanggalAkhir.setValue(LocalDate.now());
    }    

    @FXML
    private void gantiKeLaporanPembelian(){
        txtLaporan.setText("Laporan Pembelian");
        jenisLaporan = txtLaporan.getText();
    }

    @FXML
    private void gantiKeLaporanPenjualan(){
        txtLaporan.setText("Laporan Penjualan");
        jenisLaporan = txtLaporan.getText();
    }

    @FXML
    private void gantiKeLaporanKartuStok(){
        txtLaporan.setText("Laporan Kartu Stok");
        jenisLaporan = txtLaporan.getText();
    }
    
    @FXML
    private void handleBtnPDF(MouseEvent event) {
        try {
            Locale.setDefault(new Locale("id", "ID"));

            SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

            String tglAwal = dtPTanggalAwal.getValue().toString();
            String tglAkhir = dtPTanggalAkhir.getValue().toString();
            String pathReport = "";
            String logoPath = "/assets/logo/Logomark.png"; 
            
            if (jenisLaporan.equals("Laporan Pembelian")) {
                pathReport = "/main/jasperReport/laporanPembelian.jasper";
            } else if (jenisLaporan.equals("Laporan Penjualan")) {
                pathReport = "/main/jasperReport/laporanPenjualan.jasper";
            } else {
                pathReport = "/main/jasperReport/laporanKartuStok.jasper";
            }

            String adminId = Session.getIdAdmin();
            String timeStamp = fileFormat.format(new Date());

            // Cek data kosong dulu
            Connection con = Koneksi.getCon();
            String sql = "";
            if (jenisLaporan.equals("Laporan Pembelian")) {
                sql = "SELECT COUNT(*) FROM ddckasir.transaksi_beli tb "
                        + "JOIN ddckasir.detail_transaksi_beli dtb ON dtb.id_transaksi_beli = tb.id_transaksi_beli "
                        + "JOIN ddckasir.barang b ON dtb.id_barang = b.id_barang "
                        + "JOIN ddckasir.supplier s ON tb.id_supplier = s.id_supplier "
                        + "JOIN ddckasir.admin a ON tb.id_admin = a.id_admin "
                        + "WHERE DATE(tb.tanggal_transaksi_beli) BETWEEN ? AND ?";
            } else if (jenisLaporan.equals("Laporan Penjualan")) {
                sql = "SELECT COUNT(*) FROM ddckasir.transaksi_jual tj "
                        + "JOIN ddckasir.detail_transaksi_jual dtj ON dtj.id_transaksi_jual = tj.id_transaksi_jual "
                        + "JOIN ddckasir.barang b ON dtj.id_barang = b.id_barang "
                        + "JOIN ddckasir.admin a ON tj.id_admin = a.id_admin "
                        + "WHERE DATE(tj.tanggal_transaksi_jual) BETWEEN ? AND ?";
            } else {
                sql = "SELECT COUNT(*) FROM ddckasir.kartu_stok WHERE DATE(tanggal) BETWEEN ? AND ?";
            }

            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = con.prepareStatement(sql);
                ps.setString(1, tglAwal);
                ps.setString(2, tglAkhir);
                rs = ps.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count == 0) {
                        Session.animasiPanePesan(true, "Data laporan kosong, tidak ada yang bisa diekspor.", btnPDF, btnEXCEL, btnREFRESH);
                        return;
                    }
                }
            } finally {
                if (rs != null) try { rs.close(); } catch (Exception ex) {}
                if (ps != null) try { ps.close(); } catch (Exception ex) {}
            }

            InputStream logoStream = HalamanLaporanController.class.getResourceAsStream(logoPath);
            Map<String, Object> params = new HashMap<>();
            params.put("tanggalAwal", tglAwal);
            params.put("tanggalAkhir", tglAkhir);
            params.put("admin", adminId);
            params.put("logoPath", logoStream);

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss 'WIB'", new Locale("id", "ID"));
            params.put("printTime", sdf.format(new Date()));

            InputStream reportStream = HalamanLaporanController.class.getResourceAsStream(pathReport);
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, params, con);

            String userHome = System.getProperty("user.home");
            String outputPath = userHome + "/Documents/" + jenisLaporan + "_" + timeStamp + ".pdf";

            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            JasperViewer.viewReport(jasperPrint, false);

            Session.animasiPanePesan(false, "PDF berhasil disimpan di: " + outputPath, btnPDF, btnEXCEL, btnREFRESH);
            
            logoStream.close();
            reportStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Gagal generate PDF");
        }
    }

    @FXML
    private void handleBtnEXCEL(MouseEvent event) {
        try {
            Locale.setDefault(new Locale("id", "ID"));

            SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

            String tglAwal = dtPTanggalAwal.getValue().toString();
            String tglAkhir = dtPTanggalAkhir.getValue().toString();
            String pathReport = "";
            String logoPath = "/assets/logo/Logomark.png"; 
            
            if (jenisLaporan.equals("Laporan Pembelian")) {
                pathReport = "/main/jasperReport/laporanPembelian.jasper";
            } else if (jenisLaporan.equals("Laporan Penjualan")) {
                pathReport = "/main/jasperReport/laporanPenjualan.jasper";
            } else {
                pathReport = "/main/jasperReport/laporanKartuStok.jasper";
            }

            String adminId = Session.getIdAdmin();
            String timeStamp = fileFormat.format(new Date());

            Connection con = Koneksi.getCon();
            String sql = "";
            if (jenisLaporan.equals("Laporan Pembelian")) {
                sql = "SELECT COUNT(*) FROM ddckasir.transaksi_beli tb "
                    + "JOIN ddckasir.detail_transaksi_beli dtb ON dtb.id_transaksi_beli = tb.id_transaksi_beli "
                    + "JOIN ddckasir.barang b ON dtb.id_barang = b.id_barang "
                    + "JOIN ddckasir.supplier s ON tb.id_supplier = s.id_supplier "
                    + "JOIN ddckasir.admin a ON tb.id_admin = a.id_admin "
                    + "WHERE DATE(tb.tanggal_transaksi_beli) BETWEEN ? AND ?";
            } else if (jenisLaporan.equals("Laporan Penjualan")) {
                sql = "SELECT COUNT(*) FROM ddckasir.transaksi_jual tj "
                    + "JOIN ddckasir.detail_transaksi_jual dtj ON dtj.id_transaksi_jual = tj.id_transaksi_jual "
                    + "JOIN ddckasir.barang b ON dtj.id_barang = b.id_barang "
                    + "JOIN ddckasir.admin a ON tj.id_admin = a.id_admin "
                    + "WHERE DATE(tj.tanggal_transaksi_jual) BETWEEN ? AND ?";
            } else {
                sql = "SELECT COUNT(*) FROM ddckasir.kartu_stok WHERE DATE(tanggal) BETWEEN ? AND ?";
            }

            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = con.prepareStatement(sql);
                ps.setString(1, tglAwal);
                ps.setString(2, tglAkhir);
                rs = ps.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count == 0) {
                        Session.animasiPanePesan(true, "Data laporan kosong, tidak ada yang bisa diekspor.", btnPDF, btnEXCEL, btnREFRESH);
                        return;
                    }
                }
            } finally {
                if (rs != null) try { rs.close(); } catch (Exception ex) {}
                if (ps != null) try { ps.close(); } catch (Exception ex) {}
            }

            InputStream logoStream = HalamanLaporanController.class.getResourceAsStream(logoPath);
            Map<String, Object> params = new HashMap<>();
            params.put("tanggalAwal", tglAwal);
            params.put("tanggalAkhir", tglAkhir);
            params.put("admin", adminId);
            params.put("logoPath", logoStream);

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss 'WIB'", new Locale("id", "ID"));
            params.put("printTime", sdf.format(new Date()));

            InputStream reportStream = getClass().getResourceAsStream(pathReport);
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, params, con);

            String userHome = System.getProperty("user.home");
            String outputPath = userHome + "/Documents/" + jenisLaporan + "_" + timeStamp + ".xlsx";

            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputPath));

            SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
            config.setDetectCellType(true);
            config.setOnePagePerSheet(false);
            config.setCollapseRowSpan(false);
            exporter.setConfiguration(config);

            exporter.exportReport();

            Session.animasiPanePesan(false, "Excel berhasil disimpan di: " + outputPath, btnPDF, btnEXCEL, btnREFRESH);

            File excelFile = new File(outputPath);
            if (excelFile.exists()) {
                Desktop.getDesktop().open(excelFile);
            }
            
            logoStream.close();
            reportStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Gagal generate Excel");
        }
    }
}