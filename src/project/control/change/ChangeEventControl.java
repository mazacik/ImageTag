package project.control.change;

import project.database.control.DataElementControl;
import project.database.control.TagElementControl;
import project.gui.component.*;

import java.util.ArrayList;

public abstract class ChangeEventControl {
    /* change */
    private static final ArrayList<Class> changeListenersGlobal = new ArrayList<>();

    /* public */
    public static void notifyListeners(ChangeEventEnum changeEventNotifier) {
        for (Class component : changeEventNotifier.getListeners()) {
            if (component.equals(TopPane.class)) {
                TopPane.refreshComponent();
            } else if (component.equals(LeftPane.class)) {
                LeftPane.refreshComponent();
            } else if (component.equals(GalleryPane.class)) {
                GalleryPane.refreshComponent();
            } else if (component.equals(PreviewPane.class)) {
                PreviewPane.refreshComponent();
            } else if (component.equals(RightPane.class)) {
                RightPane.refreshComponent();
            }
        }
    }
    public static void requestReloadGlobal() {
        DataElementControl.sortAll();
        TagElementControl.sortAll();
        TopPane.refreshComponent();
        LeftPane.refreshComponent();
        GalleryPane.refreshComponent();
        PreviewPane.refreshComponent();
        RightPane.refreshComponent();
    }
    public static void requestReload(Class... components) {
        for (Class component : components) {
            if (component.equals(TopPane.class)) {
                TopPane.refreshComponent();
            } else if (component.equals(LeftPane.class)) {
                LeftPane.refreshComponent();
            } else if (component.equals(GalleryPane.class)) {
                GalleryPane.refreshComponent();
            } else if (component.equals(PreviewPane.class)) {
                PreviewPane.refreshComponent();
            } else if (component.equals(RightPane.class)) {
                RightPane.refreshComponent();
            }
        }
    }
    public static void subscribe(Class changeEventSubscriber, ChangeEventEnum... changeEventNotifiers) {
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
