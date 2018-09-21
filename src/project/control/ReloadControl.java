package project.control;

import project.gui.component.GUINode;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.leftpane.LeftPane;
import project.gui.component.previewpane.PreviewPane;
import project.gui.component.rightpane.RightPane;
import project.gui.component.toppane.TopPane;

public class ReloadControl {
    private boolean topPane;
    private boolean leftPane;
    private boolean galleryPane;
    private boolean previewPane;
    private boolean rightPane;

    ReloadControl() {
        topPane = false;
        leftPane = false;
        galleryPane = false;
        previewPane = false;
        rightPane = false;
    }

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
            MainControl.getDataControl().getCollection().sort();
            MainControl.getFilterControl().getCollection().sort();
            MainControl.getSelectionControl().getCollection().sort();

            MainControl.getTagControl().getCollection().sort();
            MainControl.getFilterControl().getWhitelist().sort();
            MainControl.getFilterControl().getBlacklist().sort();
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
