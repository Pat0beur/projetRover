package controllers;

import models.ModelMap;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

/**
 * ControllerMap
 * gère la vue et les entrées clavier.
 * Il met à jour le MapModel et dessine la “caméra” + minimap sur les Canvas.
 */
public class ControllerMap {

    // Constantes de configuration (tu peux ajuster si besoin)
    private static final double WINDOW_WIDTH  = 800;  // taille initiale de la fenêtre
    private static final double WINDOW_HEIGHT = 600;

    private static final double MAP_WIDTH  = 2000;   // taille “réelle” de la carte
    private static final double MAP_HEIGHT = 2000;

    private static final double ROVER_SIZE = 20;     // taille du rover en pixels

    private static final double MINI_WIDTH  = 200;   // taille de la minimap
    private static final double MINI_HEIGHT = 200;

    private static final double ROVER_SPEED = 5;     // px par frame

    // Ces deux Canvas sont injectés par JavaFX après chargement du FXML
    @FXML
    private Canvas mainCanvas;

    @FXML
    private Canvas miniMapCanvas;

    // Le Model (données) :
    private ModelMap model;

    // Stocke l’état des touches pressées pour bouger le rover
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    /**
     * Méthode appelée automatiquement par JavaFX après le chargement du FXML.
     * On l’utilise pour :
     *  1. initialiser le Model,
     *  2. configurer la taille des Canvas (utile si on veut forcer un size),
     *  3. démarrer la boucle AnimationTimer.
     */
    @FXML
    public void initialize() {
        // 1) Créer le model
        model = new ModelMap(MAP_WIDTH, MAP_HEIGHT);

        // 2) Redéfinir la taille (optionnel si le FXML fixe déjà prefWidth/prefHeight)
        mainCanvas.setWidth(WINDOW_WIDTH);
        mainCanvas.setHeight(WINDOW_HEIGHT);
        miniMapCanvas.setWidth(MINI_WIDTH);
        miniMapCanvas.setHeight(MINI_HEIGHT);

        // 3) Lancer la boucle de rendu (~60 FPS)
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
     * updateModel() calcule, en fonction de l’état des touches,
     * le déplacement (dx, dy) et alimente le Model.
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
     * drawAll() : efface et redessine le mainCanvas + miniMapCanvas selon l'état du Model.
     */
    private void drawAll() {
        drawMainView();
        drawMiniMap();
    }

    /**
     * Dessine sur le mainCanvas : une vue centrée sur le rover.
     * On choisit une “caméra” de la taille de la fenêtre (WINDOW_WIDTH × WINDOW_HEIGHT).
     */
    private void drawMainView() {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        // Effacer tout
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());

        // Calculer la “caméra” centrée sur le rover
        double camX = model.getRoverX() - WINDOW_WIDTH / 2.0;
        double camY = model.getRoverY() - WINDOW_HEIGHT / 2.0;

        // Gérer les bords de la carte pour que la caméra ne sorte pas
        if (camX < 0)             camX = 0;
        if (camX + WINDOW_WIDTH > MAP_WIDTH)  camX = MAP_WIDTH - WINDOW_WIDTH;
        if (camY < 0)             camY = 0;
        if (camY + WINDOW_HEIGHT > MAP_HEIGHT) camY = MAP_HEIGHT - WINDOW_HEIGHT;

        // Optionnel : dessiner une grille ou un fond texturé
        // Pour l'instant, on reste sur un fond uni (déjà fait). On pourrait faire :
        // for (int i = 0; i < MAP_WIDTH / 50; i++) { … } dessiner des lignes.
        // Mais on laisse gris uni.

        // Dessiner le rover (en rouge), position relative à la caméra
        double roverScreenX = model.getRoverX() - camX;
        double roverScreenY = model.getRoverY() - camY;

        gc.setFill(Color.RED);
        gc.fillRect(
            roverScreenX - ROVER_SIZE/2.0,
            roverScreenY - ROVER_SIZE/2.0,
            ROVER_SIZE,
            ROVER_SIZE
        );
    }

    /**
     * Dessine la minimap sur miniMapCanvas : 
     * - fond sombre, 
     * - petit point vert pour le rover (rapport d'échelle).
     */
    private void drawMiniMap() {
        GraphicsContext gc = miniMapCanvas.getGraphicsContext2D();

        // 1) Fond
        gc.setFill(Color.rgb(30, 30, 30));
        gc.fillRect(0, 0, MINI_WIDTH, MINI_HEIGHT);

        // 2) Calcul du ratio carte réelle → miniMap
        double scaleX = MINI_WIDTH  / MAP_WIDTH;
        double scaleY = MINI_HEIGHT / MAP_HEIGHT;

        // 3) Position du rover sur la miniMap
        double roverMiniX = model.getRoverX() * scaleX;
        double roverMiniY = model.getRoverY() * scaleY;

        // 4) Dessiner le “point” vert (taille fixe 4px)
        double dotSize = 4;
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(
            roverMiniX - dotSize / 2.0,
            roverMiniY - dotSize / 2.0,
            dotSize,
            dotSize
        );

        // (Optionnel : dessiner un cadre autour de la minimap)
        gc.setStroke(Color.WHITE);
        gc.strokeRect(0, 0, MINI_WIDTH, MINI_HEIGHT);
    }

    // ——————————————————————————————————————————————————————————————
    // Gestion des événements clavier (injection depuis la Scene/Stage)
    // On a deux méthodes @FXML qui seront appelées par le Scene Graph :
    //  - onKeyPressed(KeyEvent)
    //  - onKeyReleased(KeyEvent)
    //
    // **Attention** : pour que cela fonctionne, il faut que la scène (Scene) ait
    // le focus clavier. Dans Main.java on s’assurera d’appeler scene.requestFocus().
    // ——————————————————————————————————————————————————————————————

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
