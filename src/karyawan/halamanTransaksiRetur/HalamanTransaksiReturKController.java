package karyawan.halamanTransaksiRetur;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import main.Koneksi;
import main.Session;

public class HalamanTransaksiReturKController implements Initializable {

    @FXML private DatePicker dtPTanggalAwal, dtPTanggalAkhir;
    @FXML private ChoiceBox<String> cbxJenisTransaksi, cbxTipePengembalian;
    @FXML private ComboBox<String> cbxIdTransaksi;
    @FXML private TextArea txtAKeterangan;
    @FXML private TableView<Barang> tabelDetailBarang;
    @FXML private TableColumn<Barang, String> colNamaBarang, colSubtotal;
    @FXML private TableColumn<Barang, Integer> colJumlahBarang;
    @FXML private Label lblTotalRetur;
    @FXML private Button btnEdit, btnHapus;

    private ObservableList<Barang> listDetailBarang = FXCollections.observableArrayList();
    
    Barang barangTerpilih;
    String idBarangTerpilih;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setKomponen();
        setTabelDetailBarang();
        getIdTransaksi();
    }
    
    public class Barang{
        String idBarang, namaBarang, subtotal;
        int jumlahBarang, hargaJual;
        int batasJumlahBarang;

        public Barang(String idBarang, String namaBarang, int jumlahBarang, int hargaJual, String subtotal) {
            this.idBarang = idBarang;this.namaBarang = namaBarang;this.jumlahBarang = jumlahBarang;this.hargaJual = hargaJual;this.subtotal = subtotal;this.batasJumlahBarang=this.jumlahBarang;}
        public String getIdBarang() {return idBarang;}
        public String getNamaBarang() {return namaBarang;}
        public int getJumlahBarang() {return jumlahBarang;}
        public int gethargaJual() {return hargaJual;}
        public String getSubtotal() {return subtotal;}
        public void setJumlahBarang(int newJumlahBarang){
            if(newJumlahBarang <= batasJumlahBarang){this.jumlahBarang=newJumlahBarang;setSubtotal();}
        }
        public void setSubtotal(){this.subtotal=Session.convertIntToRupiah(jumlahBarang * hargaJual);}
    }
    
    private void setKomponen(){
        dtPTanggalAwal.getEditor().setDisable(true);
        dtPTanggalAwal.getEditor().setOpacity(1);
        dtPTanggalAkhir.getEditor().setDisable(true);
        dtPTanggalAkhir.getEditor().setOpacity(1);
        LocalDate hariIni = LocalDate.now();
        
        // Listener untuk dtPTanggalAwal
        dtPTanggalAwal.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dtPTanggalAkhir.setDayCellFactory(picker -> new DateCell() {
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

        // Listener untuk dtPTanggalAkhir
        dtPTanggalAkhir.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dtPTanggalAwal.setDayCellFactory(picker -> new DateCell() {
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
        
        dtPTanggalAwal.setValue(hariIni);
        dtPTanggalAkhir.setValue(hariIni);
        
        cbxJenisTransaksi.getItems().add("Transaksi Jual");
        cbxJenisTransaksi.getItems().add("Transaksi Beli");
        cbxJenisTransaksi.setValue("Transaksi Jual");
        
        cbxTipePengembalian.getItems().add("Uang");
        cbxTipePengembalian.getItems().add("Barang");
        cbxTipePengembalian.setValue("Uang");
        
        cbxIdTransaksi.setVisibleRowCount(15);
    }
    
    private void setTabelDetailBarang(){
        colNamaBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colJumlahBarang.setCellValueFactory(new PropertyValueFactory<>("jumlahBarang"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tabelDetailBarang.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                btnEdit.setDisable(false);
                btnHapus.setDisable(false);
            } else {
                btnEdit.setDisable(true);
                btnHapus.setDisable(true);
            }
        });
        
        tabelDetailBarang.setOnMouseClicked(event ->{
            int barisTerpilih = tabelDetailBarang.getSelectionModel().getSelectedIndex();
            if (barisTerpilih != -1) {
                barangTerpilih = tabelDetailBarang.getItems().get(barisTerpilih);
                idBarangTerpilih = barangTerpilih.getIdBarang();
            }
        });
        
        colJumlahBarang.setCellFactory(column -> {
            return new TextFieldTableCell<Barang, Integer>(new IntegerStringConverter()) {
                private TextField textField;
                private Integer oldValue;

                @Override
                public void startEdit() {
                    oldValue = getItem(); // simpan nilai lama
                    super.startEdit();
                    textField = (TextField) getGraphic();

                    if (textField != null) {
                        Session.setTextFieldNumeric(textField); // batasi input angka aja

                        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                            if (!isNowFocused) {
                                String input = textField.getText().trim();
                                if (input.isEmpty()) {
                                    cancelEdit(); // balikin ke nilai lama
                                } else {
                                    try {
                                        commitEdit(Integer.parseInt(input));
                                    } catch (NumberFormatException e) {
                                        cancelEdit();
                                    }
                                }
                            }
                        });
                    }
                }

                @Override
                public void commitEdit(Integer newValue) {
                    if (newValue == null) {
                        cancelEdit();
                    } else {
                        // Cek apakah nilai 0, kalau iya, hapus barisnya
                        if (newValue == 0) {
                            listDetailBarang.remove(barangTerpilih);
                        } else {
                            super.commitEdit(newValue);
                            // Update data model
                            Barang barang = getTableView().getItems().get(getIndex());
                            barang.setJumlahBarang(newValue);
                        }
                        tabelDetailBarang.setItems(listDetailBarang);
                        getTableView().refresh();
                        setTotalRetur();
                    }
                }
            };
        });
        
        btnEdit.setOnAction(event -> {
            // Pastikan ada row yang dipilih
            Barang selectedBarang = tabelDetailBarang.getSelectionModel().getSelectedItem();
            if (selectedBarang != null) {
                // Trigger edit mode untuk cell yang dipilih
                tabelDetailBarang.edit(tabelDetailBarang.getSelectionModel().getSelectedIndex(), colJumlahBarang);
            }
        });
        
        btnHapus.setOnAction(event -> {
            listDetailBarang.remove(barangTerpilih);
            tabelDetailBarang.setItems(listDetailBarang);
            setTotalRetur();
        });        
    }
    
    @FXML
    private void getIdTransaksi(){
        String tanggalAwal = dtPTanggalAwal.getValue().toString();
        String tanggalAkhir = dtPTanggalAkhir.getValue().toString();
        String jenisTransaksi = cbxJenisTransaksi.getValue();
        
        cbxIdTransaksi.getItems().clear();
        try {
            String id = jenisTransaksi.equals("Transaksi Jual") ? "id_transaksi_jual" : "id_transaksi_beli";
            String tabel = jenisTransaksi.equals("Transaksi Jual") ? "transaksi_jual" : "transaksi_beli";
            String tanggal = jenisTransaksi.equals("Transaksi Jual") ? "tanggal_transaksi_jual" : "tanggal_transaksi_beli";

            String query = String.format(
                "SELECT %s FROM %s WHERE DATE(%s) BETWEEN ? AND ? ORDER BY %s DESC",
                id, tabel, tanggal, id
            );

            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, tanggalAwal);
            statement.setString(2, tanggalAkhir);

            ResultSet result = statement.executeQuery();
            
            while(result.next()) {
                cbxIdTransaksi.getItems().add(result.getString(id));
            }
            
            if(!cbxIdTransaksi.getItems().isEmpty()){
                cbxIdTransaksi.setValue(cbxIdTransaksi.getItems().get(0));
            }
            
            getDataTabelDetailBarang();
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void getDataTabelDetailBarang(){
        String idTransaksiTerpilih = cbxIdTransaksi.getValue();
        String jenisTransaksi = cbxJenisTransaksi.getValue();
        
        listDetailBarang.clear();
        if(idTransaksiTerpilih == null || idTransaksiTerpilih.isEmpty()){
            return;
        }
        
        try {
            String id = jenisTransaksi.equals("Transaksi Jual") ? "id_transaksi_jual" : "id_transaksi_beli";
            String tabel = jenisTransaksi.equals("Transaksi Jual") ? "detail_transaksi_jual" : "detail_transaksi_beli";
            String singkatan = jenisTransaksi.equals("Transaksi Jual") ? "dtj" : "dtb";
            
            String query = String.format("SELECT %s.id_barang, brg.merek, brg.harga_jual,\n" +
            "%s.jumlah_barang, %s.subtotal\n" +
            "FROM %s %s\n" +
            "JOIN barang brg ON brg.id_barang = %s.id_barang\n" +
            "WHERE %s.%s=?" , singkatan, singkatan, singkatan, tabel, singkatan, singkatan, singkatan, id);
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idTransaksiTerpilih);
            
            ResultSet result = statement.executeQuery();
            
            while(result.next()) {
                String idBarang = result.getString(singkatan + ".id_barang");
                String namaBarang = result.getString("brg.merek");
                int hargaJual = result.getInt("brg.harga_jual");
                int jumlahBarang = result.getInt(singkatan + ".jumlah_barang");
                String subtotal = Session.convertIntToRupiah(result.getInt(singkatan + ".subtotal"));
                
                Barang brg = new Barang(idBarang, namaBarang, jumlahBarang, hargaJual, subtotal);
                listDetailBarang.add(brg);
            }
            
            tabelDetailBarang.setItems(listDetailBarang);
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTotalRetur();
    }
    
    private void setTotalRetur(){
        int totalRetur = 0;
        for(Barang brg : listDetailBarang){
            totalRetur += Session.convertRupiahToInt(brg.getSubtotal());
        }
        
        lblTotalRetur.setText(Session.convertIntToRupiah(totalRetur));
    }
    
}