package karyawan.halamanTransaksiRetur;

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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.converter.IntegerStringConverter;
import main.Koneksi;
import main.Session;

public class HalamanTransaksiReturKController implements Initializable {

    @FXML private DatePicker dtPTanggalAwal, dtPTanggalAkhir;
    @FXML private ChoiceBox<String> cbxJenisTransaksi, cbxTipePengembalian;
    @FXML private ComboBox<String> cbxIdTransaksi;
    @FXML private TextArea txtAAlasanRetur;
    @FXML private TableView<Barang> tabelDetailBarang;
    @FXML private TableColumn<Barang, String> colNamaBarang, colSubtotal;
    @FXML private TableColumn<Barang, Integer> colJumlahBarang;
    @FXML private Label lblTotalRetur;
    @FXML private Button btnEdit, btnHapus, btnProses;

    private ObservableList<Barang> listDetailBarang = FXCollections.observableArrayList();
    
    //RIWAYAT RETUR
    @FXML private ChoiceBox<String> cbxJenisReturRiwayat, cbxTipePengembalianRiwayat, cbxStatusReturRiwayat;
    @FXML private DatePicker dtPTanggalAwalRiwayat, dtPTanggalAkhirRiwayat;
    @FXML private Button btnDetail;
    @FXML private TableView<Retur> tabelRiwayatRetur;
    @FXML private TableColumn<Retur, String> colKaryawan, colTanggal, colJenisRetur, colIdTransaksi, colTotalRetur, colTipePengembalian; 
    @FXML private TableColumn<Retur, String> colStatusRetur; 
    @FXML private TableColumn<Retur, HBox> colAksi; 
    @FXML private TableRow<Retur> colTerakhir;
    @FXML private ImageView imgDetail;
        
    private ObservableList<Retur> listRiwayatRetur = FXCollections.observableArrayList();
    
    //DETAIL RETUR
    @FXML private AnchorPane paneDetailRetur;
    @FXML private Label btnTutup;
    @FXML private TableView<DetailRetur> tabelDetailRetur;
    @FXML private TableColumn<DetailRetur, String> colNamaBarangDetailRetur, colJumlahBarangDetailRetur, colSubtotalDetailRetur;
    @FXML private TextArea txtAAlasanDetailRetur;
    
    private ObservableList<DetailRetur> listDetailRetur = FXCollections.observableArrayList();
    
    Barang barangTerpilih;
    String idBarangTerpilih;
    String idReturTerpilih;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setKomponen();
        setTabelDetailBarang();
        getIdTransaksi();
        
        //RIWAYAT RETUR
        setKomponenRiwayatRetur();
        setTabelRiwayatRetur();
        
        //DETAIL RETUR
        setTabelDetailRetur();
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
        
        cbxJenisTransaksi.setOnAction(event -> {
           getIdTransaksi();
           cbxTipePengembalian.getItems().clear();
           if(cbxJenisTransaksi.getValue().equals("Transaksi Jual")){
                cbxTipePengembalian.getItems().add("Uang");
                cbxTipePengembalian.getItems().add("Barang");
                cbxTipePengembalian.setValue("Uang");
           }else{
                cbxTipePengembalian.getItems().add("Barang");
                cbxTipePengembalian.setValue("Barang");
           }
        });
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
                "SELECT %s FROM %s WHERE DATE(%s) BETWEEN ? AND ? AND %s NOT IN " +
                "(SELECT %s FROM transaksi_retur WHERE %s IS NOT NULL) " +
                "ORDER BY %s DESC",
                id, tabel, tanggal, id, id, id, id
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
    
    @FXML
    private void batalRetur(){
        getDataTabelDetailBarang();
        txtAAlasanRetur.setText("");
    }
    
    private void setTotalRetur(){
        int totalRetur = 0;
        for(Barang brg : listDetailBarang){
            totalRetur += Session.convertRupiahToInt(brg.getSubtotal());
        }
        
        lblTotalRetur.setText(Session.convertIntToRupiah(totalRetur));
    }
    
