package geometry;

import static java.lang.Math.*;

// ax + by + c = 0
public class Line2D {
    private double a;
    private double b;
    private double c;

    public Line2D(Point2D A, Point2D B){
        this.a = -B.getY()+A.getY();
        this.b = B.getX()-A.getX();
        this.c = -this.a*A.getX()  - this.b*A.getY();
    }

    public static Line2D normalTroughPoint(Line2D l, Point2D P){
        return new Line2D(new Point2D(P.getX() + l.a,P.getY() + l.b),P);
    }

    public static Point2D solveSystem(Line2D l, Line2D k){
        double W = l.a*k.b-k.a*l.b;
        double Wx =-l.c*k.b+k.c*l.b;
        double Wy =-l.a*k.c+k.a*l.c;

        if(W == 0 && Wx == 0 && Wy == 0) return null;
        if(W == 0)  return null;

        return new Point2D(Wx/W,Wy/W);
    }

    public static double distanceFromPointToLine(Point2D A, Line2D l){
        return abs(l.a*A.getX()+l.b*A.getY()+l.c)/sqrt(l.a*l.a+l.b*l.b);
    }

    public static double distanceFromPointToLineSegment(Point2D A, Point2D lineSegmentBegin, Point2D lineSegmentEnd){

        Line2D tmp = new Line2D(lineSegmentBegin,lineSegmentEnd);
        Point2D junction = Line2D.solveSystem(Line2D.normalTroughPoint(tmp,A),tmp);

        if(junction != null) return Line2D.distanceFromPointToLine(A,tmp);

        return min(Point2D.distaceBetween(A,lineSegmentBegin),Point2D.distaceBetween(A,lineSegmentEnd));
    }

    @Override
    public String toString() {
        return "A: " + a + " B: " + b + " C: " + c;
    }
}
