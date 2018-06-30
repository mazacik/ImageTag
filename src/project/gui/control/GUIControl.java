package project.gui.control;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import project.control.change.ChangeEventControl;
import project.gui.component.GalleryPane;
import project.gui.component.PreviewPane;

public abstract class GUIControl {
    /* imports */
    private static final Region GALLERYPANE = GalleryPane.getInstance();
    private static final Region PREVIEWPANE = PreviewPane.getInstance();
    private static final SplitPane SPLITPANE = GUIStage.getSplitPane();
    private static final ObservableList<Node> SPLITPANE_ITEMS = SPLITPANE.getItems();

    /* public */
    public static void swapDisplayMode() {
        double[] dividerPositions = SPLITPANE.getDividerPositions();
        if (SPLITPANE_ITEMS.contains(GALLERYPANE)) {
            SPLITPANE_ITEMS.set(SPLITPANE_ITEMS.indexOf(GALLERYPANE), PREVIEWPANE);
            PreviewPane.setCanvasSize(GALLERYPANE.getWidth(), GALLERYPANE.getHeight());
            ChangeEventControl.requestReload(PreviewPane.class);
        } else {
            SPLITPANE_ITEMS.set(SPLITPANE_ITEMS.indexOf(PREVIEWPANE), GALLERYPANE);
        }
        SPLITPANE.setDividerPositions(dividerPositions);
    }
    public static boolean isPreviewFullscreen() {
        return SPLITPANE_ITEMS.contains(PREVIEWPANE);
    }
}
