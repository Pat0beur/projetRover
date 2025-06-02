package controllers;
import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
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

		btnQuitter.setOnAction(event -> {
			System.exit(0);
		});
    }
}