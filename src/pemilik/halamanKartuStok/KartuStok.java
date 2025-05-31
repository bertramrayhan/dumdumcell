
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pemilik.halamanKartuStok;

import java.time.LocalDate;

public class KartuStok {

    private String idBarang;
    private String namaBarang;
    private String tgl;
    private int masuk;
    private int keluar;
    private int sisa;
    private String ket;
    

    // Constructor lengkap
    public KartuStok(String idBarang, String namaBarang, String tgl, int masuk, int keluar, int sisa, String ket ) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.tgl = tgl;
        this.masuk = masuk;
        this.keluar = keluar;
        this.sisa = sisa;
        this.ket = ket;
        
    }

    // Getter
    public String getIdBarang() {
        return idBarang;
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
    
  

    // Setter (opsional, kalau kamu butuh edit data nanti)
    public void setSisa(int sisa) {
        this.sisa = sisa;
    }
}