package demo.rxandroid.op;

/**
 * Created by Raviteja on 9/21/2016. RxAndroid
 */

import java.util.HashSet;
import java.util.Set;

public class Subject {

    private Set<Observer> observers = new HashSet<>();
    private int state;

    int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        notifyAllObservers();
    }

    public void attach(Observer observer) {
        if (!observers.contains(observer))
            observers.add(observer);
    }

    public void detach(Observer observer) {
        observers.remove(observer);
    }

    private void notifyAllObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
