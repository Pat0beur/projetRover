// src/main/java/controllers/ControllerPause.java
package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import app.App;

public class ControllerPause {
    private ControllerMap parent;

    @FXML private Button btnReprendre;
    @FXML private Button btnPersonnaliser;
    @FXML private Button btnCommandes;
    @FXML private Button btnQuitter;

    /** Appelé par ControllerMap après FXML.load() */
    public void setParent(ControllerMap parent) {
        this.parent = parent;
    }

    @FXML
    public void initialize() {
        // Reprendre : ferme la boîte modale
        btnReprendre.setOnAction(e -> {
            Stage s = (Stage) btnReprendre.getScene().getWindow();
            s.close();
        });

        // Personnaliser
        btnPersonnaliser.setOnAction(e -> {
            try {
                App.setFromPause(true);
                Stage s = (Stage) btnPersonnaliser.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/app/personnalisation.fxml"));
                s.setScene(new Scene(root));
                s.setTitle("Personnalisation");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Commandes
        btnCommandes.setOnAction(e -> {
            try {
                App.setFromPause(true);
                Stage s = (Stage) btnCommandes.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/app/commandes.fxml"));
                s.setScene(new Scene(root));
                s.setTitle("Commandes");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Quitter
        btnQuitter.setOnAction(e -> System.exit(0));
    }
}
