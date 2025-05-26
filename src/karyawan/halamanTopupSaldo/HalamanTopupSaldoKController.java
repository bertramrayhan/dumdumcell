package karyawan.halamanTopupSaldo;

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
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import main.Koneksi;
import main.Session;

public class HalamanTopupSaldoKController implements Initializable {

    @FXML private TabPane tabPaneTopupSaldo;
    @FXML private Pane paneGelap;
    
    //TOPUP SALDO PELANGGAN
    @FXML private ChoiceBox<String> cbxAplikasiSaldo, cbxCaraPembayaran;
    @FXML private TextField txtHargaJualSaldo, txtNomorTelepon, txtSaldoMinus, txtNamaRekening, txtCashback;
    @FXML private Label lblTotalSaldoMinus, lblTotalJualSaldo;
    @FXML private Button btnBatal, btnKonfirmasi, btnHapusTopupSaldoPelanggan;
    @FXML private TableColumn<TopupSaldoPelanggan, String> colKaryawan, colTanggal, colAplikasiSaldo, colNomorTelepon;
    @FXML private TableColumn<TopupSaldoPelanggan, String> colNamaRekening, colHargaJualSaldo, colSaldoMinus, colCashback;
    @FXML private TableView<TopupSaldoPelanggan> tabelTopupSaldoPelanggan;

    private ObservableList<TopupSaldoPelanggan> listTopupSaldoPelanggan = FXCollections.observableArrayList();

    //HAPUS TOPUP SALDO PELANGGAN
    @FXML private AnchorPane paneHapusTopupSaldoPelanggan;
    @FXML private Button btnTidakHapusTopupSaldoPelanggan, btnIyaHapusTopupSaldoPelanggan;

    private TopupSaldoPelanggan topupSaldoPelangganTerpilih;
    private String idTopupSaldoPelangganTerpilih;
    
    //TOPUP SALDO APLIKASI
    @FXML private ChoiceBox<String> cbxAplikasiSaldoTopupSaldoAplikasi;
    @FXML private TextField txtTopupSaldoTopupSaldoAplikasi;
    @FXML private Label lblTotalTopupSaldo;
    @FXML private Button btnBatalTopupSaldoAplikasi, btnKonfirmasiTopupSaldoAplikasi, btnHapusTopupSaldoAplikasi;
    @FXML private TableColumn<TopupSaldoPelanggan, String> colKaryawanTopupSaldoAplikasi, colTanggalTopupSaldoAplikasi, 
    colAplikasiSaldoTopupSaldoAplikasi, colTopupSaldoTopupSaldoAplikasi;
    @FXML private TableView<TopupSaldoAplikasi> tabelTopupSaldoAplikasi;
    
    private ObservableList<TopupSaldoAplikasi> listTopupSaldoAplikasi = FXCollections.observableArrayList();
    
    //HAPUS TOPUP SALDO APLIKASI
    @FXML private AnchorPane paneHapusTopupSaldoAplikasi;
    @FXML private Button btnTidakHapusTopupSaldoAplikasi, btnIyaHapusTopupSaldoAplikasi;

    private TopupSaldoAplikasi topupSaldoAplikasiTerpilih;
    private String idTopupSaldoAplikasiTerpilih;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTabelTopupSaldoPelanggan();
        setKomponen();
        getDataTabelTopupSaldoPelanggan();   
        
