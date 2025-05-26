package pemilik.halamanLaporan;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import main.Koneksi;
import main.Session;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class HalamanLaporanController implements Initializable {

    @FXML private DatePicker dtPTanggalAwal, dtPTanggalAkhir;
    @FXML private Button btnPDF, btnEXCEL, btnREFRESH, btnPembelian, btnPenjualan, btnStokbarang;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dtPTanggalAwal.getEditor().setDisable(true);
        dtPTanggalAwal.getEditor().setOpacity(1);
        
        dtPTanggalAkhir.getEditor().setDisable(true);
        dtPTanggalAkhir.getEditor().setOpacity(1);

        dtPTanggalAwal.setValue(LocalDate.now());
        dtPTanggalAkhir.setValue(LocalDate.now());
    }    
    
    @FXML
    private void handleBtnPDF(MouseEvent event) {
        try {
            // Ambil tanggal dari DatePicker
            LocalDate tglAwal = dtPTanggalAwal.getValue();
            LocalDate tglAkhir = dtPTanggalAkhir.getValue();

            if (tglAwal == null || tglAkhir == null) {
                Session.animasiPanePesan(true, "Silakan pilih tanggal awal dan akhir terlebih dahulu.");
                return;
            }

            // Format tanggal ke String
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String strTglAwal = tglAwal.format(formatter);
            String strTglAkhir = tglAkhir.format(formatter);

            // Parameter laporan
            Map<String, Object> params = new HashMap<>();
            params.put("tanggalAwal", strTglAwal);
            params.put("tanggalAkhir", strTglAkhir);
            params.put("admin", Session.getIdAdmin());

            String pathReport = "src/main//jasperReport/laporanPembelian.jasper";
            JasperPrint jasperPrint = JasperFillManager.fillReport(pathReport, params, Koneksi.getCon());

            // Tampilkan preview
            JasperViewer.viewReport(jasperPrint, false);

            // Path ke Downloads
            String userHome = System.getProperty("user.home");
            String outputPath = userHome + "/Documents/laporan_pembelian.pdf";

            // Simpan PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);

            System.out.println("PDF berhasil disimpan di: " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Gagal generate PDF");
        }
    }
}