package pemilik.halamanTransaksiBeli;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import main.Koneksi;
import main.Session;

public class HalamanTransaksiBeliPController implements Initializable {
    
    @FXML private StackPane panePesan;
    @FXML private Label lblTanggal, lblJam, lblKasir, lblTotal, lblKembalian, lblPesan;
    @FXML private TextField txtBarcode, txtHargaBeliBarcode, txtQtyBarcode;
    @FXML private TextField txtHargaBeliManual, txtQtyManual, txtBayar;//txtBayar onKeyReleased
    @FXML private TextArea txtACatatan;
    @FXML private Button btnTambahProdukBarcode, btnTambahProdukManual;
    @FXML private Button btnBatalTransaksi, btnKonfirmasiTransaksi;
    @FXML private ChoiceBox<String> cbxKategori, cbxSupplier, cbxCaraBayar;
    @FXML private ComboBox<String> cbxProduk;
    @FXML private TableView<Barang> tabelBarang;
    @FXML private TableColumn<Barang, String> colBarcode, colBarang, colSubtotal;
    @FXML private TableColumn<Barang, Integer> colHarga, colQty;
    @FXML private TableColumn<Barang, Button> colBatal;
    
    private ObservableList<Barang> listBarang = FXCollections.observableArrayList();
    
    //RIWAYAT TRANSAKSI
    @FXML private Tab tabRiwayatTransaksi;
    @FXML private Label lblTotalPembelianBarang;
    @FXML private ChoiceBox<String> cbxShift;
    @FXML private DatePicker dtPTanggalAwal, dtPTanggalAkhir;
    @FXML private Button btnDetail;
    @FXML private ImageView imgDetail;
    @FXML private TableView<Transaksi> tabelTransaksi;
    @FXML private TableColumn<Transaksi, String> colKaryawan, colSupplier, colTanggal, colWaktu, colJenisPembayaran, colTotalPembelian, colKembalian;
    
    private ObservableList<Transaksi> listTransaksi = FXCollections.observableArrayList();
    
    //DETAIL TRANSAKSI
    @FXML AnchorPane paneDetailTransaksi;
    @FXML Label btnTutup;
    @FXML TableView<DetailTransaksi> tabelDetailTransaksi;
    @FXML TableColumn<DetailTransaksi, String> colNamaBarangDetail, colJumlahBarangDetail, colSubtotalDetail;
    @FXML TextArea txtACatatanDetail;
    
    ObservableList<DetailTransaksi> listDetailTransaksi = FXCollections.observableArrayList();
    
    private final DateTimeFormatter formatWaktu = DateTimeFormatter.ofPattern("HH:mm");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setKasir();
        setWaktuDanTanggal();
        setTextFieldNumeric();
        setCbxTransaksi();
        setTabelBarang();
        cbxKategori.setOnAction(event -> {
            setCbxProduk();
        });
        cbxProduk.setVisibleRowCount(15);
        
        //RIWAYAT TRANSAKSI
        setKomponenRiwayatTransaksi();
        setTabelTransaksi();
        
