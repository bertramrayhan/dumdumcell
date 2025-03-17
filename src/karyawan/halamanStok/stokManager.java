/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package karyawan.halamanStok;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Koneksi;
import main.Session;

/**
 *
 * @author indra
 */
public class stokManager {
    
     public void hapusStokJikaHabis(String namaBarang) {
        String query = "DELETE FROM barang WHERE nama_barang = ? AND stok <= 0"; // Pastikan stok <= 0
    try (PreparedStatement statement = Koneksi.getCon().prepareStatement(query)) {
        statement.setString(1, namaBarang);
        int rowsAffected = statement.executeUpdate();
        
        if (rowsAffected > 0) {
            System.out.println("Barang " + namaBarang + " dihapus karena stok habis.");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
 }
     private String getOrderBy(String sortOption) {
    if (sortOption == null || sortOption.equals("Sort by")) {
        return ""; // Tidak ada sorting
    }
    switch (sortOption) {
                case "Nama Barang (A-Z)":
            return " ORDER BY b.nama_barang ASC";
        case "Nama Barang (Z-A)":
            return " ORDER BY b.nama_barang DESC";
        case "Barang Hampir Expired":
            return " ORDER BY b.exp ASC";
        case "Kategori":
            return " ORDER BY k.nama_kategori ASC";
        case "Stok Terbanyak":
            return " ORDER BY b.stok DESC";
        case "Stok Terdikit":
            return " ORDER BY b.stok ASC";
        default:
            return "";
        }
    }
    public ObservableList<Stok> getAllStok(String sortOption, String keyword) {
        ObservableList<Stok> listStok = FXCollections.observableArrayList();
        
        // Query dasar
    String query = "SELECT b.nama_barang, b.harga_jual, b.stok, b.tipe, b.exp, b.merek, k.nama_kategori " +
                   "FROM barang b " +
                   "JOIN kategori k ON b.id_kategori = k.id_kategori";
    
    // Cek apakah keyword angka atau teks
    boolean isAngka = keyword != null && keyword.matches("\\d+"); // Deteksi angka
    boolean isSearch = keyword != null && !keyword.trim().isEmpty();
    
     if (isSearch) {
        if (isAngka) {
           query += " WHERE (b.stok = ? OR YEAR(b.exp) LIKE ?)";
        } else {
            query += " WHERE (b.nama_barang LIKE ? OR k.nama_kategori LIKE ? OR b.merek LIKE ? OR b.tipe LIKE ?)";
        }
    }
     
    // Tambahkan sorting
    query += getOrderBy(sortOption);
    
        try (PreparedStatement statement = Koneksi.getCon().prepareStatement(query)) {
        if (isSearch) {
            if (isAngka) {
                int angka = Integer.parseInt(keyword);
                statement.setInt(1, angka); // stok
                 statement.setString(2, "%" + angka + "%"); // tahun exp
            } else {
                String searchKeyword = "%" + keyword + "%";
                statement.setString(1, searchKeyword);
                statement.setString(2, searchKeyword);
                statement.setString(3, searchKeyword);
                statement.setString(4, searchKeyword);
            }
        }

         ResultSet result = statement.executeQuery();
         while (result.next()) {
            String namaBarang = result.getString("nama_barang");
            String kategori = result.getString("nama_kategori");
            String hargaJual = Session.convertIntToRupiah(result.getInt("harga_jual"));
            String tipe = result.getString("tipe");
            int stok = result.getInt("stok");
            String exp = Session.convertTanggalIndo(result.getString("exp"));
            String merek = result.getString("merek");

            listStok.add(new Stok(namaBarang, kategori, merek, tipe, exp, hargaJual, stok));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
        return listStok;
 }
}
