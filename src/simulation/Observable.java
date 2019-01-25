package simulation;

import app.Observer;

public interface Observable {
    void addObserver(Observer observer);
    void updateObservers(Object o);
}
