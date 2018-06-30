package project.gui;

import project.database.DataElementDatabase;
import project.database.TagElementDatabase;

import java.util.ArrayList;

public abstract class ChangeEventControl {
    /* change */
    private static final ArrayList<ChangeEventListener> changeListenersGlobal = new ArrayList<>();

    /* public */
    public static void notifyListeners(ChangeEventEnum changeEventNotifier) {
        for (ChangeEventListener changeEventListener : changeEventNotifier.getListeners()) {
            changeEventListener.refreshComponent();
        }
    }
    public static void requestReloadGlobal() {
        DataElementDatabase.sort();
        TagElementDatabase.sortAll();
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
