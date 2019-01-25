package app;

import simulation.*;

import java.util.ArrayList;

public class Map {

    ArrayList<Road> roads;
    ArrayList<Intersection> intersections;
    ArrayList<SpawnPoint> spawnPoints;
    ArrayList<DestinationPoint> destinationPoints;

    public Map(ArrayList<Road> roads, ArrayList<Intersection> intersections, ArrayList<SpawnPoint> spawnPoints, ArrayList<DestinationPoint> destinationPoints){
        this.roads =roads;
        this.intersections= intersections;
        this.spawnPoints=spawnPoints;
        this.destinationPoints= destinationPoints;
    }

}
