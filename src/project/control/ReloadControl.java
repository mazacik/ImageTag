package project.control;

import project.database.control.DataElementControl;
import project.database.control.TagElementControl;
import project.gui.component.*;

public abstract class ReloadControl {
    /* vars */
    private static boolean reloadTopPane = false;
    private static boolean reloadLeftPane = false;
    private static boolean reloadGalleryPane = false;
    private static boolean reloadPreviewPane = false;
    private static boolean reloadRightPane = false;

    /* public */
    public static void requestReloadGlobal(boolean sortElementControls) {
        if (sortElementControls) {
            DataElementControl.sortAll();
            TagElementControl.sortAll();
        }
        TopPane.reload();
        LeftPane.reload();
        GalleryPane.reload();
        PreviewPane.reload();
        RightPane.reload();
    }
    public static void requestReloadOf(Class... components) {
        ReloadControl.requestReloadOf(false, components);
    }
    public static void requestReloadOf(boolean instant, Class... components) {
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
        if (instant) {
            doReload();
        }
    }
    public static void doReload() {
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
    //todo try Object instead of Class
}
