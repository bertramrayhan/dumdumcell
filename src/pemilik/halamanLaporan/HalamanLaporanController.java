package pemilik.halamanLaporan;

import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
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

    @FXML private Pane paneLaporanPembelian, paneLaporanPenjualan, paneLaporanStok;
    @FXML private DatePicker dtPTanggalAwal, dtPTanggalAkhir, dtPTanggalAwalPenjualan, dtPTanggalAkhirPenjualan;
    @FXML private Button btnPDF, btnEXCEL, btnREFRESH, btnPembelian, btnPenjualan, btnStokbarang;

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

            LocalDate tglAwal = null;
            LocalDate tglAkhir = null;
            String pathReport = "";
            String outputFilename = "";

            if (paneLaporanPembelian.isVisible()) {
                tglAwal = dtPTanggalAwal.getValue();
                tglAkhir = dtPTanggalAkhir.getValue();
                pathReport = "src/main/jasperReport/laporanPembelian.jasper";
                outputFilename = "laporan_pembelian.pdf";
            } else if (paneLaporanPenjualan.isVisible()) {
                tglAwal = dtPTanggalAwalPenjualan.getValue();
                tglAkhir = dtPTanggalAkhirPenjualan.getValue();
                pathReport = "src/main/jasperReport/laporanPenjualan.jasper";
                outputFilename = "laporan_penjualan.pdf";
            }

            if (tglAwal == null || tglAkhir == null) {
                Session.animasiPanePesan(true, "Silakan pilih tanggal awal dan akhir terlebih dahulu.");
                return;
            }

            String strTglAwal = tglAwal.format(formatter);
            String strTglAkhir = tglAkhir.format(formatter);

            Map<String, Object> params = new HashMap<>();
            params.put("tanggalAwal", strTglAwal);
            params.put("tanggalAkhir", strTglAkhir);
            params.put("admin", Session.getIdAdmin());
            
             // Format waktu cetak di Java jadi String
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss 'WIB'", new Locale("id", "ID"));
            String formattedPrintTime = sdf.format(new Date());
            params.put("printTime", formattedPrintTime);

            JasperPrint jasperPrint = JasperFillManager.fillReport(pathReport, params, Koneksi.getCon());
            JasperViewer.viewReport(jasperPrint, false);

            String userHome = System.getProperty("user.home");
            String outputPath = userHome + "/Documents/" + outputFilename;
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);

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

        LocalDate tglAwal = null;
        LocalDate tglAkhir = null;
        String pathReport = "";
        String outputFilename = "";

        if (paneLaporanPembelian.isVisible()) {
            tglAwal = dtPTanggalAwal.getValue();
            tglAkhir = dtPTanggalAkhir.getValue();
            pathReport = "src/main/jasperReport/laporanPembelian.jasper";
            outputFilename = "laporan_pembelian.xlsx";
        } else if (paneLaporanPenjualan.isVisible()) {
            tglAwal = dtPTanggalAwalPenjualan.getValue();
            tglAkhir = dtPTanggalAkhirPenjualan.getValue();
            pathReport = "src/main/jasperReport/laporanPenjualan.jasper";
            outputFilename = "laporan_penjualan.xlsx";
        }

        if (tglAwal == null || tglAkhir == null) {
            Session.animasiPanePesan(true, "Silakan pilih tanggal awal dan akhir terlebih dahulu.");
            return;
        }

        String strTglAwal = tglAwal.format(formatter);
        String strTglAkhir = tglAkhir.format(formatter);
        
        Map<String, Object> params = new HashMap<>();
        params.put("tanggalAwal", strTglAwal);
        params.put("tanggalAkhir", strTglAkhir);
        params.put("admin", Session.getIdAdmin());
        
         // Format waktu cetak di Java jadi String
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss 'WIB'", new Locale("id", "ID"));
        String formattedPrintTime = sdf.format(new Date());
        params.put("printTime", formattedPrintTime);

        JasperPrint jasperPrint = JasperFillManager.fillReport(pathReport, params, Koneksi.getCon());

        String userHome = System.getProperty("user.home");
        String outputPath = userHome + "/Documents/" + outputFilename;

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