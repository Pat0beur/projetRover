package controllers;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
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
    private int  indice = 0;
    private Image[] images = {
    new Image(getClass().getResource("/images/kiwi.jpg").toExternalForm()),
    new Image(getClass().getResource("/images/pamplemousse.jpg").toExternalForm())
    // new Image(getClass().getResource("/images/kiwi.jpg").toExternalForm())
    };

    @FXML
    public void initialize() {
        // imageView = new ImageView();
        URL url = getClass().getResource("/images/kiwi.jpg");
        if (url != null) {
            Image img = new Image(url.toExternalForm(), false);
            imageView.setImage(img);
            imageView.setVisible(true); 
            imageView.setManaged(true);
            System.out.println("Je suis là");
        } else {
            System.out.println("Image introuvable !");
        }
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
            idSuivant();
        });
        btnGauche.setOnAction(event -> {
            idPrecedent();
        });
    }

    @FXML
    public void idSuivant(){
            indice = (indice + 1) % images.length;
            imageView.setImage(images[indice]);
            // imageView.setImage(images[0]);
            imageView.setVisible(true);
            System.out.println("Je suis dans idSuivant");
    }
    @FXML
    public void idPrecedent(){
        if(indice==0){
            indice = images.length-1;
        }
        else{
            indice = (indice - 1) % images.length;
        }
        imageView.setImage(images[indice]);
        imageView.setVisible(true);
        imageView.setManaged(true);
        System.out.println("Je suis dans idPrecedent");
    }
}   