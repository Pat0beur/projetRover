package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;

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
    }
}