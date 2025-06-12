package controllers;

import java.io.IOException;
import app.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.ModelMap;
import views.ViewMap;
import models.Model;
import models.ModelCar; 

public class ControllerFinPartie {
    @FXML private Button btnPersonnalisation;
    @FXML private Button btnQuitter;
    @FXML private Button btnRejouer;
    @FXML private Label scoreLabel;
    private ModelMap modelMap;
    private ModelCar modelCar;
    private Model model;

    @FXML
    public void initialize(){
        modelMap = App.getModelMap();
        modelCar = App.getModelCar();
        model = App.getModel();
        if(modelMap.getIndiceFinPartie()==2){
        scoreLabel.setText(model.getScore()+"");
        }
        btnPersonnalisation.setOnAction(event -> {
            try {
                Stage stage = (Stage) btnPersonnalisation.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/personnalisation.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setTitle("Personnalisation");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        btnRejouer.setOnAction(event -> {
            App.resetGame();
            Stage currentStage = (Stage) btnRejouer.getScene().getWindow();
            currentStage.setTitle("En jeu");
            new ViewMap(currentStage).show();
        });
        btnQuitter.setOnAction(event -> {
            System.exit(0);
        });
    }
}
