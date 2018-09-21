package project.control;

import project.database.control.DataControl;
import project.database.control.TagControl;
import project.gui.component.GUINode;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.leftpane.LeftPane;
import project.gui.component.previewpane.PreviewPane;
import project.gui.component.rightpane.RightPane;
import project.gui.component.toppane.TopPane;

public class ReloadControl {
    private boolean topPane = false;
    private boolean leftPane = false;
    private boolean galleryPane = false;
    private boolean previewPane = false;
    private boolean rightPane = false;

    public void reload(boolean instant, GUINode... nodes) {
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
    public void reload(GUINode... items) {
        reload(false, items);
    }
    public void reloadAll(boolean sort) {
        if (sort) {
            DataControl.getCollection().sort();
            Control.getFilterControl().getCollection().sort();
            Control.getSelectionControl().getCollection().sort();

            TagControl.getCollection().sort();
            Control.getFilterControl().getWhitelist().sort();
            Control.getFilterControl().getBlacklist().sort();
        }
        TopPane.reload();
        LeftPane.reload();
        GalleryPane.reload();
        PreviewPane.reload();
        RightPane.reload();
    }
    public void doReload() {
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
