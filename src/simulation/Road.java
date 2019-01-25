package simulation;

import app.Observer;
import app.Connectable;
import geometry.Pair;
import geometry.Point2D;

import java.util.ArrayList;
import java.util.LinkedList;

import static java.lang.Math.min;

public abstract class Road implements Observable, Attachable, Connectable {
    protected LinkedList<Car> listOfCars;
    protected Point2D beginPoint;
    protected Point2D endPoint;
    protected double maxVelocity = 10.0;

    protected double length;
    public static final double width = 40;

    protected Attachable endAttach;
    protected ArrayList<Observer> observers;


    public Road(){
        this.listOfCars = null;
        this.beginPoint=null;
        this.endPoint = null;
        this.endAttach = null;
        this.observers =null;
    }

    public Road(Point2D beginPoint, Point2D endPoint) {
        this.beginPoint = beginPoint;
        this.endPoint = endPoint;
        this.listOfCars = new LinkedList<Car>();
        this.observers = new ArrayList<>();
    }

    public abstract Point2D getBeginOuterVector();
    public abstract Point2D getEndOuterVector();
    protected abstract double calculateLength();

    //SETTERS:

    public void setMaxVelocity(double maxVelocity){
        this.maxVelocity = maxVelocity;
    }

    public void setBeginPoint(Point2D beginPoint) {
        this.beginPoint = beginPoint;
        this.length=calculateLength();
    }

    public void setEndPoint(Point2D endPoint) {
        this.endPoint = endPoint;
        this.length=calculateLength();
    }

    public void setEndAttach(Attachable endAttach) {
        this.endAttach = endAttach;
    }

    //GETTERS:
    public Car getLast(){
        if(listOfCars.size() == 0) return null;
        return listOfCars.getLast();
    }

    public Car getFirst(){
        if(listOfCars.size() == 0) return null;
        return listOfCars.getFirst();
    }

    public Point2D getBeginPoint(){
        return this.beginPoint;
    }

    public Point2D getEndPoint() {
        return this.endPoint;
    }

    public double getWidth() {
        return width;
    }

    public double getAngle(){                   //returns directed angle from vector [0,-1]
        double x = this.endPoint.getX() - this.beginPoint.getX();
        double y = this.endPoint.getY() - this.beginPoint.getY();

        if(x >= 0) return Math.toDegrees(Point2D.angleBetween(new Point2D(0,-1),new Point2D(x,y)));
        else return 360 - Math.toDegrees(Point2D.angleBetween(new Point2D(0,-1),new Point2D(x,y)));
    }

    public Attachable getEndAttach(){
        return this.endAttach;
    }

    //METHODS OF INTERFACE ATTACHABLE
    @Override
    public Pair<Car,Double> getNearest(Car c, double searchRadius) {
        if(this.getLast() != null){
            if(this.getLast().getPosition() > searchRadius) return new Pair<>(null,0.0);
            else {
                return new Pair<>(this.getLast(),this.getLast().getPosition());
            }
        }
        else{
            if(this.endAttach == null) return new Pair<>(null,0.0);
            else {
                Pair<Car,Double> res = endAttach.getNearest(c, searchRadius - this.getLength());
                res.setRight(res.getRight() + this.getLength());
                return res;
            }
        }
    }

    @Override
    public void addAtBegin(Car c){
        this.listOfCars.addLast(c);
        c.setPosition(0);
        this.updateObservers(Operation.ADD);
    }

    @Override
    public boolean allwaysAllowEnter() {
        return true;
    }

    @Override
    public double getLength(){
        return this.length;
    }



    public void calculate(){

        if(!this.listOfCars.isEmpty()){

            for (int i = 1; i < this.listOfCars.size(); i++) {
                double dist = listOfCars.get(i - 1).getPosition() - listOfCars.get(i).getPosition();
                listOfCars.get(i).calculate(listOfCars.get(i - 1),this.maxVelocity, dist);
            }

            //first car on road
            Car c =this.listOfCars.getFirst();
            if (c.getSearchRadius() + c.getPosition() > this.getLength()) {
                Pair<Car, Double> next = endAttach.getNearest(c, c.getSearchRadius() - (this.getLength() - c.getPosition()));
                c.calculate(next.getLeft(), this.maxVelocity,next.getRight() + this.getLength() - c.getPosition());
            }
            else{
                c.calculate(null,this.maxVelocity,0.0);
            }

            //second car on road if exists
            if(!endAttach.allwaysAllowEnter() && this.listOfCars.size() >=2){
                Car c1 = this.listOfCars.get(1);
                double calculated1 = c1.getFutureVelocity();
                c1.calculate(new Car(0,0,0,null),this.maxVelocity, this.getLength() - c1.getPosition());
                c1.setFutureVelocity(min(calculated1,c1.getFutureVelocity()));
            }
        }
    }

    public void applyCalculations(){
        for(Car c: this.listOfCars){
            c.applyCalculations();
        }
    }

    public void moveCars(){
        if(!listOfCars.isEmpty()) {
            for (Car c : listOfCars) {
                c.move();
            }
            if (this.listOfCars.getFirst().getPosition() > this.getLength()) {
                endAttach.addAtBegin(this.listOfCars.getFirst());
                this.listOfCars.removeFirst();
                this.updateObservers(Operation.REMOVE);
            }
        }
    }

    public boolean crosses(Road other){

        Point2D vector1 = Point2D.vectorFromTO(this.getBeginPoint(),this.getEndPoint());
        Point2D vector2 = Point2D.vectorFromTO(this.getBeginPoint(),other.getBeginPoint());
        Point2D vector3 = Point2D.vectorFromTO(this.getBeginPoint(),other.getEndPoint());

        if(Point2D.vectorProduct(vector1,vector2) * Point2D.vectorProduct(vector1,vector3) > 0) return false;

        vector1 = Point2D.vectorFromTO(other.getBeginPoint(),other.getEndPoint());
        vector2 = Point2D.vectorFromTO(other.getBeginPoint(),this.getBeginPoint());
        vector3 = Point2D.vectorFromTO(other.getBeginPoint(),this.getEndPoint());

        if(Point2D.vectorProduct(vector1,vector2) * Point2D.vectorProduct(vector1,vector3) > 0) return false;

        return true;
    }


    //METHODS OF INTERFACE OBSERVABLE
    @Override
    public void addObserver(Observer observer){
        this.observers.add(observer);
    }

    @Override
    public void updateObservers(Object obj){
        for(Observer o: observers) o.update(obj);
    }


    //METHODS OF CLONEABLE INTERFACE
    @Override
    public Object clone() throws CloneNotSupportedException {
        StraightRoad clone = (StraightRoad) super.clone();
        clone.listOfCars = (LinkedList<Car>) this.listOfCars.clone();
        for(int i = 0; i < this.listOfCars.size(); i++){
            clone.listOfCars.addLast((Car) this.listOfCars.get(i).clone());
        }
        return clone;
    }

//    @Override
//    public String toString() {
//        StringBuilder stringBuilder = new StringBuilder();
//        for(int i = 0; i<listOfCars.size();i++){
//            stringBuilder.append("Autko: " + i + "\n" + listOfCars.get(i).toString());
//        }
//        return stringBuilder.toString();
//    }
}
