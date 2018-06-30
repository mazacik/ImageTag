package project.control.change;

import project.control.FilterControl;
import project.control.FocusControl;
import project.control.SelectionControl;

import java.util.ArrayList;

public enum ChangeEventEnum {
    /* options */
    FILTER(FilterControl.getChangeListeners()),
    FOCUS(FocusControl.getChangeListeners()),
    SELECTION(SelectionControl.getChangeListeners());

    /* vars */
    private ArrayList<Class> listeners;

    /* constructors */
    ChangeEventEnum(ArrayList<Class> listeners) {
        this.listeners = listeners;
    }

    /* public */
    public void addToSubscribers(Class subscriber) {
        if (!listeners.contains(subscriber)) {
            listeners.add(subscriber);
        }
    }

    /* get */
    public ArrayList<Class> getListeners() {
        return listeners;
    }
}
