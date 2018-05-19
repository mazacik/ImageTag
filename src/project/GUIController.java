package project;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.common.Keybinds;
import project.common.Selection;
import project.component.gallery.GalleryPaneBack;
import project.component.gallery.GalleryPaneFront;
import project.component.left.LeftPaneBack;
import project.component.left.LeftPaneFront;
import project.component.preview.PreviewPaneBack;
import project.component.right.RightPaneBack;
import project.component.right.RightPaneFront;
import project.component.top.TopPaneBack;
import project.component.top.TopPaneFront;
import project.database.Database;
import project.database.Serialization;

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
        splitPane.setDividerPositions(0.0, 1.0);

        setTitle("JavaExplorer");
        setMinWidth(800);
        setMinHeight(600);
        setMaximized(true);
        setScene(mainScene);
        setOnCloseRequest(event -> Serialization.writeToDisk());
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

    /* private methods */
    private void loadLazySingletons() {
        Keybinds.getInstance(mainScene);
        Selection.getInstance();
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
