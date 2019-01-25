package geometry;

import static java.lang.Math.*;

public class Point2D  implements Cloneable{
    private double x;
    private double y;

    public Point2D(Point2D p){
        this(p.getX(),p.getY());
    }

    public Point2D(){
        this(0,0);
    }

    public Point2D(double x, double y){
        setX(x);
        setY(y);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString(){
        return "( x: " + this.x +", y: " + this.y + ")";
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(other == null) return false;
        if(other instanceof Point2D) {
            Point2D otherPoint2D = (Point2D) other;
            return (this.x == otherPoint2D.x && this.y == otherPoint2D.y);
        }
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public double distance(Point2D A){
        return sqrt((this.getX() - A.getX())*(this.getX() - A.getX()) + (this.getY() - A.getY())*(this.getY() - A.getY()));
    }

    public Point2D add(Point2D v){
        return new Point2D(this.getX() + v.getX(),this.getY() + v.getY());
    }

    public Point2D getReverse(){
        return new Point2D(-this.getX(),-this.getY());
    }
    // punkt może też być traktowany jako wektor
    //trochę metod statycznych

    public static double scalarProduct(Point2D u, Point2D v){
        return u.getX()*v.getX()+u.getY()*v.getY();
    }

    public static double norm(Point2D u){
        return sqrt(u.getX()*u.getX()+u.getY()*u.getY());
    }

    public static double angleBetween(Point2D u, Point2D v){
        return acos(cosOfAngleBetween(u,v));
    }

    public static double cosOfAngleBetween(Point2D u, Point2D v){
        return scalarProduct(u,v)/(norm(u)*norm(v));
    }

    public static Point2D vectorFromTO(Point2D u, Point2D v){
        return new Point2D(v.getX()-u.getX(),v.getY()-u.getY());
    }

    public static double directedAngle(Point2D A){
        double angle = Point2D.angleBetween(A , new Point2D(1,0));
        if(A.getY() >= 0) return angle;
        else return 2*PI - angle;
    }

    public static double distaceBetween(Point2D u, Point2D v){
        return sqrt((u.getX()-v.getX())*(u.getX()-v.getX()) + (u.getY()-v.getY())*(u.getY()-v.getY()));
    }

    public static Point2D turn(Point2D u, double directedAngle){
        return new Point2D(u.getX()*cos(directedAngle) - u.getY()*sin(directedAngle),u.getX()*sin(directedAngle) + u.getY()*cos(directedAngle));
    }

    public static double vectorProduct(Point2D u, Point2D v){
        return u.getX()*v.getY()-u.getY()*v.getX();
    }
}
