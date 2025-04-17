package karyawan.halamanJual;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.Koneksi;
import main.Session;

public class DetailController implements Initializable {
    private Stage detailTransaksiStage;
    
    @FXML Label btnTutup;
    @FXML TableView tabelDetailTransaksi;
    @FXML TableColumn<DetailTransaksi, String> colNamaBarang, colJumlahBarang, colSubtotal;
    @FXML TextArea txtAreaCatatan;
    
    ObservableList<DetailTransaksi> listDetailTransaksi = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTabelDetailTransaksi();
        getDetailTransaksi();
    }    
    
    private void getDetailTransaksi(){
        try {
            String query = "SELECT brg.merek AS nama_barang, dtj.jumlah_barang AS jumlah_barang, "
            + "dtj.subtotal AS subtotal, tj.catatan AS catatan\n" +
            "FROM detail_transaksi_jual dtj\n" +
            "JOIN barang brg ON brg.id_barang = dtj.id_barang\n" +
            "JOIN transaksi_jual tj ON tj.id_transaksi_jual = dtj.id_transaksi_jual\n" +
            "WHERE dtj.id_transaksi_jual = ?"
            + "ORDER BY dtj.jumlah_barang ASC";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, HalamanJualKController.idTransaksiTerpilih);
            ResultSet result = statement.executeQuery();
                        
            if (result.next()) {
                String catatan = result.getString("catatan");
                txtAreaCatatan.setText(catatan);

                // Tambahkan detail transaksi ke dalam list
                do {
                    String namaBarang = result.getString("nama_barang");
                    String jumlahBarang = result.getString("jumlah_barang");
                    String subtotal = Session.convertIntToRupiah(result.getInt("subtotal"));

                    DetailTransaksi detailTransaksi = new DetailTransaksi(namaBarang, jumlahBarang, subtotal);
                    listDetailTransaksi.add(detailTransaksi);
                } while (result.next());
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        tabelDetailTransaksi.setItems(listDetailTransaksi);
    }
    
    public void setDetailTransaksiStage(Stage detailTransaksiStage) {
        this.detailTransaksiStage = detailTransaksiStage;
    }
    
    @FXML
    private void tutupDetailTransaksi(){
        detailTransaksiStage.close();
    }
    
    public class DetailTransaksi{
        String namaBarang, jumlahBarang, subtotal;

        public DetailTransaksi(String namaBarang, String jumlahBarang, String subtotal) {
            this.namaBarang = namaBarang;
            this.jumlahBarang = jumlahBarang;
            this.subtotal = subtotal;
        }
        public String getNamaBarang() {return namaBarang;}
        public String getJumlahBarang() {return jumlahBarang;}
        public String getSubtotal() {return subtotal;}
    }
    
    private void setTabelDetailTransaksi(){
        colNamaBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colJumlahBarang.setCellValueFactory(new PropertyValueFactory<>("JumlahBarang"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }
}
