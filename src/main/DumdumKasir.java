package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DumdumKasir extends Application {
    private static Stage halamanUtama;
    
    @Override
    public void start(Stage stage) throws Exception {
        halamanUtama = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/karyawan/halamanUtama/halamanUtamaK.fxml")); // /login/halamanLogin.fxml
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        //stage.setMaximized(true);
        stage.show();
        
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Aplikasi ditutup, menutup koneksi...");
        Koneksi.closeConnection();
    }

    public static void switchToBeranda() throws Exception {
        Stage newStage = new Stage();
        newStage.initStyle(StageStyle.UNDECORATED); // Hilangin title bar

        Parent root = FXMLLoader.load(DumdumKasir.class.getResource("/karyawan/halamanUtama/halamanUtamaK.fxml"));
        Scene scene = new Scene(root);

        newStage.setScene(scene);
        newStage.show();
        halamanUtama.close(); // Tutup login setelah sukses
        
        halamanUtama = newStage;
    }
    
    public static void main(String[] args) {
        Koneksi koneksi = new Koneksi();
        koneksi.connect();
        
        launch(args);
    }
    
}
