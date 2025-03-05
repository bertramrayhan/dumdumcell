package main;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Session {
    private static final DateTimeFormatter formatTanggal = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
    private static final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    
    private static String idAdmin = "";
    private static final String pathHalamanLogin = "/login/halamanLogin.fxml";
    
    private static final String pathHalamanUtama = "/karyawan/halamanUtama/halamanUtamaK.fxml";
    private static final String pathBeranda = "/karyawan/beranda/halamanBerandaK.fxml";
    private static final String pathHalamanProfil = "/karyawan/halamanProfil/halamanProfilK.fxml";
    private static final String pathHalamanJual = "/karyawan/halamanJual/halamanJualK.fxml";

    public static void setIdAdmin(String idAdmin) {
        Session.idAdmin = idAdmin;
    }

    public static String getIdAdmin() {
        return idAdmin;
    }

    public static String getPathHalamanLogin() {
        return pathHalamanLogin;
    }

    public static String getPathHalamanUtama() {
        return pathHalamanUtama;
    }

    public static String getPathBeranda() {
        return pathBeranda;
    }

    public static String getPathHalamanProfil() {
        return pathHalamanProfil;
    }

    public static String getPathHalamanJual() {
        return pathHalamanJual;
    }
        
    public static String convertTanggalIndo(String tanggal){
        LocalDate tglExp = LocalDate.parse(tanggal, DateTimeFormatter.ISO_LOCAL_DATE);

        return tglExp.format(formatTanggal);
    }
    
    public static String convertIntToRupiah(int harga){
        return formatRupiah.format(harga);
    }
    
    public static int convertRupiahToInt(String rupiah){
        int harga = 0;
        try {
            harga = formatRupiah.parse(rupiah).intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return harga;
    }
}
