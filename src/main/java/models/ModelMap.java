// src/main/java/models/ModelMap.java
package models;

import app.App;

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
    private String[] Inventaire;

    /**
     * @param mapWidth  largeur “réelle” de la carte (en pixels logiques)
     * @param mapHeight hauteur “réelle” de la carte (en pixels logiques)
     */
    public ModelMap(double mapWidth, double mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        // Initialiser le rover au centre
        this.roverX = mapWidth / 2.0;
        this.roverY = mapHeight / 2.0;

        // Créer une instance de ModelCar
        this.model = App.getModel();
        // this.car = new ModelCar(model.getDifficulte());
        this.car = App.getModelCar();
        // this.Inventaire = new String[4];
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
