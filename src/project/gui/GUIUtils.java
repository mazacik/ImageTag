package project.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import project.control.ReloadControl;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.previewpane.PreviewPane;

public abstract class GUIUtils {
    /* imports */
    private static final Region galleryPane = GalleryPane.getInstance();
    private static final Region previewPane = PreviewPane.getInstance();
    private static final SplitPane splitPane = GUIInstance.getSplitPane();
    private static final ObservableList<Node> splitPaneItems = splitPane.getItems();

    /* public */
    public static void swapDisplayMode() {
        double[] dividerPositions = splitPane.getDividerPositions();
        if (splitPaneItems.contains(galleryPane)) {
            splitPaneItems.set(splitPaneItems.indexOf(galleryPane), previewPane);
            ReloadControl.requestComponentReload(PreviewPane.class);
        } else {
            splitPaneItems.set(splitPaneItems.indexOf(previewPane), galleryPane);
            ReloadControl.requestComponentReload(GalleryPane.class);
        }
        splitPane.setDividerPositions(dividerPositions);
    }
    public static boolean isPreviewFullscreen() {
        return splitPaneItems.contains(previewPane);
    }
}
