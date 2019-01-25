package simulation;

import javafx.scene.image.Image;
import geometry.Point2D;

import static java.lang.Math.min;
import static java.lang.Math.sqrt;

public class Car implements Cloneable{
    private double position;
    private double velocity;
    private double maxAcceleration;
    private double minAcceleration;

    private double futureVelocity;
    private Point2D destPoint;

    double length = 66.0;

    private String imagePath;

    public Car(double v, double maxAcc, double minAcc, String imagePath){
        this(null,v,maxAcc,minAcc,imagePath);
    }

    public Car(Point2D destPoint, double v, double maxAcc, double minAcc, String imagePath){
        this.destPoint = destPoint;
        this.setVelocity(v);
        this.setBoundsOfAcceleration(minAcc,maxAcc);

        if (imagePath != null) {
            this.imagePath = new String(imagePath);
            this.length = (new Image(this.imagePath)).getHeight();
        }
    }

    void calculate(Car next, double desiredVelocity, double distance){

        double safeVelocity;

        if(next == null) safeVelocity = Integer.MAX_VALUE;
        else {
            distance = distance - 0.5*(this.length + next.length);
            safeVelocity = this.minAcceleration+sqrt(this.minAcceleration*this.minAcceleration+next.velocity*next.velocity - 2*this.minAcceleration*(distance - 0.25*this.length));
        }

        double calculatedVelocity = min(min(this.velocity+this.maxAcceleration,desiredVelocity),safeVelocity);
        this.setFutureVelocity(calculatedVelocity);
    }

    void applyCalculations(){
        this.velocity = this.futureVelocity;
    }

    void move(){
        this.position = this.position + this.velocity;
    }

    double getSearchRadius(){
        double t = - (this.velocity + this.maxAcceleration)/this.minAcceleration;
        double basicSearchRadius = (this.velocity + 0.5*this.maxAcceleration) + (this.velocity + this.maxAcceleration)*t + 0.5*this.minAcceleration*t*t;

        return basicSearchRadius + 2*this.length;
    }


    //      SETTERS
    private void setBoundsOfAcceleration(double minAcceleration, double maxAcceleration){

        if(maxAcceleration >= 0) this.maxAcceleration = maxAcceleration;
        else throw new IllegalArgumentException("Maximum value of acceleration should be positive");

        if(minAcceleration <= 0) this.minAcceleration = minAcceleration;
        else throw new IllegalArgumentException(("Minimum value of acceleration should be negative"));
    }

    private void setVelocity(double velocity) {
        if(velocity < 0) {
            this.velocity =0;
            return;
        }
        this.velocity=velocity;
    }

    void setFutureVelocity(double velocity) {
        if(velocity < 0) {
            this.futureVelocity =0;
            return;
        }
        this.futureVelocity=velocity;
    }

    public void setPosition(double position){
        this.position = position;
    }

    public void setDestPoint(Point2D destPoint) {
        this.destPoint = destPoint;
    }


    //       GETTERS
    public double getPosition() {
        return position;
    }

    public String getImagePath(){
        return this.imagePath;
    }

    public double getFutureVelocity() {
        return futureVelocity;
    }

    public Point2D getDestPoint() {
        return destPoint;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        Car clone = (Car) super.clone();
        clone.imagePath = this.imagePath;
        return clone;
    }

//    @Override
//    public String toString() {
//        StringBuilder s = new StringBuilder();
//        s.append("Position: ");
//        s.append(this.position);
//        s.append('\n');
//        s.append("Velocity: ");
//        s.append(this.velocity);
//        s.append('\n');
//        s.append("Accleration: ");
//        s.append(signum(this.futureVelocity - this.velocity));
//        s.append('\n');
//        return s.toString();
//    }
}
