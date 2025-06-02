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

    // Exemple de variable d'état : index de l'élément à personnaliser
    private int indexOption = 0;

    @FXML
    public void initialize() {
        System.out.println("✅ ControllerPersonnalisation.initialize() appelé");

        // Initialiser le label (ou tout autre composant) si besoin
        labelPersonnalisation.setText("Personnalisation (option " + indexOption + ")");

        // Bouton Retour : revenir à Menu.fxml
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

        // Bouton Valider : appliquer la personnalisation et revenir au menu ou lancer le jeu
        btnValider.setOnAction(event -> {
            // System.out.println("✔ Option personnalisée : " + indexOption);
            // Ici, enregistrez l'option choisie ou transmettez-la au modèle,
            // puis revenez à la vue Menu ou lancez la partie.
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
    }
}
