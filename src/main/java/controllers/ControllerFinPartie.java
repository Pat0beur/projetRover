package controllers;

import java.io.IOException;

import app.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import models.ModelMap;
import views.ViewMap;

public class ControllerFinPartie {
    @FXML private Button btnPersonnalisation;
    @FXML private Button btnQuitter;
    @FXML private Button btnRejouer;
    private ModelMap modelMap;

    @FXML
    public void initialize(){
        modelMap = App.getModelMap();
        btnPersonnalisation.setOnAction(event -> {
            try {
                Stage stage = (Stage) btnPersonnalisation.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/personnalisation.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        btnRejouer.setOnAction(event -> {
            Stage currentStage = (Stage) btnRejouer.getScene().getWindow();
            modelMap = App.setModelMap();
            new ViewMap(currentStage).show();
        });
        btnQuitter.setOnAction(event -> {
            System.exit(0);
        });
    }
}
