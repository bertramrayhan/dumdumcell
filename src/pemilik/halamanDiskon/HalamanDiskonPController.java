package pemilik.halamanDiskon;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import main.Koneksi;
import main.Session;

public class HalamanDiskonPController implements Initializable {

    @FXML private Pane paneGelap;
    @FXML private TextField txtSearchBar;
    @FXML private ImageView btnX;
    @FXML private TableView<Diskon> tabelDiskon;
    @FXML private TableColumn<Diskon, String> colNamaDiskon, colJenisDiskon, colPotonganHarga, colTanggalMulai, colTanggalBerakhir, colStatus;
    private ObservableList<Diskon> listDiskon = FXCollections.observableArrayList();
    
    //TAMBAH DISKON
    @FXML private Label lblRupiahTambah, lblPersentaseTambah;
    @FXML private TextField txtNamaDiskonTambah, txtPotonganHargaTambah;
    @FXML private ChoiceBox cbxJenisDiskonTambah;
    @FXML private DatePicker dtPTanggalMulaiTambah, dtPTanggalBerakhirTambah;
    @FXML private Button btnBatalTambahDiskon, btnIyaTambahDiskon;
    
    //EDIT DISKON
    @FXML private Label lblRupiahEdit, lblPersentaseEdit;
    @FXML private AnchorPane paneEditDiskon;
    @FXML private Button btnEditDiskon, btnBatalEditDiskon, btnIyaEditDiskon;
    @FXML private TextField txtNamaDiskonEdit, txtPotonganHargaEdit;
    @FXML private ChoiceBox cbxJenisDiskonEdit;
    @FXML private DatePicker dtPTanggalMulaiEdit, dtPTanggalBerakhirEdit;
    
    //HAPUS DISKON
    @FXML private AnchorPane paneHapusDiskon;
    @FXML private Button btnHapusDiskon, btnBatalHapusDiskon, btnIyaHapusDiskon;

    private Diskon diskonTerpilih;
    private String idDiskonTerpilih;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTabelDiskon();
        getDataTabelDiskon();
        
        //TAMBAH DISKON
        setKomponenTambahDiskon();
        
