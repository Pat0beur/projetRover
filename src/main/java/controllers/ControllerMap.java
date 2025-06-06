// src/main/java/app/controllers/ControllerMap.java
package controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import models.ModelMap;
import models.ModelCar;

/**
 * ControllerMap affiche la carte (avec fond) et dessine l'image du rover
 * (skin) à la position du modèle. On garde en bas à droite la minimap.
 */
public class ControllerMap {

    // ----- Constantes de configuration -----
    private static final double WINDOW_WIDTH  = 800;   // taille de la fenêtre (canvas principal)
    private static final double WINDOW_HEIGHT = 600;

    private static final double MAP_WIDTH  = 2000;     // taille réelle de la carte
    private static final double MAP_HEIGHT = 2000;

    private static final double ROVER_DISPLAY_WIDTH  = 98;   // largeur du sprite du rover (en px)
    private static final double ROVER_DISPLAY_HEIGHT = 164;  // hauteur du sprite du rover (en px)

    private static final double MINI_WIDTH  = 200;     // largeur de la minimap
    private static final double MINI_HEIGHT = 200;     // hauteur de la minimap

    private static final double ROVER_SPEED = 5;       // vitesse du rover (px/frame)

    // ----- Injection des deux Canvas depuis map.fxml -----
    @FXML private Canvas mainCanvas;
    @FXML private Canvas miniMapCanvas;

    // Modèle stockant la position du rover + ModelCar
    private ModelMap model;

    // Image de fond (ici mars 4000×4000 ou 2000×2000 selon ce que vous avez chargé)
    private Image backgroundImage;

    // Flags pour l’état des touches
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    /**
     * Appelé automatiquement après le chargement du FXML.
     * On initialise le modèle, on force la taille des Canvas, on charge l’image de fond,
     * puis on lance la boucle de rendu.
     */
    @FXML
    public void initialize() {
        // 1) Créer le modèle
        model = new ModelMap(MAP_WIDTH, MAP_HEIGHT);

        // 2) Forcer la taille des Canvas (au cas où le FXML ait des valeurs différentes)
        mainCanvas.setWidth(WINDOW_WIDTH);
        mainCanvas.setHeight(WINDOW_HEIGHT);
        miniMapCanvas.setWidth(MINI_WIDTH);
        miniMapCanvas.setHeight(MINI_HEIGHT);

        // 3) Charger l’image “mars.png” : 
        //    Assurez-vous qu'elle est bien dans src/main/resources/images/planete/mars.png
        try {
            backgroundImage = new Image(getClass().getResourceAsStream("/images/planete/mars.png"));
        } catch (Exception e) {
            backgroundImage = null;
            System.err.println("Impossible de charger /images/planete/mars.png");
        }

        // 4) Démarrer la boucle AnimationTimer (~60 FPS)
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
     * Lit l’état des flags up/down/left/right, puis déplace le rover en conséquence.
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
     * Dessine à chaque frame la vue principale (fond + image du rover) 
     * puis la minimap avec un petit carré vert.
     */
    private void drawAll() {
        drawMainView();
        drawMiniMap();
    }

    /**
     * Dessine la portion de fond correspondant à la position du rover, 
     * puis place le sprite du rover au centre de la caméra.
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

        // 3) Afficher la portion de l’image de fond (si chargée)
        if (backgroundImage != null) {
            gc.clearRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            gc.drawImage(
                backgroundImage,
                /* sx= */ camX,                   // coin haut-gauche dans l’image
                /* sy= */ camY,
                /* sw= */ WINDOW_WIDTH,           // largeur à découper
                /* sh= */ WINDOW_HEIGHT,          // hauteur à découper
                /* dx= */ 0,                      // affichage à (0,0) sur le canvas
                /* dy= */ 0,
                /* dw= */ WINDOW_WIDTH,           // affiché en 800×600
                /* dh= */ WINDOW_HEIGHT
            );
        } else {
            // Fallback : gris si l’image n’existe pas
            gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
            gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        }

        // 4) Dessiner le rover : récupérer son skin et le centrer
        ModelCar car = model.getCar();
        Image skin = car.getSkin();
        if (skin != null) {
            // On veut centrer le sprite du rover (98×164) sur la position roverX, roverY
            double roverScreenX = model.getRoverX() - camX - (ROVER_DISPLAY_WIDTH  / 2.0);
            double roverScreenY = model.getRoverY() - camY - (ROVER_DISPLAY_HEIGHT / 2.0);
            gc.drawImage(skin, roverScreenX, roverScreenY);
        } else {
            // Si pour une raison l’image est nulle, on peut dessiner un carré rouge
            double fallbackSize = 20;
            double fx = model.getRoverX() - camX - fallbackSize / 2.0;
            double fy = model.getRoverY() - camY - fallbackSize / 2.0;
            gc.setFill(javafx.scene.paint.Color.RED);
            gc.fillRect(fx, fy, fallbackSize, fallbackSize);
        }
    }

    /**
     * Dessine la minimap (vue d’ensemble) dans miniMapCanvas :
     * - fond sombre
     * - petit carré vert indiquant la position du rover
     * - cadre blanc
     */
    private void drawMiniMap() {
        GraphicsContext gc = miniMapCanvas.getGraphicsContext2D();

        // 1) Fond sombre
        gc.setFill(javafx.scene.paint.Color.rgb(30, 30, 30));
        gc.fillRect(0, 0, MINI_WIDTH, MINI_HEIGHT);

        // 2) Calculer le ratio réel → minimap
        double scaleX = MINI_WIDTH  / MAP_WIDTH;
        double scaleY = MINI_HEIGHT / MAP_HEIGHT;

        // 3) Position du rover sur la minimap
        double roverMiniX = model.getRoverX() * scaleX;
        double roverMiniY = model.getRoverY() * scaleY;

        // 4) Petit carré vert
        double dotSize = 4;
        gc.setFill(javafx.scene.paint.Color.LIMEGREEN);
        gc.fillRect(
            roverMiniX - dotSize / 2.0,
            roverMiniY - dotSize / 2.0,
            dotSize,
            dotSize
        );

        // 5) Cadre blanc
        gc.setStroke(javafx.scene.paint.Color.WHITE);
        gc.strokeRect(0, 0, MINI_WIDTH, MINI_HEIGHT);
    }

    // ————————————————————————————
    // Gestion des événements clavier (onKeyPressed/onKeyReleased)
    // ————————————————————————————

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