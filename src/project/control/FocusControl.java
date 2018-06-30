package project.control;

import project.database.element.DataElement;
import project.gui.ChangeEventControl;
import project.gui.ChangeEventEnum;
import project.gui.ChangeEventListener;
import project.gui.component.part.GalleryTile;

import java.util.ArrayList;

public abstract class FocusControl {
    /* change */
    private static final ArrayList<ChangeEventListener> changeListeners = new ArrayList<>();
    public static ArrayList<ChangeEventListener> getChangeListeners() {
        return changeListeners;
    }

    /* vars */
    private static DataElement currentFocus = null;
    private static DataElement previousFocus = null;

    /* public */
    public static void setFocus(DataElement dataElement) {
        /* store old focus position */
        if (currentFocus != null)
            previousFocus = currentFocus;

        /* apply new focus effect */
        currentFocus = dataElement;
        GalleryTile.generateEffect(currentFocus);

        /* remove old focus effect */
        if (previousFocus != null) {
            GalleryTile.generateEffect(previousFocus);
        }

        ChangeEventControl.notifyListeners(ChangeEventEnum.FOCUS);
    }

    /* get */
    public static DataElement getCurrentFocus() {
        return currentFocus;
    }
}
