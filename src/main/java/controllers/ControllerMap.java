// src/main/java/app/controllers/ControllerMap.java
package controllers;

import app.App;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.ModelMap;
import models.Model;
import models.ModelCar;

import java.io.IOException;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
/**
 * ControllerMap affiche la carte (avec fond) et dessine l'image du rover
 * (skin) à la position du modèle. On garde en bas à droite la minimap.
 */
public class ControllerMap {

    // ----- Constantes de configuration -----
    private static final double WINDOW_WIDTH  = 800;   // taille de la fenêtre (canvas principal)
    private static final double WINDOW_HEIGHT = 600;

    private static final double MAP_WIDTH  = 2048;     // taille réelle de la carte
    private static final double MAP_HEIGHT = 2048;

    private static final double ROVER_DISPLAY_WIDTH  = 40;   // largeur désirée du sprite
    private static final double ROVER_DISPLAY_HEIGHT = 40 * (164.0 / 98.0);  // on garde le ratio original

    private static final double MINI_WIDTH  = 200;     // largeur de la minimap
    private static final double MINI_HEIGHT = 200;     // hauteur de la minimap

    private static final double ROVER_SPEED = 5;       // vitesse du rover (px/frame)

    // ----- Injection des deux Canvas depuis map.fxml -----
    @FXML private Canvas mainCanvas;
    @FXML private Canvas miniMapCanvas;
    @FXML private Canvas chronoCanvas;
    @FXML private ProgressBar progressBar;
    @FXML private Label label;
    @FXML private Canvas borderCanvas;
    @FXML private Canvas firstCanvas;
    @FXML private Canvas secondCanvas;
    @FXML private Canvas thirdCanvas;
    @FXML private Canvas fourthCanvas;
    @FXML private Button btnRejouer;
    @FXML private Button btnQuitter;
    @FXML private Button btnPersonnalisation;

    private int remainingSeconds = 120;
    private boolean isDraggingFromInventory = false;
    private int draggingInventoryIndex = -1;
    private int etatAntenne = 0;

    private long lastNanoTime;
    private AnimationTimer gameLoop;
    private Timeline countdownTimeline;




    // Modèle stockant la position du rover + ModelCar
    private ModelMap modelmap;
    private ModelCar modelCar;
    private Model model;
    private Image roverSkin;

    // Tout ce qui se rapporte aux objets
    private Image[] Inventaire = new Image[4];


    private boolean[] objetAttrape ={false,false,false,false};
    private boolean[] Ramasser = new boolean[4];
    private int currentIndex;

    private double decalageX = 0;
    private double decalageY = 0;
    
    private Image backgroundImage;

    private double baseCarteX = 1000;  
    private double baseCarteY = 900;
    private Image marsBase = new Image(getClass().getResourceAsStream("/images/objets/Base.png"));
    private static final double BASE_DISPLAY_WIDTH  = 256;
    private static final double BASE_DISPLAY_HEIGHT = 256; 
    
    // Flags pour l’état des touches
    private boolean upPressed, downPressed, leftPressed, rightPressed, escapePressed;
    