        //EDIT DISKON
        setKomponenEditDiskon();
    }    
    
    public class Diskon{
        String idDiskon, namaDiskon, jenisDiskon, tanggalMulai, tanggalBerakhir, status, potonganHarga;

        public Diskon(String idDiskon, String namaDiskon, String jenisDiskon, String tanggalMulai, String tanggalBerakhir, String status, String potonganHarga) {
            this.idDiskon = idDiskon;
            this.namaDiskon = namaDiskon;
            this.jenisDiskon = jenisDiskon;
            this.tanggalMulai = tanggalMulai;
            this.tanggalBerakhir = tanggalBerakhir;
            this.status = status;
            this.potonganHarga = potonganHarga;
        }
        public String getIdDiskon() {return idDiskon;}
        public String getNamaDiskon() {return namaDiskon;}
        public String getJenisDiskon() {return jenisDiskon;}
        public String getTanggalMulai() {return tanggalMulai;}
        public String getTanggalBerakhir() {return tanggalBerakhir;}
        public String getStatus() {return status;}
        public String getPotonganHarga() {return potonganHarga;}
    }
    
    private void setTabelDiskon(){
        colNamaDiskon.setCellValueFactory(new PropertyValueFactory<>("namaDiskon"));
        colJenisDiskon.setCellValueFactory(new PropertyValueFactory<>("jenisDiskon"));
        colPotonganHarga.setCellValueFactory(new PropertyValueFactory<>("potonganHarga"));
        colTanggalMulai.setCellValueFactory(new PropertyValueFactory<>("tanggalMulai"));
        colTanggalBerakhir.setCellValueFactory(new PropertyValueFactory<>("tanggalBerakhir"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        tabelDiskon.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !paneEditDiskon.isVisible() && !paneHapusDiskon.isVisible()) {
                btnEditDiskon.setDisable(false);
                btnHapusDiskon.setDisable(false);
            } else {
                btnEditDiskon.setDisable(true);
                btnHapusDiskon.setDisable(true);
            }
        });
        
        tabelDiskon.setOnMouseClicked(event -> {
            Diskon diskon = tabelDiskon.getSelectionModel().getSelectedItem();
            if (diskon != null) {
                diskonTerpilih = diskon;
                idDiskonTerpilih = diskon.getIdDiskon();
            }
        });
    }
    
    @FXML
    private void getDataTabelDiskon() {
        listDiskon.clear();
        String keyword = txtSearchBar.getText().trim();
        
        String query = "SELECT * FROM diskon";
        
        boolean isAngka = keyword != null && keyword.matches("\\d+"); // Deteksi angka
        boolean isSearch = keyword != null && !keyword.trim().isEmpty();

        if (isSearch) {
            if (isAngka) {
                query += " WHERE (harga_diskon LIKE ? OR tanggal_mulai LIKE ? OR tanggal_berakhir LIKE ?)";
            } else {
                query += " WHERE (nama_diskon LIKE ? OR jenis_diskon LIKE ? OR status LIKE ?)";
            }
        }

        try (PreparedStatement statement = Koneksi.getCon().prepareStatement(query)) {
            if (isSearch) {
                if (isAngka) {
                    int angka = Integer.parseInt(keyword);
                    statement.setInt(1, angka);
                    statement.setString(2, "%" + angka + "%");
                    statement.setString(3, "%" + angka + "%");
                } else {
                    String searchKeyword = "%" + keyword + "%";
                    statement.setString(1, searchKeyword);
                    statement.setString(2, searchKeyword);
                    statement.setString(3, searchKeyword);
                }
            }

             ResultSet result = statement.executeQuery();
             while (result.next()) {
                String idDiskon = result.getString("id_diskon");
                String namaDiskon = result.getString("nama_diskon");
                String jenisDiskon = result.getString("jenis_diskon");
                String hargaDiskon = jenisDiskon.equals("Nominal") ? Session.convertIntToRupiah(result.getInt("harga_diskon")) 
                        : result.getString("harga_diskon") + "%";
                String tanggalMulai = Session.convertTanggalIndo(result.getString("tanggal_mulai"));
                String tanggalBerakhir = Session.convertTanggalIndo(result.getString("tanggal_berakhir"));
                String status = result.getString("status");

                listDiskon.add(new Diskon(idDiskon, namaDiskon, jenisDiskon, tanggalMulai, tanggalBerakhir, status, hargaDiskon));
            }

             result.close();
             statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabelDiskon.setItems(listDiskon);
    }
    
    @FXML
    private void clearSearchBar(){
        txtSearchBar.setText("");
        getDataTabelDiskon();
    }
    
    //TAMBAH DISKON
    private void setKomponenTambahDiskon(){
        Session.triggerOnEnter(this::tambahDiskon, txtNamaDiskonTambah, txtPotonganHargaTambah);
        cbxJenisDiskonTambah.getItems().add("Nominal");
        cbxJenisDiskonTambah.getItems().add("Persentase");
        cbxJenisDiskonTambah.setValue("Nominal");
        Session.setTextFieldNumeric(txtPotonganHargaTambah);
        
        cbxJenisDiskonTambah.setOnAction(event -> {
            if(cbxJenisDiskonTambah.getValue().equals("Nominal")){
                lblRupiahTambah.setVisible(true);
                lblPersentaseTambah.setVisible(false);
            }else{
                lblPersentaseTambah.setVisible(true);
                lblRupiahTambah.setVisible(false);
            }
        });
        
        dtPTanggalMulaiTambah.getEditor().setDisable(true);
        dtPTanggalMulaiTambah.getEditor().setOpacity(1);
        dtPTanggalBerakhirTambah.getEditor().setDisable(true);
        dtPTanggalBerakhirTambah.getEditor().setOpacity(1);

        // Tanggal mulai: hanya bisa pilih besok dan setelahnya
        dtPTanggalMulaiTambah.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Set default tanggal mulai ke besok
        dtPTanggalMulaiTambah.setValue(LocalDate.now());

        // Fungsi untuk update batasan tanggal berakhir
        Runnable updateTanggalBerakhirFactory = () -> {
            LocalDate tanggalMulai = dtPTanggalMulaiTambah.getValue();
            dtPTanggalBerakhirTambah.setDayCellFactory(dp -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item.isBefore(tanggalMulai.plusDays(1))) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });

            // Reset value tanggal berakhir kalau sebelumnya gak valid
            if (dtPTanggalBerakhirTambah.getValue() == null ||
                !dtPTanggalBerakhirTambah.getValue().isAfter(tanggalMulai)) {
                dtPTanggalBerakhirTambah.setValue(tanggalMulai.plusDays(1));
            }
        };

        // Listener buat update batasan tanggal berakhir pas tanggal mulai berubah
        dtPTanggalMulaiTambah.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateTanggalBerakhirFactory.run();
        });

        // Set batasan awal
        updateTanggalBerakhirFactory.run();
    }
    
    @FXML
    private void clearTambahDiskon(){
        txtNamaDiskonTambah.setText("");
        cbxJenisDiskonTambah.setValue(cbxJenisDiskonTambah.getItems().get(0));
        txtPotonganHargaTambah.setText("");
        dtPTanggalMulaiTambah.setValue(LocalDate.now().plusDays(1));
        dtPTanggalBerakhirTambah.setValue(LocalDate.now().plusDays(2));
    }
    
    @FXML
    private void tambahDiskon(){
        String namaDiskon = txtNamaDiskonTambah.getText().trim();
        String jenisDiskon = (String) cbxJenisDiskonTambah.getValue();
        String potonganHarga = txtPotonganHargaTambah.getText().trim();
        String tanggalMulai = dtPTanggalMulaiTambah.getValue().toString();
        String tanggalBerakhir = dtPTanggalBerakhirTambah.getValue().toString();
        
        if(namaDiskon.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nama Diskon", btnIyaTambahDiskon);
            return;
        }else if(potonganHarga.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Potongan Harga", btnIyaTambahDiskon);
            return;
        }else if(Session.cekDataSama("SELECT * FROM diskon WHERE nama_diskon=?", namaDiskon)){
            Session.animasiPanePesan(true, "Diskon Sudah Ada", btnIyaTambahDiskon);
            return;
        }
        
        String idDiskonBaru = Session.membuatIdBaru("diskon", "id_diskon", "dsk", 2);
        try {
            String query = "INSERT INTO diskon VALUES (?,?,?,?,?,?,?)";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idDiskonBaru);
            statement.setString(2, namaDiskon);
            statement.setString(3, jenisDiskon);
            statement.setString(4, potonganHarga);
            statement.setString(5, tanggalMulai);
            statement.setString(6, tanggalBerakhir);
            statement.setString(7, "aktif");
            statement.executeUpdate();
            
            getDataTabelDiskon();
            Session.animasiPanePesan(false, "Diskon berhasil ditambahkan");
            clearTambahDiskon();
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
            
    //EDIT DISKON
    private void setKomponenEditDiskon(){
        Session.triggerOnEnter(this::editDiskon, txtNamaDiskonEdit, txtPotonganHargaEdit);
        cbxJenisDiskonEdit.getItems().add("Nominal");
        cbxJenisDiskonEdit.getItems().add("Persentase");
        Session.setTextFieldNumeric(txtPotonganHargaEdit);
        
        cbxJenisDiskonEdit.setOnAction(event -> {
            if(cbxJenisDiskonEdit.getValue().equals("Nominal")){
                lblRupiahEdit.setVisible(true);
                lblPersentaseEdit.setVisible(false);
            }else{
                lblPersentaseEdit.setVisible(true);
                lblRupiahEdit.setVisible(false);
            }
        });
        
        dtPTanggalMulaiEdit.getEditor().setDisable(true);
        dtPTanggalMulaiEdit.getEditor().setOpacity(1);
        dtPTanggalBerakhirEdit.getEditor().setDisable(true);
        dtPTanggalBerakhirEdit.getEditor().setOpacity(1);

        // Tanggal mulai: hanya bisa pilih besok dan setelahnya
        dtPTanggalMulaiEdit.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Set default tanggal mulai ke besok
        dtPTanggalMulaiEdit.setValue(LocalDate.now().plusDays(1));

        // Fungsi untuk update batasan tanggal berakhir
        Runnable updateTanggalBerakhirFactory = () -> {
            LocalDate tanggalMulai = dtPTanggalMulaiEdit.getValue();
            dtPTanggalBerakhirEdit.setDayCellFactory(dp -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item.isBefore(tanggalMulai.plusDays(1))) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });

            // Reset value tanggal berakhir kalau sebelumnya gak valid
            if (dtPTanggalBerakhirEdit.getValue() == null ||
                !dtPTanggalBerakhirEdit.getValue().isAfter(tanggalMulai)) {
                dtPTanggalBerakhirEdit.setValue(tanggalMulai.plusDays(1));
            }
        };

        // Listener buat update batasan tanggal berakhir pas tanggal mulai berubah
        dtPTanggalMulaiEdit.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateTanggalBerakhirFactory.run();
        });

        // Set batasan awal
        updateTanggalBerakhirFactory.run();
    }
    
    @FXML
    private void bukaEditDiskon(){
        Session.setShowPane(paneEditDiskon, paneGelap);
        Session.setMouseTransparentTrue(txtNamaDiskonTambah, dtPTanggalMulaiTambah, dtPTanggalBerakhirTambah, btnIyaTambahDiskon, txtSearchBar, tabelDiskon);
                
        txtNamaDiskonEdit.setText(diskonTerpilih.getNamaDiskon());
        cbxJenisDiskonEdit.setValue(diskonTerpilih.getJenisDiskon());
        String potonganHarga = diskonTerpilih.getPotonganHarga().replaceAll("[^0-9]", "");
        txtPotonganHargaEdit.setText(diskonTerpilih.getJenisDiskon().equals("Nominal") ? potonganHarga.substring(0, potonganHarga.length() - 2) : potonganHarga);

        String tanggalMulaiStr = diskonTerpilih.getTanggalMulai();
        String tanggalBerakhirStr = diskonTerpilih.getTanggalBerakhir();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("id"));

        LocalDate tanggalMulaiLocal = LocalDate.parse(tanggalMulaiStr, formatter);
        LocalDate tanggalBerakhirLocal = LocalDate.parse(tanggalBerakhirStr, formatter);

        dtPTanggalMulaiEdit.setValue(tanggalMulaiLocal);
        dtPTanggalBerakhirEdit.setValue(tanggalBerakhirLocal);
    }
    
    @FXML
    private void tutupEditDiskon(){
        Session.setHidePane(paneEditDiskon, paneGelap);
        Session.setMouseTransparentFalse(txtNamaDiskonTambah, dtPTanggalMulaiTambah, dtPTanggalBerakhirTambah, btnIyaTambahDiskon, txtSearchBar, tabelDiskon);
    }
    
    @FXML
    private void editDiskon(){
        String namaDiskon = txtNamaDiskonEdit.getText().trim();
        String jenisDiskon = (String) cbxJenisDiskonEdit.getValue();
        String potonganHarga = txtPotonganHargaEdit.getText().trim();
        String tanggalMulai = dtPTanggalMulaiEdit.getValue().toString();
        String tanggalBerakhir = dtPTanggalBerakhirEdit.getValue().toString();
        
        if(namaDiskon.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Nama Diskon", btnIyaEditDiskon);
            return;
        }else if(potonganHarga.isEmpty()){
            Session.animasiPanePesan(true, "Masukkan Potongan Harga", btnIyaEditDiskon);
            return;
        }else if(!diskonTerpilih.getNamaDiskon().toLowerCase().equals(namaDiskon.toLowerCase()) && Session.cekDataSama("SELECT * FROM diskon WHERE nama_diskon=?", namaDiskon)){
            Session.animasiPanePesan(true, "Diskon Sudah Ada", btnIyaEditDiskon);
            return;
        }
        
        try {
            String query = "UPDATE diskon SET nama_diskon = ?, jenis_diskon = ?,\n" +
            "harga_diskon = ?, tanggal_mulai = ?, tanggal_berakhir = ?\n" +
            "WHERE id_diskon = ?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, namaDiskon);
            statement.setString(2, jenisDiskon);
            statement.setString(3, potonganHarga);
            statement.setString(4, tanggalMulai);
            statement.setString(5, tanggalBerakhir);
            statement.setString(6, idDiskonTerpilih);
            statement.executeUpdate();
            
            getDataTabelDiskon();
            Session.animasiPanePesan(false, "Diskon berhasil diperbarui");
            tutupEditDiskon();
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //HAPUS DISKON
    @FXML
    private void bukaHapusDiskon(){
        Session.setShowPane(paneHapusDiskon, paneGelap);
        Session.setMouseTransparentTrue(txtNamaDiskonTambah, cbxJenisDiskonTambah, txtPotonganHargaTambah, dtPTanggalMulaiTambah, dtPTanggalBerakhirTambah, btnIyaTambahDiskon, btnBatalTambahDiskon, btnHapusDiskon, txtSearchBar, tabelDiskon);
    }
    
    @FXML
    private void tutupHapusDiskon(){
        Session.setHidePane(paneHapusDiskon, paneGelap);
        Session.setMouseTransparentFalse(txtNamaDiskonTambah, cbxJenisDiskonTambah, txtPotonganHargaTambah, dtPTanggalMulaiTambah, dtPTanggalBerakhirTambah, btnIyaTambahDiskon, btnBatalTambahDiskon, btnHapusDiskon, txtSearchBar, tabelDiskon);
    }
    
    @FXML
    private void hapusDiskon(){
        try {
            String query = "DELETE FROM diskon WHERE id_diskon=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idDiskonTerpilih);
            statement.executeUpdate();
            
            getDataTabelDiskon();
            Session.animasiPanePesan(false, "Diskon berhasil dihapus");
            tutupHapusDiskon();
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

