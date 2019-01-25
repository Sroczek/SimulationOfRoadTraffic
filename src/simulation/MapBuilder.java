package simulation;

import javafx.scene.Group;
import app.Connectable;
import app.Map;
import geometry.Pair;
import geometry.Point2D;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class MapBuilder implements Cloneable{


    private ArrayList<Pair<Point2D,ArrayList<Connectable>>> junctions;
    private ArrayList<Pair<Point2D,Connectable>> freePoints;
    private double intersectionRadius;
    private Group root;

    private double defaultOutFlow = 1.0;
    private double defaultMaxTicsPerCar = 3.0;

    public MapBuilder(double intersectionRadius, Group root){
        this.root = root;
        this.intersectionRadius = intersectionRadius;
        this.junctions = new ArrayList<Pair<Point2D,ArrayList<Connectable>>>();
        this.freePoints = new  ArrayList<Pair<Point2D,Connectable>>();
    }


    public void addRoad(Connectable addingRoad){

        boolean beginConnected = false;
        boolean endConnected = false;

        if(addingRoad.getLength() < 2*intersectionRadius) throw new IllegalArgumentException("Cannot create road - it is to short");

        if(this.freePoints.isEmpty() && this.junctions.isEmpty()) {

            this.freePoints.add(new Pair<>(addingRoad.getBeginPoint(),addingRoad));
            this.freePoints.add(new Pair<>(addingRoad.getEndPoint(),addingRoad));

            this.deleteReapeted();
            return;
        }

        Point2D begin = addingRoad.getBeginPoint();
        Point2D end = addingRoad.getEndPoint();

        //check if we attach to existing junction
        for(Pair<Point2D,ArrayList<Connectable>> junction: junctions){
            if(junction.getLeft().distance(begin) < intersectionRadius){
                addingRoad.setBeginPoint(junction.getLeft());
                junction.getRight().add(addingRoad);
                beginConnected = true;

            }
            if(junction.getLeft().distance(end) < intersectionRadius){
                addingRoad.setEndPoint(junction.getLeft());
                junction.getRight().add(addingRoad);
                endConnected = true;

            }
        }

        if(beginConnected && endConnected)  {
            this.deleteReapeted();
            return;
        }



        for(Pair<Point2D,Connectable> freePoint: freePoints){
            if(!beginConnected) {
                if (freePoint.getLeft().distance(addingRoad.getBeginPoint()) < intersectionRadius){
                    addingRoad.setBeginPoint(freePoint.getLeft());

                    ArrayList<Connectable> tmp = new ArrayList<>();
                    tmp.add(addingRoad);
                    tmp.add(freePoint.getRight());

                    junctions.add(new Pair<Point2D,ArrayList<Connectable>>(addingRoad.getBeginPoint(),tmp));

                    beginConnected = true;
                }

            }
            if(!endConnected){
                if (freePoint.getLeft().distance(addingRoad.getEndPoint()) < intersectionRadius){
                    addingRoad.setEndPoint(freePoint.getLeft());

                    ArrayList<Connectable> tmp = new ArrayList<>();
                    tmp.add(addingRoad);
                    tmp.add(freePoint.getRight());


                    junctions.add(new Pair<Point2D,ArrayList<Connectable>>(addingRoad.getEndPoint(),tmp));

                    endConnected = true;
                }
            }
        }
        if(beginConnected && endConnected) {
            this.deleteReapeted();
            return;
        }


        if(!beginConnected){
            freePoints.add(new Pair<Point2D,Connectable>(addingRoad.getBeginPoint(),addingRoad));

        }
        if(!endConnected){
            freePoints.add(new Pair<Point2D,Connectable>(addingRoad.getEndPoint(),addingRoad));
        }

        this.deleteReapeted();
    }

    public double getIntersectionRadius() {
        return intersectionRadius;
    }

    public static Pair<ArrayList<Road>,ArrayList<Intersection>> getRoadsTrimmedAndIntersections(MapBuilder mapBuilder){
        ArrayList<Road> allRoads = new ArrayList<>();
        ArrayList<Intersection> allIntersections = new ArrayList<>();

        for(Pair<Point2D,ArrayList<Connectable>> junction : mapBuilder.junctions){


            ArrayList<StraightRoad> inRoads = new ArrayList<>();
            ArrayList<StraightRoad> outRoads = new ArrayList<>();

            //preparing roads: trimming and spliting
            for(Connectable c: junction.getRight()){

                if(c instanceof StraightRoad) {
                    if (c.getEndPoint().equals(junction.getLeft())) {
                        inRoads.add((StraightRoad) c);
                        ((StraightRoad) c).trim(c.getEndPoint(),mapBuilder.intersectionRadius);
                    }
                    if (c.getBeginPoint().equals(junction.getLeft())){
                        outRoads.add((StraightRoad) c);
                        ((StraightRoad) c).trim(c.getBeginPoint(),mapBuilder.intersectionRadius);
                    }
                }

                if(c instanceof BidirectionalRoad){
                    if(c.getBeginPoint().equals(junction.getLeft())){
                        outRoads.add(((BidirectionalRoad) c).getRoad1());
                        inRoads.add(((BidirectionalRoad) c).getRoad2());
                        ((BidirectionalRoad) c).trim(c.getBeginPoint(),mapBuilder.intersectionRadius);
                    }
                    if(c.getEndPoint().equals(junction.getLeft())){
                        inRoads.add(((BidirectionalRoad) c).getRoad1());
                        outRoads.add(((BidirectionalRoad) c).getRoad2());
                        ((BidirectionalRoad) c).trim(c.getEndPoint(),mapBuilder.intersectionRadius);
                    }
                }

            }

            ArrayList<Fork> forks = new ArrayList<>();
            for(StraightRoad iRoad: inRoads){
                Fork fork = new Fork(iRoad);

                if(!allRoads.contains(iRoad)) allRoads.add(iRoad);

                for(StraightRoad oRoad: outRoads){
                    ConnectionRoad connectionRoad = new ConnectionRoad(iRoad,oRoad);

                    if(!(abs(Point2D.scalarProduct(iRoad.getEndOuterVector(), Point2D.vectorFromTO(iRoad.getEndPoint(),oRoad.getBeginPoint()))) < 1e-2))
                        fork.addToFork(connectionRoad);

                    if(!allRoads.contains(oRoad)) allRoads.add(oRoad);
                }
                forks.add(fork);
            }

            for(Road road: allRoads) if(road.getEndAttach() instanceof Fork)
                if(((Fork)(road.getEndAttach())).insidePaths.isEmpty())
                    forks.remove(road.getEndAttach());

            Intersection intersection = new Intersection(forks);
            allIntersections.add(intersection);

            inRoads.clear();
            outRoads.clear();
        }

        for(Road road: allRoads) if(road.getEndAttach() instanceof Fork)
            if(((Fork)(road.getEndAttach())).insidePaths.isEmpty()){
                road.setEndAttach(new DestinationPoint(road.getEndPoint(),1));
            }

        return new Pair<> (allRoads,allIntersections);
    }

    public static ArrayList<DestinationPoint> getDestinasionsPoints(  ArrayList<Pair<Point2D,Connectable>> freePoints, double defaultOutFlow){
        ArrayList<DestinationPoint> destinationPoints = new ArrayList<>();

        for(Pair<Point2D,Connectable> point2DConnectablePair: freePoints){
            if(point2DConnectablePair.getRight().getEndPoint().equals(point2DConnectablePair.getLeft())){
                DestinationPoint destinationPoint = new DestinationPoint(point2DConnectablePair.getLeft(),defaultOutFlow);
                if(point2DConnectablePair.getRight() instanceof StraightRoad){
                    ((StraightRoad)(point2DConnectablePair.getRight())).setEndAttach(destinationPoint);
                    destinationPoints.add(destinationPoint);
                }

                if(point2DConnectablePair.getRight() instanceof BidirectionalRoad) {
                    ((BidirectionalRoad)point2DConnectablePair.getRight()).getRoad1().setEndAttach(destinationPoint);
                    destinationPoints.add(destinationPoint);
                }
            }

            if(point2DConnectablePair.getRight().getBeginPoint().equals(point2DConnectablePair.getLeft())){
                DestinationPoint destinationPoint = new DestinationPoint(point2DConnectablePair.getLeft(),defaultOutFlow);
                if(point2DConnectablePair.getRight() instanceof BidirectionalRoad) {
                    ((BidirectionalRoad)point2DConnectablePair.getRight()).getRoad2().setEndAttach(destinationPoint);
                    destinationPoints.add(destinationPoint);
                }
            }
        }

        return destinationPoints;
    }

    public static ArrayList<SpawnPoint> getSpawnPoints(Car standard, double defaultMaxSecondsPerCar, ArrayList<Pair<Point2D,Connectable>> freePoints, ArrayList<DestinationPoint> destinationPoints){
        ArrayList<SpawnPoint> spawnPoints = new ArrayList<>();

        for(Pair<Point2D,Connectable> point2DConnectablePair: freePoints){
            if(point2DConnectablePair.getRight().getBeginPoint().equals(point2DConnectablePair.getLeft())){
                SpawnPoint spawnPoint = null;
                if(point2DConnectablePair.getRight() instanceof StraightRoad) {
                    spawnPoint = new SpawnPoint(standard, (StraightRoad) point2DConnectablePair.getRight(), destinationPoints, defaultMaxSecondsPerCar);
                    spawnPoints.add(spawnPoint);
                }
                if(point2DConnectablePair.getRight() instanceof BidirectionalRoad) {
                    spawnPoint = new SpawnPoint(standard, ((BidirectionalRoad) point2DConnectablePair.getRight()) . getRoad1(), destinationPoints, defaultMaxSecondsPerCar);
                    spawnPoints.add(spawnPoint);
                }

            }

            if(point2DConnectablePair.getRight().getEndPoint().equals(point2DConnectablePair.getLeft())){
                SpawnPoint spawnPoint = null;
                if(point2DConnectablePair.getRight() instanceof BidirectionalRoad){
                    spawnPoint = new SpawnPoint(standard, ((BidirectionalRoad) point2DConnectablePair.getRight()) . getRoad2(), destinationPoints, defaultMaxSecondsPerCar);
                    spawnPoints.add(spawnPoint);
                }
            }

        }

        return spawnPoints;
    }

    public Map export(){

        Pair<ArrayList<Road>,ArrayList<Intersection>> roadsAndIntesections = this.getRoadsTrimmedAndIntersections(this);

        ArrayList<DestinationPoint> destinasionsPoints = this.getDestinasionsPoints(this.freePoints,this.defaultOutFlow);

        Car wzorzec = new Car(10,0.1,-0.01,"car.png");
        ArrayList<SpawnPoint> spawnPoints = this.getSpawnPoints(wzorzec, this.defaultMaxTicsPerCar ,this.freePoints, destinasionsPoints);

        Map map = new Map(roadsAndIntesections.getLeft(),roadsAndIntesections.getRight(),spawnPoints,destinasionsPoints);

        return map;
    }

    private void deleteReapeted(){
        for(Pair<Point2D,ArrayList<Connectable>> junction: junctions)
        {
            for(Pair<Point2D,Connectable> freePoint : freePoints){
                if(junction.getLeft().equals(freePoint.getLeft())) freePoints.remove(freePoint);
            }
        }
    }
}
