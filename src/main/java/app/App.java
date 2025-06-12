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
import models.Model;
import models.ModelCar;
import models.ModelMap;

public class App extends Application {
    private static final double MAP_WIDTH  = 2048;     // taille réelle de la carte
    private static final double MAP_HEIGHT = 2048;
    private static ModelCar modelCar = new ModelCar();
    private static Model model = new Model();
    private static ModelMap modelMap/* = new ModelMap(MAP_WIDTH, MAP_HEIGHT)*/;
    // private static ModelMenu modelMenu = new ModelMenu();
    private static boolean fromPause = false;

    public static void resetGame() {
        // recrée la map et remet la batterie de la voiture à 100
        modelMap = new ModelMap(MAP_WIDTH, MAP_HEIGHT);
        modelCar.resetBattery();
    }
    
    public static ModelCar getModelCar() {
        return modelCar;
    }
    public static Model getModel() {
        return model;
    }
    public static ModelMap getModelMap(){
        return modelMap;
    }
    
    // public static ModelMap setModelMap(){
    //     modelMap = new ModelMap(MAP_WIDTH, MAP_HEIGHT);
    //     return modelMap;
    // }



    @SuppressWarnings("exports")
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/Menu.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Menu");
        primaryStage.show();
    }


    public static void setFromPause(boolean b) { fromPause = b;}
    public static boolean isFromPause(){ return fromPause;}

    public static void main(String[] args) {
        launch(args);
    }
}