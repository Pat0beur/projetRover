package controllers;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.ModeleCar;

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
    private ModeleCar modeleCar;
    private int  indice = 0;
    private String VoitureSelectionne;
    private Image[] images = {
    new Image(getClass().getResource("/images/skins/Car_AM_General_Hummer_98x164.png").toExternalForm()),
    new Image(getClass().getResource("/images/skins/Car_Audi_Sport_Quattro_Rally_86x145.png").toExternalForm()),
    new Image(getClass().getResource("/images/skins/Car_Chevrolet_Camaro_ZL-1_86x145.png").toExternalForm()),
    new Image(getClass().getResource("/images/skins/Car_DeLorean_DMC_86x145.png").toExternalForm()),
    new Image(getClass().getResource("/images/skins/Car_Ferrari_F40_86x145.png").toExternalForm()),
    new Image(getClass().getResource("/images/skins/Car_Ford_GT40_86x145.png").toExternalForm()),
    new Image(getClass().getResource("/images/skins/Car_Plymouth_Hemi_Quada_98x164.png").toExternalForm()),
    new Image(getClass().getResource("/images/skins/Lancia_Delta_Integrale_Police_125x209.png").toExternalForm()),
    new Image(getClass().getResource("/images/skins/rover.png").toExternalForm()),
    };

    @FXML
    public void initialize() {
        
        //Affiche par défaut le premier élément dans le tableau images
        imageView.setImage(images[0]);
        imageView.setVisible(true); 
        imageView.setManaged(true);
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
                SetVoiture(imageView.getImage()); // Récupère l'image 
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
            imageView.setVisible(true);
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
    }
    public void SetVoiture(Image image){
        modeleCar.notifyCarChanged(image);
    }
}   