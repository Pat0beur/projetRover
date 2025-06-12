// src/main/java/app/views/ViewMap.java
package views;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * ViewMap est responsable de charger map.fxml et d’afficher
 * cette vue dans le Stage passé en paramètre.
 */
public class ViewMap {

    // Chemin relatif à partir de resources/...
    private static final String FXML_PATH = "/app/map.fxml";

    private final Stage stage;

    /**
     * Constructeur : on passe le Stage existant (par exemple celui du menu).
     * @param stageStage le Stage sur lequel on veut afficher la carte.
     */
    @SuppressWarnings ("exports")
    public ViewMap(Stage stageStage) {
        this.stage = stageStage;
    }

    /**
     * Charge map.fxml, crée la Scene et la fixe sur le Stage existant.
     */
    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
            Parent root = loader.load();

            // Créer la scène à 800×600
            Scene sceneMap = new Scene(root, 800, 600);

            // Donner le focus au root pour que ControllerMap capte le clavier
            root.requestFocus();

            // Appliquer la nouvelle scène sur le stage fourni
            stage.setScene(sceneMap);

            // Si le stage n’était pas déjà visible, on le montre
            if (!stage.isShowing()) {
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
