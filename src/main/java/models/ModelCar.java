package models;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import java.util.function.Consumer;


public class ModelCar {
    private ModelCar modelCar;
    private String[] Inventaire;
    private String Skin;
    private int Batterie;
    private boolean EtatBatterie;
    private final List<Consumer<Image>> listeners = new ArrayList<>();
    private final double maxBatterySeconds = 20.0;
    private double batterySecondsRemaining = maxBatterySeconds;

    // private ControllerPersonnalisation controllerPersonnalisation;
    public ModelCar(){
        this.Skin = "/images/skins/Car_AM_General_Hummer_98x164.png";
        this.Batterie = 100;
        this.EtatBatterie = true;
        this.Inventaire = new String[4];
        // this.Inventaire = ;
    }
    // public void notifyCarChanged(String image){
    //     Skin = image;
    // }

    /** Appelée chaque frame pour dépenser dt secondes de batterie. */
    public void tick(double dt) {
        batterySecondsRemaining -= dt;
        if (batterySecondsRemaining < 0) batterySecondsRemaining = 0;
    }

     /** Appelée quand on recharge dt secondes (sur la base). */
    public void recharge(double dt) {
        batterySecondsRemaining += dt;
        if (batterySecondsRemaining > maxBatterySeconds)
            batterySecondsRemaining = maxBatterySeconds;
    }

    /** % restant entre 0 et 1. */
    public double getBatteryPercentage() {
        return batterySecondsRemaining / maxBatterySeconds;
    }

    /** True si la batterie est totalement vide. */
    public boolean isEmpty() {
        return batterySecondsRemaining <= 0;
    }
    
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

    public void resetBattery() {
        this.Batterie = 100;
        EtatBatterie = true;
    }
    // public void add(Image objet){
    //     System.out.println("je suis ici");
    //     for(int i=0;i<Inventaire.length;i++){
    //         if(Inventaire[i]!=""){
    //         }
    //     }
    // }
}
