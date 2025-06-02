module app {
    requires javafx.controls;
    requires javafx.fxml;
    opens controllers to javafx.fxml;
    opens models to javafx.fxml;
    opens views to javafx.fxml;
    opens app to javafx.fxml;
    exports app;
}