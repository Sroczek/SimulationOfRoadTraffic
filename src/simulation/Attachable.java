package simulation;

import geometry.Pair;

public interface Attachable {
    public Pair<Car,Double> getNearest(Car c, double searchRadius);

    public void addAtBegin(Car c);

    public boolean allwaysAllowEnter();

    public double getLength();
}
