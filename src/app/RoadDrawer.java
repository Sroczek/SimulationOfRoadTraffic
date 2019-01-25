package app;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import simulation.Operation;
import simulation.Road;

import java.util.LinkedList;


public class RoadDrawer implements Observer {

    private LinkedList<CarDrawer> carDrawers;
    private Road road;
    private Line roadSurface;
    private Group root;


    public RoadDrawer(Road road, Group root){
        this.carDrawers = new LinkedList<>();
        this.road = road;
        this.root = root;

        this.roadSurface = new Line(road.getBeginPoint().getX(), road.getBeginPoint().getY(), road.getEndPoint().getX(), road.getEndPoint().getY());
        this.roadSurface.setStrokeLineCap(StrokeLineCap.ROUND);
        this.roadSurface.setStrokeWidth(road.getWidth());
        this.roadSurface.setStroke(Color.BLACK);

        this.root.getChildren().add(roadSurface);

    }

    public void draw(){
//        if(this.road instanceof ConnectionRoad){
//            if( ((ConnectionRoad) road).isAllowEnter() && road.getFirst() == null) this.roadSurface.setStroke(Color.GREEN);
//            if( ((ConnectionRoad) road).isAllowEnter() && road.getFirst() != null) this.roadSurface.setStroke(Color.DARKGREEN);
//
//            if( !((ConnectionRoad) road).isAllowEnter() && road.getFirst() == null) this.roadSurface.setStroke(Color.RED);
//            if( !((ConnectionRoad) road).isAllowEnter() && road.getFirst() != null) this.roadSurface.setStroke(Color.DARKRED);
//            this.roadSurface.setOpacity(0.6);
//        }
        for(CarDrawer carDrawer: carDrawers) carDrawer.draw(road);
    }

    private void removeCarDrawer(CarDrawer carDrawer){
        this.carDrawers.remove(carDrawer);
        carDrawer.delete();
    }

    private void add(){
        carDrawers.add(new CarDrawer(road.getLast(),this.root));
    }

    @Override
    public void update(Object operation){
        if(operation instanceof Operation){
            Operation tmp = (Operation) operation;
            if(tmp.equals(Operation.ADD)){
                this.add();
            }
            if(tmp.equals(Operation.REMOVE)){
                this.removeCarDrawer(this.carDrawers.getFirst());
            }
        }
    }
}
