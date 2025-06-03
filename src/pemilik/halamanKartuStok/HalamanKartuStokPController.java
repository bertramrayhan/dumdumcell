package pemilik.halamanKartuStok;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import main.Koneksi;
import main.Pelengkap;
import main.Session;

public class HalamanKartuStokPController implements Initializable, Pelengkap {

    @FXML private Pane paneGelap;
    @FXML private TableView<KartuStok> tableKartuStok;
    @FXML private TableColumn<KartuStok, String> colNamabarang, colKet, colTgl;
    @FXML private TableColumn<KartuStok, Integer> colMasuk, colKeluar, colSisa;
    @FXML private ComboBox<String> cbxBarang;
    @FXML private DatePicker dPtglAwal;
    @FXML private DatePicker dPtglAkhir;
    @FXML private TextField txtSearch;
    @FXML private ImageView btnX;
    
    private ObservableList<KartuStok> dataKartuStok = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTableKartuStok();
        setDatePicker();
        getinfoTableKartuStok();
        loadChoiceBoxBarang();  // ini harus dipanggil biar ChoiceBox terisi
        
        btnX.setOnMouseClicked((MouseEvent event) -> {
            txtSearch.clear(); // Hapus teks pencarian
        });
        
        // Pasang listener supaya reload data tiap filter berubah
        cbxBarang.setOnAction(e -> getinfoTableKartuStok());
        dPtglAwal.valueProperty().addListener((obs, oldV, newV) -> getinfoTableKartuStok());
        dPtglAkhir.valueProperty().addListener((obs, oldV, newV) -> getinfoTableKartuStok());
        txtSearch.textProperty().addListener((obs, oldV, newV) -> getinfoTableKartuStok());
    }    

    @Override
    public void perbarui() {
        getinfoTableKartuStok();
    }
        
    private void setTableKartuStok(){
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

            String query = "SELECT DISTINCT nama_barang FROM barang ORDER BY nama_barang";
            PreparedStatement ps = Koneksi.getCon().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String namaBarang = rs.getString("nama_barang");
                
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
    
    private void getinfoTableKartuStok(){
        dataKartuStok.clear();
        try {
            String baseQuery = "SELECT b.merek, DATE(ks.tanggal) AS tanggal, TIME(ks.tanggal) AS waktu"
            + ", ks.jumlah_masuk, ks.jumlah_keluar, ks.sisa, ks.keterangan FROM kartu_stok ks " +
            "JOIN barang b ON b.id_barang = ks.id_barang " +
            "WHERE 1=1 ";

            if (cbxBarang.getValue() != null && !cbxBarang.getValue().equals("Semua")) {
                baseQuery += "AND b.nama_barang = ? ";
            }
            if (dPtglAwal.getValue() != null && dPtglAkhir.getValue() != null) {
                baseQuery += "AND DATE(ks.tanggal) BETWEEN ? AND ? ";
            }
            if (!txtSearch.getText().isEmpty()) {
                baseQuery += "AND (b.merek LIKE ? OR ks.keterangan LIKE ?) ";
            }

            PreparedStatement statement = Koneksi.getCon().prepareStatement(baseQuery);

            int paramIndex = 1;
            if (cbxBarang.getValue() != null && !cbxBarang.getValue().equals("Semua")) {
                // Ambil id barang dari nama barang
                statement.setString(paramIndex++, cbxBarang.getValue());
            }
            if (dPtglAwal.getValue() != null && dPtglAkhir.getValue() != null) {
                statement.setString(paramIndex++, dPtglAwal.getValue().toString());
                statement.setString(paramIndex++, dPtglAkhir.getValue().toString());
            }
            if (!txtSearch.getText().isEmpty()) {
                String keyword = "%" + txtSearch.getText() + "%";
                statement.setString(paramIndex++, keyword);
                statement.setString(paramIndex++, keyword);
            }

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String namaBarang = result.getString("merek");
                String tgl = Session.convertTanggalIndo(result.getString("tanggal")) + " " + result.getString("waktu");
                int masuk = result.getInt("jumlah_masuk");
                int keluar = result.getInt("jumlah_keluar");
                int sisa = result.getInt("sisa");
                String ket = result.getString("keterangan");

                dataKartuStok.add(new KartuStok(namaBarang, tgl, masuk, keluar, sisa, ket));
            }

            result.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableKartuStok.setItems(dataKartuStok);
    }
    
    private void setDatePicker(){
        LocalDate hariIni = LocalDate.now();
        dPtglAwal.getEditor().setDisable(true);
        dPtglAwal.getEditor().setOpacity(1);
        dPtglAkhir.getEditor().setDisable(true);
        dPtglAkhir.getEditor().setOpacity(1);

        // Listener untuk dPtglAwal
        dPtglAwal.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dPtglAkhir.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        boolean isDisabled = empty || item.isBefore(newDate) || item.isAfter(hariIni);
                        setDisable(isDisabled);
                        if (isDisabled) {
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                });
            }
        });

        // Listener untuk dPtglAkhir
        dPtglAkhir.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dPtglAwal.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        boolean isDisabled = empty || item.isAfter(newDate) || item.isAfter(hariIni);
                        setDisable(isDisabled);
                        if (isDisabled) {
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                });
            }
        });

        dPtglAwal.setValue(hariIni);
        dPtglAkhir.setValue(hariIni);
    }
}
    
