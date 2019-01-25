package app;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import simulation.BidirectionalRoad;
import simulation.MapBuilder;
import simulation.StraightRoad;
import geometry.Point2D;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    private Application application;

    private ArrayList<Point2D> clicked;
    private int clicksCounter;
    private Consumer consumer;


    @FXML
    private Pane pane;
    @FXML
    private Label errorTextField;
    @FXML
    private Slider maxVelocitySlider,  maxInflowSlider,  scaleSlider;
    @FXML
    private Button addOneWayRoadButton, addTwoWayRoadButton, clearButton, startButton, stopButton, buildButton;

    public void initController(Application application){
        this.clicked = new ArrayList<>();
        this.application = application;
    }

    public Pane getPane(){
        return this.pane;
    }


    public void setScale(){
        application.setScale(scaleSlider.getValue());
    }

    public void setMaxVelocity(){
        this.application.simulation.setMaxVelocity(maxVelocitySlider.getValue());
    }

    public void setDefaultMaxInflow(){
        this.application.simulation.setDefaultMaxSecondPerCar(maxInflowSlider.getMax() - maxInflowSlider.getValue());
    }

    public void addStraightRoad(){
        this.clicked.clear();

        clicksCounter = 2;

        this.consumer = new Consumer() {
            @Override
            public void accept(ArrayList<Point2D> clicks) {
                StraightRoad addingRoad = new StraightRoad(clicks.get(0),clicks.get(1));
                try{
                    application.mapBuilder.addRoad(addingRoad);
                    RoadDrawer roadDrawer = new RoadDrawer(addingRoad,application.root);
                    roadDrawer.draw();
                }
                catch (Exception e){
                    errorTextField.setText(e.getMessage());
                }
                addStraightRoad();

                RoadDrawer roadDrawer1 = new RoadDrawer(addingRoad,application.root);
                application.root.getChildren().add(new Circle(addingRoad.getBeginPoint().getX(),addingRoad.getBeginPoint().getY(),application.mapBuilder.getIntersectionRadius(),Color.PURPLE));
                application.root.getChildren().add(new Circle(addingRoad.getEndPoint().getX(),addingRoad.getEndPoint().getY(),application.mapBuilder.getIntersectionRadius(),Color.PURPLE));

            }
        };

        pane.setDisable(false);
    }

    public void addBidirectionalRoad(){
        clicked.clear();
        clicksCounter = 2;

        this.consumer = new Consumer() {
            @Override
            public void accept(ArrayList<Point2D> clicks) {
                BidirectionalRoad addingRoad = new BidirectionalRoad(clicks.get(0),clicks.get(1));
                try {
                    application.mapBuilder.addRoad(addingRoad);
                }
                catch (Exception e){
                    errorTextField.setText(e.getMessage());
                }

                RoadDrawer roadDrawer1 = new RoadDrawer(addingRoad.getRoad1(),application.root);
                RoadDrawer roadDrawer2 = new RoadDrawer(addingRoad.getRoad2(),application.root);
                application.root.getChildren().add(new Circle(addingRoad.getBeginPoint().getX(),addingRoad.getBeginPoint().getY(),application.mapBuilder.getIntersectionRadius(),Color.PURPLE));
                application.root.getChildren().add(new Circle(addingRoad.getEndPoint().getX(),addingRoad.getEndPoint().getY(),application.mapBuilder.getIntersectionRadius(),Color.PURPLE));

                addBidirectionalRoad();
            }
        };

        pane.setDisable(false);
    }


    public void buildSimulation(){
        addOneWayRoadButton.setDisable(true);
        addTwoWayRoadButton.setDisable(true);
        clearButton.setDisable(true);
        buildButton.setDisable(true);
        startButton.setDisable(false);
        application.root.getChildren().clear();
        try {
            application.simulation = new Simulation(application.mapBuilder.export(), application.root);
        }
        catch (Exception e){
            errorTextField.setText(e.getMessage());
        }
    }

    public void startSimulation(){

        addOneWayRoadButton.setDisable(true);
        addTwoWayRoadButton.setDisable(true);
        clearButton.setDisable(true);
        startButton.setDisable(true);
        stopButton.setDisable(false);
        maxInflowSlider.setDisable(false);
        maxVelocitySlider.setDisable(false);
        maxInflowSlider.setDisable(false);
        maxVelocitySlider.setValue((maxVelocitySlider.getMax() - maxVelocitySlider.getMin()) / 2 +maxVelocitySlider.getMin());
        maxInflowSlider.setValue((maxInflowSlider.getMax()-maxInflowSlider.getMin())/2 + maxInflowSlider.getMin());
        try{
            application.simulation.startSimulation();
        }
        catch (Exception e){
            errorTextField.setText(e.getMessage());
        }
    }

    public void stopSimulation(){
        clearButton.setDisable(false);
        startButton.setDisable(false);
        stopButton.setDisable(true);

        maxInflowSlider.setDisable(true);
        maxVelocitySlider.setDisable(true);
        try{
            application.simulation.stopSimulation();
        }
        catch (Exception e){
            errorTextField.setText(e.getMessage());
        }
    }

    public void clear(){
        addOneWayRoadButton.setDisable(false);
        addTwoWayRoadButton.setDisable(false);
        startButton.setDisable(true);
        buildButton.setDisable(false);

        application.root.getChildren().clear();
        application.mapBuilder = new MapBuilder(application.intersectionRadius,application.root);
    }


    private interface Consumer{
        void accept(ArrayList<Point2D> o);
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        addOneWayRoadButton.setDisable(false);
        addTwoWayRoadButton.setDisable(false);
        clearButton.setDisable(false);
        startButton.setDisable(true);
        stopButton.setDisable(true);
        scaleSlider.setDisable(false);
        buildButton.setDisable(false);
        maxVelocitySlider.setDisable(true);
        maxInflowSlider.setDisable(true);

        pane.setDisable(true);

        pane.setOnMousePressed( new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                clicked.add( new Point2D(event.getX()/application.scale,event.getY()/application.scale));
                clicksCounter--;

                if(clicksCounter==0) {
                    try {
                        pane.setDisable(true);
                        consumer.accept(clicked);
                    }
                    catch (Exception e){
                        errorTextField.setText(e.getMessage());
                    }
                    finally {
                        clicked.clear();
                    }

                }
            }
        });

    }

}