    /**
     * Appelé automatiquement après le chargement du FXML.
     * On initialise le modèle, on force la taille des Canvas, on charge l’image de fond,
     * puis on lance la boucle de rendu.
     * @throws IOException 
     */
    @FXML
    public void initialize() throws IOException {
        modelmap = App.getModelMap();
        model = App.getModel();
        if(modelmap.getEndGame()){
            modelmap.setEndGame(false);
            modelmap.setJeuArrete(escapePressed);
        }
        // 1) Créer le modèle
        startTimer();
        this.modelCar = App.getModelCar();
        lastNanoTime = System.nanoTime();
        GraphicsContext gc1 = chronoCanvas.getGraphicsContext2D();

        gc1.setStroke(javafx.scene.paint.Color.GRAY);
        gc1.setGlobalAlpha(0.2);
        gc1.fillText(null, MAP_WIDTH, MAP_HEIGHT);
        gc1.strokeRect(0, 0, MINI_WIDTH, MINI_HEIGHT);
        gc1.setFill(javafx.scene.paint.Color.WHITE);
        gc1.fillRect(0, 0, MINI_WIDTH, MINI_HEIGHT);
        GraphicsContext gc2 = borderCanvas.getGraphicsContext2D();
        gc2.setLineWidth(8F);
        gc2.strokeRect(0,0,340,40);

        String skinPath = modelCar.getSkin();
        try{
            roverSkin = new Image(getClass().getResourceAsStream(skinPath));
        } catch (Exception e) {
            // Fallback si le skin n'est pas trouvé
            roverSkin = new Image(getClass().getResourceAsStream("/images/skins/rover.png"));
        }
        modelCar.addCarListener(newSkin -> {
            roverSkin = newSkin;
        });
        
        // 2) Forcer la taille des Canvas 
        mainCanvas.setWidth(WINDOW_WIDTH);
        mainCanvas.setHeight(WINDOW_HEIGHT);
        miniMapCanvas.setWidth(MINI_WIDTH);
        miniMapCanvas.setHeight(MINI_HEIGHT);
        
        // 3) Charger l’image “mars.png” : 
        try {
            backgroundImage = new Image(getClass().getResourceAsStream("/images/planete/mars.png"));
        } catch (Exception e) {
            backgroundImage = null;
            System.err.println("Impossible de charger /images/planete/mars.png");
        }
        
        // 4) Démarrer la boucle AnimationTimer (60 FPS
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // 1) calculer dt en secondes
                double dt = (now - lastNanoTime) / 1_000_000_000.0;
                lastNanoTime = now;

                // 2) batterie : selon la distance, tick ou recharge
                double dxB = modelmap.getRoverX() - baseCarteX;
                double dyB = modelmap.getRoverY() - baseCarteY;
                double dist = Math.hypot(dxB, dyB);
                if (dist <= 150) {
                    modelCar.recharge(dt);
                } else {
                    modelCar.tick(dt);
                }
                if(remainingSeconds == 0 && !modelmap.getEndGame()){
                    gameLoop.stop();
                    countdownTimeline.pause();
                    modelmap.setIndiceFinPartie(0);
                    modelmap.setEndGame(true);
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/perdu.fxml"));
                    Parent root = null;
                    try {
                        root = fxmlLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Création de la fenêtre
                    Stage stage = new Stage();
                    stage.setTitle("Game Over");
                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();
                }
                // 3) mettre à jour la ProgressBar à chaque frame
                progressBar.setProgress(modelCar.getBatteryPercentage());
                // 4) si batterie vide on quitter
                if (modelCar.isEmpty() && !modelmap.getEndGame()) {
                    gameLoop.stop();
                    countdownTimeline.pause();
                    modelmap.setIndiceFinPartie(1);
                    modelmap.setEndGame(true);
                    gameLoop.stop();
                    countdownTimeline.pause();
                    modelmap.setJeuArrete(true);
                    String target = null;
                    if(modelmap.getIndiceFinPartie()==1){
                        target = "/app/perdu.fxml";
                    }
                    else if(modelmap.getIndiceFinPartie()==2){
                        target = "/app/gagne.fxml";
                    }

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(target));
                    Parent root = null;
                    try {
                        root = fxmlLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Création de la fenêtre
                    Stage stage = new Stage();
                    stage.setTitle("Game Over");
                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();
                }

                // 5) le reste : déplacer et dessiner
                updateModel();
                drawAll();
            }
        };
        gameLoop.start();




