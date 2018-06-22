package project.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import project.gui.component.GalleryPane;
import project.gui.component.PreviewPane;

import java.util.ArrayList;

public class GUIController {
    /* change listeners */
    private static final ArrayList<ChangeNotificationHelper> changeListeners = new ArrayList<>();

    /* public methods */
    public static void notifyOfChange(ChangeEvent notifier) {
        for (ChangeNotificationHelper listener : notifier.getChangeListeners()) {
            listener.refresh();
        }
    }

    public static void requestReload() {
        for (ChangeNotificationHelper listener : changeListeners) {
            listener.refresh();
        }
    }

    public static void requestReload(ChangeNotificationHelper... targets) {
        for (ChangeNotificationHelper changeNotificationHelper : targets) {
            changeNotificationHelper.refresh();
        }
    }

    public static void subscribe(ChangeNotificationHelper subscriber, ChangeEvent... notifiers) {
        if (!changeListeners.contains(subscriber)) {
            changeListeners.add(subscriber);
        }

        for (ChangeEvent notifier : notifiers) {
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
            GUIStage.getPreviewPane().refresh();
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
