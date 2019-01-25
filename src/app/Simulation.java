package app;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import simulation.*;

import java.util.ArrayList;

public class Simulation {

    private Map map;
    private ArrayList<RoadDrawer> roadDrawers;
    private Group root;

    AnimationTimer animationTimer =  new AnimationTimer() {
        long startNanoTime = System.nanoTime();
        int counter = 0;
        int counterOfCounter = 0;

        public void handle(long currentNanoTime) {
            double t = (currentNanoTime - startNanoTime) / 1000000000.0;

            if (t >= 1 / 60) {

                //SPAWNNING
                for(SpawnPoint spawnPoint: map.spawnPoints) {
                    try {
                        spawnPoint.spawn();
                    }
                    catch(CloneNotSupportedException e){
                        e.printStackTrace();
                    }
                }

                //CALCULATING
                for (Road road : map.roads) {
                    road.calculate();
                    road.applyCalculations();
                }

                //MOVING
                for (Road road : map.roads) {
                    road.moveCars();
                }

                //UPDATING INTERSECTIONS
                if(counter > 20) {
                    for(Intersection intersection: map.intersections) intersection.update();
                    counter = 0;
                    counterOfCounter ++;
                }

                //DRAWING
                for (RoadDrawer roadDrawer : roadDrawers) {
                    roadDrawer.draw();
                }

                counter++;
                startNanoTime = currentNanoTime;
            }

        }
    };

    public void setDefaultMaxSecondPerCar(double maxSecondPerCar){
        for(SpawnPoint spawnPoint:map.spawnPoints) spawnPoint.setMaxSecondPerCar(maxSecondPerCar);
    }

    public void setMaxVelocity(double maxVelocity){
        for(Road road: map.roads) road.setMaxVelocity(maxVelocity);
    }

    public Simulation(Map map, Group root){
        this.map = map;
        this.root = root;
        this.roadDrawers = new ArrayList<>();

        for(Road road: map.roads){
            RoadDrawer roadDrawer= new RoadDrawer(road, root);
            road.addObserver(roadDrawer);
            roadDrawers.add(roadDrawer);
        }

        for(Intersection intersection: map.intersections){
            for(Fork fork: intersection.getForks()){
                for(ConnectionRoad connectionRoad: fork.getInsidePaths()){
                    RoadDrawer roadDrawer= new RoadDrawer(connectionRoad, root);
                    connectionRoad.addObserver(roadDrawer);
                    roadDrawers.add(roadDrawer);
                    map.roads.add(connectionRoad);
                }

            }

        }
    }

    public void startSimulation() {
        animationTimer.start();
    }

    public void stopSimulation(){
        animationTimer.stop();
    }
}
