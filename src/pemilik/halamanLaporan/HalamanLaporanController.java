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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import main.Koneksi;
import main.Session;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.view.JasperViewer;

public class HalamanLaporanController implements Initializable {

    @FXML private Pane paneLaporanPembelian, paneLaporanPenjualan;
    @FXML private DatePicker dtPTanggalAwal, dtPTanggalAkhir, dtPTanggalAwalPenjualan, dtPTanggalAkhirPenjualan;
    @FXML private Button btnPDF, btnEXCEL, btnREFRESH, btnPembelian, btnPenjualan;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dtPTanggalAwal.getEditor().setDisable(true);
        dtPTanggalAwal.getEditor().setOpacity(1);
        
        dtPTanggalAkhir.getEditor().setDisable(true);
        dtPTanggalAkhir.getEditor().setOpacity(1);

        dtPTanggalAwal.setValue(LocalDate.now());
        dtPTanggalAkhir.setValue(LocalDate.now());
        
        // Inisialisasi DatePicker Penjualan
        dtPTanggalAwalPenjualan.getEditor().setDisable(true);
        dtPTanggalAwalPenjualan.getEditor().setOpacity(1);
        dtPTanggalAwalPenjualan.setValue(LocalDate.now());

        dtPTanggalAkhirPenjualan.getEditor().setDisable(true);
        dtPTanggalAkhirPenjualan.getEditor().setOpacity(1);
        dtPTanggalAkhirPenjualan.setValue(LocalDate.now());
    }    
    
    @FXML
private void handleBtnPDF(MouseEvent event) {
    try {
        Locale.setDefault(new Locale("id", "ID"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

        LocalDate tglAwal = null;
        LocalDate tglAkhir = null;
        String pathReport = "";
        String jenisLaporan = "";

        if (paneLaporanPembelian.isVisible()) {
            tglAwal = dtPTanggalAwal.getValue();
            tglAkhir = dtPTanggalAkhir.getValue();
            pathReport = "/main/jasperReport/laporanPembelian.jasper";
            jenisLaporan = "laporan_pembelian";
        } else if (paneLaporanPenjualan.isVisible()) {
            tglAwal = dtPTanggalAwalPenjualan.getValue();
            tglAkhir = dtPTanggalAkhirPenjualan.getValue();
            pathReport = "/main/jasperReport/laporanPenjualan.jasper";
            jenisLaporan = "laporan_penjualan";
        }

        if (tglAwal == null || tglAkhir == null) {
            Session.animasiPanePesan(true, "Silakan pilih tanggal awal dan akhir terlebih dahulu.");
            return;
        }

        String strTglAwal = tglAwal.format(formatter);
        String strTglAkhir = tglAkhir.format(formatter);
        String adminId = Session.getIdAdmin();
        String timeStamp = fileFormat.format(new Date());
        
          // Cek data kosong dulu
        Connection con = Koneksi.getCon();
        String sql = "";
        if (jenisLaporan.equals("laporan_pembelian")) {
            sql = "SELECT COUNT(*) FROM laporan_pembelian WHERE tanggal_transaksi_beli BETWEEN ? AND ?";
        } else if (jenisLaporan.equals("laporan_penjualan")) {
            sql = "SELECT COUNT(*) FROM laporan_penjualan WHERE tanggal_transaksi_jual BETWEEN ? AND ?";
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, strTglAwal);
            ps.setString(2, strTglAkhir);
            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) {
                    Session.animasiPanePesan(true, "Data laporan kosong, tidak ada yang bisa diekspor.");
                    return;
                }
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ex) {}
            if (ps != null) try { ps.close(); } catch (Exception ex) {}
        }

        Map<String, Object> params = new HashMap<>();
        params.put("tanggalAwal", strTglAwal);
        params.put("tanggalAkhir", strTglAkhir);
        params.put("admin", adminId);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss 'WIB'", new Locale("id", "ID"));
        params.put("printTime", sdf.format(new Date()));

        InputStream reportStream = getClass().getResourceAsStream(pathReport);
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, params, Koneksi.getCon());

        String userHome = System.getProperty("user.home");
        String outputPath = userHome + "/Documents/" + jenisLaporan + "_" + adminId + "_" + timeStamp + ".pdf";

        JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
        JasperViewer.viewReport(jasperPrint, false);

        System.out.println("PDF berhasil disimpan di: " + outputPath);

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Gagal generate PDF");
    }
}
   @FXML
