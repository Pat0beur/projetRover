package controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    @FXML private ImageView imageView;
    private final int  indice = 1;
    private boolean suivant= false;
    private final Image image1 = new Image(getClass().getResource("/images/kiwi.jpg").toExternalForm());
    private Image[] Images = {
    new Image(getClass().getResource("/images/kiwi.jpg").toExternalForm()),
    new Image(getClass().getResource("/images/pamplemousse.jpg").toExternalForm())
    // new Image(getClass().getResource("/images/kiwi.jpg").toExternalForm())
    };

    @FXML
    public void initialize() {
        imageView.setImage(image1);
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
            suivant = true;
            ChangerImage(suivant);
        });
        btnGauche.setOnAction(event -> {
            ChangerImage(suivant);
            System.out.println("Vous avez appuyé sur le bouton de gauche");
        });
    }
    @FXML
    public void ChangerImage(boolean a){
        if(a){
            imageView.setImage(Images[indice +1]);
        }
        else {
            imageView.setImage(Images[indice -1]);
        }
    }
}