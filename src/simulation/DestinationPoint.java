package simulation;

import geometry.Pair;
import geometry.Point2D;

public class DestinationPoint implements Attachable {

    private int counter;
    private double outFlow;
    private Point2D position;

    public DestinationPoint(Point2D position, double outFlow){
        this.counter =0;
        this.position = position;
        this.outFlow= outFlow;
    }


    //GETTERS:
    public int getCounter() {
        return counter;
    }

    public double getOutFlow() {
        return outFlow;
    }

    public Point2D getPosition() {
        return position;
    }

    //SETTERS:
    public void setOutFlow(double outFlow) {

        if(outFlow > 0) this.outFlow = outFlow;
        else throw new IllegalArgumentException("Outflow cannot be lower than zero");

    }

    //METHODS OF INTERFACE ATTACHABLE
    @Override
    public Pair<Car, Double> getNearest(Car c, double searchRadius) {
        return new Pair<Car,Double>(null,0.0);
    }

    @Override
    public void addAtBegin(Car c) {
        counter ++;
    }

    @Override
    public boolean allwaysAllowEnter() {
        return true;
    }

    @Override
    public double getLength() {
        return 0;
    }

}
