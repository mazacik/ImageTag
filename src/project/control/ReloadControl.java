package project.control;

import project.database.control.DataControl;
import project.database.control.TagControl;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.leftpane.LeftPane;
import project.gui.component.previewpane.PreviewPane;
import project.gui.component.rightpane.RightPane;
import project.gui.component.toppane.TopPane;

public abstract class ReloadControl {
    /* vars */
    private static boolean reloadTopPane = false;
    private static boolean reloadLeftPane = false;
    private static boolean reloadGalleryPane = false;
    private static boolean reloadPreviewPane = false;
    private static boolean reloadRightPane = false;

    /* public */
    public static void requestGlobalReload(boolean sortElementControls) {
        if (sortElementControls) {
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
    public static void request(Class... components) {
        ReloadControl.request(false, components);
    }
    public static void request(boolean instant, Class... components) {
        for (Class component : components) {
            if (component.equals(TopPane.class)) {
                reloadTopPane = true;
            } else if (component.equals(LeftPane.class)) {
                reloadLeftPane = true;
            } else if (component.equals(GalleryPane.class)) {
                reloadGalleryPane = true;
            } else if (component.equals(PreviewPane.class)) {
                reloadPreviewPane = true;
            } else if (component.equals(RightPane.class)) {
                reloadRightPane = true;
            }
        }
        if (instant) forceReload();
    }
    public static void forceReload() {
        if (reloadTopPane) {
            TopPane.reload();
            reloadTopPane = false;
        }
        if (reloadLeftPane) {
            LeftPane.reload();
            reloadLeftPane = false;
        }
        if (reloadGalleryPane) {
            GalleryPane.reload();
            reloadGalleryPane = false;
        }
        if (reloadPreviewPane) {
            PreviewPane.reload();
            reloadPreviewPane = false;
        }
        if (reloadRightPane) {
            RightPane.reload();
            reloadRightPane = false;
        }
    }
}
