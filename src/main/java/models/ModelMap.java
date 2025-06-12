// src/main/java/models/ModelMap.java
package models;

import java.util.concurrent.ThreadLocalRandom;

import app.App;
import javafx.scene.image.Image;

/**
 * ModelMap : contient la taille de la carte (mapWidth, mapHeight), 
 * la position (roverX, roverY) et une instance de ModelCar pour le skin.
 */
public class ModelMap {

    private final double mapWidth;
    private final double mapHeight;

    private double roverX;
    private double roverY;

    // NEURO: Instance du ModelCar pour récupérer getSkin()
    private final ModelCar car;
    private Model model;
    private boolean EndGame = false;
    private int indiceFinPartie;
    private boolean JeuArrete;
    private boolean[] depose = {false,false,false,false};
    private double roverAngle = 0; // en degrés
    private double[] objetsCarteX;
    private double[] objetsCarteY;
    private double[] objetEcranX = new double[5];
    private double[] objetEcranY = new double[5];
        private Image[] objetsImages = {
        new Image(getClass().getResourceAsStream("/images/objets/circuit_imprime_test.png")),
        new Image(getClass().getResourceAsStream("/images/objets/panneau_solaire.png")),
        new Image(getClass().getResourceAsStream("/images/objets/tournevis_test.png")),
        new Image(getClass().getResourceAsStream("/images/objets/vis_test.png"))
    };
    private double antenneCarteX;
    private double antenneCarteY;
    private Image[] antenneImages = {
        new Image(getClass().getResourceAsStream("/images/antenne/antenne_cassee.png")),
        new Image(getClass().getResourceAsStream("/images/antenne/antenne_un_peu_cassee.png")),
        new Image(getClass().getResourceAsStream("/images/antenne/antenne.png"))
    };




    /**
     * @param mapWidth  largeur “réelle” de la carte (en pixels logiques)
     * @param mapHeight hauteur “réelle” de la carte (en pixels logiques)
     */
    public ModelMap(double mapWidth, double mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        int min = 0;
        int max = 0;
        this.model = App.getModel();  // ou injecté autrement
        int difficulte = model.getDifficulte();
        System.out.println("model.getDifficulte() = "+model.getDifficulte());
        if(difficulte==1){
            min=700;
            max=1300;
        }
        else if(difficulte==2){
            min=500;
            max=1500;
        }
        else if(difficulte==3){
            min=200;
            max=1800;
        }
        this.objetsCarteX = new double[] { 
            ThreadLocalRandom.current().nextInt(min, max + 1), 
            ThreadLocalRandom.current().nextInt(min, max + 1), 
            ThreadLocalRandom.current().nextInt(min, max + 1), 
            ThreadLocalRandom.current().nextInt(min, max + 1) }; // X de chaque objet
        this.objetsCarteY = new double[] { 
            ThreadLocalRandom.current().nextInt(min, max + 1), 
            ThreadLocalRandom.current().nextInt(min, max + 1), 
            ThreadLocalRandom.current().nextInt(min, max + 1), 
            ThreadLocalRandom.current().nextInt(min, max + 1) }; // Y de chaque objet

         this.antenneCarteX = ThreadLocalRandom.current().nextInt(min, max + 1);
         this.antenneCarteY = ThreadLocalRandom.current().nextInt(min, max + 1);

        // Initialiser le rover au centre
        this.roverX = mapWidth / 2.0;
        this.roverY = mapHeight / 2.0;

        // Créer une instance de ModelCar
        this.model = App.getModel();
        // this.car = new ModelCar(model.getDifficulte());
        this.car = App.getModelCar();
        // this.Inventaire = new String[4];
    }


    public void setObjetEcranX(double a, int indice){
        this.objetEcranX[indice] = a;
    }
    public double getObjetEcranX(int indice){
        return objetEcranX[indice];
    }

    public void setObjetEcranY(double a, int indice){
        this.objetEcranY[indice] = a;
    }
    public double getObjetEcranY(int indice){
        return objetEcranY[indice];
    }


    public Image getAntennneImages(int indice){
        return antenneImages[indice];
    }
    public Image[] getAntennneImages(){
        return antenneImages;
    }
    public Image getObjetsImages(int indice){
        return objetsImages[indice];
    }
    public Image[] getObjetsImages(){
        return objetsImages;
    }

    public double getAntenneCarteX(){
        return antenneCarteX;
    }
    public void setAntenneCarteX(double a){
        this.antenneCarteX = a;
    }
    public double getAntenneCarteY(){
        return antenneCarteY;
    }
    public void setAntenneCarteY(double a){
        this.antenneCarteY = a;
    }

    public void setObjetsCarteX(double a,int indice){
        this.objetsCarteX[indice] = a;
    }
    public void setObjetsCarteY(double a,int indice){
        this.objetsCarteY[indice] = a;
    }
    public double getObjetsCarteX(int indice){
        return objetsCarteX[indice];
    }
    public double getObjetsCarteY(int indice){
        return objetsCarteY[indice];
    }

    public double getMapWidth() {
        return mapWidth;
    }

    public double getMapHeight() {
        return mapHeight;
    }

    public double getRoverX() {
        return roverX;
    }

    public double getRoverY() {
        return roverY;
    }
    public boolean getEndGame(){
        return EndGame;
    }
    public void setEndGame(boolean a){
        this.EndGame = a;
    }
    public ModelMap getModelMap(){
        return this;
    }
    public void setIndiceFinPartie(int a){
        this.indiceFinPartie = a;
    }
    public int getIndiceFinPartie(){
        return indiceFinPartie;
    }
    public void setJeuArrete(boolean a){
        this.JeuArrete = a;
    }
    public boolean getJeuArrete(){
        return JeuArrete;
    }
    public void setdepose(boolean a,int indice){
        this.depose[indice] = a;
    }
    public boolean[] getdepose(){
        return depose;
    }
    public boolean getdepose(int indice){
        return depose[indice];
    }
    public void setroverAngle(double a){
        this.roverAngle = a;
    }
    public double getroverAngle(){
        return roverAngle;
    }

    /**
     * Déplace le rover de (dx, dy) en s'assurant de rester dans les limites [0..mapWidth]×[0..mapHeight]
     */
    public void moveRover(double dx, double dy) {
        double newX = roverX + dx;
        double newY = roverY + dy;

        if (newX < 0) newX = 0;
        if (newX > mapWidth) newX = mapWidth;
        if (newY < 0) newY = 0;
        if (newY > mapHeight) newY = mapHeight;

        this.roverX = newX;
        this.roverY = newY;
    }

    /** Renvoie l’instance de ModelCar pour récupérer l’image du rover */
    public ModelCar getCar() {
        return car;
    }
}
