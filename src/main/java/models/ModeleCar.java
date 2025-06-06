package models;

import java.util.ArrayList;

import controllers.ControllerMenu;
import controllers.ControllerPersonnalisation;
import javafx.scene.image.Image;

public class ModeleCar {
    private ModeleCar modeleCar;
    private ArrayList<String> Inventaire;
    private Image Skin;
    private int Batterie;
    private boolean EtatBatterie;
    private ControllerPersonnalisation controllerPersonnalisation;
    public void notifyCarChanged(Image image){
        modeleCar.Skin = image;
    }
}
