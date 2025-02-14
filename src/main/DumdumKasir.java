package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DumdumKasir extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/login/halamanLogin.fxml"));
        
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
    
    public static void main(String[] args) {
        Koneksi koneksi = new Koneksi();
        koneksi.connect();
        
        launch(args);
    }
    
}
