/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package karyawan.halamanStok;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.Koneksi;
import main.Session;

/**
 * FXML Controller class
 *
 * @author indra
 */
public class HalamanStokKController implements Initializable {
    @FXML TextField txtSearchBar;
    @FXML Button buttonX;
    @FXML ChoiceBox sortBy;
    @FXML TableView tableStok;
    @FXML TableColumn<Stok, String>  colNamaBarang, colKategori, colHargaJual, colTipe, colMerek, colExp;
    @FXML TableColumn<Stok, Integer> colStok;
    static ObservableList<Stok> listStok = FXCollections.observableArrayList();
    
    
    public void initialize(URL url, ResourceBundle rb) {
        settableStok();
        
    }    
    private void settableStok(){
        colNamaBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colHargaJual.setCellValueFactory(new PropertyValueFactory<>("hargaJual"));
        colTipe.setCellValueFactory(new PropertyValueFactory<>("tipe"));
        colMerek.setCellValueFactory(new PropertyValueFactory<>("merek"));
        colExp.setCellValueFactory(new PropertyValueFactory<>("exp"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
        listStok.clear();
            try {
                String query = "SELECT b b.nama_barang, b.harga_jual, b.stok, b.tipe, b.exp, b.merek, k.nama_kategori " +
                   "FROM barang b " +
                   "JOIN kategori k ON b.id_kategori = k.id_kategori";

             
                PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
                ResultSet result = statement.executeQuery();
            
            while(result.next()){
                String namaBarang = result.getString("nama_barang");
                String kategori = result.getString("nama_kategori");
                String hargaJual = Session.convertIntToRupiah(result.getInt("harga_jual"));
                String tipe = result.getString("tipe");
                int stok = result.getInt("stok");
                String exp = Session.convertTanggalIndo(result.getString("exp"));
                String merek = result.getString("merek");
                

                listStok.add(new Stok(namaBarang, kategori, merek, tipe, exp, hargaJual, stok));
                
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableStok.setItems(listStok);
    }
    
}
