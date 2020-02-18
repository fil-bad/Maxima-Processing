import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import processing.core.PApplet;

public class Main extends Application {

    public static void main(String[] args) {
//        PApplet.main("Test_3D");
        PApplet.main("ProcessingClass");
        //launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("gluonBoundary/fxmlSrc/FxHome.fxml"));
        Scene scene = new Scene(root);

        stage.setTitle("RobInd");
        stage.setScene(scene);
        stage.show();
    }
}
