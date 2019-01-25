package app;


import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import simulation.MapBuilder;

public class Application extends javafx.application.Application {

    double intersectionRadius = 80.0;
    MapBuilder mapBuilder = new MapBuilder(this.intersectionRadius,this.root);

    Simulation simulation;
    Group root = new Group();
    Double scale = 0.3;

    private Scale fxScale;

    public void setScale(double scale){
        this.scale = scale;
        fxScale.setX(scale);
        fxScale.setY(scale);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{

        fxScale = new Scale(scale,scale);
        root.getTransforms().add(fxScale);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent parent = loader.load();
        primaryStage.setTitle("Simulation of Road Traffic");
        primaryStage.setScene(new Scene(parent));

        Controller c = loader.getController();

        c.initController(this);
        c.getPane().getChildren().add(root);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
