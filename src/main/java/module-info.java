module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    opens controllers to javafx.fxml;
    exports controllers;
    opens models to javafx.fxml;
    exports models;
    opens views to javafx.fxml;
    exports views;
    opens app to javafx.fxml;
    exports app;
}