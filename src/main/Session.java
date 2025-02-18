package main;

public class Session {
    private static String idAdmin = "";
    private static final String pathHalamanLogin = "/login/halamanLogin.fxml";
    
    private static final String pathHalamanUtama = "/karyawan/halamanUtama/halamanUtamaK.fxml";
    private static final String pathBeranda = "/karyawan/beranda/halamanBerandaK.fxml";
    private static final String pathHalamanProfil = "/karyawan/halamanProfil/halamanProfilK.fxml";

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
    
}
