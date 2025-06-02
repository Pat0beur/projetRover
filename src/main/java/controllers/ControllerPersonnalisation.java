package controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Contrôleur pour la vue Personnalisation.fxml
 */
public class ControllerPersonnalisation {

    @FXML private Label  labelPersonnalisation;
    @FXML private Button btnGauche;
    @FXML private Button btnDroite;
    @FXML private Button btnRetour;
    @FXML private Button btnValider;

    @FXML
    public void initialize() {
        System.out.println("ControllerPersonnalisation.initialize() appelé");

        btnRetour.setOnAction(event -> {
            try {
                Stage stage = (Stage) btnRetour.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/app/Menu.fxml")
                );
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnValider.setOnAction(event -> {
            try {
                Stage stage = (Stage) btnValider.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/app/Menu.fxml")
                );
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        btnDroite.setOnAction(event -> {
            System.out.println("Vous avez appuyé sur le bouton de droite");
        });
        btnGauche.setOnAction(event -> {
            System.out.println("Vous avez appuyé sur le bouton de gauche");
        });
    }
}