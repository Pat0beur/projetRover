package controllers;

import java.io.IOException;

import app.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ControllerCommandes {

    @FXML private Button btnRetour;

    @FXML
    public void initialize() {
        System.out.println("ControllerCommande.initialize() appelé");

        btnRetour.setOnAction(event -> {
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            String target = App.isFromPause() ? "/app/pause.fxml" : "/app/menu.fxml";
            switch(target){
                case "/app/pause.fxml":  stage.setTitle("Pause"); break;
                case "/app/menu.fxml":  stage.setTitle("Menu"); break;
            }
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(target)
                );
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