        //DETAIL TRANSAKSI
        setTabelDetailTransaksi();
    }    
    
    
    public class Barang{
        private String idBarang, barcode, barang, subtotal;
        private int harga, qty;
        private Button batal;
        public Barang(String idBarang, String barcode, String barang, int harga, int qty, Button batal) {
            this.idBarang = idBarang;this.barcode = barcode;this.barang = barang;this.harga = harga;this.qty = qty;this.subtotal = Session.convertIntToRupiah((harga * qty));this.batal = batal;
        }
        public String getIdBarang() {return idBarang;}
        public String getBarcode() {return barcode;}
        public String getBarang() {return barang;}
        public int getHarga() {return harga;}
        public String getSubtotal() {return subtotal;}
        public int getQty() {return qty;}
        public Button getBatal() {return batal;}
        public void setHarga(int harga) {this.harga = harga;setSubtotal();}
        public void setQty(int qty){
            if(qty == 0){
                listBarang.remove(this);
            }
            this.qty = qty;
            setSubtotal();
        }
        public void setSubtotal(){this.subtotal = Session.convertIntToRupiah(this.qty * this.harga);}
        @Override
        public String toString() {return "Nama Barang : " + this.barang;}    
    }
    
    private void setTabelBarang(){
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colBarang.setCellValueFactory(new PropertyValueFactory<>("barang"));
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colBatal.setCellValueFactory(new PropertyValueFactory<>("batal"));

        tabelBarang.setEditable(true);

        colHarga.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colQty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        
        colHarga.setCellFactory(col -> {
            return new TextFieldTableCell<Barang, Integer>(new IntegerStringConverter()) {
                private TextField textField;
                
                @Override
                public void startEdit() {
                    super.startEdit();
                    textField = (TextField) getGraphic();

                    if (textField != null) {
                        Session.setTextFieldNumeric(textField); // batasi input angka aja

                        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                            if (!isNowFocused) {
                                String input = textField.getText().trim();
                                if (input.isEmpty() || input.equals("0")) {
                                    Barang barang = getTableView().getItems().get(getIndex());
                                    commitEdit(barang.getHarga());
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
                public void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && item != null) {
                        setText(Session.convertIntToRupiah(item)); // format ke rupiah
                    } else {
                        setText(null);
                    }
                }

                @Override
                public void commitEdit(Integer newValue) {
                    if(newValue != 0 || newValue == null){
                        super.commitEdit(newValue);
                        Barang barang = getTableView().getItems().get(getIndex());
                        barang.setHarga(newValue);
                        tabelBarang.refresh();
                    }
                }
            };
        });

        colQty.setCellFactory(col -> {
            return new TextFieldTableCell<Barang, Integer>(new IntegerStringConverter()) {
                private TextField textField;
                
                @Override
                public void startEdit() {
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
                        Barang barang = getTableView().getItems().get(getIndex());
                        if (newValue == 0) {
                            listBarang.remove(barang);
                        } else {
                            super.commitEdit(newValue);
                            barang.setQty(newValue);
                        }
                        tabelBarang.setItems(listBarang);
                        getTableView().refresh();
                    }
                }
            };
        });
    }
    
    @FXML
    public void refresh(){
        txtBarcode.setText("");
        txtHargaBeliBarcode.setText("");
        txtQtyBarcode.setText("");
        txtHargaBeliManual.setText("");
        txtQtyManual.setText("");
        txtBayar.setText("");
        txtACatatan.setText("");
        listBarang.clear();
        tabelBarang.setItems(listBarang);
        tabelBarang.refresh();
        setKembalian();
        setTotal();
    }
    
    private void setWaktuDanTanggal() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime now = LocalTime.now();
            lblJam.setText(now.format(formatWaktu));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        lblTanggal.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
    
    private void setKasir(){
        try {
            String query = "SELECT username FROM admin WHERE id_admin=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, Session.getIdAdmin());
            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                lblKasir.setText(result.getString("username"));
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setTextFieldNumeric(){
        Session.setTextFieldNumeric(txtBayar);
        Session.setTextFieldNumeric(txtBarcode, 13);
        Session.setTextFieldNumeric(txtQtyBarcode);
        Session.setTextFieldNumeric(txtHargaBeliBarcode);
        Session.setTextFieldNumeric(txtQtyManual);
        Session.setTextFieldNumeric(txtHargaBeliManual);
    }
    
    private void setCbxTransaksi(){
        //combo box cara bayar
        cbxCaraBayar.getItems().addAll("Tunai", "Transfer");
        cbxCaraBayar.setValue("Tunai");

        try {
            cbxKategori.getItems().add("Semua");
            
            //combo box kategori
            String query = "SELECT nama_kategori FROM kategori";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);

            ResultSet result = statement.executeQuery();
            while(result.next()){
                cbxKategori.getItems().add(result.getString("nama_kategori"));
            }
            
            if (!cbxKategori.getItems().isEmpty()) {
                cbxKategori.setValue(cbxKategori.getItems().get(0));
            }
            
            //combo box supplier
            query = "SELECT nama_supplier, nama_toko FROM supplier";
            statement = Koneksi.getCon().prepareStatement(query);
            
            result = statement.executeQuery();
            while(result.next()){
                String namaSupplier = result.getString("nama_supplier");
                String namaToko = result.getString("nama_toko");
                cbxSupplier.getItems().add(namaSupplier + ", " + namaToko);
            }
            
            if (!cbxSupplier.getItems().isEmpty()) {
                cbxSupplier.setValue(cbxSupplier.getItems().get(0));
            }
            
            //combo box produk
            setCbxProduk();
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setCbxProduk(){
        cbxProduk.getItems().clear();
        cbxProduk.setValue(null);
        try {
            //combo box produk
            String query = "SELECT merek FROM barang ";
            if (!cbxKategori.getValue().equals("Semua")) {
                query += "WHERE id_kategori = (SELECT id_kategori FROM kategori WHERE nama_kategori=?) ";
            }
            
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            if (!cbxKategori.getValue().equals("Semua")) {
                statement.setString(1, cbxKategori.getValue());
            }
            
            ResultSet result = statement.executeQuery();
            
            while(result.next()){
                cbxProduk.getItems().add(result.getString("merek"));
            }
            
            if (!cbxProduk.getItems().isEmpty()) {
                cbxProduk.setValue(cbxProduk.getItems().get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void tambahBarisTabelBarang(MouseEvent event) {
        Button btn = (Button) event.getSource();
        if (btn.getId().equals("btnTambahProdukManual")) {
            if(cbxProduk.getValue() == null){
                Session.animasiPanePesan(true, panePesan, lblPesan, "Produk tidak ditemukan", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                return;
            }else if(txtHargaBeliManual.getText().isEmpty() || txtHargaBeliManual.getText().equals("0")){
                Session.animasiPanePesan(true, panePesan, lblPesan, "Masukkan harga beli produk", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                return;
            }else if (txtQtyManual.getText().isEmpty() || txtQtyManual.getText().equals("0")) {
                Session.animasiPanePesan(true, panePesan, lblPesan, "Masukkan jumlah produk yang dipesan", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                return;
            }else{
                String namaProduk = cbxProduk.getValue();
                int hargaBeli = Integer.parseInt(txtHargaBeliManual.getText());
                int qty = Integer.parseInt(txtQtyManual.getText());
                tambahProduk(namaProduk, hargaBeli, qty, false);
            }
        } else if (btn.getId().equals("btnTambahProdukBarcode")) {
            if(txtBarcode.getText().trim().isEmpty()){
                Session.animasiPanePesan(true, panePesan, lblPesan, "Masukkan barcode produk", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                return;
            }else if (txtHargaBeliBarcode.getText().isEmpty() || txtHargaBeliBarcode.getText().equals("0")) {
                Session.animasiPanePesan(true, panePesan, lblPesan, "Masukkan harga beli produk", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                return;
            }else if (txtQtyBarcode.getText().isEmpty() || txtQtyBarcode.getText().equals("0")) {
                Session.animasiPanePesan(true, panePesan, lblPesan, "Masukkan jumlah produk yang dipesan", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                return;
            } else {
                String inpBarcode = txtBarcode.getText().trim();
                int hargaBeli = Integer.parseInt(txtHargaBeliBarcode.getText());
                int qty = Integer.parseInt(txtQtyBarcode.getText());
                tambahProduk(inpBarcode, hargaBeli, qty, true);
            }
        }

        tabelBarang.setItems(listBarang);
        tabelBarang.refresh();
    }

    private void tambahProduk(String identifier, int hargaBeli, int qty, boolean isBarcode) {
        try {
            boolean produkAda = false;
            for (Barang barang : listBarang) {
                if (isBarcode) {
                    if (barang.getBarcode().equals(identifier)) {
                        barang.setHarga(hargaBeli);
                        barang.setQty(barang.getQty() + qty);
                        setTotal();
                        produkAda = true;
                        break;
                    }
                } else {
                    if (barang.getBarang().equals(identifier)) {
                        barang.setHarga(hargaBeli);
                        barang.setQty(barang.getQty() + qty);
                        setTotal();
                        produkAda = true;
                        break;
                    }
                }
            }

            if (!produkAda) {
                String query = "SELECT b.id_barang, b.barcode, b.merek\n" +
                               "FROM barang b " +
                               "WHERE " + (isBarcode ? "b.barcode = ? " : "b.merek = ? ");
//                               "AND b.exp > DATE(NOW())"
                PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
                statement.setString(1, identifier);

                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    String idBarang = result.getString("id_barang");
                    String barcode = result.getString("barcode");
                    String merek = result.getString("merek");
                    Button batal = new Button();
                    batal.setStyle(
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

                    ImageView iconHapus = new ImageView(new Image(getClass().getResource("/assets/icons/delete16px.png").toExternalForm()));
                    iconHapus.setFitHeight(16);
                    iconHapus.setFitWidth(16);
                    batal.setGraphic(iconHapus);
                    
                    Barang barang = new Barang(idBarang, barcode, merek, hargaBeli, qty, batal);

                    batal.setOnAction(e -> {
                        listBarang.remove(barang);
                        setTotal();
                    });
                    listBarang.add(barang);
                }else{
                    Session.animasiPanePesan(true, panePesan, lblPesan, "Produk tidak ditemukan", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
                }

                result.close();
                statement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Session.animasiPanePesan(true, panePesan, lblPesan, "Terjadi kesalahan: " + e.getMessage(), btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
        }

        if (isBarcode) {
            txtBarcode.setText("");
            txtHargaBeliBarcode.setText("");
            txtQtyBarcode.setText("");
        } else {
            txtHargaBeliManual.setText("");
            txtQtyManual.setText("");
        }
        setTotal();
    }
    
    private void setTotal(){
        int total = 0;
        
        for(Barang barang : listBarang){
            total += Session.convertRupiahToInt(barang.getSubtotal());
        }
        lblTotal.setText(Session.convertIntToRupiah(total));
        
        setKembalian();
    }
    
    @FXML
    private void setKembalian(){
        int bayar = txtBayar.getText().isEmpty() ? 0 : Integer.parseInt(txtBayar.getText());
        int total = Session.convertRupiahToInt(lblTotal.getText());
        
        lblKembalian.setText(Session.convertIntToRupiah(bayar - total));
    }
    
    @FXML
    private void konfirmasiTransaksi() {
        if (listBarang.isEmpty()) {
            Session.animasiPanePesan(true, panePesan, lblPesan, "Tabel pesan masih kosong", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
            return;
        } else if (lblKembalian.getText().contains("-")) {
            Session.animasiPanePesan(true, panePesan, lblPesan, "Pembayaran kurang", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
            return;
        }

        String idTransaksiBaru = Session.membuatIdBaru("transaksi_beli", "id_transaksi_beli", "beli", 4);

        try {
            int total = Session.convertRupiahToInt(lblTotal.getText());
            int kembalian = Session.convertRupiahToInt(lblKembalian.getText());

            String query = "INSERT INTO transaksi_beli VALUES (?,?,?,NOW(),?,?,?,?)";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idTransaksiBaru);
            statement.setString(2, Session.getIdAdmin());
            String namaSupplier = cbxSupplier.getValue();
            statement.setString(3, Session.getId("supplier", "id_supplier", "nama_supplier", namaSupplier.split(",\\s*")[0]));
            statement.setString(4, cbxCaraBayar.getValue());
            statement.setInt(5, total);
            statement.setInt(6, kembalian);
            statement.setString(7, txtACatatan.getText().trim());

            statement.executeUpdate();

            for (Barang barang : listBarang) {
                query = "INSERT INTO detail_transaksi_beli VALUES (?,?,?,?)";
                statement = Koneksi.getCon().prepareStatement(query);
                statement.setString(1, idTransaksiBaru);
                statement.setString(2, barang.getIdBarang());
                statement.setInt(3, barang.getQty());
                statement.setInt(4, Session.convertRupiahToInt(barang.getSubtotal()));
                statement.executeUpdate();
            }

            statement.close();
            refresh();
            Session.animasiPanePesan(false, panePesan, lblPesan, "Transaksi berhasil!", btnTambahProdukManual, btnTambahProdukBarcode, btnBatalTransaksi, btnKonfirmasiTransaksi);
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    //RIWAYAT TRANSAKSI
    public class Transaksi{
        String idTransaksi, karyawan, supplier, tanggal, waktu, jenisPembayaran, totalPembelian, kembalian;

        public Transaksi(String idTransaksi, String karyawan, String supplier, String tanggal, String waktu, String jenisPembayaran, 
                String totalPembelian, String kembalian) {
            this.idTransaksi = idTransaksi;this.karyawan = karyawan;this.supplier = supplier;this.tanggal = tanggal;this.waktu = waktu;this.jenisPembayaran = jenisPembayaran;this.totalPembelian = totalPembelian;this.kembalian = kembalian;
        }
        public String getIdTransaksi() {return idTransaksi;}
        public String getKaryawan() {return karyawan;}
        public String getTanggal() {return tanggal;}
        public String getWaktu() {return waktu;}
        public String getJenisPembayaran() {return jenisPembayaran;}
        public String getSupplier() {return supplier;}
        public String getTotalPembelian() {return totalPembelian;}
        public String getKembalian() {return kembalian;}
        @Override
        public String toString() {
            return String.format(
            "Transaksi [Karyawan: %s, Supplier: %s, Tanggal: %s, Waktu: %s, Jenis Pembayaran: %s, Total Pembelian: %s, Kembalian: %s]",
            karyawan, supplier, tanggal, waktu, jenisPembayaran, totalPembelian, kembalian);
        }
    }
    
    private void setKomponenRiwayatTransaksi(){
        cbxShift.getItems().addAll("Pagi", "Malam");
        cbxShift.setValue("Pagi");
        cbxShift.setOnAction(event -> {
            getSemuaTransaksi();
        });
        
        btnDetail.setDisable(true);
        imgDetail.setOpacity(0.5F);
                
        dtPTanggalAwal.getEditor().setDisable(true);
        dtPTanggalAwal.getEditor().setOpacity(1);
        
        dtPTanggalAkhir.getEditor().setDisable(true);
        dtPTanggalAkhir.getEditor().setOpacity(1);

        dtPTanggalAwal.setValue(LocalDate.now());
        dtPTanggalAkhir.setValue(LocalDate.now());

        // Setup DatePicker Tanggal Awal
        dtPTanggalAwal.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                // Blokir tanggal di masa depan
                if (item.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Warna merah muda buat disabled
                }

                // Blokir kalau tanggal awal lebih dari tanggal akhir
                if (dtPTanggalAkhir.getValue() != null && item.isAfter(dtPTanggalAkhir.getValue())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Setup DatePicker Tanggal Akhir
        dtPTanggalAkhir.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                // Blokir tanggal di masa depan
                if (item.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }

                // Blokir kalau tanggal akhir kurang dari tanggal awal
                if (dtPTanggalAwal.getValue() != null && item.isBefore(dtPTanggalAwal.getValue())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Listener untuk update DatePicker saat ada perubahan nilai
        dtPTanggalAwal.valueProperty().addListener((obs, oldValue, newValue) -> {
            dtPTanggalAkhir.setDayCellFactory(dp -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item.isAfter(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }

                    if (dtPTanggalAwal.getValue() != null && item.isBefore(dtPTanggalAwal.getValue())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });
        });

        dtPTanggalAkhir.valueProperty().addListener((obs, oldValue, newValue) -> {
            dtPTanggalAwal.setDayCellFactory(dp -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item.isAfter(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }

                    if (dtPTanggalAkhir.getValue() != null && item.isAfter(dtPTanggalAkhir.getValue())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });
        });
    }
    
    private void setTabelTransaksi(){
        colKaryawan.setCellValueFactory(new PropertyValueFactory<>("karyawan"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colWaktu.setCellValueFactory(new PropertyValueFactory<>("waktu"));
        colJenisPembayaran.setCellValueFactory(new PropertyValueFactory<>("jenisPembayaran"));
        colTotalPembelian.setCellValueFactory(new PropertyValueFactory<>("totalPembelian"));
        colKembalian.setCellValueFactory(new PropertyValueFactory<>("kembalian"));
        
        tabelTransaksi.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                btnDetail.setDisable(false);
                imgDetail.setOpacity(1F);
            } else {
                btnDetail.setDisable(true);
                imgDetail.setOpacity(0.5F);
            }
        });
    }
    
    @FXML
    private void getSemuaTransaksi(){
        String shift = cbxShift.getValue();
        String tanggalAwal = dtPTanggalAwal.getValue().toString();
        String tanggalAkhir = dtPTanggalAkhir.getValue().toString();
        
        if(tanggalAwal.isEmpty() || tanggalAkhir.isEmpty()){
            return;
        }
        
        String[] waktuShift = getWaktuShift(shift);
        listTransaksi.clear();
        try {
            String query = "SELECT tb.id_transaksi_beli AS id_transaksi, adm.username AS username, DATE(tb.tanggal_transaksi_beli) AS tanggal, \n" +
            "TIME(tb.tanggal_transaksi_beli) AS waktu, tb.cara_bayar AS jenis_pembayaran,\n" +
            "spl.nama_supplier AS supplier, tb.total_transaksi_beli AS total_pembelian,\n" +
            "tb.kembalian AS kembalian\n" +
            "FROM transaksi_beli tb\n" +
            "JOIN admin adm ON adm.id_admin = tb.id_admin\n" +
            "LEFT JOIN supplier spl ON spl.id_supplier = tb.id_supplier\n" +
            "WHERE DATE(tanggal_transaksi_beli) BETWEEN ? AND ?\n" +
            "AND TIME(tb.tanggal_transaksi_beli) BETWEEN ? AND ?\n" +
            "ORDER BY tanggal_transaksi_beli ASC";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, tanggalAwal);
            statement.setString(2, tanggalAkhir);
            statement.setString(3, waktuShift[0]);
            statement.setString(4, waktuShift[1]);
            ResultSet result = statement.executeQuery();
            
            int totalPembelianBarang = 0;
            
            while(result.next()) {
                String idTransaksi = result.getString("id_transaksi");
                String karyawan = result.getString("username");
                String tanggal = Session.convertTanggalIndo(result.getString("tanggal"));
                String waktu = result.getString("waktu");
                String jenisPembayaran = result.getString("jenis_pembayaran");
                String supplier = result.getString("supplier");
                int totalPembelian = result.getInt("total_pembelian");
                String kembalian = Session.convertIntToRupiah(result.getInt("kembalian"));
                
                Transaksi trs = new Transaksi(idTransaksi, karyawan, supplier, tanggal, waktu, jenisPembayaran, 
                Session.convertIntToRupiah(totalPembelian), kembalian);
                listTransaksi.add(trs);
                
                totalPembelianBarang += totalPembelian;
            }
            
            lblTotalPembelianBarang.setText(Session.convertIntToRupiah(totalPembelianBarang));
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        tabelTransaksi.setItems(listTransaksi);
        tabelTransaksi.refresh();
    }
    
    private String[] getWaktuShift(String shift){
        String[] waktuShift = new String[2];
        
        try {
            String query = "SELECT jam_masuk, jam_selesai FROM shift WHERE nama_shift=?";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, shift);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                waktuShift[0] = result.getString("jam_masuk");
                waktuShift[1] = result.getString("jam_selesai");
            }
            
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return waktuShift;
    }
    
    //DETAIL TRANSAKSI
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
        colNamaBarangDetail.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colJumlahBarangDetail.setCellValueFactory(new PropertyValueFactory<>("JumlahBarang"));
        colSubtotalDetail.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }
    
    @FXML
    private void bukaDetailTransaksi(){
        Session.setShowPane(paneDetailTransaksi);
        
        listDetailTransaksi.clear();
        try {
            int barisTerpilih = tabelTransaksi.getSelectionModel().getSelectedIndex();
            Transaksi transaksiTerpilih = listTransaksi.get(barisTerpilih);
            String idTransaksiTerpilih = transaksiTerpilih.getIdTransaksi();
            
            String query = "SELECT brg.merek AS nama_barang, dtb.jumlah_barang AS jumlah_barang, "
            + "dtb.subtotal AS subtotal, tb.catatan AS catatan\n" +
            "FROM detail_transaksi_beli dtb\n" +
            "JOIN barang brg ON brg.id_barang = dtb.id_barang\n" +
            "JOIN transaksi_beli tb ON tb.id_transaksi_beli = dtb.id_transaksi_beli\n" +
            "WHERE dtb.id_transaksi_beli = ?"
            + "ORDER BY dtb.jumlah_barang ASC";
            PreparedStatement statement = Koneksi.getCon().prepareStatement(query);
            statement.setString(1, idTransaksiTerpilih);
            ResultSet result = statement.executeQuery();
                        
            if (result.next()) {
                String catatan = result.getString("catatan");
                txtACatatanDetail.setText(catatan);

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
    
    @FXML
    private void tutupDetailTransaksi(){
        Session.setHidePane(paneDetailTransaksi);
    }    
}
