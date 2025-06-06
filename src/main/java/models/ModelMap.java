// src/main/java/models/ModelMap.java
package models;

/**
 * ModelMap : stocke la taille de la carte et la position du rover.
 * Maintenant, il contient aussi un ModelVoiture (skin) pour le rover.
 */
public class ModelMap {

    private final double mapWidth;
    private final double mapHeight;

    private double roverX;
    private double roverY;

    // Nouveau : une instance de ModelVoiture pour le skin
    private ModelCar car;

    /**
     * @param mapWidth  largeur “réelle” de la carte (en pixels logiques)
     * @param mapHeight hauteur “réelle” de la carte (en pixels logiques)
     */
    public ModelMap(double mapWidth, double mapHeight) {
        this.mapWidth  = mapWidth;
        this.mapHeight = mapHeight;

        // Initialiser le rover au centre de la carte
        this.roverX = mapWidth  / 2.0;
        this.roverY = mapHeight / 2.0;

        // Créer le model “voiture” dès qu’on instancie ModelMap
        car = new ModelCar();
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
     * Déplace le rover de (dx, dy) en s'assurant de rester dans les limites [0, mapWidth]×[0, mapHeight]
     */
    public void moveRover(double dx, double dy) {
        double newX = roverX + dx;
        double newY = roverY + dy;

        // Bords horizontaux
        if (newX < 0) {
            newX = 0;
        } else if (newX > mapWidth) {
            newX = mapWidth;
        }

        // Bords verticaux
        if (newY < 0) {
            newY = 0;
        } else if (newY > mapHeight) {
            newY = mapHeight;
        }

        roverX = newX;
        roverY = newY;
    }

    /**
     * Retourne l’instance de ModelVoiture (pour obtenir le skin du rover)
     */
    public ModelCar getVoiture() {
        return car;
    }
}
