package project.gui;

import java.util.ArrayList;

public abstract class ChangeEventControl {
    /* change listeners */
    private static final ArrayList<ChangeEventListener> changeListenersGlobal = new ArrayList<>();

    /* public methods */
    public static void notifyListeners(ChangeEventEnum changeEventNotifier) {
        for (ChangeEventListener changeEventListener : changeEventNotifier.getListeners()) {
            changeEventListener.refresh();
        }
    }

    public static void requestReload() {
        for (ChangeEventListener changeEventListener : changeListenersGlobal) {
            changeEventListener.refresh();
        }
    }

    public static void requestReload(ChangeEventListener... changeEventListeners) {
        for (ChangeEventListener changeEventListener : changeEventListeners) {
            changeEventListener.refresh();
        }
    }

    public static void subscribe(ChangeEventListener changeEventSubscriber, ChangeEventEnum... changeEventNotifiers) {
        if (!changeListenersGlobal.contains(changeEventSubscriber)) {
            changeListenersGlobal.add(changeEventSubscriber);
        }

        for (ChangeEventEnum changeEventNotifier : changeEventNotifiers) {
            changeEventNotifier.addToSubscribers(changeEventSubscriber);
        }
    }
}
