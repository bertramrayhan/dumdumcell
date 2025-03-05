package karyawan.beranda;

public class Barang {
    private String nama, harga, total, expired;

    public Barang(String nama, String harga, String total, String expired) {
        this.nama = nama;
        this.harga = harga;
        this.total = total;
        this.expired = expired;
    }

    public String getNama() {
        return nama;
    }

    public String getHarga() {
        return harga;
    }

    public String getTotal() {
        return total;
    }

    public String getExpired() {
        return expired;
    }
}