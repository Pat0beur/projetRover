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
// import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
// import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.ModelMap;
// import views.ViewMap;
import models.ModelCar;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
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
    private Timeline batteryTimeline;




    // Modèle stockant la position du rover + ModelCar
    private ModelMap modelmap;
    private ModelCar modelCar;
    private Image roverSkin;
    // private ImageView test;

    // Tout ce qui se rapporte aux objets
    private Image[] Inventaire = new Image[4];
    int min = 0;
    int max = 2000;
    // int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1); // entre min et max inclus
    private double[] objetsCarteX = { ThreadLocalRandom.current().nextInt(min, max + 1), 
        ThreadLocalRandom.current().nextInt(min, max + 1), 
        ThreadLocalRandom.current().nextInt(min, max + 1), 
        ThreadLocalRandom.current().nextInt(min, max + 1) }; // X de chaque objet
    private double[] objetsCarteY = { ThreadLocalRandom.current().nextInt(min, max + 1), 
        ThreadLocalRandom.current().nextInt(min, max + 1), 
        ThreadLocalRandom.current().nextInt(min, max + 1), 
        ThreadLocalRandom.current().nextInt(min, max + 1) }; // Y de chaque objet
    private Image[] objetsImages = {
        new Image(getClass().getResourceAsStream("/images/objets/circuit_imprime_test.png")),
        new Image(getClass().getResourceAsStream("/images/objets/panneau_solaire.png")),
        new Image(getClass().getResourceAsStream("/images/objets/tournevis_test.png")),
        new Image(getClass().getResourceAsStream("/images/objets/vis_test.png"))
    };
    private double antenneCarteX = ThreadLocalRandom.current().nextInt(min, max + 1);
    private double antenneCarteY = ThreadLocalRandom.current().nextInt(min, max + 1);
    private Image[] antenneImages = {
        new Image(getClass().getResourceAsStream("/images/antenne/antenne_cassee.png")),
        new Image(getClass().getResourceAsStream("/images/antenne/antenne_un_peu_cassee.png")),
        new Image(getClass().getResourceAsStream("/images/antenne/antenne.png"))
    };
    private boolean[] objetAttrape ={false,false,false,false};
    private double[] objetEcranX = new double[5];
    private double[] objetEcranY = new double[5];
    private boolean[] Ramasser = new boolean[4];
    private int currentIndex;

    private ControllerMenu controllerMenu;

    private double decalageX = 0;
    private double decalageY = 0;
    
    // Image de fond (ici mars 4000×4000 ou 2000×2000 selon ce que vous avez chargé)
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
        // progressBar.setProgress(1);
        modelmap = App.getModelMap();
        // if(modelmap.getEndGame()){
        //     modelmap.setEndGame(false);
        // }
        // else{
        // 1) Créer le modèle
        startTimer();
        // mainCanvas = new Canvas();
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

        // test = new ImageView(roverSkin);
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
        gameLoop = new AnimationTimer() {
            private long startTime = 120;
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
                    modelmap.setIndiceFinPartie(0); // Faire test ici
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
                // 4) si batterie vide → quitter
                if (modelCar.isEmpty() && !modelmap.getEndGame()) {
                    gameLoop.stop();
                    countdownTimeline.pause();
                    modelmap.setIndiceFinPartie(1); // Faire test ici
                    modelmap.setEndGame(true);
                    // modelmap.setJeuArrete(true);
                    String target = null;
                    if(modelmap.getIndiceFinPartie()==1 /*&& modelmap.getIndiceFinPartie()==0*/){
                        target = "/app/perdu.fxml";
                    }
                    else if(modelmap.getIndiceFinPartie()==2){
                        target = "/app/gagne.fxml";
                    }

                    // System.out.println("Les valeurs de EndGame et de setJeuArrete on bien été modifié");
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
            // Conversion coordonnées écran → carte
            for(int i=0;i<objetsImages.length;i++){
                double camX = modelmap.getRoverX() - WINDOW_WIDTH  / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;
                double objetLargeur = 48; // même taille que pour l'affichage
                double objetHauteur = 48;
                objetEcranX[i]= objetsCarteX[i] - camX - objetLargeur / 2.0;
                objetEcranY[i]= objetsCarteY[i] - camY - objetHauteur / 2.0;
                
                // Vérifie si le clic est sur l'objet
                if (event.getX() >= objetEcranX[i] && event.getX() <= objetEcranX[i] + objetLargeur &&
                event.getY() >= objetEcranY[i] && event.getY() <= objetEcranY[i] + objetHauteur) {
                    objetAttrape[i] = true;
                    //Permet de savoir qu'il s'agît de cet item qui est déplacé
                    currentIndex = i;
                    decalageX = event.getX() - objetEcranX[i];
                    decalageY = event.getY() - objetEcranY[i];
                }
            }
        });
        firstCanvas.setOnMousePressed(event -> {
            if (Inventaire[0] != null) {
                isDraggingFromInventory = true;
                draggingInventoryIndex = 0;
                currentIndex = 0; // réutilise ta logique existante

                // On met l'objet sur la carte à la position du clic (centrée)
                double camX = modelmap.getRoverX() - WINDOW_WIDTH / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;

                GraphicsContext gc = firstCanvas.getGraphicsContext2D();
                gc.clearRect(0, 0, firstCanvas.getWidth(), firstCanvas.getHeight());

                // Supprime l'objet de l'inventaire
                Inventaire[0] = null;

                objetsCarteX[currentIndex] = camX + event.getSceneX() - 24;
                objetsCarteY[currentIndex] = camY + event.getSceneY() - 24;
                Ramasser[currentIndex] = false; // il n'est plus dans l'inventaire
                drawAll();
            }
        });
        
        secondCanvas.setOnMousePressed(event -> {
            if (Inventaire[1] != null) {
                isDraggingFromInventory = true;
                draggingInventoryIndex = 1;
                currentIndex = 1; // réutilise ta logique existante

                // On met l'objet sur la carte à la position du clic (centrée)
                double camX = modelmap.getRoverX() - WINDOW_WIDTH / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;

                GraphicsContext gc = secondCanvas.getGraphicsContext2D();
                gc.clearRect(0, 0, secondCanvas.getWidth(), secondCanvas.getHeight());

                // Supprime l'objet de l'inventaire
                Inventaire[1] = null;

                objetsCarteX[currentIndex] = camX + event.getSceneX() - 24;
                objetsCarteY[currentIndex] = camY + event.getSceneY() - 24;
                Ramasser[currentIndex] = false; // il n'est plus dans l'inventaire
                drawAll();
            }
        });
        thirdCanvas.setOnMousePressed(event -> {
            if (Inventaire[2] != null) {
                isDraggingFromInventory = true;
                draggingInventoryIndex = 2;
                currentIndex = 2; // réutilise ta logique existante

                // On met l'objet sur la carte à la position du clic (centrée)
                double camX = modelmap.getRoverX() - WINDOW_WIDTH / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;

                GraphicsContext gc = thirdCanvas.getGraphicsContext2D();
                gc.clearRect(0, 0, thirdCanvas.getWidth(), thirdCanvas.getHeight());

                // Supprime l'objet de l'inventaire
                Inventaire[2] = null;

                objetsCarteX[currentIndex] = camX + event.getSceneX() - 24;
                objetsCarteY[currentIndex] = camY + event.getSceneY() - 24;
                Ramasser[currentIndex] = false; // il n'est plus dans l'inventaire
                drawAll();
            }
        });
        fourthCanvas.setOnMousePressed(event -> {
            if (Inventaire[3] != null) {
                isDraggingFromInventory = true;
                draggingInventoryIndex = 3;
                currentIndex = 3; // réutilise ta logique existante

                // On met l'objet sur la carte à la position du clic (centrée)
                double camX = modelmap.getRoverX() - WINDOW_WIDTH / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;
                GraphicsContext gc = fourthCanvas.getGraphicsContext2D();
                gc.clearRect(0, 0, fourthCanvas.getWidth(), fourthCanvas.getHeight());

                // Supprime l'objet de l'inventaire
                Inventaire[3] = null;

                objetsCarteX[currentIndex] = camX + event.getSceneX() - 24;
                objetsCarteY[currentIndex] = camY + event.getSceneY() - 24;
                Ramasser[currentIndex] = false; // il n'est plus dans l'inventaire
                drawAll();
            }
        });
        mainCanvas.setOnMouseDragged(event -> {
            if (isDraggingFromInventory) {
                double camX = modelmap.getRoverX() - WINDOW_WIDTH  / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;
                
                objetsCarteX[draggingInventoryIndex] = camX + event.getX() - 24;
                objetsCarteY[draggingInventoryIndex] = camY + event.getY() - 24;

                drawAll(); // Redessine la map
            } else if (objetAttrape[currentIndex]) {
                // Conversion coordonnées écran → carte
                double camX = modelmap.getRoverX() - WINDOW_WIDTH  / 2.0;
                double camY = modelmap.getRoverY() - WINDOW_HEIGHT / 2.0;
                objetsCarteX[currentIndex] = camX + event.getX() + objetsImages[currentIndex].getWidth() / 2.0 - decalageX;
                objetsCarteY[currentIndex] = camY + event.getY() + objetsImages[currentIndex].getHeight() / 2.0 - decalageY;
                System.out.println("L'objet est en train d'être drag and drop");
            }
        });
        // Position du rover relative ??
        mainCanvas.setOnMouseReleased(event -> {
            if (isDraggingFromInventory) {
                isDraggingFromInventory = false;
                // draggingInventoryIndex = -1;
                drawAll(); // Redessine avec l'objet relâché
            // Place l’objet sur la carte si valide
                if (Math.abs(modelmap.getRoverX() - objetsCarteX[draggingInventoryIndex]) < 100 &&
                    Math.abs(modelmap.getRoverY() - objetsCarteY[draggingInventoryIndex]) < 100) {
                    System.out.println("L'objet de l'inventaire a été placé sur la carte !");
                    Ramasser[draggingInventoryIndex] = false;
                    drawAll();
                } else {
                    System.out.println("Objet relâché hors zone.");
                }

            draggingInventoryIndex = -1;
            } else {
            objetAttrape[currentIndex] = false;
            if (Math.abs(modelmap.getRoverX() - objetsCarteX[currentIndex]) <20 && Math.abs(modelmap.getRoverY() - objetsCarteY[currentIndex]) < 40) {
                System.out.println("L'objet est posé sur le rover !");
                Inventaire[currentIndex] = objetsImages[currentIndex];
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
                System.out.println("Voici ce qu'il y a dans l'Inventaire"+Inventaire[0]+" "+Inventaire[1]+" "+Inventaire[2]+" "+Inventaire[3]+" ");
                drawAll();
                // Ici tu pourras ajouter à l'inventaire
            } else if(Math.abs(antenneCarteX - objetsCarteX[currentIndex]) <20 && Math.abs(antenneCarteY - objetsCarteY[currentIndex]) < 20) {
                System.out.println("Bravo vous avez déposé l'objet sur l'antenne !");
                modelmap.setdepose(true, currentIndex);
                if(modelmap.getdepose(0) == true 
                    && modelmap.getdepose(1) == true 
                    && modelmap.getdepose(2) == true 
                    && modelmap.getdepose(3) == true){
                    modelmap.setEndGame(true);
                    modelmap.setJeuArrete(true);
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
                System.out.println("La valeur d'etatAntenne : "+etatAntenne);
                drawAll();
            }
            else{                
                System.out.println("Voici la position du rover :"+ modelmap.getRoverX()+" , "+modelmap.getRoverY());
                System.out.println("Voici la position de l'objet :"+ objetsCarteX[currentIndex]+" , "+objetsCarteY[currentIndex]);
                System.out.println("L'objet n'est PAS sur le rover.");
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
        if (leftPressed){  dx -= ROVER_SPEED;
            // test.setRotate(90);
        }
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

        // 3) Clamp dynamique : jamais sortir de [0 .. mapWidth-windowW] et [0 .. mapHeight-windowH]
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

        // 5) Dessiner les objets
        double objetW = 64, objetH = 64;
        for (int i = 0; i < objetsImages.length; i++) {
            if (!Ramasser[i] && !modelmap.getdepose(i) && objetsImages[i] != null) {
                double ox = objetsCarteX[i] - camX - objetW / 2.0;
                double oy = objetsCarteY[i] - camY - objetH / 2.0;
                gc.drawImage(objetsImages[i], ox, oy, objetW, objetH);
            }
        }
    double bx = baseCarteX - camX - (BASE_DISPLAY_WIDTH  / 2.0);
        double by = baseCarteY - camY - (BASE_DISPLAY_HEIGHT / 2.0);
        gc.drawImage(marsBase, bx, by, BASE_DISPLAY_WIDTH, BASE_DISPLAY_HEIGHT);

        // 6) Dessiner l’antenne
        if(VerifObjetDepose(modelmap.getdepose())<2){
            double antW = 128, antH = 128;
            double ax = antenneCarteX - camX - antW / 2.0;
            double ay = antenneCarteY - camY - antH / 2.0;
            gc.drawImage(antenneImages[0], ax, ay, antW, antH);
        }
        else if(VerifObjetDepose(modelmap.getdepose())<4){
            double antW = 128, antH = 128;
            double ax = antenneCarteX - camX - antW / 2.0;
            double ay = antenneCarteY - camY - antH / 2.0;
            gc.drawImage(antenneImages[1], ax, ay, antW, antH);
        }
        else if(VerifObjetDepose(modelmap.getdepose())==4){
            double antW = 128, antH = 128;
            double ax = antenneCarteX - camX - antW / 2.0;
            double ay = antenneCarteY - camY - antH / 2.0;
            gc.drawImage(antenneImages[2], ax, ay, antW, antH);
        }

        // 7) Dessiner le rover (skin)
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
        
        // 1) Fond sombre
        gc.setFill(javafx.scene.paint.Color.rgb(30, 30, 30));
        gc.fillRect(0, 0, MINI_WIDTH, MINI_HEIGHT);
        
        // 2) Calculer le ratio réel → minimap
        double scaleX = MINI_WIDTH  / MAP_WIDTH;
        double scaleY = MINI_HEIGHT / MAP_HEIGHT;
        
        // 3) Position du rover sur la minimap
        double roverMiniX = modelmap.getRoverX() * scaleX;
        double roverMiniY = modelmap.getRoverY() * scaleY;
        
        // Récupère les coordonnées de l'antenne sur la minimap
        double antenneMiniX = antenneCarteX * scaleX;
        double antenneMiniY = antenneCarteY * scaleY;
        
        double[] objetsMiniX = new double[4];
        double[] objetsMiniY = new double[4];
        
        // Récupère les coordonnées des objets sur la minimap
        for(int i=0;i<objetsImages.length;i++){
            objetsMiniX[i] = objetsCarteX[i] * scaleX;
            objetsMiniY[i] = objetsCarteY[i] * scaleY;
        }
        
        // 4) Petit carré vert
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
        for(int i=0;i<objetsImages.length;i++){
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
    public void miseAJourSkin(String skinPath) {
        // Actualiser l’image du skin ici
        modelmap.getCar().notifyCarChanged(new Image(getClass().getResource(skinPath).toExternalForm()));
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
            // 2) Charge pause.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/pause.fxml"));
            Parent root = loader.load();

            // 3) Récupère le controller de la pause et passe-lui une référence si besoin
            ControllerPause ctrl = loader.getController();
            ctrl.setParent(this);

            // 4) Ouvre une fenêtre modale
            Stage pauseStage = new Stage();
            pauseStage.initModality(Modality.APPLICATION_MODAL);
            pauseStage.setTitle("Pause");
            pauseStage.setScene(new Scene(root));
            pauseStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

        lastNanoTime = System.nanoTime();

        // 5) Relance le jeu quand la fenêtre de pause se ferme
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
