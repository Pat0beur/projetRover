package models;

import java.util.ArrayList;

import controllers.ControllerMenu;
import controllers.ControllerPersonnalisation;
import javafx.scene.image.Image;

public class ModelCar {
    private ModelCar modelCar;
    private ArrayList<String> Inventaire;
    private Image Skin;
    private int Batterie;
    private boolean EtatBatterie;
    // private ControllerPersonnalisation controllerPersonnalisation;
    public ModelCar(){
        this.Skin = new Image(getClass().getResource("/images/skins/Car_AM_General_Hummer_98x164.png").toExternalForm());
        this.Batterie = 100;
        this.EtatBatterie = true;
        this.Inventaire = new ArrayList<>();
    }
    public void notifyCarChanged(Image image){
        modelCar.Skin = image;
    }
    public Image getSkin(){
        return Skin;
    }
}
