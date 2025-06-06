// src/main/java/app/controllers/ControllerMap.java
package controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import models.ModelMap;

/**
 * ControllerMap avec un fond “mars.png” pour la map.
 * On dessine à chaque frame la portion de l’image correspondant
 * à la caméra centrée sur le rover, puis on dessine le carré rouge.
 */
public class ControllerMap {

    // ----- Constantes de configuration -----
    private static final double WINDOW_WIDTH  = 800;   // taille de la fenêtre (canvas principal)
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

    // Image de fond (mars.png) qui couvre toute la map (2 000×2 000)
    private Image backgroundImage;

    // Flags pour l’état des touches
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    /**
     * Appelé automatiquement après le chargement du FXML.
     * On initialise le modèle, on force la taille des Canvas,
     * on charge l’image de fond et on lance la boucle de jeu.
     */
    @FXML
    public void initialize() {
        // 1) Créer le model
        model = new ModelMap(MAP_WIDTH, MAP_HEIGHT);

        // 2) Forcer la taille des Canvas (au cas où le FXML en aurait mis d’autres)
        mainCanvas.setWidth(WINDOW_WIDTH);
        mainCanvas.setHeight(WINDOW_HEIGHT);
        miniMapCanvas.setWidth(MINI_WIDTH);
        miniMapCanvas.setHeight(MINI_HEIGHT);

        // 3) Charger l’image “mars.png” en fond de map
        //    (placer mars.png dans src/main/resources/images/planete/mars.png)
        try {
            backgroundImage = new Image(getClass().getResourceAsStream("/images/planete/mars.png"));
        } catch (Exception e) {
            // Si l’image n’est pas trouvée, on utilisera un fond gris en fallback
            backgroundImage = null;
            System.err.println("Impossible de charger /images/planete/mars.png, fallback gris utilisé.");
        }

        // 4) Lancer la boucle AnimationTimer (~60 FPS)
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
     * Dessine d’abord la vue principale (fond + rover), puis la minimap.
     */
    private void drawAll() {
        drawMainView();
        drawMiniMap();
    }

    /**
     * Dessine dans mainCanvas la portion de backgroundImage correspondant à la caméra,
     * puis dessine le rover comme un carré rouge au centre de cette caméra.
     */
    private void drawMainView() {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        // 1) Calculer la caméra centrée sur le rover
        double camX = model.getRoverX() - WINDOW_WIDTH  / 2.0;
        double camY = model.getRoverY() - WINDOW_HEIGHT / 2.0;

        // 2) Contraindre la caméra pour qu’elle ne sorte pas de la carte
        if (camX < 0)                        camX = 0;
        if (camX + WINDOW_WIDTH > MAP_WIDTH)  camX = MAP_WIDTH - WINDOW_WIDTH;
        if (camY < 0)                        camY = 0;
        if (camY + WINDOW_HEIGHT > MAP_HEIGHT) camY = MAP_HEIGHT - WINDOW_HEIGHT;

        // 3) Dessiner le fond : si l’image est chargée, on découpe la portion (camX, camY, WINDOW_WIDTH, WINDOW_HEIGHT)
        if (backgroundImage != null) {
            gc.drawImage(
                backgroundImage,
                /* sx= */ camX,                 // coin haut-gauche dans l’image
                /* sy= */ camY,
                /* sw= */ WINDOW_WIDTH,         // largeur de la sous-image à extraire
                /* sh= */ WINDOW_HEIGHT,        // hauteur de la sous-image à extraire
                /* dx= */ 0,                    // coin de dessin sur le canvas
                /* dy= */ 0,
                /* dw= */ WINDOW_WIDTH,         // largeur affichée sur le canvas
                /* dh= */ WINDOW_HEIGHT         // hauteur affichée sur le canvas
            );
        } else {
            // Fallback : un fond gris clair si l’image n’a pas pu être chargée
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        }

        // 4) Dessiner le rover comme un carré rouge
        double roverScreenX = model.getRoverX() - camX - (ROVER_SIZE / 2.0);
        double roverScreenY = model.getRoverY() - camY - (ROVER_SIZE / 2.0);

        gc.setFill(Color.RED);
        gc.fillRect(roverScreenX, roverScreenY, ROVER_SIZE, ROVER_SIZE);
    }

    /**
     * Dessine la minimap (vue d’ensemble) dans miniMapCanvas :
     * - fond sombre
     * - petit carré vert indiquant la position du rover
     * - cadre blanc autour
     */
    private void drawMiniMap() {
        GraphicsContext gc = miniMapCanvas.getGraphicsContext2D();

        // 1) Fond sombre
        gc.setFill(Color.rgb(30, 30, 30));
        gc.fillRect(0, 0, MINI_WIDTH, MINI_HEIGHT);

        // 2) Calculer les ratios réel → minimap
        double scaleX = MINI_WIDTH  / MAP_WIDTH;
        double scaleY = MINI_HEIGHT / MAP_HEIGHT;

        // 3) Position du rover sur la minimap
        double roverMiniX = model.getRoverX() * scaleX;
        double roverMiniY = model.getRoverY() * scaleY;

        // 4) Dessiner un petit carré vert pour le rover
        double dotSize = 4;
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(
            roverMiniX - dotSize / 2.0,
            roverMiniY - dotSize / 2.0,
            dotSize,
            dotSize
        );

        // 5) Cadre blanc autour de la minimap
        gc.setStroke(Color.WHITE);
        gc.strokeRect(0, 0, MINI_WIDTH, MINI_HEIGHT);
    }

    // ——————————————————————————————
    // Gestion des événements clavier (flèches ou WASD) pour sets/clears des flags
    // L’AnchorPane dans map.fxml doit avoir onKeyPressed et onKeyReleased pointant vers ces méthodes.
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

        // btnCommandes.setOnDragDetected(event -> {
        // Dragboard db = btnCommandes.startDragAndDrop(TransferMode.ANY);
        
        // /* Put a string on a dragboard */
        // ClipboardContent content = new ClipboardContent();
        // content.putString(btnCommandes.getText());
        // db.setContent(content);
        
        // event.consume();
        // });
        // btnJouer.setOnDragOver(event -> {
        // /* data is dragged over the target */
        // /* accept it only if it is not dragged from the same node 
        //  * and if it has a string data */
        // if (event.getGestureSource() != btnJouer &&
        //         event.getDragboard().hasString()) {
        //     /* allow for both copying and moving, whatever user chooses */
        //     event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        // }
        
        // event.consume();
        // });
        // btnJouer.setOnDragEntered(event -> {
        // /* the drag-and-drop gesture entered the target */
        // /* show to the user that it is an actual gesture target */
        //     if (event.getGestureSource() != btnJouer &&
        //             event.getDragboard().hasString()) {
        //         btnJouer.setTextFill(Color.GREEN);
        //     } 
        //     event.consume();
        // });
        // btnJouer.setOnDragExited(event -> {
        //     /* mouse moved away, remove the graphical cues */
        //     btnJouer.setTextFill(Color.BLACK);

        //     event.consume();
        // });
        // btnJouer.setOnDragDropped(event -> {
        // /* data dropped */
        // /* if there is a string data on dragboard, read it and use it */
        // Dragboard db = event.getDragboard();
        // boolean success = false;
        // if (db.hasString()) {
        //    btnJouer.setText(db.getString());
        //    success = true;
        // }
        // /* let the source know whether the string was successfully 
        //  * transferred and used */
        // event.setDropCompleted(success);
        
        // event.consume();
        // });
        // btnCommandes.setOnDragDone(event -> {
        // /* the drag and drop gesture ended */
        // /* if the data was successfully moved, clear it */
        // if (event.getTransferMode() == TransferMode.MOVE) {
        //     btnCommandes.setText("");
        // }
        // event.consume();
        // });