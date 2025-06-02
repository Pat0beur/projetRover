package controllers;


import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class ControllerMenu {

    @FXML
    private Button btnJouer;
    @FXML
    private Button btnPersonnaliser;
    @FXML
    private Button btnCommandes;

    @FXML
    private Button btnQuitter;

    @FXML
    private RadioButton radiobtnEasy;
    @FXML
    private RadioButton radiobtnMedium;
    @FXML
    private RadioButton radiobtnHard;

    @FXML
    public void initialize() {
        System.out.println("✅ Contrôleur initialisé !");

<<<<<<< HEAD
    btnQuitter.setOnAction(event -> {
        System.out.println("👋 Fermeture de l'application");
        System.exit(0);
    });

    btnPersonnaliser.setOnAction(event -> {
        try {
            Stage stage = (Stage)btnPersonnaliser.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/Personnalisation.fxml"));
            Parent root= loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    btnCommandes.setOnAction(event -> {
        try {
            Stage stage = (Stage)btnCommandes.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/Commandes.fxml"));
            Parent root;
            root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
=======
        btnQuitter.setOnAction(event -> {
            System.out.println("👋 Fermeture de l'application");
            System.exit(0);
        });
        btnCommandes.setOnAction(event -> {
            try {
                // Stage stage = new Stage();
                Stage stage = (Stage)btnCommandes.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/Commandes.fxml"));
                Parent root;
                root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
>>>>>>> 9189fdae8dfdd12d2ae7d39c132410a734f2111a

        });

        btnJouer.setOnAction(e -> {
            System.out.println("🚀 Lancement du crawl Star Wars (sans audio) …");
            // Récupère la Stage courante
            Stage stage = (Stage) btnJouer.getScene().getWindow();
            // Instancie et démarre le CrawlPane
            CrawlPane crawlPane = new CrawlPane(stage);
            crawlPane.startCrawl();
        });
    }
}