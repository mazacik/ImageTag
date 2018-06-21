package project.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import project.gui.component.gallery.GalleryPane;
import project.gui.component.preview.PreviewPane;
import project.gui.component.preview.PreviewPaneBack;

public class GUIController {
    public static void notifyOfChange(GUIComponent notifier) {
        for (GUIComponent listener : notifier.getChangeListeners()) {
            listener.refresh();
        }
    }

    public static void subscribe(GUIComponent subscriber, GUIComponent... notifiers) {
        for (GUIComponent notifier : notifiers) {
            notifier.addToSubscribers(subscriber);
        }
    }

    public static void swapDisplayMode() {
        GalleryPane galleryPane = GUIStage.getGalleryPane();
        PreviewPane previewPane = GUIStage.getPreviewPane();
        SplitPane splitPane = GUIStage.getSplitPane();
        ObservableList<Node> splitPaneItems = splitPane.getItems();

        double[] dividerPositions = splitPane.getDividerPositions();
        if (splitPaneItems.contains(galleryPane)) {
            splitPaneItems.set(splitPaneItems.indexOf(galleryPane), previewPane);
            previewPane.setCanvasSize(galleryPane.getWidth(), galleryPane.getHeight());
            PreviewPaneBack.getInstance().reloadContent();
        } else {
            splitPaneItems.set(splitPaneItems.indexOf(previewPane), galleryPane);
        }
        splitPane.setDividerPositions(dividerPositions);
    }

    public static boolean isPreviewFullscreen() {
        SplitPane splitPane = GUIStage.getSplitPane();
        PreviewPane previewPane = GUIStage.getPreviewPane();
        return splitPane.getItems().contains(previewPane);
    }
}
