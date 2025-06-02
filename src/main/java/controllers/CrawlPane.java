package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * CrawlPane qui adapte la taille du crawl à la taille réelle de la Stage,
 * pour éviter qu'un "carré noir" ne couvre qu'une partie de l'écran.
 */
public class CrawlPane {

    private final Stage stage;

    public CrawlPane(Stage stage) {
        this.stage = stage;
    }

    /**
     * Démarre le crawl Star Wars (sans audio) en plein écran (ou à la taille actuelle de la Stage).
     */
    public void startCrawl() {
        // 1. Texte du crawl (modifiez à volonté)
        String crawlText =
            "ÉPISODE I\n" +
            "LA MENACE FANTÔME\n" +
            "\n" +
            "Il y a bien longtemps, dans une galaxie lointaine, très lointaine...\n" +
            "\n" +
            "Les Seigneurs Sith, autrefois réduits au silence, resurgissent dans l'ombre.\n" +
            "Obi-Wan Kenobi, désormais tuteur du jeune Anakin Skywalker, se trouve confronté\n" +
            "à une nouvelle et mystérieuse menace qui pèse sur la République.\n" +
            "\n" +
            "Pendant ce temps, sur Coruscant, le Chancelier Valorum organise secrètement\n" +
            "l'ordre de la République pour affronter les complots croissants de l'ombre Sith.\n" +
            "\n" +
            "Mais une prophétie oubliée divise les esprits : celui qui unifiera la Force\n" +
            "doit naître d'un choix difficile… Le destin de la galaxie est en jeu.\n" +
            "\n" +
            "⬇ DÉCOUVREZ VOTRE MISSION ⬇\n" +
            "\n" +
            "Vous incarnez un jeune pilote rebelle nommé Talin Voss, recruté par la Nouvelle\n" +
            "République. Votre but :\n" +
            "   • Explorer les systèmes inconnus\n" +
            "   • Trouver l'artefact sacré des Jedis au cœur de la planète Ossus\n" +
            "   • Échapper aux sbires du Seigneur Sith Malakar\n" +
            "   • Ramener l'équilibre dans la Force\n" +
            "\n" +
            "Bonne chance, jeune Padawan…\n";

        // 2. Créer le texte avec wrapping dépendant de la largeur de l'écran
        Text textNode = new Text(crawlText);
        // Police taille 28 (plus lisible et tient mieux à l'écran)
        textNode.setFont(Font.font("Arial", 28));
        textNode.setStyle("-fx-fill: gold;");  // Couleur or
        textNode.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // 3. Placer le Text dans un Group pour appliquer une rotation 3D
        Group textGroup = new Group(textNode);

        // 4. Conteneur principal (StackPane) sur fond noir
        StackPane root = new StackPane(textGroup);
        root.setStyle("-fx-background-color: black;");
        StackPane.setAlignment(textGroup, Pos.BOTTOM_CENTER);

        // 5. Appliquer une rotation de 30° sur l'axe X pour l'effet de recul
        Rotate rotate = new Rotate(30, Rotate.X_AXIS);
        textGroup.getTransforms().add(rotate);

        // 6. Récupérer les dimensions actuelles de la Stage
        double width  = stage.getWidth();
        double height = stage.getHeight();

        // 7. Définir la largeur de wrapping du texte en fonction de la largeur de l'écran
        //    Ici on prend 70 % de la largeur, mais c'est modulable.
        double wrappingWidth = width * 0.7;
        textNode.setWrappingWidth(wrappingWidth);

        // 8. Créer la scène 3D aux dimensions exactes de la Stage
        Scene scene = new Scene(root, width, height, true);
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1500);  // Recule la caméra pour bien voir l'effet 3D
        camera.setNearClip(0.1);
        camera.setFarClip(5000);
        scene.setCamera(camera);

        // 9. Afficher cette nouvelle scène
        stage.setScene(scene);
        stage.show();

        // 10. Une fois la scène affichée, on attend le prochain cycle JavaFX pour
        //     que le layout soit calculé. Cela nous permet de mesurer correctement
        //     la hauteur du texte après wrapping.
        Platform.runLater(() -> {
            // Forcer l'application du CSS + layout de tous les nœuds enfants
            root.applyCss();
            root.layout();

            // 11. Position initiale : on place le groupe juste en-dessous de la fenêtre
            double initialY = height + 50;
            textGroup.setTranslateY(initialY);

            // 12. Mesurer la hauteur véritable du Text après wrapping
            double textHeight = textNode.getBoundsInLocal().getHeight();

            // 13. Calculer la position finale (hors écran par le haut)
            double finalY = -textHeight - 50;

            // 14. Créer l’animation : de initialY à finalY sur 30 secondes
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,          new KeyValue(textGroup.translateYProperty(), initialY)),
                new KeyFrame(Duration.seconds(30),   new KeyValue(textGroup.translateYProperty(), finalY))
            );
            timeline.play();

            // 15. Quand l’animation se termine, vous pouvez, par exemple,
            //     recharger la scène du menu ou lancer la scène de jeu.
            timeline.setOnFinished(evt -> {
                // loadMenuAgain() ou lancerGameScene() ; à implémenter si besoin
            });
        });
    }
}