        // Capturer Échap une fois la Scene attachée
       mainCanvas.sceneProperty().addListener((obs, oldS, newS) -> {
        if (newS != null) {
            newS.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ESCAPE) {
              showPauseDialog();
               ev.consume();
             }
           });
         }
        });
        
        // Ajoute ces listeners pour le drag and drop :
        mainCanvas.setOnMousePressed(event -> {
            // Conversion coordonnées écran carte
            for(int i=0;i<modelmap.getObjetsImages().length;i++){
                double camX = modelmap.getRoverX() - WINDOW_WIDTH  / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;
                double objetLargeur = 48;
                double objetHauteur = 48;
                modelmap.setObjetEcranX(modelmap.getObjetsCarteX(i) - camX - objetLargeur / 2.0, i);
                modelmap.setObjetEcranY(modelmap.getObjetsCarteY(i) - camY - objetHauteur / 2.0, i);
                
                // Vérifie si le clic est sur l'objet
                if (event.getX() >= modelmap.getObjetEcranX(i) && event.getX() <= modelmap.getObjetEcranX(i) + objetLargeur &&
                event.getY() >= modelmap.getObjetEcranY(i) && event.getY() <= modelmap.getObjetEcranY(i) + objetHauteur) {
                    objetAttrape[i] = true;
                    //Permet de savoir qu'il s'agît de cet item qui est déplacé
                    currentIndex = i;
                    decalageX = event.getX() - modelmap.getObjetEcranX(i);
                    decalageY = event.getY() - modelmap.getObjetEcranY(i);
                }
            }
        });
        //Première case de l'inventaire
        firstCanvas.setOnMousePressed(event -> {
            if (Inventaire[0] != null) {
                isDraggingFromInventory = true;
                draggingInventoryIndex = 0;
                currentIndex = 0; 

                // On met l'objet sur la carte à la position du clic (centrée)
                double camX = modelmap.getRoverX() - WINDOW_WIDTH / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;

                GraphicsContext gc = firstCanvas.getGraphicsContext2D();
                gc.clearRect(0, 0, firstCanvas.getWidth(), firstCanvas.getHeight());

                // Supprime l'objet de l'inventaire
                Inventaire[0] = null;

                modelmap.setObjetsCarteX(camX + event.getSceneX() - 24, currentIndex);
                modelmap.setObjetsCarteY(camY + event.getSceneY() - 24, currentIndex);
                Ramasser[currentIndex] = false; // il n'est plus dans l'inventaire
                drawAll();
            }
        });
        //Deuxième case de l'inventaire
        secondCanvas.setOnMousePressed(event -> {
            if (Inventaire[1] != null) {
                isDraggingFromInventory = true;
                draggingInventoryIndex = 1;
                currentIndex = 1; 

                // On met l'objet sur la carte à la position du clic (centrée)
                double camX = modelmap.getRoverX() - WINDOW_WIDTH / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;

                GraphicsContext gc = secondCanvas.getGraphicsContext2D();
                gc.clearRect(0, 0, secondCanvas.getWidth(), secondCanvas.getHeight());

                // Supprime l'objet de l'inventaire
                Inventaire[1] = null;

                modelmap.setObjetsCarteX(camX + event.getSceneX() - 24, currentIndex);
                modelmap.setObjetsCarteY(camY + event.getSceneY() - 24, currentIndex);
                Ramasser[currentIndex] = false; // il n'est plus dans l'inventaire
                drawAll();
            }
        });
        //Troisième case de l'inventaire
        thirdCanvas.setOnMousePressed(event -> {
            if (Inventaire[2] != null) {
                isDraggingFromInventory = true;
                draggingInventoryIndex = 2;
                currentIndex = 2; 

                // On met l'objet sur la carte à la position du clic (centrée)
                double camX = modelmap.getRoverX() - WINDOW_WIDTH / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;

                GraphicsContext gc = thirdCanvas.getGraphicsContext2D();
                gc.clearRect(0, 0, thirdCanvas.getWidth(), thirdCanvas.getHeight());

                // Supprime l'objet de l'inventaire
                Inventaire[2] = null;

                modelmap.setObjetsCarteX(camX + event.getSceneX() - 24, currentIndex);
                modelmap.setObjetsCarteY(camY + event.getSceneY() - 24, currentIndex);
                Ramasser[currentIndex] = false; // il n'est plus dans l'inventaire
                drawAll();
            }
        });
        // Quatrième case de l'inventaire 
        fourthCanvas.setOnMousePressed(event -> {
            if (Inventaire[3] != null) {
                isDraggingFromInventory = true;
                draggingInventoryIndex = 3;
                currentIndex = 3; 

                // On met l'objet sur la carte à la position du clic (centrée)
                double camX = modelmap.getRoverX() - WINDOW_WIDTH / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;
                GraphicsContext gc = fourthCanvas.getGraphicsContext2D();
                gc.clearRect(0, 0, fourthCanvas.getWidth(), fourthCanvas.getHeight());

                // Supprime l'objet de l'inventaire
                Inventaire[3] = null;

                modelmap.setObjetsCarteX(camX + event.getSceneX() - 24, currentIndex);
                modelmap.setObjetsCarteY(camY + event.getSceneY() - 24, currentIndex);
                Ramasser[currentIndex] = false; // il n'est plus dans l'inventaire
                drawAll();
            }
        });
        mainCanvas.setOnMouseDragged(event -> {
            //Vérifie si l'objet était dans l'inventaire
            if (isDraggingFromInventory) {
                double camX = modelmap.getRoverX() - WINDOW_WIDTH  / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;
                
                modelmap.setObjetsCarteX(camX + event.getSceneX() - 24, draggingInventoryIndex);
                modelmap.setObjetsCarteY(camY + event.getSceneY() - 24, draggingInventoryIndex);

                drawAll(); // Redessine la map
            } else if (objetAttrape[currentIndex]) {
                // Conversion coordonnées écran → carte
                double camX = modelmap.getRoverX() - WINDOW_WIDTH  / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;
                modelmap.setObjetsCarteX(camX + event.getX() + modelmap.getObjetsImages(currentIndex).getWidth() / 2.0 - decalageX, currentIndex);
                modelmap.setObjetsCarteY(camY + event.getY() + modelmap.getObjetsImages(currentIndex).getWidth() / 2.0 - decalageY, currentIndex);
                System.out.println("L'objet est en train d'être drag and drop");
            }
        });
        // Position du rover 
        mainCanvas.setOnMouseReleased(event -> {
            if (isDraggingFromInventory) {
                isDraggingFromInventory = false;
                drawAll(); // Redessine avec l'objet relâché
                // Place l’objet sur la carte si valide
                if (Math.abs(modelmap.getRoverX() - modelmap.getObjetsCarteX(draggingInventoryIndex)) < 100 &&
                    Math.abs(modelmap.getRoverY() - modelmap.getObjetsCarteY(draggingInventoryIndex)) < 100) {
                    Ramasser[draggingInventoryIndex] = false;
                    drawAll();
                } else {
                    System.out.println("Objet relâché hors zone.");
                }

            draggingInventoryIndex = -1;
            } else {
            objetAttrape[currentIndex] = false;
            if (Math.abs(modelmap.getRoverX() - modelmap.getObjetsCarteX(currentIndex)) <20 && Math.abs(modelmap.getRoverY() - modelmap.getObjetsCarteY(currentIndex)) < 40) {
                Inventaire[currentIndex] = modelmap.getObjetsImages(currentIndex);
                Ramasser[currentIndex] = true;
                if(currentIndex ==0){
                    GraphicsContext gcobjet = firstCanvas.getGraphicsContext2D();
                    gcobjet.drawImage(Inventaire[currentIndex], 15, -5);
                }
                else if(currentIndex ==1){
                    GraphicsContext gcobjet = secondCanvas.getGraphicsContext2D();
                    gcobjet.drawImage(Inventaire[currentIndex], 15, -5);
                }
                else if(currentIndex ==2){
                    GraphicsContext gcobjet = thirdCanvas.getGraphicsContext2D();
                    gcobjet.drawImage(Inventaire[currentIndex], 15, -5);
                }
                else if(currentIndex ==3){
                    GraphicsContext gcobjet = fourthCanvas.getGraphicsContext2D();
                    gcobjet.drawImage(Inventaire[currentIndex], 15, -5);
                }
                drawAll();
                
            } else if(Math.abs(modelmap.getAntenneCarteX() - modelmap.getObjetsCarteX(currentIndex)) <40 && Math.abs(modelmap.getAntenneCarteY() - modelmap.getObjetsCarteY(currentIndex)) < 40) {
                modelmap.setdepose(true, currentIndex);
                if(modelmap.getdepose(0) == true 
                    && modelmap.getdepose(1) == true 
                    && modelmap.getdepose(2) == true 
                    && modelmap.getdepose(3) == true){
                    
                    //Dis que c'est la fin de la partie
                    modelmap.setEndGame(true);
                    //Précise que c'est le jeu est arrêté
                    modelmap.setJeuArrete(true);
                    modelmap.setIndiceFinPartie(2);
                    gameLoop.stop();
                    countdownTimeline.pause();
                    model.setScore(remainingSeconds);
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/gagne.fxml"));
                    Parent root = null;
                    try {
                        root = fxmlLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Création de la fenêtre
                    Stage stage = new Stage();
                    stage.setTitle("Game Over");
                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();
                }
                drawAll();
            }
        }
        });
    // }
}
    /**
     * Lit l’état des flags up/down/left/right, puis déplace le rover en conséquence.
     */
    private void updateModel() {
        double dx = 0, dy = 0;
        if (leftPressed) dx -= ROVER_SPEED;

        if (rightPressed) dx += ROVER_SPEED;
        if (upPressed)    dy -= ROVER_SPEED;
        if (downPressed)  dy += ROVER_SPEED;
        if (dx != 0 || dy != 0) {
            modelmap.moveRover(dx, dy);

            modelmap.setroverAngle(Math.toDegrees(Math.atan2(dy, dx)));
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
    /**
 * Dessine la vue principale :
 *  - le fond (backgroundImage) recadré ou étiré selon la taille du Canvas,
 *  - tous les objets à leurs positions relatives à la caméra,
 *  - le rover (skin) centré sur sa position.
 */
    private void drawMainView() {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        // 1) Dimensions dynamiques du Canvas principal
        double windowW = mainCanvas.getWidth();
        double windowH = mainCanvas.getHeight();
        if (windowW <= 0 || windowH <= 0) return;  // pas encore dimensionné

        // 2) Calculer la “caméra” centrée sur le rover
        double camX = modelmap.getRoverX() - windowW  / 2.0;
        double camY = modelmap.getRoverY() - windowH / 2.0;

        // 3) Clamp dynamique 
        if (camX < 0)                       camX = 0;
        else if (camX + windowW > MAP_WIDTH) camX = MAP_WIDTH - windowW;

        if (camY < 0)                       camY = 0;
        else if (camY + windowH > MAP_HEIGHT) camY = MAP_HEIGHT - windowH;

        // 4) Afficher le fond
        if (backgroundImage != null) {
            gc.clearRect(0, 0, windowW, windowH);
            // Extraire la portion [camX,camY, windowW×windowH] de l’image
            gc.drawImage(
                backgroundImage,
                /* sx= */ camX,             /* sy= */ camY,
                /* sw= */ windowW,          /* sh= */ windowH,
                /* dx= */ 0,                /* dy= */ 0,
                /* dw= */ windowW,          /* dh= */ windowH
            );
        } else {
            // Fallback : fond gris
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, windowW, windowH);
        }
        double bx = baseCarteX - camX - (BASE_DISPLAY_WIDTH  / 2.0);
        double by = baseCarteY - camY - (BASE_DISPLAY_HEIGHT / 2.0);
        gc.drawImage(marsBase, bx, by, BASE_DISPLAY_WIDTH, BASE_DISPLAY_HEIGHT);
        // 5) Dessiner les objets
        double objetW = 64, objetH = 64;
        for (int i = 0; i < modelmap.getObjetsImages().length; i++) {
            if (!Ramasser[i] && !modelmap.getdepose(i) && modelmap.getObjetsImages(i) != null) {
                double ox = modelmap.getObjetsCarteX(i) - camX - objetW / 2.0;
                double oy = modelmap.getObjetsCarteY(i) - camY - objetH / 2.0;
                gc.drawImage(modelmap.getObjetsImages(i), ox, oy, objetW, objetH);
            }
        }


        // Dessiner l’antenne
        //En fonction du nombre d'objet déposé cela dessine un skin différent
        if(VerifObjetDepose(modelmap.getdepose())<2){
            double antW = 128, antH = 128;
            double ax = modelmap.getAntenneCarteX() - camX - antW / 2.0;
            double ay = modelmap.getAntenneCarteY() - camY - antH / 2.0;
            gc.drawImage(modelmap.getAntennneImages(0), ax, ay, antW, antH);
        }
        else if(VerifObjetDepose(modelmap.getdepose())<4){
            double antW = 128, antH = 128;
            double ax = modelmap.getAntenneCarteX() - camX - antW / 2.0;
            double ay = modelmap.getAntenneCarteY() - camY - antH / 2.0;
            gc.drawImage(modelmap.getAntennneImages(1), ax, ay, antW, antH);
        }
        else if(VerifObjetDepose(modelmap.getdepose())==4){
            double antW = 128, antH = 128;
            double ax = modelmap.getAntenneCarteX() - camX - antW / 2.0;
            double ay = modelmap.getAntenneCarteY() - camY - antH / 2.0;
            gc.drawImage(modelmap.getAntennneImages(2), ax, ay, antW, antH);
        }

        // Dessiner le rover (skin)
        if (roverSkin != null) {
            double rx = modelmap.getRoverX() - camX - (ROVER_DISPLAY_WIDTH / 2.0);
            double ry = modelmap.getRoverY() - camY - (ROVER_DISPLAY_HEIGHT / 2.0);
            gc.save(); // Sauvegarde l’état du contexte graphique
            gc.translate(rx + ROVER_DISPLAY_WIDTH / 2.0, ry + ROVER_DISPLAY_HEIGHT / 2.0); // Centre de rotation
            gc.rotate(modelmap.getroverAngle()+90); // Applique la rotation
            gc.drawImage(
                roverSkin,
                -ROVER_DISPLAY_WIDTH / 2.0, -ROVER_DISPLAY_HEIGHT / 2.0,
                ROVER_DISPLAY_WIDTH, ROVER_DISPLAY_HEIGHT
            );
    gc.restore(); // Restaure l’état initial
        } else {
            // Fallback : petit carré rouge
            double sz = 10;
            double fx = modelmap.getRoverX() - camX - sz / 2.0;
            double fy = modelmap.getRoverY() - camY - sz / 2.0;
            gc.setFill(Color.RED);
            gc.fillRect(fx, fy, sz, sz);
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
        
        //  Fond sombre
        gc.setFill(javafx.scene.paint.Color.rgb(30, 30, 30));
        gc.fillRect(0, 0, MINI_WIDTH, MINI_HEIGHT);
        
        // Calculer le ratio réel en minimap
        double scaleX = MINI_WIDTH  / MAP_WIDTH;
        double scaleY = MINI_HEIGHT / MAP_HEIGHT;
        
        // Position du rover sur la minimap
        double roverMiniX = modelmap.getRoverX() * scaleX;
        double roverMiniY = modelmap.getRoverY() * scaleY;
        
        // Récupère les coordonnées de l'antenne sur la minimap
        double antenneMiniX = modelmap.getAntenneCarteX() * scaleX;
        double antenneMiniY = modelmap.getAntenneCarteY() * scaleY;
        
        double[] objetsMiniX = new double[4];
        double[] objetsMiniY = new double[4];
        
        // Récupère les coordonnées des objets sur la minimap
        for(int i=0;i<modelmap.getObjetsImages().length;i++){
            objetsMiniX[i] = modelmap.getObjetsCarteX(i) * scaleX;
            objetsMiniY[i] = modelmap.getObjetsCarteY(i) * scaleY;
        }
        
        // Petit carré vert
        double dotSize = 4;
        
        //Position du rover
        gc.setFill(javafx.scene.paint.Color.LIMEGREEN);
        gc.fillRect(
            roverMiniX - dotSize / 2.0,
            roverMiniY - dotSize / 2.0,
            dotSize,
            dotSize
            );
        //Position de l'antenne
        gc.setFill(javafx.scene.paint.Color.BLUE);
        gc.fillRect(
            antenneMiniX - dotSize / 2.0,
            antenneMiniY - dotSize / 2.0,
            dotSize,
            dotSize
            );
                
        //Position des objets
        for(int i=0;i<modelmap.getObjetsImages().length;i++){
            // Vérifie que l'objet n'est pas dans l'inventaire ni dans l'antenne
            if(!Ramasser[i] && !modelmap.getdepose(i)){
                gc.setFill(javafx.scene.paint.Color.RED);
                gc.fillRect(
                    objetsMiniX[i] - dotSize / 2.0,
                    objetsMiniY[i] - dotSize / 2.0,
                    dotSize,
                    dotSize
                    );
                }
            }
            
            // Cadre blanc
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
            case ESCAPE: escapePressed = true; break;
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
            case ESCAPE: escapePressed = false; break;
            default: break;
        }
    }
            
    private void startTimer() {
        countdownTimeline = new Timeline(
        new KeyFrame(Duration.seconds(1), ev -> {
            if (remainingSeconds > 0) {
            remainingSeconds--;
            int m = remainingSeconds / 60;
            int s = remainingSeconds % 60;
            label.setText(String.format("%02d:%02d", m, s));
            } else {
            label.setText("Temps écoulé !");
            }
        })
        );
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }

    private void showPauseDialog() {
        // 1) Stoppe l'animation
        gameLoop.stop();
        countdownTimeline.pause();

        try {
            //  Charge pause.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/pause.fxml"));
            Parent root = loader.load();

            //  Récupère le controller de la pause 
            ControllerPause ctrl = loader.getController();
            ctrl.setParent(this);

            // Ouvre une fenêtre modale
            Stage pauseStage = new Stage();
            pauseStage.initModality(Modality.APPLICATION_MODAL);
            pauseStage.setTitle("Pause");
            pauseStage.setScene(new Scene(root));
            pauseStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

        lastNanoTime = System.nanoTime();

        // Relance le jeu quand la fenêtre de pause se ferme
        gameLoop.start();
        countdownTimeline.play();
    }
    public int VerifObjetDepose(boolean[] a){
        int acc = 0;
        for(int i=0;i<a.length;i++){
            if(a[i]==true){
                acc++;
            }
        }
        return acc;
    }
    }