    @FXML
    private void prosesRetur(){
        if(listDetailBarang.isEmpty()){
            Session.animasiPanePesan(true, "Tabel retur kosong", btnProses);
            return;
        }

        String idTransaksi = cbxIdTransaksi.getValue();
        String jenisTransaksi = cbxJenisTransaksi.getValue();
        String newIdRetur = Session.membuatIdBaru("transaksi_retur", "id_retur", "rtr", 3);
        String pengembalian = cbxTipePengembalian.getValue();
        String alasanRetur = txtAAlasanRetur.getText();
        String totalRetur = lblTotalRetur.getText();
        
        try {
            String query = "INSERT INTO transaksi_retur VALUES (?,?,?,?,?,NOW(),?,?,?,?)";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, newIdRetur);
            statement.setString(2, Session.getIdAdmin());
            statement.setString(3, jenisTransaksi);
            if(jenisTransaksi.equals("Transaksi Jual")){
                statement.setString(4, idTransaksi);
                statement.setNull(5, java.sql.Types.CHAR);
            }else{
                statement.setNull(4, java.sql.Types.CHAR);
                statement.setString(5, idTransaksi);
            }
            statement.setInt(6, Session.convertRupiahToInt(totalRetur));
            statement.setString(7, pengembalian);
            statement.setString(8, alasanRetur);
            statement.setString(9, "Diproses");
            
            statement.executeUpdate();
            
            for(Barang barang : listDetailBarang){
                query = "INSERT INTO detail_retur VALUES (?,?,?,?)";
                statement = Koneksi.getCon().prepareStatement(query);
                statement.setString(1, newIdRetur);
                statement.setString(2, barang.getIdBarang());
                statement.setInt(3, barang.getJumlahBarang());
                statement.setInt(4, Session.convertRupiahToInt(barang.getSubtotal()));
                
                statement.executeUpdate();
            }
            
            getIdTransaksi();
            Session.animasiPanePesan(false, "Retur berhasil diproses", btnProses);
            txtAAlasanRetur.setText("");
            
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //RIWAYAT RETUR
    public class Retur{
        String idRetur, karyawan, tanggal, jenisRetur, idTransaksi, totalRetur, tipePengembalian, statusRetur;
        HBox aksi;
        public Retur(String idRetur, String karyawan, String tanggal, String jenisRetur, String idTransaksi, String tipePengembalian, String totalRetur, String statusRetur, HBox aksi) {
            this.idRetur = idRetur;this.karyawan = karyawan;this.tanggal = tanggal;this.jenisRetur = jenisRetur;this.idTransaksi = idTransaksi;this.totalRetur = totalRetur;this.tipePengembalian = tipePengembalian;this.statusRetur = statusRetur;this.aksi = aksi;
        }
        public String getIdRetur() {return idRetur;}
        public String getKaryawan() {return karyawan;}
        public String getTanggal() {return tanggal;}
        public String getJenisRetur() {return jenisRetur;}
        public String getIdTransaksi() {return idTransaksi;}
        public String getTotalRetur() {return totalRetur;}
        public String getTipePengembalian() {return tipePengembalian;}
        public String getStatusRetur() {return statusRetur;}
        public HBox getAksi() {return aksi;}
        public void setAksi(HBox aksi) {this.aksi = aksi;}
    }
    
    private void setKomponenRiwayatRetur(){
        cbxJenisReturRiwayat.getItems().addAll("Semua", "Transaksi Jual", "Transaksi Beli");
        cbxJenisReturRiwayat.setValue("Semua");
        cbxTipePengembalianRiwayat.getItems().addAll("Semua", "Uang", "Barang");
        cbxTipePengembalianRiwayat.setValue("Semua");
        cbxStatusReturRiwayat.getItems().addAll("Semua", "Selesai", "Diproses", "Ditolak");
        cbxStatusReturRiwayat.setValue("Semua");
        
        dtPTanggalAwalRiwayat.getEditor().setDisable(true);
        dtPTanggalAwalRiwayat.getEditor().setOpacity(1);
        dtPTanggalAkhirRiwayat.getEditor().setDisable(true);
        dtPTanggalAkhirRiwayat.getEditor().setOpacity(1);
        LocalDate hariIni = LocalDate.now();
        
        // Listener untuk dtPTanggalAwalRiwayat
        dtPTanggalAwalRiwayat.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dtPTanggalAkhirRiwayat.setDayCellFactory(picker -> new DateCell() {
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

        // Listener untuk dtPTanggalAkhirRiwayat
        dtPTanggalAkhirRiwayat.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dtPTanggalAwalRiwayat.setDayCellFactory(picker -> new DateCell() {
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
        
        dtPTanggalAwalRiwayat.setValue(hariIni);
        dtPTanggalAkhirRiwayat.setValue(hariIni);
        
        cbxJenisReturRiwayat.setOnAction(event -> {
            getDataTabelRiwayatRetur();
        });
        cbxTipePengembalianRiwayat.setOnAction(event -> {
            getDataTabelRiwayatRetur();
        });
        cbxStatusReturRiwayat.setOnAction(event -> {
            getDataTabelRiwayatRetur();
        });
        
        btnDetail.setDisable(true);
        imgDetail.setOpacity(0.5F);
        
        tabelRiwayatRetur.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                btnDetail.setDisable(false);
                imgDetail.setOpacity(1F);
            } else {
                btnDetail.setDisable(true);
                imgDetail.setOpacity(0.5F);
            }
        });
    }
    
    private void setTabelRiwayatRetur(){
        colKaryawan.setCellValueFactory(new PropertyValueFactory<>("karyawan"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJenisRetur.setCellValueFactory(new PropertyValueFactory<>("jenisRetur"));
        colIdTransaksi.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
        colTipePengembalian.setCellValueFactory(new PropertyValueFactory<>("tipePengembalian"));
        colTotalRetur.setCellValueFactory(new PropertyValueFactory<>("totalRetur"));
        colStatusRetur.setCellValueFactory(new PropertyValueFactory<>("statusRetur"));
        colAksi.setCellValueFactory(new PropertyValueFactory<>("aksi"));
        
        tabelRiwayatRetur.setRowFactory(tv -> new TableRow<Retur>() {
            @Override
            protected void updateItem(Retur item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                    setOnMousePressed(null);
                    setOnMouseReleased(null);
                } else {
                    String status = item.getStatusRetur();

                    // Set default background color according to status
                    if (status.equals("Ditolak")) {
                        setStyle("-fx-background-color: #ffcccc; -fx-text-fill: black;");
                    } else if (status.equals("Selesai")) {
                        setStyle("-fx-background-color: #ccffcc; -fx-text-fill: black;");
                    }
                    
                    // Add mouse press/release listeners to change styles on press
                    setOnMousePressed(event -> {
                        if(colTerakhir != null){
                            if(colTerakhir.getItem().getStatusRetur().equals("Ditolak")){
                                colTerakhir.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: black;");
                            }else if(colTerakhir.getItem().getStatusRetur().equals("Selesai")){
                                colTerakhir.setStyle("-fx-background-color: #ccffcc; -fx-text-fill: black;");
                            }
                        }
                        
                        if (status.equals("Ditolak")) {
                            setStyle("-fx-background-color: #e57373; -fx-text-fill: black;");
                        } else if (status.equals("Selesai")) {
                            setStyle("-fx-background-color: #81c784; -fx-text-fill: black;");
                        }
                        colTerakhir = this;
                    });
                }
            }
        });
        
        tabelRiwayatRetur.setOnMouseClicked(event -> {
            Retur retur = tabelRiwayatRetur.getSelectionModel().getSelectedItem();
            if (retur != null) {
                idReturTerpilih = retur.getIdRetur();
            }
        });
    }
    
    @FXML
    private void getDataTabelRiwayatRetur() {
        colTerakhir = null;
        listRiwayatRetur.clear();
        String jenisRetur = cbxJenisReturRiwayat.getValue();
        String tipePengembalian = cbxTipePengembalianRiwayat.getValue();
        String statusRetur = cbxStatusReturRiwayat.getValue();
        String tanggalAwal = dtPTanggalAwalRiwayat.getValue().toString();
        String tanggalAkhir = dtPTanggalAkhirRiwayat.getValue().toString();
        
        String query = "SELECT tr.id_retur, adm.username, tr.id_transaksi_jual, tr.id_transaksi_beli,\n" +
        "DATE(tr.tanggal_retur) AS tanggal, TIME(tr.tanggal_retur) AS waktu,\n" +
        "tr.total_retur, tr.pengembalian, tr.status_retur, tr.jenis_retur\n" +
        "FROM transaksi_retur tr\n" +
        "JOIN admin adm ON adm.id_admin = tr.id_admin\n" +
        "WHERE DATE(tr.tanggal_retur) BETWEEN ? AND ?\n";
        
        query += "AND tr.pengembalian LIKE ? AND tr.status_retur LIKE ? AND tr.jenis_retur LIKE ?";

        try (PreparedStatement statement = Koneksi.getCon().prepareStatement(query)) {
            statement.setString(1, tanggalAwal);
            statement.setString(2, tanggalAkhir);
            statement.setString(3, tipePengembalian.equals("Semua") ? "%%" : "%" + tipePengembalian + "%");
            statement.setString(4, statusRetur.equals("Semua") ? "%%" : "%" + statusRetur + "%");
            statement.setString(5, jenisRetur.equals("Semua") ? "%%" : "%" + jenisRetur + "%");

            ResultSet result = statement.executeQuery();
            while (result.next()) {
               String idRetur = result.getString("id_retur");
               String karyawan = result.getString("adm.username");
               String idTransaksiJual = result.getString("id_transaksi_jual");
               String idTransaksiBeli = result.getString("id_transaksi_beli");
               String idTransaksi = "";
               if(idTransaksiJual != null){
                   idTransaksi = idTransaksiJual;
               }else{
                   idTransaksi = idTransaksiBeli;
               }
               String tanggal = Session.convertTanggalIndo(result.getString("tanggal")) + " " + result.getString("waktu");
               String totalRetur = Session.convertIntToRupiah(result.getInt("total_retur"));
               String pengembalian = result.getString("pengembalian");
               String status = result.getString("status_retur");
               String jenis = result.getString("jenis_retur");

               HBox aksiBox = new HBox(10);
               aksiBox.setAlignment(Pos.CENTER);
               if(status.equals("Diproses")){
                   Button konfirmasi = new Button();
                   konfirmasi.setStyle(
                       "-fx-background-color: #4CAF50;" +
                       "-fx-text-fill: white;" +
                       "-fx-font-weight: bold;" +
                       "-fx-font-size: 11px;" +
                       "-fx-pref-width: 26px;" +
                       "-fx-pref-height: 26px;" +
                       "-fx-background-radius: 5px;" +
                       "-fx-cursor: hand;" +
                       "-fx-padding: 0;"
                   );

                   Button tolak = new Button();
                   tolak.setStyle(
                       "-fx-background-color: #F44336;" +
                       "-fx-text-fill: white;" +               
                       "-fx-font-weight: bold;" +
                       "-fx-font-size: 11px;" +
                       "-fx-pref-width: 26px;" +
                       "-fx-pref-height: 26px;" +
                       "-fx-background-radius: 5px;" +
                       "-fx-cursor: hand;" +
                       "-fx-padding: 0;"
                   );

                   ImageView iconKonfirmasi = new ImageView(new Image(getClass().getResource("/assets/icons/v16px.png").toExternalForm()));
                   iconKonfirmasi.setFitHeight(16);
                   iconKonfirmasi.setFitWidth(16);
                   konfirmasi.setGraphic(iconKonfirmasi);

                   ImageView iconTolak = new ImageView(new Image(getClass().getResource("/assets/icons/x16px.png").toExternalForm()));
                   iconTolak.setFitHeight(16);
                   iconTolak.setFitWidth(16);
                   tolak.setGraphic(iconTolak);

                   aksiBox.getChildren().addAll(konfirmasi, tolak);

                   konfirmasi.setOnAction(event -> {
                       try {
                           String queryKonfirmasi = "UPDATE transaksi_retur SET status_retur=? WHERE id_retur=?";
                           PreparedStatement statementKonfirmasi = Koneksi.getCon().prepareStatement(queryKonfirmasi);
                           statementKonfirmasi.setString(1, "Selesai");
                           statementKonfirmasi.setString(2, idRetur);
                           statementKonfirmasi.executeUpdate();

                           statementKonfirmasi.close();
                           getDataTabelRiwayatRetur();
                       } catch (SQLException e) {
                           e.printStackTrace();
                       } 
                   });

                   tolak.setOnAction(event -> {
                       try {
                           String queryTolak = "UPDATE transaksi_retur SET status_retur=? WHERE id_retur=?";
                           PreparedStatement statementTolak = Koneksi.getCon().prepareStatement(queryTolak);
                           statementTolak.setString(1, "Ditolak");
                           statementTolak.setString(2, idRetur);
                           statementTolak.executeUpdate();

                           statementTolak.close();
                           getDataTabelRiwayatRetur();
                       } catch (SQLException e) {
                           e.printStackTrace();
                       } 
                   });
               }
               Retur retur = new Retur(idRetur, karyawan, tanggal, jenis, idTransaksi, pengembalian, totalRetur, status, aksiBox);
               listRiwayatRetur.add(retur);
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabelRiwayatRetur.setItems(listRiwayatRetur);
    }
    
    //DETAIL RETUR
    public class DetailRetur{
        String namaBarang, jumlahBarang, subtotal;

        public DetailRetur(String namaBarang, String jumlahBarang, String subtotal) {
            this.namaBarang = namaBarang;this.jumlahBarang = jumlahBarang;this.subtotal = subtotal;
        }
        public String getNamaBarang() {return namaBarang;}
        public String getJumlahBarang() {return jumlahBarang;}
        public String getSubtotal() {return subtotal;}
    }
    
    private void setTabelDetailRetur(){
        colNamaBarangDetailRetur.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colJumlahBarangDetailRetur.setCellValueFactory(new PropertyValueFactory<>("jumlahBarang"));
        colSubtotalDetailRetur.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }
    
    @FXML
    private void bukaDetailRetur(){
        Session.setShowPane(paneDetailRetur);
        btnDetail.setDisable(true);
        imgDetail.setOpacity(0.5F);
        
        listDetailRetur.clear();
        try {
            String query = "SELECT brg.merek AS nama_barang, dr.jumlah AS jumlah_barang, "
            + "dr.subtotal AS subtotal, tr.alasan_retur AS alasan\n" +
            "FROM detail_retur dr\n" +
            "JOIN barang brg ON brg.id_barang = dr.id_barang\n" +
            "JOIN transaksi_retur tr ON tr.id_retur = dr.id_retur\n" +
            "WHERE dr.id_retur = ?"
            + "ORDER BY dr.jumlah ASC";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idReturTerpilih);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                String catatan = result.getString("alasan");
                txtAAlasanDetailRetur.setText(catatan);

                do {
                    String namaBarang = result.getString("nama_barang");
                    String jumlahBarang = result.getString("jumlah_barang");
                    String subtotal = Session.convertIntToRupiah(result.getInt("subtotal"));

                    DetailRetur detailRetur = new DetailRetur(namaBarang, jumlahBarang, subtotal);
                    listDetailRetur.add(detailRetur);
                } while (result.next());
            }
            
            result.close();
            statement.close();
            
            tabelDetailRetur.setItems(listDetailRetur);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void tutupDetailRetur(){
        Session.setHidePane(paneDetailRetur);
    }
}