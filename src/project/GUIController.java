package project;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.common.Database;
import project.common.Keybinds;
import project.custom.component.gallery.GalleryPaneBack;
import project.custom.component.gallery.GalleryPaneFront;
import project.custom.component.left.LeftPaneBack;
import project.custom.component.left.LeftPaneFront;
import project.custom.component.preview.PreviewPaneBack;
import project.custom.component.right.RightPaneBack;
import project.custom.component.right.RightPaneFront;
import project.custom.component.top.TopPaneBack;
import project.custom.component.top.TopPaneFront;
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