        //TOPUP SALDO APLIKASI
        setTabelTopupSaldoAplikasi();
        setKomponenTopupSaldoAplikasi();
        getDataTabelTopupSaldoAplikasi();
    }    

    public class TopupSaldoPelanggan{
        String idTopupSaldoPelanggan, karyawan, tanggal, aplikasiSaldo, nomorTelepon;
        String namaRekening, hargaJualSaldo, saldoMinus, cashback;

        public TopupSaldoPelanggan(String idTopupSaldoPelanggan, String karyawan, String tanggal, String aplikasiSaldo, String nomorTelepon, String namaRekening, String hargaJualSaldo, String saldoMinus, String cashback) {
            this.idTopupSaldoPelanggan = idTopupSaldoPelanggan;this.karyawan = karyawan;this.tanggal = tanggal;this.aplikasiSaldo = aplikasiSaldo;this.nomorTelepon = nomorTelepon;this.namaRekening = namaRekening;this.hargaJualSaldo = hargaJualSaldo;this.saldoMinus = saldoMinus;this.cashback = cashback;
        }
        public String getIdTopupSaldoPelanggan() {return idTopupSaldoPelanggan;}
        public String getKaryawan() {return karyawan;}
        public String getTanggal() {return tanggal;}
        public String getAplikasiSaldo() {return aplikasiSaldo;}
        public String getNomorTelepon() {return nomorTelepon;}
        public String getNamaRekening() {return namaRekening;}
        public String getHargaJualSaldo() {return hargaJualSaldo;}
        public String getSaldoMinus() {return saldoMinus;}
        public String getCashback() {return cashback;}
    }

    private void setTabelTopupSaldoPelanggan(){
        colKaryawan.setCellValueFactory(new PropertyValueFactory<>("karyawan"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colAplikasiSaldo.setCellValueFactory(new PropertyValueFactory<>("aplikasiSaldo"));
        colNomorTelepon.setCellValueFactory(new PropertyValueFactory<>("nomorTelepon"));
        colNamaRekening.setCellValueFactory(new PropertyValueFactory<>("namaRekening"));
        colHargaJualSaldo.setCellValueFactory(new PropertyValueFactory<>("hargaJualSaldo"));
        colSaldoMinus.setCellValueFactory(new PropertyValueFactory<>("saldoMinus"));
        colCashback.setCellValueFactory(new PropertyValueFactory<>("cashback"));

        tabelTopupSaldoPelanggan.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !paneHapusTopupSaldoPelanggan.isVisible()) {
                btnHapusTopupSaldoPelanggan.setDisable(false);
            } else {
                btnHapusTopupSaldoPelanggan.setDisable(true);
            }
        });

        tabelTopupSaldoPelanggan.setOnMouseClicked(event -> {
            TopupSaldoPelanggan topupSaldoPelanggan = tabelTopupSaldoPelanggan.getSelectionModel().getSelectedItem();
            if (topupSaldoPelanggan != null) {
                topupSaldoPelangganTerpilih = topupSaldoPelanggan;
                idTopupSaldoPelangganTerpilih = topupSaldoPelanggan.getIdTopupSaldoPelanggan();
            }
        });
    }

    private void setKomponen(){
        cbxCaraPembayaran.getItems().addAll("Debit", "Kredit");
        cbxCaraPembayaran.setValue("Debit");
        cbxCaraPembayaran.setOnAction(event ->{
            if(cbxCaraPembayaran.getValue().equals("Kredit")){
                txtNamaRekening.setDisable(false);
            }else{
                txtNamaRekening.clear();
                txtNamaRekening.setDisable(true);
            }
        });

        try {
            String query = "SELECT nama_aplikasi_saldo FROM aplikasi_saldo";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);

            ResultSet result = statement.executeQuery();

            while(result.next()) {
                cbxAplikasiSaldo.getItems().add(result.getString("nama_aplikasi_saldo"));
            }

            if(!cbxAplikasiSaldo.getItems().isEmpty()){
                cbxAplikasiSaldo.setValue(cbxAplikasiSaldo.getItems().get(0));
            }

            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Session.setTextFieldNumeric(txtHargaJualSaldo, txtSaldoMinus, txtCashback);
        Session.setTextFieldNumeric(8, txtNomorTelepon);

        Session.triggerOnEnter(this::prosesTopupSaldoPelanggan, txtHargaJualSaldo, txtSaldoMinus, txtNomorTelepon, txtNamaRekening, txtCashback);
    }

    private void getDataTabelTopupSaldoPelanggan(){
        listTopupSaldoPelanggan.clear();

        try {
            String query = "SELECT adm.username, DATE(tsp.tanggal) AS tanggal, TIME(tsp.tanggal) AS waktu,\n" +
            "aps.nama_aplikasi_saldo, tsp.nomor_telepon, tsp.nama_rekening, tsp.harga_jual_saldo,\n" +
            "tsp.total_saldo_minus, tsp.total_cashback,tsp.id_topup_saldo_pelanggan\n" +
            "FROM topup_saldo_pelanggan tsp\n" +
            "JOIN admin adm ON adm.id_admin = tsp.id_admin\n" +
            "JOIN aplikasi_saldo aps ON aps.id_aplikasi_saldo = tsp.id_aplikasi_saldo";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);

            ResultSet result = statement.executeQuery();

            int totalSaldoMinus = 0;
            int totalJualSaldo = 0;

            while(result.next()) {
                String idTopupSaldoPelanggan = result.getString("tsp.id_topup_saldo_pelanggan");
                String karyawan = result.getString("adm.username");
                String tanggal = Session.convertTanggalIndo(result.getString("tanggal")) + " " + result.getString("waktu");
                String aplikasiSaldo = result.getString("aps.nama_aplikasi_saldo");
                String nomorTelepon = result.getString("tsp.nomor_telepon");
                String namaRekening = result.getString("tsp.nama_rekening");
                int hargaJualSaldo = result.getInt("tsp.harga_jual_saldo");
                int saldoMinus = result.getInt("tsp.total_saldo_minus");
                String cashback = Session.convertIntToRupiah(result.getInt("tsp.total_cashback"));


                totalJualSaldo += hargaJualSaldo;
                totalSaldoMinus += saldoMinus;
                listTopupSaldoPelanggan.add(new TopupSaldoPelanggan(idTopupSaldoPelanggan, karyawan, tanggal, aplikasiSaldo, nomorTelepon, namaRekening, Session.convertIntToRupiah(hargaJualSaldo), Session.convertIntToRupiah(saldoMinus), cashback));
            }

            lblTotalSaldoMinus.setText(Session.convertIntToRupiah(totalSaldoMinus));
            lblTotalJualSaldo.setText(Session.convertIntToRupiah(totalJualSaldo));

            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tabelTopupSaldoPelanggan.setItems(listTopupSaldoPelanggan);
    }

    @FXML
    private void batalTransaksi(){
        txtHargaJualSaldo.clear();
        txtNomorTelepon.clear();
        txtSaldoMinus.clear();
        txtNamaRekening.clear();
        txtCashback.clear();
    }

    @FXML
    private void prosesTopupSaldoPelanggan(){
        if(cbxAplikasiSaldo.getValue().isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Aplikasi Saldo", btnKonfirmasi);
            return;
        }else if(txtHargaJualSaldo.getText().isEmpty() || txtHargaJualSaldo.getText().equals("0")){
            Session.animasiPanePesan(true, "Masukkan Harga Jual Saldo", btnKonfirmasi);
            return;            
        }else if(txtNomorTelepon.getText().isEmpty() || txtNomorTelepon.getText().length() != 8){
            Session.animasiPanePesan(true, "Masukkan Nomor Telepon dengan Panjang 8 Digit", btnKonfirmasi);
            return;            
        }else if(txtSaldoMinus.getText().isEmpty() || txtSaldoMinus.getText().equals("0")){
            Session.animasiPanePesan(true, "Masukkan Saldo Minus", btnKonfirmasi);
            return;            
        }else if(cbxCaraPembayaran.getValue().equals("Kredit") && txtNamaRekening.getText().isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nama Rekening", btnKonfirmasi);
            return;
        }else if(!cekJumlahSaldo(cbxAplikasiSaldo.getValue(), Integer.parseInt(txtSaldoMinus.getText()))){
            Session.animasiPanePesan(true, "Jumlah Saldo Kurang", btnKonfirmasi);
            return;
        }

        String idTopupSaldoPelangganBaru = Session.membuatIdBaru("topup_saldo_pelanggan", "id_topup_saldo_pelanggan", "tsp", 4);
        String idAplikasiSaldo = Session.getId("aplikasi_saldo", "id_aplikasi_saldo", "nama_aplikasi_saldo", cbxAplikasiSaldo.getValue());
        int hargaJualSaldo = Integer.parseInt(txtHargaJualSaldo.getText());
        String nomorTelepon = txtNomorTelepon.getText();
        int saldoMinus = Integer.parseInt(txtSaldoMinus.getText());
        String namaRekening = txtNamaRekening.getText();
        int cashback = txtCashback.getText().isEmpty() ? 0 : Integer.parseInt(txtCashback.getText());

        try {
            String query = "INSERT INTO topup_saldo_pelanggan VALUES (?,?,?,NOW(),?,?,?,?,?)";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idTopupSaldoPelangganBaru);
            statement.setString(2, Session.getIdAdmin());
            statement.setString(3, idAplikasiSaldo);
            statement.setString(4, nomorTelepon);
            statement.setInt(5, hargaJualSaldo);
            statement.setInt(6, saldoMinus);
            statement.setInt(7, cashback);
            statement.setString(8, namaRekening);

            statement.executeUpdate();

            Session.animasiPanePesan(false, "Topup Saldo Berhasil Diproses", btnKonfirmasi);

            statement.close();
            getDataTabelTopupSaldoPelanggan();
            batalTransaksi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean cekJumlahSaldo(String aplikasiSaldo, int saldoMinus){
        boolean cukup = false;

        try {
            String query = "SELECT * FROM aplikasi_saldo WHERE nama_aplikasi_saldo= ? AND total_saldo >= ?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, aplikasiSaldo);
            statement.setInt(2, saldoMinus);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                cukup = true;
            }

            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cukup;
    }

    //HAPUS TOPUP SALDO PELANGGAN
    @FXML
    private void bukaHapusTopupSaldoPelanggan(){
        Session.setShowPane(paneHapusTopupSaldoPelanggan, paneGelap);
        Session.setMouseTransparentTrue(tabPaneTopupSaldo);
    }

    @FXML
    private void tutupHapusTopupSaldoPelanggan(){
        Session.setHidePane(paneHapusTopupSaldoPelanggan, paneGelap);
        Session.setMouseTransparentFalse(tabPaneTopupSaldo);
    }
    
    @FXML
    private void hapusTopupSaldoPelanggan(){        
        try {
            String query = "DELETE FROM topup_saldo_pelanggan WHERE id_topup_saldo_pelanggan=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idTopupSaldoPelangganTerpilih);
            statement.executeUpdate();
            
            getDataTabelTopupSaldoPelanggan();
            Session.animasiPanePesan(false, "Transaksi berhasil dihapus");
            tutupHapusTopupSaldoPelanggan();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //TOPUP SALDO APLIKASI
    public class TopupSaldoAplikasi{
        String idTopupSaldoAplikasi, karyawan, tanggal, aplikasiSaldo, topupSaldo;

        public TopupSaldoAplikasi(String idTopupSaldoAplikasi, String karyawan, String tanggal, String aplikasiSaldo, String topupSaldo) {
            this.idTopupSaldoAplikasi = idTopupSaldoAplikasi;this.karyawan = karyawan;this.tanggal = tanggal;this.aplikasiSaldo = aplikasiSaldo;this.topupSaldo = topupSaldo;
        }
        public String getIdTopupSaldoAplikasi() {return idTopupSaldoAplikasi;}
        public String getKaryawan() {return karyawan;}
        public String getTanggal() {return tanggal;}
        public String getAplikasiSaldo() {return aplikasiSaldo;}
        public String getTopupSaldo() {return topupSaldo;}
    }
    
    private void setTabelTopupSaldoAplikasi(){
        colKaryawanTopupSaldoAplikasi.setCellValueFactory(new PropertyValueFactory<>("karyawan"));
        colTanggalTopupSaldoAplikasi.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colAplikasiSaldoTopupSaldoAplikasi.setCellValueFactory(new PropertyValueFactory<>("aplikasiSaldo"));
        colTopupSaldoTopupSaldoAplikasi.setCellValueFactory(new PropertyValueFactory<>("topupSaldo"));

        tabelTopupSaldoAplikasi.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !paneHapusTopupSaldoAplikasi.isVisible()) {
                btnHapusTopupSaldoAplikasi.setDisable(false);
            } else {
                btnHapusTopupSaldoAplikasi.setDisable(true);
            }
        });

        tabelTopupSaldoAplikasi.setOnMouseClicked(event -> {
            TopupSaldoAplikasi topupSaldoAplikasi = tabelTopupSaldoAplikasi.getSelectionModel().getSelectedItem();
            if (topupSaldoAplikasi != null) {
                topupSaldoAplikasiTerpilih = topupSaldoAplikasi;
                idTopupSaldoAplikasiTerpilih = topupSaldoAplikasi.getIdTopupSaldoAplikasi();
            }
        });
    }
    
    private void setKomponenTopupSaldoAplikasi(){
        try {
            String query = "SELECT nama_aplikasi_saldo FROM aplikasi_saldo";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);

            ResultSet result = statement.executeQuery();

            while(result.next()) {
                cbxAplikasiSaldoTopupSaldoAplikasi.getItems().add(result.getString("nama_aplikasi_saldo"));
            }

            if(!cbxAplikasiSaldoTopupSaldoAplikasi.getItems().isEmpty()){
                cbxAplikasiSaldoTopupSaldoAplikasi.setValue(cbxAplikasiSaldoTopupSaldoAplikasi.getItems().get(0));
            }

            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Session.setTextFieldNumeric(txtTopupSaldoTopupSaldoAplikasi);

        Session.triggerOnEnter(this::prosesTopupSaldoAplikasi, txtTopupSaldoTopupSaldoAplikasi);
    }
    
    private void getDataTabelTopupSaldoAplikasi(){
        listTopupSaldoAplikasi.clear();

        try {
            String query = "SELECT adm.username, DATE(tsa.tanggal) AS tanggal, TIME(tsa.tanggal) AS waktu, \n" +
            "aps.nama_aplikasi_saldo, tsa.total_topup, tsa.id_topup_saldo_aplikasi\n" +
            "FROM topup_saldo_aplikasi tsa \n" +
            "JOIN admin adm ON adm.id_admin = tsa.id_admin \n" +
            "JOIN aplikasi_saldo aps ON aps.id_aplikasi_saldo = tsa.id_aplikasi_saldo";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);

            ResultSet result = statement.executeQuery();

            int totalTopupSaldo = 0;

            while(result.next()) {
                String idTopupSaldoAplikasi = result.getString("tsa.id_topup_saldo_aplikasi");
                String karyawan = result.getString("adm.username");
                String tanggal = Session.convertTanggalIndo(result.getString("tanggal")) + " " + result.getString("waktu");
                String aplikasiSaldo = result.getString("aps.nama_aplikasi_saldo");
                int topupSaldo = result.getInt("tsa.total_topup");

                totalTopupSaldo += topupSaldo;
                listTopupSaldoAplikasi.add(new TopupSaldoAplikasi(idTopupSaldoAplikasi, karyawan, tanggal, aplikasiSaldo, Session.convertIntToRupiah(topupSaldo)));
            }

            lblTotalTopupSaldo.setText(Session.convertIntToRupiah(totalTopupSaldo));

            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tabelTopupSaldoAplikasi.setItems(listTopupSaldoAplikasi);
    }
    
    @FXML
    private void batalTransaksiTopupSaldoAplikasi(){
        txtTopupSaldoTopupSaldoAplikasi.clear();
    }
    
    @FXML
    private void prosesTopupSaldoAplikasi(){
        if(cbxAplikasiSaldoTopupSaldoAplikasi.getValue().isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Aplikasi Saldo", btnKonfirmasiTopupSaldoAplikasi);
            return;
        }else if(txtTopupSaldoTopupSaldoAplikasi.getText().isEmpty() || txtTopupSaldoTopupSaldoAplikasi.getText().equals("0")){
            Session.animasiPanePesan(true, "Masukkan Topup Saldo", btnKonfirmasiTopupSaldoAplikasi);
            return;            
        }

        String idTopupSaldoAplikasiBaru = Session.membuatIdBaru("topup_saldo_aplikasi", "id_topup_saldo_aplikasi", "tsa", 4);
        String idAplikasiSaldo = Session.getId("aplikasi_saldo", "id_aplikasi_saldo", "nama_aplikasi_saldo", cbxAplikasiSaldoTopupSaldoAplikasi.getValue());
        int topupSaldo = Integer.parseInt(txtTopupSaldoTopupSaldoAplikasi.getText());

        try {
            String query = "INSERT INTO topup_saldo_aplikasi VALUES (?,?,?,NOW(),?)";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idTopupSaldoAplikasiBaru);
            statement.setString(2, Session.getIdAdmin());
            statement.setString(3, idAplikasiSaldo);
            statement.setInt(4, topupSaldo);

            statement.executeUpdate();

            Session.animasiPanePesan(false, "Topup Saldo Berhasil Diproses", btnKonfirmasiTopupSaldoAplikasi);

            statement.close();
            getDataTabelTopupSaldoAplikasi();
            batalTransaksiTopupSaldoAplikasi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //HAPUS TOPUP SALDO APLIKASI
    @FXML
    private void bukaHapusTopupSaldoAplikasi(){
        Session.setShowPane(paneHapusTopupSaldoAplikasi, paneGelap);
        Session.setMouseTransparentTrue(tabPaneTopupSaldo);
    }

    @FXML
    private void tutupHapusTopupSaldoAplikasi(){
        Session.setHidePane(paneHapusTopupSaldoAplikasi, paneGelap);
        Session.setMouseTransparentFalse(tabPaneTopupSaldo);
    }
    
    @FXML
    private void hapusTopupSaldoAplikasi(){        
        try {
            String query = "DELETE FROM topup_saldo_aplikasi WHERE id_topup_saldo_aplikasi=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idTopupSaldoAplikasiTerpilih);
            statement.executeUpdate();
            
            getDataTabelTopupSaldoAplikasi();
            Session.animasiPanePesan(false, "Transaksi berhasil dihapus");
            tutupHapusTopupSaldoAplikasi();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}