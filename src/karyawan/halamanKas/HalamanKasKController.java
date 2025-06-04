package karyawan.halamanKas;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.Koneksi;
import main.Session;

public class HalamanKasKController implements Initializable {
    
    @FXML private Label lblTotalPemasukanKas, lblTotalPengeluaranKas, lblTotalKasSaatIni;
    @FXML private TextField txtNominal;
    @FXML private TextArea txtADeskripsi;
    @FXML private Button btnBatal, btnProses;
    @FXML private TableView<Kas> tabelKas;
    @FXML private TableColumn<Kas, String> colKaryawan, colTanggal, colNominal, colDeskripsi;
    
    private ObservableList<Kas> listKas = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setKomponen();
        setTabelKas();
        getDataTabelKas();
    }    
    
    public class Kas{
        String karyawan, tanggal, nominal, deskripsi;

        public Kas(String karyawan, String tanggal, String nominal, String deskripsi) {
            this.karyawan = karyawan;this.tanggal = tanggal;this.nominal = nominal;this.deskripsi = deskripsi;}
        
        public String getKaryawan() {return karyawan;}
        public String getTanggal() {return tanggal;}
        public String getNominal() {return nominal;}
        public String getDeskripsi() {return deskripsi;}
    }
    
    private void setKomponen(){
        Session.setTextFieldNumeric(txtNominal);
        Session.triggerOnEnter(this::prosesKas, txtNominal);
    }
    
    private void setTabelKas(){
        colKaryawan.setCellValueFactory(new PropertyValueFactory<>("karyawan"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colNominal.setCellValueFactory(new PropertyValueFactory<>("nominal"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
    }
    
    private void getDataTabelKas(){
        LocalTime[] jamShift = Session.getWaktuShiftByNow();

        LocalDate tanggalHariIni = LocalDate.now();

        LocalDateTime dateTimeMulai = tanggalHariIni.atTime(jamShift[0]);
        LocalDateTime dateTimeSelesai = tanggalHariIni.atTime(jamShift[1]);

        Timestamp timestampMulai = Timestamp.valueOf(dateTimeMulai);
        Timestamp timestampSelesai = Timestamp.valueOf(dateTimeSelesai);
        
        listKas.clear();
        
        String query = "SELECT kas.jenis_kas, adm.username, DATE(kas.tanggal_kas) AS tanggal, " +
        "TIME(kas.tanggal_kas) AS waktu, kas.nominal, kas.deskripsi " +
        "FROM kas " +
        "JOIN admin adm ON adm.id_admin = kas.id_admin " +
        "WHERE kas.tanggal_kas >= DATE_SUB(NOW(), INTERVAL 10 DAY) " +
        "ORDER BY kas.tanggal_kas DESC";

        try {
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            ResultSet result = statement.executeQuery();
            int totalPemasukanKas = 0;
            int totalPengeluaranKas = 0;

            while (result.next()) {
               String karyawan = result.getString("adm.username");
               String tanggal = Session.convertTanggalIndo(result.getString("tanggal")) + " " + result.getString("waktu");
               int nominal = result.getInt("nominal");
               String deskripsi = result.getString("deskripsi");
               String jenisKas = result.getString("kas.jenis_kas");
               
               if(jenisKas.equals("Pemasukan")){
                   totalPemasukanKas += nominal;
               }else{
                   totalPengeluaranKas += nominal;
                   listKas.add(new Kas(karyawan, tanggal, Session.convertIntToRupiah(nominal), deskripsi));
               }
               
            }
            lblTotalPemasukanKas.setText(Session.convertIntToRupiah(totalPemasukanKas));
            lblTotalPengeluaranKas.setText(Session.convertIntToRupiah(totalPengeluaranKas));

            query = "SELECT"
                    + "SUM(CASE"
                    + "WHEN jenis_kas = 'Pemasukan' THEN nominal"
                    + "WHEN jenis_kas = 'Pengeluaran' THEN -nominal"
                    + "ELSE 0"
                    + "END) AS total_kas"
                    + "FROM kas";
            
            statement = Koneksi.getCon().prepareStatement(query);
            result = statement.executeQuery();
            if(result.next()){
                lblTotalKasSaatIni.setText(Session.convertIntToRupiah(result.getInt("total_kas")));
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabelKas.setItems(listKas);
    }
    
    @FXML
    private void batalKas(){
        txtNominal.clear();
        txtADeskripsi.clear();
    }
    
    @FXML
    private void prosesKas(){
        if(txtNominal.getText().isEmpty() || txtNominal.getText().matches("0+")){
            Session.animasiPanePesan(true, "Masukkan nominal kas", btnProses);
            return;
        }else if(txtADeskripsi.getText().trim().isEmpty()){
            Session.animasiPanePesan(true, "Masukkan deskripsi kas", btnProses);
            return;
        }else if(Session.convertRupiahToInt(lblTotalPemasukanKas.getText()) - Integer.parseInt(txtNominal.getText()) < 0){
            Session.animasiPanePesan(true, "Pemasukan Kas tidak mencukupi", btnProses);
            return;
        }

        int nominal = Integer.parseInt(txtNominal.getText());
        String deskripsi = txtADeskripsi.getText().trim();
        String idKas = Session.membuatIdBaru("kas", "id_kas", "kas", 3);
        
        try {
            String query = "INSERT INTO kas VALUES (?,?,NOW(),?,?,?)";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idKas);
            statement.setString(2, Session.getIdAdmin());
            statement.setString(3, "Pengeluaran");
            statement.setInt(4, nominal);
            statement.setString(5, deskripsi);
            
            statement.executeUpdate();
            
            Session.animasiPanePesan(false, "Kas berhasil diproses", btnProses);
            
            statement.close();
            getDataTabelKas();
            batalKas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

