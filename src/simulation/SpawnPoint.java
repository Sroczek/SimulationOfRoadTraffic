package simulation;


import geometry.Pair;

import java.util.ArrayList;

import static java.lang.Math.ceil;
import static java.lang.Math.random;

public class SpawnPoint {
    private Car standard;
    private StraightRoad straightRoad;

    private ArrayList<Pair<DestinationPoint,Double>> destinationPoints;
    private double totalOutFlow;

    private double maxTicsPerCar;
    private int counter;

    public void setMaxSecondPerCar(double maxSecondPerCar) {
        this.maxTicsPerCar = maxSecondPerCar*60;
    }

    public SpawnPoint(Car standard, StraightRoad straightRoad, ArrayList<DestinationPoint> destinationPoints, double maxSecondPerCar){
        this.standard = standard;
        this.straightRoad = straightRoad;
        this.totalOutFlow =0;
        this.destinationPoints = new ArrayList<>();

        for(DestinationPoint destinationPoint: destinationPoints) {
            this.totalOutFlow += destinationPoint.getOutFlow();
            this.destinationPoints.add(new Pair<DestinationPoint,Double>( destinationPoint , this.totalOutFlow));
        }

        this.setMaxSecondPerCar(maxSecondPerCar);
        this.counter = 0;
    }

    public void spawn() throws CloneNotSupportedException{
        if(counter < (double) maxTicsPerCar) counter ++;
        else {
            Car c = null;
            try {
                c = (Car) standard.clone();
            }
            catch (CloneNotSupportedException e){
                throw new CloneNotSupportedException("Cloning of Car in SpawnPoint method 'spawn' failed");
            }

            double rand = random()*this.totalOutFlow;

            for(int i = 0; i < this.destinationPoints.size(); i++) {
                if(this.destinationPoints.get(i).getRight() > rand){
                    c.setDestPoint(this.destinationPoints.get(i).getLeft().getPosition());
                    break;
                }
                if( i == this.destinationPoints.size()) throw new IllegalArgumentException("Calculating target failed");
            }

            if(this.canSpawn(c)){
                straightRoad.addAtBegin(c);
                counter = counter - (int)(ceil((double) maxTicsPerCar));
            }
            else counter++;
        }

    }

    private boolean canSpawn(Car c){
        Pair<Car,Double> next = straightRoad.getNearest(c, c.getSearchRadius());

        if(next.getLeft() == null) return true;
        if(next.getRight() - 1.5*standard.length < 0) return false;

        return true;
    }

}
