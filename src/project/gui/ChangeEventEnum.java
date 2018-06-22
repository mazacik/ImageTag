package project.gui;

import project.database.Selection;
import project.database.TagDatabase;

import java.util.ArrayList;

public enum ChangeEventEnum {
    /* options */
    SELECTION(Selection.getChangeListeners()),
    FOCUS(Selection.getChangeListeners()),
    FILTER(TagDatabase.getChangeListeners());

    /* variables */
    private ArrayList<ChangeEventListener> listeners;

    /* constructors */
    ChangeEventEnum(ArrayList<ChangeEventListener> listeners) {
        this.listeners = listeners;
    }

    /* public methods */
    public void addToSubscribers(ChangeEventListener subscriber) {
        if (!listeners.contains(subscriber)) {
            listeners.add(subscriber);
        }
    }

    /* getters */
    public ArrayList<ChangeEventListener> getListeners() {
        return listeners;
    }
}