private void handleBtnEXCEL(MouseEvent event) {
    try {
        Locale.setDefault(new Locale("id", "ID"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

        LocalDate tglAwal = null;
        LocalDate tglAkhir = null;
        String pathReport = "";
        String jenisLaporan = "";

        if (paneLaporanPembelian.isVisible()) {
            tglAwal = dtPTanggalAwal.getValue();
            tglAkhir = dtPTanggalAkhir.getValue();
            pathReport = "/main/jasperReport/laporanPembelian.jasper";
            jenisLaporan = "laporan_pembelian";
        } else if (paneLaporanPenjualan.isVisible()) {
            tglAwal = dtPTanggalAwalPenjualan.getValue();
            tglAkhir = dtPTanggalAkhirPenjualan.getValue();
            pathReport = "/main/jasperReport/laporanPenjualan.jasper";
            jenisLaporan = "laporan_penjualan";
        }

        if (tglAwal == null || tglAkhir == null) {
            Session.animasiPanePesan(true, "Silakan pilih tanggal awal dan akhir terlebih dahulu.");
            return;
        }

        String strTglAwal = tglAwal.format(formatter);
        String strTglAkhir = tglAkhir.format(formatter);
        String adminId = Session.getIdAdmin();
        String timeStamp = fileFormat.format(new Date());
        
         // Cek data kosong dulu
        Connection con = Koneksi.getCon();
        String sql = "";
        if (jenisLaporan.equals("laporan_pembelian")) {
            sql = "SELECT COUNT(*) FROM laporan_pembelian WHERE tanggal_transaksi_beli BETWEEN ? AND ?";
        } else if (jenisLaporan.equals("laporan_penjualan")) {
            sql = "SELECT COUNT(*) FROM laporan_penjualan WHERE tanggal_transaksi_jual BETWEEN ? AND ?";
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, strTglAwal);
            ps.setString(2, strTglAkhir);
            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) {
                    Session.animasiPanePesan(true, "Data laporan kosong, tidak ada yang bisa diekspor.");
                    return;
                }
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ex) {}
            if (ps != null) try { ps.close(); } catch (Exception ex) {}
        }

        Map<String, Object> params = new HashMap<>();
        params.put("tanggalAwal", strTglAwal);
        params.put("tanggalAkhir", strTglAkhir);
        params.put("admin", adminId);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss 'WIB'", new Locale("id", "ID"));
        params.put("printTime", sdf.format(new Date()));

        InputStream reportStream = getClass().getResourceAsStream(pathReport);
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, params, Koneksi.getCon());

        String userHome = System.getProperty("user.home");
        String outputPath = userHome + "/Documents/" + jenisLaporan + "_" + adminId + "_" + timeStamp + ".xlsx";

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputPath));

        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
        config.setDetectCellType(true);
        config.setOnePagePerSheet(false);
        config.setCollapseRowSpan(false);
        exporter.setConfiguration(config);

        exporter.exportReport();

        System.out.println("Excel berhasil disimpan di: " + outputPath);

        File excelFile = new File(outputPath);
        if (excelFile.exists()) {
            Desktop.getDesktop().open(excelFile);
        }

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Gagal generate Excel");
    }
}
    @FXML
    private void bukaLaporanPenjualan(){
        Session.setShowPane(paneLaporanPenjualan);
        Session.setHidePane(paneLaporanPembelian);
    }
    @FXML
    private void bukaLaporanPembelian(){
        Session.setHidePane(paneLaporanPenjualan);
        Session.setShowPane(paneLaporanPembelian);
    }
}