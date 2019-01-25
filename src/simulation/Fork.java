package simulation;

import geometry.Pair;
import geometry.Point2D;

import java.util.ArrayList;

import static java.lang.Math.PI;


public class Fork implements Attachable, Comparable<Fork> {
    ArrayList<ConnectionRoad> insidePaths;
    Road inRoad;

    public ArrayList<ConnectionRoad> getInsidePaths() {
        return insidePaths;
    }

    public Fork(Road inRoad){
        this.inRoad = inRoad;
        this.inRoad.setEndAttach(this);
        this.insidePaths = new ArrayList<>();
    }

    public void addToFork(ConnectionRoad connectionRoad){
            this.insidePaths.add(connectionRoad);
    }

    public ConnectionRoad findBestRoad(Car c){
        Point2D destinationPointVector = Point2D.vectorFromTO(this.inRoad.getEndPoint(),c.getDestPoint());

        ConnectionRoad choosenRoad = insidePaths.get(0);
        double angle = 2*PI;
        for(ConnectionRoad connectionRoad: insidePaths){
            double newAngle = Point2D.angleBetween(connectionRoad.getOutRoadDirection(),destinationPointVector);
            if( newAngle < angle){
                choosenRoad = connectionRoad;
                angle = newAngle;
            }
        }
        return choosenRoad;
    }

    public void allowEnter(){
        findBestRoad(this.inRoad.getFirst()).setAllowEnter(true);
    }

    public void setAllOpened(){
        for(ConnectionRoad connectionRoad:this.insidePaths) connectionRoad.setAllowEnter(true);
    }

    public void setAllClosed(){
        for(ConnectionRoad connectionRoad:this.insidePaths) connectionRoad.setAllowEnter(false);
    }


    @Override
    public int compareTo(Fork o) {
        if(this.inRoad.listOfCars.isEmpty() && o.inRoad.listOfCars.isEmpty()) return 0;
        if(this.inRoad.listOfCars.isEmpty()) return 1;
        if(o.inRoad.listOfCars.isEmpty()) return -1;

        double distThis = this.inRoad.length - this.inRoad.getFirst().getPosition();
        double distO = o.inRoad.length-o.inRoad.getFirst().getPosition();
        return (int) (distThis - distO);
    }

    //METHODS OF INTERFACE ATTACHABLE
    @Override
    public Pair<Car, Double> getNearest(Car c, double searchRadius) {
        if(this.insidePaths.isEmpty()) throw new IllegalArgumentException("There aren't any inside path in fork");
        Road choosenRoad = this.findBestRoad(c);
        return choosenRoad.getNearest(c,searchRadius);
    }

    @Override
    public void addAtBegin(Car c) {
        ConnectionRoad choosenRoad = this.findBestRoad(c);
        choosenRoad.addAtBegin(c);
    }

    @Override
    public double getLength() {
        return 0;
    }

    @Override
    public boolean allwaysAllowEnter() {
        return false;
    }

}
