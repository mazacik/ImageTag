package project;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.backend.Keybinds;
import project.database.Database;
import project.database.Serialization;
import project.gui.component.gallery.GalleryPaneBack;
import project.gui.component.gallery.GalleryPaneFront;
import project.gui.component.left.LeftPaneBack;
import project.gui.component.left.LeftPaneFront;
import project.gui.component.preview.PreviewPaneBack;
import project.gui.component.preview.PreviewPaneFront;
import project.gui.component.right.RightPaneBack;
import project.gui.component.right.RightPaneFront;
import project.gui.component.top.TopPaneBack;
import project.gui.component.top.TopPaneFront;

public class GUIController extends Stage {
    /* lazy singleton */
    private static GUIController instance;
    public static GUIController getInstance() {
        if (instance == null) instance = new GUIController();
        return instance;
    }

    /* components */
    private final SplitPane splitPane = new SplitPane(LeftPaneFront.getInstance(), GalleryPaneFront.getInstance(), RightPaneFront.getInstance());
    private final BorderPane mainPane = new BorderPane(splitPane, TopPaneFront.getInstance(), null, null, null);
    private final Scene mainScene = new Scene(mainPane);

    /* constructors */
    private GUIController() {
        setTitle("JavaExplorer");
        setMinWidth(800);
        setMinHeight(600);
        setMaximized(true);
        setScene(mainScene);
        setOnCloseRequest(event -> Serialization.writeToDisk());

        splitPane.setDividerPositions(0.0, 1.0);

        loadLazySingletons();

        show();
    }

    /* public methods */
    public void reloadComponentData(boolean sortDatabase) {
        if (sortDatabase) Database.sort();
        LeftPaneBack.getInstance().reloadContent();
        RightPaneBack.getInstance().reloadContent();
        GalleryPaneBack.getInstance().reloadContent();
        PreviewPaneBack.getInstance().reloadContent();
    }

    /* utility methods */
    public static void swapPreviewMode() {
        GalleryPaneFront galleryPaneFront = GalleryPaneFront.getInstance();
        PreviewPaneFront previewPaneFront = PreviewPaneFront.getInstance();

        SplitPane splitPane = getInstance().getSplitPane();
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

    public static boolean isPreviewFullscreen() {
        return getInstance().getSplitPane().getItems().contains(PreviewPaneFront.getInstance());
    }

    /* private methods */
    private void loadLazySingletons() {
        Keybinds.getInstance(mainScene);
        GalleryPaneBack.getInstance();
        LeftPaneBack.getInstance();
        PreviewPaneBack.getInstance();
        RightPaneBack.getInstance();
        TopPaneBack.getInstance();
    }

    /* getters */
    public SplitPane getSplitPane() {
        return splitPane;
    }
}
