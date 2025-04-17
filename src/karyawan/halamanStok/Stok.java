package karyawan.halamanStok;

public class Stok {
    private String namaBarang, kategori, merek, hargaJual, exp, barcode;
    private int stok;

    // Constructor lengkap
    public Stok(String namaBarang, String kategori, String merek, String exp, String hargaJual, String barcode, int stok) {
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.merek = merek;
        this.hargaJual = hargaJual;
        this.exp = exp;
        this.stok = stok;
        this.barcode = barcode;
    }

    // Getter
    public String getNamaBarang() {
        return namaBarang;
    }

    public String getKategori() {
        return kategori;
    }

    public String getMerek() {
        return merek;
    }

    public String getHargaJual() {
        return hargaJual;
    }

    public String getExp() {
        return exp;
    }

    public int getStok() {
        return stok;
    }
    
    public String getBarcode() {
        return barcode;
    }

    // Setter
    public void setStok(int stok) {
        this.stok = stok;
    }
}
