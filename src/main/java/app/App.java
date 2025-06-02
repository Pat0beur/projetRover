// package app;

// import javafx.application.Application;
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.stage.Stage;
// import controllers.ControllerMenu;
// import java.net.URL;

// import java.io.IOException;

// /**
//  * JavaFX App
//  */
// public class App extends Application {

//     private static Scene scene;

    // @Override
    // public void start(Stage stage) throws IOException {
    //     scene = new Scene(loadFXML("secondary"), 640, 480);
    //     stage.setScene(scene);
    //     stage.show();
    // }

    // static void setRoot(String fxml) throws IOException {
    //     scene.setRoot(loadFXML(fxml));
    // }

    // private static Parent loadFXML(String fxml) throws IOException {
    //     FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    //     return fxmlLoader.load();
    // }
//     public void start(Stage primaryStage) throws Exception {
//         FXMLLoader loader = new FXMLLoader();
//         loader.setLocation(getClass().getResource("/app/Menu.fxml"));
//         Parent root = loader.load();
//         Scene scene = new Scene(root);
//         primaryStage.setScene(scene) ;
//         primaryStage.show() ;
//     }
//     public static void main(String[] args) {
//         launch();
//     }

// }
package app;    
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("FXML found: " + getClass().getResource("C:/Users/raph_/Desktop/Polytech/ET3/S6/IHM/Projet/projetRover/src/main/resources/app/Menu.fxml")); // test de debug
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/Menu.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Menu");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
