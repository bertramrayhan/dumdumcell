package karyawan.beranda;

public class Barang {
    private String nama, total, expired;

    public Barang(String nama, String total, String expired) {
        this.nama = nama;
        this.total = total;
        this.expired = expired;
    }

    public String getNama() {
        return nama;
    }

    public String getTotal() {
        return total;
    }

    public String getExpired() {
        return expired;
    }
}