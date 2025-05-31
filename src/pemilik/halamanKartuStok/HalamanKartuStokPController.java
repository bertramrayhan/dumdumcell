
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package pemilik.halamanKartuStok;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import main.Koneksi;
import main.Session;

/**
 * FXML Controller class
 *
 * @author indra
 */
public class HalamanKartuStokPController implements Initializable {

      @FXML private Pane paneGelap;
    @FXML private TableView<KartuStok> tableKartuStok;
    @FXML private TableColumn<KartuStok, String> colIdbarang, colNamabarang, colKet, colAdmin, colTgl;
    @FXML private TableColumn<KartuStok, Integer> colMasuk, colKeluar, colSisa;
    @FXML private ChoiceBox<String> cbxBarang;
    @FXML private DatePicker dPtglAwal;
    @FXML private DatePicker dPtglAkhir;
    @FXML private TextField txtSearch;
    
    private ObservableList<KartuStok> dataKartuStok = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
    
    private void getinfoTableKartuStok(){
        dataKartuStok.clear();

        try {
            
            String query = "SELECT ks.id_barang, b.nama_barang, ks.tanggal, ks.jumlah_masuk, ks.jumlah_keluar, ks.sisa, ks.keterangan " +
                       "FROM kartu_stok ks " +
                       "JOIN barang b ON ks.id_barang = b.id_barang"; // ganti sesuai nama tabelmu
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
               String idBarang = result.getString("id_barang");
               String namaBarang = result.getString("nama_barang");
               String tgl = Session.convertTanggalIndo(result.getString("tanggal"));
               Integer masuk = result.getInt("masuk");
               Integer keluar = result.getInt("keluar");
               Integer sisa = result.getInt("sisa");
               String ket = result.getString("keterangan");
                              
               dataKartuStok.add(new KartuStok(idBarang, namaBarang,tgl, masuk, keluar, sisa, ket ));
            }

            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Tambah alert kalau mau tampilkan error ke user
        }
         tableKartuStok.setItems(dataKartuStok);
    }
}
    
