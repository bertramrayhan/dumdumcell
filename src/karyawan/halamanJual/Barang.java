package karyawan.halamanJual;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import main.Session;

public class Barang{
    private String barcode, barang, harga, diskon, subtotal;
    private int qty;
    private Button batal;
    
    public Barang(String barcode, String barang, int harga, int qty, int diskon, Button batal) {
        this.barcode = barcode;
        this.barang = barang;
        this.harga = Session.convertIntToRupiah(harga);
        this.qty = qty;
        this.diskon = Session.convertIntToRupiah(diskon);
        this.subtotal = Session.convertIntToRupiah((harga * qty) - diskon);
        
        this.batal = batal;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getBarang() {
        return barang;
    }

    public String getHarga() {
        return harga;
    }

    public String getDiskon() {
        return diskon;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public int getQty() {
        return qty;
    }

    public Button getBatal() {
        return batal;
    }
    
    public void setQty(int qty){
        if(qty == 0){
            HalamanJualKController.listBarang.remove(this);
        }
        this.qty = qty;
        setSubtotal();
    }
    
    public void setSubtotal(){
        this.subtotal = Session.convertIntToRupiah(this.qty * Session.convertRupiahToInt(this.harga) - Session.convertRupiahToInt(this.diskon));
    }
    
    @Override
    public String toString() {
        return "Nama Barang : " + this.barang; 
    }    
    
}