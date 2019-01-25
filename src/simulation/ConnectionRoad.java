package simulation;

import geometry.Pair;
import geometry.Point2D;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class ConnectionRoad extends Road {

    private boolean allowEnter;
    private Road inRoad;
    private Road outRoad;


    public ConnectionRoad(Road inRoad, Road outRoad){
        super(inRoad.getEndPoint(), outRoad.getBeginPoint());
        this.allowEnter = false;
        this.inRoad = inRoad;
        this.outRoad = outRoad;

        this.setEndAttach(outRoad);

        this.length = this.calculateLength();
    }


    //GETTERS:
    public boolean isAllowEnter() {
        return allowEnter;
    }

    public Point2D getOutRoadDirection(){
        return Point2D.vectorFromTO(outRoad.getBeginPoint(), outRoad.getEndPoint());
    }


    //SETTERS:
    public void setAllowEnter(boolean allowEnter) {
        this.allowEnter = allowEnter;
    }


    //METHODES OF INTERFACE ATTACHABLE
    @Override
    public Pair<Car, Double> getNearest(Car c, double searchRadius) {
        if(this.allowEnter && this.listOfCars.isEmpty()) return super.getNearest(c, searchRadius);
        else return new Pair<>(new Car(0,0,0,null), 0.0);
    }

    @Override
    public void addAtBegin(Car c) {
        super.addAtBegin(c);
        this.setAllowEnter(false);
    }

    @Override
    public boolean allwaysAllowEnter(){
        return false;
    }

    @Override
    public double getLength() {
        return length;
    }

    //METHODS FROM ROAD
    public Point2D getBeginOuterVector(){
        return inRoad.getEndOuterVector().getReverse();
    }

    public  Point2D getEndOuterVector(){
        return outRoad.getBeginOuterVector().getReverse();
    }

    @Override
    protected double calculateLength() {
        return sqrt(pow(this.endPoint.getX()-this.beginPoint.getX(),2) + pow(this.endPoint.getY()-this.beginPoint.getY(),2));
    }


}
