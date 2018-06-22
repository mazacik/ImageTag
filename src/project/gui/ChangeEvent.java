package project.gui;

import project.backend.Selection;
import project.database.TagDatabase;

import java.util.ArrayList;

public enum ChangeEvent {
    SELECTION(Selection.getChangeListeners()),
    FOCUS(Selection.getChangeListeners()),
    FILTER(TagDatabase.getChangeListeners());

    private ArrayList<ChangeNotificationHelper> changeListeners;

    ChangeEvent(ArrayList<ChangeNotificationHelper> changeListeners) {
        this.changeListeners = changeListeners;
    }

    public void addToSubscribers(ChangeNotificationHelper subscriber) {
        if (!changeListeners.contains(subscriber)) {
            changeListeners.add(subscriber);
        }
    }

    public ArrayList<ChangeNotificationHelper> getChangeListeners() {
        return changeListeners;
    }
}
