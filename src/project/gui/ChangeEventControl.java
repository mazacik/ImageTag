package project.gui;

import project.database.ItemDatabase;
import project.database.TagDatabase;

import java.util.ArrayList;

public abstract class ChangeEventControl {
    /* change listeners */
    private static final ArrayList<ChangeEventListener> changeListenersGlobal = new ArrayList<>();

    /* public methods */
    public static void notifyListeners(ChangeEventEnum changeEventNotifier) {
        for (ChangeEventListener changeEventListener : changeEventNotifier.getListeners()) {
            changeEventListener.refreshComponent();
        }
    }

    public static void requestReloadGlobal() {
        ItemDatabase.sort();
        TagDatabase.sort();
        for (ChangeEventListener changeEventListener : changeListenersGlobal) {
            changeEventListener.refreshComponent();
        }
    }

    public static void requestReload(ChangeEventListener... changeEventListeners) {
        for (ChangeEventListener changeEventListener : changeEventListeners) {
            changeEventListener.refreshComponent();
        }
    }

    public static void subscribe(ChangeEventListener changeEventSubscriber, ChangeEventEnum... changeEventNotifiers) {
        if (!changeListenersGlobal.contains(changeEventSubscriber)) {
            changeListenersGlobal.add(changeEventSubscriber);
        }

        if (changeEventNotifiers != null) {
            for (ChangeEventEnum changeEventNotifier : changeEventNotifiers) {
                changeEventNotifier.addToSubscribers(changeEventSubscriber);
            }
        }
    }
}
