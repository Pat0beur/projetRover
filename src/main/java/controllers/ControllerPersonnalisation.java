package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import app.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.ModelCar;
import models.ModelMap;

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
    private ModelMap modelmap = new ModelMap(0, 0);
    private ModelCar modelCar;
    private int  indice = 0;
    private Image[] images;
    private String[] skinPaths = {
        "/images/skins/Car_AM_General_Hummer_98x164.png",
        "/images/skins/Car_Audi_Sport_Quattro_Rally_86x145.png",
        "/images/skins/Car_Chevrolet_Camaro_ZL-1_86x145.png",
        "/images/skins/Car_DeLorean_DMC_86x145.png",
        "/images/skins/Car_Ferrari_F40_86x145.png",
        "/images/skins/Car_Ford_GT40_86x145.png",
        "/images/skins/Car_Plymouth_Hemi_Quada_98x164.png",
        "/images/skins/Lancia_Delta_Integrale_Police_125x209.png",
        "/images/skins/rover.png"
    };
    @FXML
    public void initialize() {
        modelmap = App.getModelMap();
        images = new Image[skinPaths.length];
        for(int i=0;i<skinPaths.length;i++){
            try {
                this.images[i] = new Image(getClass().getResourceAsStream(
                    skinPaths[i]));
                    System.out.println("Réussi");
                } catch (Exception e) {
                    System.out.println("Raté");
                    images[i] = getDefaultImage();
                }
            }
            this.modelCar = App.getModelCar();
            String currentSkin = modelCar.getSkin();
            
            
            for (int i = 0; i < skinPaths.length; i++) {
                if (skinPaths[i].equals(currentSkin)) {
                    indice = i;
                    break;
                }
            }
            imageView.setImage(images[indice]);
            btnRetour.setOnAction(event -> {
                String target = App.isFromPause() ? "/app/pause.fxml" : "/app/menu.fxml";
                    if(modelmap.getJeuArrete()){
                        if(modelmap.getIndiceFinPartie()==1 || modelmap.getIndiceFinPartie()==0){
                            target = "/app/perdu.fxml";
                        }
                        if(modelmap.getIndiceFinPartie()==2){
                            target = "/app/gagne.fxml";
                        }
                    }
                try {
                    Stage stage = (Stage) btnRetour.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(target)
                        );
                        Parent root = loader.load();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                
            btnValider.setOnAction(event -> {
                SetVoiture(images[indice]);
                String target = null;
            // String target = App.isFromPause() ? "/app/pause.fxml" : "/app/menu.fxml";
            
                if(modelmap.getJeuArrete()){
                    if(modelmap.getIndiceFinPartie()==1 || modelmap.getIndiceFinPartie()==0){
                        target = "/app/perdu.fxml";
                    }
                    if(modelmap.getIndiceFinPartie()==2){
                        target = "/app/gagne.fxml";
                    }
                }
                try {
                    Stage stage = (Stage) btnValider.getScene().getWindow();
                    System.out.println("La valeur de target est : "+target+" les valeurs de IndiceFinPartie : "+modelmap.getIndiceFinPartie()+" et la valeur de JeuArrete : "+modelmap.getJeuArrete());
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(target)
                    );
                    Parent root = loader.load();
                    stage.setScene(new Scene(root));
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
    @SuppressWarnings("exports")
    public void SetVoiture(Image image) {
        // Sauvegarder le chemin du skin plutôt que l'image
        modelCar.setSkinName(skinPaths[indice]);
        modelCar.notifyCarChanged(skinPaths[indice]);
    }
    private Image getDefaultImage() {
        try (InputStream is = getClass().getResourceAsStream("/images/skins/rover.png")) {
            return is != null ? new Image(is) : createFallbackImage();
        } catch (IOException e) {
            return createFallbackImage();
        }
    }
    private Image createFallbackImage() {
        // Crée une image rouge de secours
        WritableImage img = new WritableImage(100, 100);
        PixelWriter pw = img.getPixelWriter();
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                pw.setColor(x, y, Color.RED);
            }
        }
        return img;
    }
}