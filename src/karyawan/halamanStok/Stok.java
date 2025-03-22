/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package karyawan.halamanStok;

import main.Session;


/**
 *
 * @author indra
 */
public class Stok {
    private String namaBarang, kategori, merek, tipe, hargaJual, exp;
    private int stok;

    // Constructor lengkap
    public Stok(String namaBarang, String kategori, String merek, String tipe, String exp, String hargaJual, int stok) {
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.merek = merek;
        this.tipe = tipe;
        this.hargaJual = hargaJual;
        this.exp = exp;
        this.stok = stok;
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

    public String getTipe() {
        return tipe;
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

    // Setter
    public void setStok(int stok) {
        this.stok = stok;
    }
}
