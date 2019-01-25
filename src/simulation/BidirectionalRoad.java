package simulation;

import app.Connectable;
import geometry.Point2D;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

public class BidirectionalRoad implements Connectable {
    private StraightRoad road1;
    private StraightRoad road2;

    private Point2D beginPoint;
    private Point2D endPoint;


    public BidirectionalRoad(Point2D beginPoint, Point2D endPoint){
        this.beginPoint = beginPoint;
        this.endPoint = endPoint;

        Point2D v = Point2D.turn(Point2D.vectorFromTO(beginPoint,endPoint),PI/2);
        v = new Point2D(0.5*Road.width * v.getX()/Point2D.norm(v), 0.5*Road.width * v.getY()/Point2D.norm(v));

        this.road1 = new StraightRoad(beginPoint.add(v),endPoint.add(v));
        v = Point2D.turn(v,PI);
        this.road2 = new StraightRoad(endPoint.add(v),beginPoint.add(v));

    }


    public void trim(Point2D site, double length){
        double lengthToTrim = sqrt(length*length -Road.width*Road.width);

        if(site.equals(this.beginPoint)){
            road1.trim(road1.getBeginPoint(),lengthToTrim);
            road2.trim(road2.getEndPoint(),lengthToTrim);
        }

        if(site.equals(this.endPoint)){
            road1.trim(road1.getEndPoint(),lengthToTrim);
            road2.trim(road2.getBeginPoint(),lengthToTrim);
        }
    }


    public Point2D getEndPoint() {
        return endPoint;
    }

    public Point2D getBeginPoint() {
        return beginPoint;
    }

    public void setEndPoint(Point2D endPoint) {
        this.endPoint = endPoint;

        Point2D v = Point2D.turn(Point2D.vectorFromTO(this.beginPoint,this.endPoint),PI/2);
        v = new Point2D(0.5*Road.width * v.getX()/Point2D.norm(v), 0.5*Road.width * v.getY()/Point2D.norm(v));

        this.road1 = new StraightRoad(this.beginPoint.add(v),this.endPoint.add(v));
        v = Point2D.turn(v,PI);
        this.road2 = new StraightRoad(this.endPoint.add(v),this.beginPoint.add(v));
    }

    public void setBeginPoint(Point2D beginPoint) {
        this.beginPoint = beginPoint;

        Point2D v = Point2D.turn(Point2D.vectorFromTO(this.beginPoint,this.endPoint),PI/2);
        v = new Point2D(0.5*Road.width * v.getX()/Point2D.norm(v), 0.5*Road.width * v.getY()/Point2D.norm(v));

        this.road1 = new StraightRoad(this.beginPoint.add(v),this.endPoint.add(v));
        v = Point2D.turn(v,PI);
        this.road2 = new StraightRoad(this.endPoint.add(v),this.beginPoint.add(v));
    }


    public StraightRoad getRoad1() {
        return road1;
    }

    public StraightRoad getRoad2() {
        return road2;
    }


    @Override
    public double getLength() {
        return road1.getLength();
    }

    @Override
    public double getWidth() {
        return Road.width;
    }
}
