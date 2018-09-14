package project.control;

import project.database.control.DataControl;
import project.database.control.TagControl;
import project.gui.component.GUINode;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.leftpane.LeftPane;
import project.gui.component.previewpane.PreviewPane;
import project.gui.component.rightpane.RightPane;
import project.gui.component.toppane.TopPane;

public abstract class ReloadControl {
    /* vars */
    private static boolean topPane = false;
    private static boolean leftPane = false;
    private static boolean galleryPane = false;
    private static boolean previewPane = false;
    private static boolean rightPane = false;

    /* public */
    public static void reload(boolean instant, GUINode... nodes) {
        for (GUINode node : nodes) {
            switch (node) {
                case TOPPANE:
                    topPane = true;
                    break;
                case LEFTPANE:
                    leftPane = true;
                    break;
                case GALLERYPANE:
                    galleryPane = true;
                    break;
                case PREVIEWPANE:
                    previewPane = true;
                    break;
                case RIGHTPANE:
                    rightPane = true;
                    break;
                default:
                    break;
            }
        }
        if (instant) doReload();
    }
    public static void reload(GUINode... items) {
        reload(false, items);
    }
    public static void reloadAll(boolean sort) {
        if (sort) {
            DataControl.getCollection().sort();
            FilterControl.getCollection().sort();
            SelectionControl.getCollection().sort();

            TagControl.getCollection().sort();
            FilterControl.getWhitelist().sort();
            FilterControl.getBlacklist().sort();
        }
        TopPane.reload();
        LeftPane.reload();
        GalleryPane.reload();
        PreviewPane.reload();
        RightPane.reload();
    }
    public static void doReload() {
        if (topPane) {
            TopPane.reload();
            topPane = false;
        }
        if (leftPane) {
            LeftPane.reload();
            leftPane = false;
        }
        if (galleryPane) {
            GalleryPane.reload();
            galleryPane = false;
        }
        if (previewPane) {
            PreviewPane.reload();
            previewPane = false;
        }
        if (rightPane) {
            RightPane.reload();
            rightPane = false;
        }
    }
}
