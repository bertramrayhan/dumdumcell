/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package pemilik.halamanLaporan;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.Koneksi;
import main.Session;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 * FXML Controller class
 *
 * @author indra
 */
public class HalamanLaporanController implements Initializable {

    @FXML
    private DatePicker CxTglawal;
    @FXML
    private DatePicker CxTglakhir;
    @FXML
    private Button btnPDF;
    @FXML
    private Button btnEXCEL;
    @FXML
    private Button btnREFRESH;
    @FXML
    private Button btnPembelian;
    @FXML
    private Button btnPenjualan;
    @FXML
    private Button btnStokbarang;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

   @FXML
private void handleBtnPDF(MouseEvent event) {
       try {
        // Ambil tanggal dari DatePicker
        LocalDate tglAwal = CxTglawal.getValue();
        LocalDate tglAkhir = CxTglakhir.getValue();

        if (tglAwal == null || tglAkhir == null) {
            showAlert("Silakan pilih tanggal awal dan akhir terlebih dahulu.");
            return;
        }

        // Format tanggal ke String dengan pattern sesuai default iReport (dd-MM-yyyy)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String strTglAwal = tglAwal.format(formatter);
        String strTglAkhir = tglAkhir.format(formatter);

        // Parameter untuk laporan (nama param harus sesuai di iReport)
        Map<String, Object> params = new HashMap<>();
        params.put("tanggalAwal", strTglAwal);
        params.put("tanggalAkhir", strTglAkhir);
        // Contoh parameter tambahan
        params.put("admin", Session.getIdAdmin());

        // Path file .jasper hasil compile iReport
        String jasperPath = "D:/GitHub/ddckasir/ddckasir2/dumdumcell/src/main/jasperReport/laporanPembelian.jasper";

        // Koneksi database
        Connection conn = Koneksi.getCon();

        // Load report
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(jasperPath));

        // Fill report dengan parameter dan koneksi DB
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

        // Preview laporan (window Swing)
        JasperViewer.viewReport(jasperPrint, false);

        // Simpan ke PDF (pastikan foldernya sudah ada)
        JasperExportManager.exportReportToPdfFile(jasperPrint, "D:/laporan_pembelian.pdf");

        System.out.println("Laporan berhasil disimpan ke PDF.");

    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Gagal mencetak laporan: " + e.getMessage());
    }
}
private void showAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("Peringatan");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
}
