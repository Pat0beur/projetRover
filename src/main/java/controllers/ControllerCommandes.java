package controllers;

import java.io.IOException;
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
            try {
                // Récupérer la Stage courante depuis n’importe quel nœud (ici btnRetour)
                Stage stage = (Stage) btnRetour.getScene().getWindow();

                // Charger Menu.fxml depuis src/resources/app/Menu.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/Menu.fxml"));
                Parent root = loader.load();

                // Créer une nouvelle Scene et l’assigner à la Stage
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
