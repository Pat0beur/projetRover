// src/main/java/app/controllers/ControllerMap.java
package controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import models.ModelMap;

/**
 * ControllerMap gère la logique de déplacement du rover et l'affichage
 * (vue principale + minimap). On dessine toujours un simple carré rouge.
 */
public class ControllerMap {

    // ----- Constantes de configuration -----
    private static final double WINDOW_WIDTH  = 800;   // taille de la fenêtre (ou du canvas principal)
    private static final double WINDOW_HEIGHT = 600;

    private static final double MAP_WIDTH  = 2000;     // taille “réelle” de la carte
    private static final double MAP_HEIGHT = 2000;

    private static final double ROVER_SIZE = 20;       // taille du carré qui représente le rover

    private static final double MINI_WIDTH  = 200;     // largeur de la minimap
    private static final double MINI_HEIGHT = 200;     // hauteur de la minimap

    private static final double ROVER_SPEED = 5;       // pixels par frame

    // ----- Injection des deux Canvas depuis map.fxml -----
    @FXML private Canvas mainCanvas;
    @FXML private Canvas miniMapCanvas;

    // Modèle stockant la position du rover
    private ModelMap model;

    // Flags pour l’état des touches
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    /**
     * Appelé automatiquement après le chargement du FXML.
     * On initialise le modèle, on force la taille des Canvas, puis on lance la boucle de jeu.
     */
    @FXML
    public void initialize() {
        // 1) Créer le model
        model = new ModelMap(MAP_WIDTH, MAP_HEIGHT);

        // 2) Forcer la taille des Canvas (au cas où le FXML ait une taille différente)
        mainCanvas.setWidth(WINDOW_WIDTH);
        mainCanvas.setHeight(WINDOW_HEIGHT);
        miniMapCanvas.setWidth(MINI_WIDTH);
        miniMapCanvas.setHeight(MINI_HEIGHT);

        // 3) Lancer la boucle AnimationTimer (~60 FPS)
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateModel();
                drawAll();
            }
        };
        gameLoop.start();
    }

    /**
     * Lit l’état des flags up/down/left/right pour calculer un déplacement (dx, dy).
     * Puis appelle model.moveRover(dx, dy) si nécessaire.
     */
    private void updateModel() {
        double dx = 0, dy = 0;
        if (leftPressed)  dx -= ROVER_SPEED;
        if (rightPressed) dx += ROVER_SPEED;
        if (upPressed)    dy -= ROVER_SPEED;
        if (downPressed)  dy += ROVER_SPEED;

        if (dx != 0 || dy != 0) {
            model.moveRover(dx, dy);
        }
    }

    /**
     * Dessine d’abord la vue principale (fond + carré rouge), puis la minimap.
     */
    private void drawAll() {
        drawMainView();
        drawMiniMap();
    }

    /**
     * Dessine la “camera” centrée sur le rover dans mainCanvas.
     * Le rover est toujours un carré rouge de taille ROVER_SIZE.
     */
    private void drawMainView() {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        // 1) Fond gris clair
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // 2) Calculer la position de la caméra centrée sur le rover
        double camX = model.getRoverX() - WINDOW_WIDTH  / 2.0;
        double camY = model.getRoverY() - WINDOW_HEIGHT / 2.0;

        // 3) Contrainte de la caméra pour ne pas sortir de la carte
        if (camX < 0)                       camX = 0;
        if (camX + WINDOW_WIDTH > MAP_WIDTH)  camX = MAP_WIDTH - WINDOW_WIDTH;
        if (camY < 0)                       camY = 0;
        if (camY + WINDOW_HEIGHT > MAP_HEIGHT) camY = MAP_HEIGHT - WINDOW_HEIGHT;

        // 4) Dessiner le rover comme un carré rouge
        double roverScreenX = model.getRoverX() - camX - (ROVER_SIZE / 2.0);
        double roverScreenY = model.getRoverY() - camY - (ROVER_SIZE / 2.0);

        gc.setFill(Color.RED);
        gc.fillRect(roverScreenX, roverScreenY, ROVER_SIZE, ROVER_SIZE);
    }

    /**
     * Dessine la minimap (vue d’ensemble) dans miniMapCanvas.
     * On affiche un fond sombre et un petit carré vert () représentant la position du rover.
     */
    private void drawMiniMap() {
        GraphicsContext gc = miniMapCanvas.getGraphicsContext2D();

        // 1) Fond sombre
        gc.setFill(Color.rgb(30, 30, 30));
        gc.fillRect(0, 0, MINI_WIDTH, MINI_HEIGHT);

        // 2) Calculer ratios carte réelle → minimap
        double scaleX = MINI_WIDTH  / MAP_WIDTH;
        double scaleY = MINI_HEIGHT / MAP_HEIGHT;

        // 3) Position du rover sur la minimap
        double roverMiniX = model.getRoverX() * scaleX;
        double roverMiniY = model.getRoverY() * scaleY;

        // 4) Dessiner un petit carré vert (dotSize px) pour le rover sur la minimap
        double dotSize = 4;
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(
            roverMiniX - dotSize / 2.0,
            roverMiniY - dotSize / 2.0,
            dotSize,
            dotSize
        );

        // 5) Optionnel : tracer un cadre blanc autour de la minimap
        gc.setStroke(Color.WHITE);
        gc.strokeRect(0, 0, MINI_WIDTH, MINI_HEIGHT);
    }

    // ———————————————————————————————
    // Gestion des touches clavier (flèches ou WASD)
    // L’AnchorPane dans map.fxml appellera ces deux méthodes automatiquement.
    // ———————————————————————————————

    @FXML
    private void onKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();
        switch (code) {
            case LEFT:  case A: leftPressed  = true; break;
            case RIGHT: case D: rightPressed = true; break;
            case UP:    case W: upPressed    = true; break;
            case DOWN:  case S: downPressed  = true; break;
            default: break;
        }
    }

    @FXML
    private void onKeyReleased(KeyEvent event) {
        KeyCode code = event.getCode();
        switch (code) {
            case LEFT:  case A: leftPressed  = false; break;
            case RIGHT: case D: rightPressed = false; break;
            case UP:    case W: upPressed    = false; break;
            case DOWN:  case S: downPressed  = false; break;
            default: break;
        }
    }
}
