package controllers;

import java.io.IOException;

import app.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import models.Model;
import models.ModelCar;
import views.ViewMap;

public class ControllerMenu {

    @FXML private Button btnJouer;
    @FXML private Button btnPersonnaliser;
    @FXML private Button btnCommandes;
    @FXML private Button btnQuitter;

    @FXML private RadioButton radiobtnEasy;
    @FXML private RadioButton radiobtnMedium;
    @FXML private RadioButton radiobtnHard;
    private ModelCar modelCar;
    private Model model;

    @FXML
    public void initialize() {
        this.modelCar = App.getModelCar();
        this.model = new Model(); //Initialise le modèle
        // this.model = App.getModel();
        System.out.println("Contrôleur Menu.initialisé !");
        // Bouton Quitter : ferme l'application
        btnQuitter.setOnAction(event -> {
            System.out.println("Fermeture de l'application");
            System.exit(0);
        });

        // Bouton Personnaliser : charge la vue Personnalisation.fxml
        btnPersonnaliser.setOnAction(event -> {
            try {
                App.setFromPause(false);
                Stage stage = (Stage) btnPersonnaliser.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/personnalisation.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Bouton Commandes : charge la vue Commandes.fxml
        btnCommandes.setOnAction(event -> {
            try {
                App.setFromPause(false);
                Stage stage = (Stage) btnCommandes.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/commandes.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnJouer.setOnAction(event -> {
            Stage currentStage = (Stage) btnJouer.getScene().getWindow();
            new ViewMap(currentStage).show();
        });
        radiobtnEasy.setOnAction(event -> {
            radiobtnHard.setSelected(false);
            radiobtnMedium.setSelected(false);
            this.model = new Model(1);
            this.model = App.getModel();
        });
        radiobtnMedium.setOnAction(event -> {
            radiobtnHard.setSelected(false);
            radiobtnEasy.setSelected(false);
            this.model = new Model(2);
            this.model = App.getModel();
        });
        radiobtnHard.setOnAction(event -> {
            radiobtnEasy.setSelected(false);
            radiobtnMedium.setSelected(false);
            this.model = new Model(3);
            this.model = App.getModel();
        });
    }
    public Model getModel(){
        return model;
    }
    public void Jouer(){
        Stage currentStage = (Stage) btnJouer.getScene().getWindow();
        new ViewMap(currentStage).show();
    }
}
