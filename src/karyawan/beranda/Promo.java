package karyawan.beranda;

public class Promo{
    private String promo, potonganHarga;

    public Promo(String promo, String potonganHarga) {
        this.promo = promo;
        this.potonganHarga = potonganHarga;
    }

    public String getPromo() {
        return promo;
    }

    public String getPotonganHarga() {
        return potonganHarga;
    }
}
