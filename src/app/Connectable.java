package app;

import geometry.Point2D;

public interface Connectable {
    public double getLength();
    public Point2D getBeginPoint();
    public Point2D getEndPoint();
    public void setBeginPoint(Point2D o);
    public void setEndPoint(Point2D o);
    public double getWidth();
}
