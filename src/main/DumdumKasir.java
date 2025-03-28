package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DumdumKasir extends Application {
    public static Stage halamanUtama;
    
    @Override
    public void start(Stage stage) throws Exception {
        halamanUtama = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Session.getPathHalamanLogin()));
        Parent root = loader.load();
               
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

    public static void switchToBeranda(String role) throws Exception {        
        Stage newStage = new Stage();
        newStage.initStyle(StageStyle.UNDECORATED); // Hilangin title bar
        
        String path = role.equals("karyawan") ? Session.getPathHalamanUtamaK() : Session.getPathHalamanUtamaP();
        System.out.println(path);
        Parent root = FXMLLoader.load(DumdumKasir.class.getResource(path));
        Scene scene = new Scene(root);

        newStage.setScene(scene);
        newStage.centerOnScreen();
        newStage.show();
        halamanUtama.close();
        
        halamanUtama = newStage;
    }
    
    public static void main(String[] args) {
        Koneksi koneksi = new Koneksi();
        koneksi.connect();
        
        launch(args);
    }
    
}
