package simulation;

import geometry.Point2D;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class StraightRoad extends Road implements Cloneable {

    public StraightRoad(){
        super();
    }
    public StraightRoad(Point2D beginPoint, Point2D endPoint){
        super(beginPoint,endPoint);
        this.length = this.calculateLength();
    }


    public void trim(Point2D site, double length){
        if(site.equals(this.beginPoint)){
            Point2D v = new Point2D ((length/this.length) * this.getEndOuterVector().getX(),(length/this.length) *this.getEndOuterVector().getY());
            this.beginPoint = this.beginPoint.add(v);
            this.length = this.calculateLength();
        }
        if(site.equals(this.endPoint)){
            Point2D v = new Point2D ((length/this.length)* this.getBeginOuterVector().getX(),(length/this.length) *this.getBeginOuterVector().getY());
            this.endPoint = this.endPoint.add(v);
            this.length = this.calculateLength();
        }
    }


    //METHODS FROM ROAD
    @Override
    protected double calculateLength() {
        return sqrt(pow(this.endPoint.getX()-this.beginPoint.getX(),2) + pow(this.endPoint.getY()-this.beginPoint.getY(),2));
    }

    @Override
    public Point2D getBeginOuterVector() {
        return Point2D.vectorFromTO(this.endPoint,this.beginPoint);
    }

    @Override
    public Point2D getEndOuterVector() {
        return Point2D.vectorFromTO(this.beginPoint,this.endPoint);
    }
}