
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package pemilik.halamanKartuStok;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import main.Koneksi;

/**
 * FXML Controller class
 *
 * @author indra
 */
public class HalamanKartuStokPController implements Initializable {

    @FXML private Pane paneGelap;
    @FXML private TableView<KartuStok> tableKartuStok;
    @FXML private TableColumn<KartuStok, String> colIdbarang, colNamabarang, colKet, colTgl;
    @FXML private TableColumn<KartuStok, Integer> colMasuk, colKeluar, colSisa;
    @FXML private ChoiceBox<String> cbxBarang;
    @FXML private DatePicker dPtglAwal;
    @FXML private DatePicker dPtglAkhir;
    @FXML private TextField txtSearch;
    @FXML private Button btnX;
    
    private ObservableList<KartuStok> dataKartuStok = FXCollections.observableArrayList();
    private Map<String, String> mapNamaKeId = new HashMap<>();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTableKartuStok();
        getinfoTableKartuStok();
        loadChoiceBoxBarang();  // ini harus dipanggil biar ChoiceBox terisi
        
        dPtglAwal.getEditor().setDisable(true);
        dPtglAwal.getEditor().setOpacity(1);
        dPtglAwal.setValue(LocalDate.now());

        dPtglAkhir.getEditor().setDisable(true);
        dPtglAkhir.getEditor().setOpacity(1);
        dPtglAkhir.setValue(LocalDate.now());
        
        btnX.setOnAction((ActionEvent event) -> {
            txtSearch.clear(); // Hapus teks pencarian
        });
        
        // Pasang listener supaya reload data tiap filter berubah
        cbxBarang.setOnAction(e -> getinfoTableKartuStok());
        dPtglAwal.valueProperty().addListener((obs, oldV, newV) -> getinfoTableKartuStok());
        dPtglAkhir.valueProperty().addListener((obs, oldV, newV) -> getinfoTableKartuStok());
        txtSearch.textProperty().addListener((obs, oldV, newV) -> getinfoTableKartuStok());
    }    
    private void setTableKartuStok(){
        colIdbarang.setCellValueFactory(new PropertyValueFactory<>("idBarang"));
        colNamabarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colTgl.setCellValueFactory(new PropertyValueFactory<>("tgl"));
        colMasuk.setCellValueFactory(new PropertyValueFactory<>("masuk"));
        colKeluar.setCellValueFactory(new PropertyValueFactory<>("keluar"));
        colSisa.setCellValueFactory(new PropertyValueFactory<>("sisa"));
        colKet.setCellValueFactory(new PropertyValueFactory<>("ket"));
    }
    
      private void loadChoiceBoxBarang() {
        try {
            cbxBarang.getItems().clear();
            cbxBarang.getItems().add("Semua"); // opsi default
            mapNamaKeId.clear();

            String query = "SELECT id_barang, nama_barang FROM barang ORDER BY nama_barang";
            PreparedStatement ps = Koneksi.getCon().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String idBarang = rs.getString("id_barang");
                String namaBarang = rs.getString("nama_barang");
                
                // Simpan mapping
                mapNamaKeId.put(namaBarang, idBarang);
                // Isi ChoiceBox dengan nama barang
                cbxBarang.getItems().add(namaBarang);
            }
            rs.close();
            ps.close();

            cbxBarang.setValue("Semua"); // default pilih Semua
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String convertTanggalIndo(String tglFull) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(tglFull, formatter);
            LocalDate date = dateTime.toLocalDate();

            DateTimeFormatter indoFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return date.format(indoFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return tglFull;
        }
}
    private void getinfoTableKartuStok(){
         dataKartuStok.clear();
        try {
            String baseQuery = "SELECT ks.id_barang, b.nama_barang, ks.tanggal, ks.jumlah_masuk, ks.jumlah_keluar, ks.sisa, ks.keterangan " +
                               "FROM kartu_stok ks JOIN barang b ON ks.id_barang = b.id_barang WHERE 1=1 ";

            if (cbxBarang.getValue() != null && !cbxBarang.getValue().equals("Semua")) {
                baseQuery += "AND ks.id_barang = ? ";
            }
            if (dPtglAwal.getValue() != null && dPtglAkhir.getValue() != null) {
                baseQuery += "AND ks.tanggal BETWEEN ? AND ? ";
            }
            if (!txtSearch.getText().isEmpty()) {
                baseQuery += "AND (b.nama_barang LIKE ? OR ks.keterangan LIKE ?) ";
            }

            PreparedStatement statement = Koneksi.getCon().prepareStatement(baseQuery);

            int paramIndex = 1;
            if (cbxBarang.getValue() != null && !cbxBarang.getValue().equals("Semua")) {
                // Ambil id barang dari nama barang
                String idBarang = mapNamaKeId.get(cbxBarang.getValue());
                statement.setString(paramIndex++, idBarang);
            }
            if (dPtglAwal.getValue() != null && dPtglAkhir.getValue() != null) {
                statement.setString(paramIndex++, dPtglAwal.getValue().toString() + " 00:00:00");
                statement.setString(paramIndex++, dPtglAkhir.getValue().toString() + " 23:59:59");
            }
            if (!txtSearch.getText().isEmpty()) {
                String keyword = "%" + txtSearch.getText() + "%";
                statement.setString(paramIndex++, keyword);
                statement.setString(paramIndex++, keyword);
            }

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String idBarang = result.getString("id_barang");
                String namaBarang = result.getString("nama_barang");
                String tgl = convertTanggalIndo(result.getString("tanggal"));
                int masuk = result.getInt("jumlah_masuk");
                int keluar = result.getInt("jumlah_keluar");
                int sisa = result.getInt("sisa");
                String ket = result.getString("keterangan");

                dataKartuStok.add(new KartuStok(idBarang, namaBarang, tgl, masuk, keluar, sisa, ket));
            }

            result.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableKartuStok.setItems(dataKartuStok);
    }
}
    
