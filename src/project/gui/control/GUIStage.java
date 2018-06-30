package project.gui.control;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.control.change.ChangeEventControl;
import project.database.loader.Serialization;
import project.gui.component.*;
import project.helper.Keybinds;

public class GUIStage extends Stage {
    /* components */
    private static final SplitPane splitPane = new SplitPane(LeftPane.getInstance(), GalleryPane.getInstance(), RightPane.getInstance());
    private static final BorderPane mainPane = new BorderPane(splitPane, TopPane.getInstance(), null, null, null);
    private static final Scene mainScene = new Scene(mainPane);

    /* constructors */
    public GUIStage() {
        initializeComponents();
        initializeProperties();
        Keybinds.initialize(mainScene);
        ChangeEventControl.requestReloadGlobal();
    }

    /* initialize */
    private void initializeComponents() {
        TopPane.initialize();
        LeftPane.initialize();
        GalleryPane.initialize();
        PreviewPane.initialize();
        RightPane.initialize();

        splitPane.setDividerPositions(0.0, 1.0);
    }
    private void initializeProperties() {
        setTitle("JavaExplorer");
        setMinWidth(800);
        setMinHeight(600);
        setMaximized(true);
        setScene(mainScene);
        setOnCloseRequest(event -> Serialization.writeToDisk());
        show();
    }

    /* get */
    public static SplitPane getSplitPane() {
        return splitPane;
    }
}
