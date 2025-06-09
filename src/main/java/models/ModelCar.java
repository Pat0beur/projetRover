package models;

import java.util.ArrayList;
import java.util.List;

import controllers.ControllerMenu;
import controllers.ControllerPersonnalisation;
import javafx.scene.image.Image;
import java.util.function.Consumer;


public class ModelCar {
    private ModelCar modelCar;
    private String[] Inventaire;
    private String Skin;
    private int Batterie;
    private boolean EtatBatterie;
    private final List<Consumer<Image>> listeners = new ArrayList<>();

    // private ControllerPersonnalisation controllerPersonnalisation;
    public ModelCar(int difficulté){
        this.Skin = "/images/skins/Car_AM_General_Hummer_98x164.png";
        this.Batterie = 100;
        this.EtatBatterie = true;
        // this.Inventaire = ;
    }
    // public void notifyCarChanged(String image){
    //     Skin = image;
    // }
    
    public String getSkin(){
        return Skin;
    }
    public void setSkinName(String skin) {
        Skin = skin;

    }
    public void notifyCarChanged(String imagePath) {
        this.Skin = imagePath;
        Image image = new Image(getClass().getResource(imagePath).toExternalForm());

        for (java.util.function.Consumer<Image> listener : listeners) {
            listener.accept(image);
        }
    }
    public void notifyCarChanged(Image image) {
        for (Consumer<Image> listener : listeners) {
            listener.accept(image);
        }
    }
    public void addCarListener(Consumer<Image> listener) {
        listeners.add(listener);
    }
}
