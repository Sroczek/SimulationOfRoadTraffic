package simulation;

import java.util.ArrayList;
import java.util.Collections;


public class Intersection {

    private ArrayList<Fork> forks;

    //CONSTURUCTORS:

    public Intersection(){
        this.forks = new ArrayList<>();
    }

    public Intersection(ArrayList<Fork> forks){
        this.forks = forks;
    }


    //GETTTERS:

    public ArrayList<Fork> getForks() {
        return forks;
    }


    //OTHER METODS

    public void add(Fork fork){
         this.forks.add(fork);
    }

    public void update(){
        Collections.sort(this.forks);

        for(int i = 0; i< forks.size(); i++){
                openIfPossible(forks.get(i));
        }
    }

    private void openIfPossible(Fork fork){
        if(!fork.inRoad.listOfCars.isEmpty()) {
            ConnectionRoad chosen = fork.findBestRoad(fork.inRoad.getFirst());

            for (Fork f : this.forks) {
                if(fork != f) {
                    for (ConnectionRoad connectionRoad : f.insidePaths) {
                        if (chosen.crosses(connectionRoad) && (connectionRoad.isAllowEnter() || !connectionRoad.listOfCars.isEmpty()))
                            return;
                    }
                }
            }
            chosen.setAllowEnter(true);
        }
    }

}
