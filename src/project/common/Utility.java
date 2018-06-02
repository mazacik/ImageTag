package project.common;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import project.GUIController;
import project.component.gallery.GalleryPaneBack;
import project.component.gallery.GalleryPaneFront;
import project.component.preview.PreviewPaneBack;
import project.component.preview.PreviewPaneFront;

public abstract class Utility {
    public static boolean isNumber(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c == '.' || c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static boolean isPreviewFullscreen() {
        return GUIController.getInstance().getSplitPane().getItems().contains(PreviewPaneFront.getInstance());
    }

    public static void swapPreviewMode() {
        GalleryPaneFront galleryPaneFront = GalleryPaneFront.getInstance();
        PreviewPaneFront previewPaneFront = PreviewPaneFront.getInstance();

        SplitPane splitPane = GUIController.getInstance().getSplitPane();
        ObservableList<Node> splitPaneItems = splitPane.getItems();

        double[] dividerPositions = splitPane.getDividerPositions();
        if (splitPaneItems.contains(galleryPaneFront)) {
            splitPaneItems.set(splitPaneItems.indexOf(galleryPaneFront), previewPaneFront);
            previewPaneFront.setCanvasSize(galleryPaneFront.getWidth(), galleryPaneFront.getHeight());
            PreviewPaneBack.getInstance().reloadContent();
        } else {
            splitPaneItems.set(splitPaneItems.indexOf(previewPaneFront), galleryPaneFront);
            GalleryPaneBack.getInstance().reloadContent();
        }
        splitPane.setDividerPositions(dividerPositions);
    }
}
