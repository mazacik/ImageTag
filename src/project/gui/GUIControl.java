package project.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import project.gui.component.PaneGallery;
import project.gui.component.PanePreview;

public abstract class GUIControl {
    /* imports */
    private static final PaneGallery GALLERYPANE = GUIStage.getPaneGallery();
    private static final PanePreview PREVIEWPANE = GUIStage.getPanePreview();
    private static final SplitPane SPLITPANE = GUIStage.getPaneSplit();
    private static final ObservableList<Node> SPLITPANE_ITEMS = SPLITPANE.getItems();

    /* public */
    public static void swapDisplayMode() {
        double[] dividerPositions = SPLITPANE.getDividerPositions();
        if (SPLITPANE_ITEMS.contains(GALLERYPANE)) {
            SPLITPANE_ITEMS.set(SPLITPANE_ITEMS.indexOf(GALLERYPANE), PREVIEWPANE);
            PREVIEWPANE.setCanvasSize(GALLERYPANE.getWidth(), GALLERYPANE.getHeight());
            ChangeEventControl.requestReload(PREVIEWPANE);
        } else {
            SPLITPANE_ITEMS.set(SPLITPANE_ITEMS.indexOf(PREVIEWPANE), GALLERYPANE);
        }
        SPLITPANE.setDividerPositions(dividerPositions);
    }
    public static boolean isPreviewFullscreen() {
        return SPLITPANE_ITEMS.contains(PREVIEWPANE);
    }
}
