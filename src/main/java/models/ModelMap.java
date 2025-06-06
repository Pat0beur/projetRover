// src/main/java/app/models/ModelMap.java
package models;

/**
 * ModelMap : contient la taille de la carte et la position du rover.
 * Ici on n’a plus de ModelCar, juste la position (roverX, roverY).
 */
public class ModelMap {
    private final double mapWidth;
    private final double mapHeight;

    private double roverX;
    private double roverY;

    /**
     * @param mapWidth  largeur “réelle” de la carte (en pixels logiques)
     * @param mapHeight hauteur “réelle” de la carte (en pixels logiques)
     */
    public ModelMap(double mapWidth, double mapHeight) {
        this.mapWidth  = mapWidth;
        this.mapHeight = mapHeight;

        // On place le rover au centre de la carte au départ
        this.roverX = mapWidth  / 2.0;
        this.roverY = mapHeight / 2.0;
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
     * Déplace le rover de (dx, dy) en restant dans les limites [0..mapWidth]×[0..mapHeight].
     */
    public void moveRover(double dx, double dy) {
        double newX = roverX + dx;
        double newY = roverY + dy;

        if (newX < 0) {
            newX = 0;
        } else if (newX > mapWidth) {
            newX = mapWidth;
        }

        if (newY < 0) {
            newY = 0;
        } else if (newY > mapHeight) {
            newY = mapHeight;
        }

        roverX = newX;
        roverY = newY;
    }
}
