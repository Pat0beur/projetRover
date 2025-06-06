// src/main/java/controllers/ControllerMap.java
package controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import models.ModelMap;
import models.ModelCar;

/**
 * ControllerMap gère :
 *  - la réception des événements clavier (flèches ou WASD) pour déplacer le rover,
 *  - la boucle d'affichage (AnimationTimer) qui dessine la vue principale + minimap sur les Canvas.
 */
public class ControllerMap {

    // --- Constantes de configuration ---
    private static final double WINDOW_WIDTH  = 800;   // taille initiale de la fenêtre
    private static final double WINDOW_HEIGHT = 600;

    private static final double MAP_WIDTH  = 2000;     // taille “réelle” de la carte
    private static final double MAP_HEIGHT = 2000;

    private static final double ROVER_SIZE = 20;       // taille de la case sur laquelle on dessine le rover si image manquante

    private static final double MINI_WIDTH  = 200;     // largeur de la minimap
    private static final double MINI_HEIGHT = 200;     // hauteur de la minimap

    private static final double ROVER_SPEED = 5;       // pixels / frame

    // --- Injection des deux Canvas depuis map.fxml ---
    @FXML
    private Canvas mainCanvas;

    @FXML
    private Canvas miniMapCanvas;

    // Modèle “Map + Voiture”
    private ModelMap model;

    // Flags pour l’état des touches
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    /**
     * Méthode appelée automatiquement après chargement du FXML.
     * On y initialise le Model, on force la taille des Canvas, puis on lance le game loop.
     */
    @FXML
    public void initialize() {
        // 1) Créer le model avec les dimensions souhaitées
        model = new ModelMap(MAP_WIDTH, MAP_HEIGHT);

        // 2) Définir explicitement la taille des Canvas (au cas où FXML aurait changé)
        mainCanvas.setWidth(WINDOW_WIDTH);
        mainCanvas.setHeight(WINDOW_HEIGHT);
        miniMapCanvas.setWidth(MINI_WIDTH);
        miniMapCanvas.setHeight(MINI_HEIGHT);

        // 3) Lancer une boucle AnimationTimer (~60 FPS)
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
     * updateModel() : calcule dx/dy selon les touches appuyées et déplace le rover.
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
     * drawAll() : efface et redessine la vue principale + minimap.
     */
    private void drawAll() {
        drawMainView();
        drawMiniMap();
    }

    /**
     * Dessine la vue “caméra” centrée sur le rover dans mainCanvas.
     */
    private void drawMainView() {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        // 1) Effacer la zone entière en gris clair
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());

        // 2) Calculer la caméra (rectangle visible) centrée sur la position du rover
        double camX = model.getRoverX() - WINDOW_WIDTH  / 2.0;
        double camY = model.getRoverY() - WINDOW_HEIGHT / 2.0;

        // Empêcher la caméra de sortir des bords de la carte
        if (camX < 0)                       camX = 0;
        if (camX + WINDOW_WIDTH > MAP_WIDTH)  camX = MAP_WIDTH - WINDOW_WIDTH;
        if (camY < 0)                       camY = 0;
        if (camY + WINDOW_HEIGHT > MAP_HEIGHT) camY = MAP_HEIGHT - WINDOW_HEIGHT;

        // 3) (Optionnel) Dessiner une grille pour aider à se repérer
        // Par exemple, tous les 50 px. Si tu veux désactiver, commente simplement ce bloc.
/*
        gc.setStroke(Color.GRAY);
        double stepGrid = 50.0;
        for (double x = 0; x <= MAP_WIDTH; x += stepGrid) {
            double screenX = x - camX;
            if (screenX >= 0 && screenX <= WINDOW_WIDTH) {
                gc.strokeLine(screenX, 0, screenX, WINDOW_HEIGHT);
            }
        }
        for (double y = 0; y <= MAP_HEIGHT; y += stepGrid) {
            double screenY = y - camY;
            if (screenY >= 0 && screenY <= WINDOW_HEIGHT) {
                gc.strokeLine(0, screenY, WINDOW_WIDTH, screenY);
            }
        }
*/

        // 4) Dessiner le rover : si l’image existe, on l’affiche ; sinon, on dessine un carré rouge.
        ModelCar voiture = model.getVoiture();
        if (voiture.getSkin() != null && voiture.getSkin().getWidth() > 1) {
            // Position du rover par rapport à la caméra
            double roverScreenX = model.getRoverX() - camX - (voiture.getSkin().getWidth() / 2.0);
            double roverScreenY = model.getRoverY() - camY - (voiture.getSkin().getHeight() / 2.0);

            gc.drawImage(
                voiture.getSkin(),
                roverScreenX,
                roverScreenY
            );
        } else {
            // Placeholder : un carré rouge de taille ROVER_SIZE × ROVER_SIZE
            double roverScreenX = model.getRoverX() - camX - (ROVER_SIZE / 2.0);
            double roverScreenY = model.getRoverY() - camY - (ROVER_SIZE / 2.0);

            gc.setFill(Color.RED);
            gc.fillRect(
                roverScreenX,
                roverScreenY,
                ROVER_SIZE,
                ROVER_SIZE
            );
        }
    }

    /**
     * Dessine la minimap (vue d’ensemble) dans miniMapCanvas.
     */
    private void drawMiniMap() {
        GraphicsContext gc = miniMapCanvas.getGraphicsContext2D();

        // 1) Fond sombre
        gc.setFill(Color.rgb(30, 30, 30));
        gc.fillRect(0, 0, MINI_WIDTH, MINI_HEIGHT);

        // 2) Calcul du ratio (taille rélle → taille mini)
        double scaleX = MINI_WIDTH  / MAP_WIDTH;
        double scaleY = MINI_HEIGHT / MAP_HEIGHT;

        // 3) Position du rover sur la miniMap
        double roverMiniX = model.getRoverX() * scaleX;
        double roverMiniY = model.getRoverY() * scaleY;

        // 4) Dessiner un petit carré vert (ou un point) pour le rover
        double dotSize = 4;
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(
            roverMiniX - dotSize / 2.0,
            roverMiniY - dotSize / 2.0,
            dotSize,
            dotSize
        );

        // 5) (Optionnel) Cadre blanc autour de la minimap
        gc.setStroke(Color.WHITE);
        gc.strokeRect(0, 0, MINI_WIDTH, MINI_HEIGHT);
    }

    // ——————————————————————————————
    // Gestion du clavier : modifier les flags when key pressed / released
    // ——————————————————————————————

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
