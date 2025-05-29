package karyawan.halamanTransaksiAntarCabang;

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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import main.Koneksi;
import main.Session;

public class HalamanTransaksiAntarCabangKController implements Initializable {

    @FXML private ChoiceBox<String> cbxJenisTransaksi;
    @FXML private TextField txtNominal, txtSearchBar;
    @FXML private TextArea txtAKeterangan;
    @FXML private Button btnBatal, btnProses;
    @FXML private ImageView btnX;
    @FXML private Label lblTotalTransaksi;
    @FXML private TableView<Transaksi> tabelTransaksi;
    @FXML private TableColumn<Transaksi, String> colJenisTransaksi, colKaryawan, colTanggal, colNominal, colKeterangan;
    
    private ObservableList<Transaksi> listTransaksi = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setKomponen();
        setTabelTransaksi();
        getDataTabelTransaksi();
    }    
    
    public class Transaksi{
        String idTransaksi, jenisTransaksi, karyawan, tanggal, nominal, keterangan;

        public Transaksi(String idTransaksi, String jenisTransaksi, String karyawan, String tanggal, String nominal, String keterangan) {
            this.idTransaksi = idTransaksi;this.jenisTransaksi = jenisTransaksi;this.karyawan = karyawan;this.tanggal = tanggal;this.nominal = nominal;this.keterangan = keterangan;
        }
        public String getIdTransaksi() {return idTransaksi;}
        public String getJenisTransaksi() {return jenisTransaksi;}
        public String getKaryawan() {return karyawan;}
        public String getTanggal() {return tanggal;}
        public String getNominal() {return nominal;}
        public String getKeterangan() {return keterangan;}
    }
    
    private void setKomponen(){
        cbxJenisTransaksi.getItems().addAll("Antar Cabang +", "Antar Cabang -");
        cbxJenisTransaksi.setValue("Antar Cabang +");
        cbxJenisTransaksi.setOnAction(event ->{
            getDataTabelTransaksi();
        });
        Session.setTextFieldNumeric(txtNominal);
        Session.triggerOnEnter(this::prosesTransaksi, txtNominal, txtAKeterangan);
    }
    
    private void setTabelTransaksi(){
        colJenisTransaksi.setCellValueFactory(new PropertyValueFactory<>("jenisTransaksi"));
        colKaryawan.setCellValueFactory(new PropertyValueFactory<>("karyawan"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colNominal.setCellValueFactory(new PropertyValueFactory<>("nominal"));
        colKeterangan.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
    }
    
    @FXML
    private void getDataTabelTransaksi(){
        listTransaksi.clear();
        String keyword = txtSearchBar.getText().trim();
        String jenisTransaksi = cbxJenisTransaksi.getValue();
        
        String query = "SELECT id_transaksi_antar_cabang, adm.username,\n" +
        "DATE(tanggal_transaksi) AS tanggal, TIME(tanggal_transaksi) AS waktu,\n" +
        "nominal, keterangan\n" +
        "FROM transaksi_antar_cabang\n" +
        "JOIN admin adm ON adm.id_admin = transaksi_antar_cabang.id_admin\n" +
        "WHERE jenis_transaksi=? ";
        
        boolean isSearch = keyword != null && !keyword.trim().isEmpty();
        
        if (isSearch) {
            query += "AND (adm.username LIKE ? OR keterangan LIKE ? OR CAST(nominal AS CHAR) LIKE ?)";
        }

        try (PreparedStatement statement = Koneksi.getCon().prepareStatement(query)) {
            statement.setString(1, jenisTransaksi);

            if (isSearch) {
                String searchKeyword = "%" + keyword + "%";
                statement.setString(2, searchKeyword);
                statement.setString(3, searchKeyword);
                statement.setString(4, searchKeyword);
            }

            ResultSet result = statement.executeQuery();
            int totalTransaksi = 0;

            while (result.next()) {
               String idTransaksi = result.getString("id_transaksi_antar_cabang");
               String karyawan = result.getString("adm.username");
               String tanggal = Session.convertTanggalIndo(result.getString("tanggal")) + " " + result.getString("waktu");
               int nominal = result.getInt("nominal");
               String keterangan = result.getString("keterangan");

               totalTransaksi += nominal;
               listTransaksi.add(new Transaksi(idTransaksi, jenisTransaksi, karyawan, tanggal, Session.convertIntToRupiah(nominal), keterangan));
            }
            lblTotalTransaksi.setText(Session.convertIntToRupiah(totalTransaksi));
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabelTransaksi.setItems(listTransaksi);
    }
    
    @FXML
    private void batalTransaksi(){
        txtNominal.clear();
        txtAKeterangan.clear();
    }
    
    @FXML
    private void clearSearchBar(){
        txtSearchBar.clear();
        getDataTabelTransaksi();
    }
    
    @FXML
    private void prosesTransaksi(){
        if(txtNominal.getText().isEmpty() || txtNominal.getText().equals("0")){
            Session.animasiPanePesan(true, "Masukkan nominal transaksi", btnProses);
            return;
        }else if(txtAKeterangan.getText().isEmpty()){
            Session.animasiPanePesan(true, "Masukkan keterangan transaksi", btnProses);
            return;
        }

        String jenisTransaksi = cbxJenisTransaksi.getValue();
        int nominal = Integer.parseInt(txtNominal.getText());
        String keterangan = txtAKeterangan.getText();
        String idTransaksiAntarCabangBaru = Session.membuatIdBaru("transaksi_antar_cabang", "id_transaksi_antar_cabang", "tac", 3);
        
        try {
            String query = "INSERT INTO transaksi_antar_cabang VALUES (?,?,NOW(),?,?,?)";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idTransaksiAntarCabangBaru);
            statement.setString(2, Session.getIdAdmin());
            statement.setString(3, jenisTransaksi);
            statement.setInt(4, nominal);
            statement.setString(5, keterangan);
            
            statement.executeUpdate();
            
            Session.animasiPanePesan(false, "Transaksi berhasil diproses", btnProses);
            
            statement.close();
            getDataTabelTransaksi();
            batalTransaksi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
