package models;

/**
 * MapModel : contient la taille de la carte “réelle” et la position du rover.
 * Fournit une méthode pour déplacer le rover, en s’assurant qu’il reste dans
 * les limites [0, mapWidth] × [0, mapHeight].
 */
public class ModelMap {

    private final double mapWidth;
    private final double mapHeight;

    private double roverX;
    private double roverY;

    /**
     * @param mapWidth  largeur de la carte (en pixels “logiques”)
     * @param mapHeight hauteur de la carte (en pixels “logiques”)
     */
    public ModelMap(double mapWidth, double mapHeight) {
        this.mapWidth  = mapWidth;
        this.mapHeight = mapHeight;

        // Initialement, placer le rover au centre de la carte
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
     * Déplace le rover de (dx, dy), en veillant à ne pas dépasser la carte.
     * @param dx déplacement en x (positif vers la droite)
     * @param dy déplacement en y (positif vers le bas)
     */
    public void moveRover(double dx, double dy) {
        double newX = roverX + dx;
        double newY = roverY + dy;

        // Bornes en X
        if (newX < 0) {
            newX = 0;
        } else if (newX > mapWidth) {
            newX = mapWidth;
        }

        // Bornes en Y
        if (newY < 0) {
            newY = 0;
        } else if (newY > mapHeight) {
            newY = mapHeight;
        }

        roverX = newX;
        roverY = newY;
    }
}
