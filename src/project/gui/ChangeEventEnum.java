package project.gui;

import project.control.FocusControl;
import project.control.SelectionControl;
import project.database.TagElementDatabase;

import java.util.ArrayList;

public enum ChangeEventEnum {
    /* options */
    FILTER(TagElementDatabase.getChangeListeners()),
    FOCUS(FocusControl.getChangeListeners()),
    SELECTION(SelectionControl.getChangeListeners());

    /* vars */
    private ArrayList<ChangeEventListener> listeners;

    /* constructors */
    ChangeEventEnum(ArrayList<ChangeEventListener> listeners) {
        this.listeners = listeners;
    }

    /* public */
    public void addToSubscribers(ChangeEventListener subscriber) {
        if (!listeners.contains(subscriber)) {
            listeners.add(subscriber);
        }
    }

    /* get */
    public ArrayList<ChangeEventListener> getListeners() {
        return listeners;
    }
}
