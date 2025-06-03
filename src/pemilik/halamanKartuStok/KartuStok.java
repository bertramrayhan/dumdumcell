package pemilik.halamanKartuStok;

public class KartuStok {

    private String namaBarang;
    private String tgl;
    private int masuk;
    private int keluar;
    private int sisa;
    private String ket;
    

    // Constructor lengkap
    public KartuStok(String namaBarang, String tgl, int masuk, int keluar, int sisa, String ket) {
        this.namaBarang = namaBarang;
        this.tgl = tgl;
        this.masuk = masuk;
        this.keluar = keluar;
        this.sisa = sisa;
        this.ket = ket;
        
    }
    
    public String getNamaBarang() {
        return namaBarang;
    }

    public String getTgl() {
        return tgl;
    }

      public int getMasuk() {
        return masuk;
    }

    public int getKeluar() {
        return keluar;
    }

    public int getSisa() {
        return sisa;
    }

    public String getKet() {
        return ket;
    }
}