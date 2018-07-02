package project.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import project.control.ReloadControl;
import project.gui.component.GalleryPane;
import project.gui.component.PreviewPane;

public abstract class GUIControl {
    /* imports */
    private static final Region GALLERYPANE = GalleryPane.getInstance();
    private static final Region PREVIEWPANE = PreviewPane.getInstance();
    private static final SplitPane SPLITPANE = GUIMain.getSplitPane();
    private static final ObservableList<Node> SPLITPANE_ITEMS = SPLITPANE.getItems();

    /* public */
    public static void swapDisplayMode() {
        double[] dividerPositions = SPLITPANE.getDividerPositions();
        if (SPLITPANE_ITEMS.contains(GALLERYPANE)) {
            SPLITPANE_ITEMS.set(SPLITPANE_ITEMS.indexOf(GALLERYPANE), PREVIEWPANE);
            ReloadControl.requestReloadOf(true, PreviewPane.class);
        } else {
            SPLITPANE_ITEMS.set(SPLITPANE_ITEMS.indexOf(PREVIEWPANE), GALLERYPANE);
            ReloadControl.requestReloadOf(true, GalleryPane.class);
        }
        SPLITPANE.setDividerPositions(dividerPositions);
    }
    public static boolean isPreviewFullscreen() {
        return SPLITPANE_ITEMS.contains(PREVIEWPANE);
    }
}
