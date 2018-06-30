package project.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import project.gui.component.PaneGallery;
import project.gui.component.PanePreview;

public abstract class GUIControl {
    /* imports */
    private static final PaneGallery PANE_GALLERY = GUIStage.getPaneGallery();
    private static final PanePreview PANE_PREVIEW = GUIStage.getPanePreview();
    private static final SplitPane splitPane = GUIStage.getPaneSplit();
    private static final ObservableList<Node> splitPaneItems = splitPane.getItems();

    /* public */
    public static void swapDisplayMode() {
        double[] dividerPositions = splitPane.getDividerPositions();
        if (splitPaneItems.contains(PANE_GALLERY)) {
            splitPaneItems.set(splitPaneItems.indexOf(PANE_GALLERY), PANE_PREVIEW);
            PANE_PREVIEW.setCanvasSize(PANE_GALLERY.getWidth(), PANE_GALLERY.getHeight());
            ChangeEventControl.requestReload(PANE_PREVIEW);
        } else {
            splitPaneItems.set(splitPaneItems.indexOf(PANE_PREVIEW), PANE_GALLERY);
        }
        splitPane.setDividerPositions(dividerPositions);
    }
    public static boolean isPreviewFullscreen() {
        return splitPaneItems.contains(PANE_PREVIEW);
    }
}
