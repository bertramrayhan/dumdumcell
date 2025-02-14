package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {
    private String url = "jdbc:mysql://localhost/ddckasir";
    private String username_xampp = "root";
    private String password_xampp = "";
    private static Connection con;
    
    public void connect(){ 
        try{
            con = DriverManager.getConnection(url, username_xampp, password_xampp);
            System.out.println("Koneksi berhasil!");
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.out.println(e.getMessage());
        }
        
    }
    
    public static Connection getCon(){
        return con;
    }
    
    public static void closeConnection() {
        if (con != null) {
            try {
                con.close();
                con = null;
                System.out.println("Koneksi ditutup!");
            } catch (SQLException e) {
                System.out.println("Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }
}
