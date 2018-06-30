package project.gui.control;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.control.change.ChangeEventControl;
import project.database.loader.Serialization;
import project.gui.component.*;
import project.helper.Keybinds;

public class GUIStage extends Stage {
    /* imports */
    private static final Node TOP_PANE = TopPane.getInstance();
    private static final Node LEFT_PANE = LeftPane.getInstance();
    private static final Node GALLERY_PANE = GalleryPane.getInstance();
    private static final Node PREVIEW_PANE = PreviewPane.getInstance();
    private static final Node RIGHT_PANE = RightPane.getInstance();

    /* components */
    private static final SplitPane SPLIT_PANE = new SplitPane(LEFT_PANE, GALLERY_PANE, RIGHT_PANE);
    private static final BorderPane MAIN_PANE = new BorderPane(SPLIT_PANE, TOP_PANE, null, null, null);
    private static final Scene MAIN_SCENE = new Scene(MAIN_PANE);

    /* constructors */
    public GUIStage() {
        initializeComponents();
        initializeProperties();
        Keybinds.initialize(MAIN_SCENE);
        ChangeEventControl.requestReloadGlobal();
    }

    /* initialize */
    private void initializeComponents() {
        TopPane.initialize();
        LeftPane.initialize();
        GalleryPane.initialize();
        PreviewPane.initialize();
        RightPane.initialize();

        SPLIT_PANE.setDividerPositions(0.0, 1.0);
    }
    private void initializeProperties() {
        setTitle("JavaExplorer");
        setMinWidth(800);
        setMinHeight(600);
        setMaximized(true);
        setScene(MAIN_SCENE);
        setOnCloseRequest(event -> Serialization.writeToDisk());
        show();
    }

    /* get */
    public static Node getPaneTop() {
        return TOP_PANE;
    }
    public static Node getLeftPane() {
        return LEFT_PANE;
    }
    public static Node getGalleryPane() {
        return GALLERY_PANE;
    }
    public static Node getPanePreview() {
        return PREVIEW_PANE;
    }
    public static Node getPaneRight() {
        return RIGHT_PANE;
    }
    public static SplitPane getSplitPane() {
        return SPLIT_PANE;
    }
}
