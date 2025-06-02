package controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class ControllerMenu {

    @FXML private Button btnJouer;
    @FXML private Button btnPersonnaliser;
    @FXML private Button btnCommandes;
    @FXML private Button btnQuitter;

    @FXML private RadioButton radiobtnEasy;
    @FXML private RadioButton radiobtnMedium;
    @FXML private RadioButton radiobtnHard;

    @FXML
    public void initialize() {
        System.out.println("Contrôleur Menu.initialisé !");

        // Bouton Quitter : ferme l'application
        btnQuitter.setOnAction(event -> {
            System.out.println("Fermeture de l'application");
            System.exit(0);
        });

        // Bouton Personnaliser : charge la vue Personnalisation.fxml
        btnPersonnaliser.setOnAction(event -> {
            try {
                Stage stage = (Stage) btnPersonnaliser.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/Personnalisation.fxml"));
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
                Stage stage = (Stage) btnCommandes.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/Commandes.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
