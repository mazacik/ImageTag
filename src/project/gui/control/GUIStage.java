package project.gui.control;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.database.loader.Serialization;
import project.gui.change.ChangeEventControl;
import project.gui.component.*;
import project.helper.Keybinds;

public class GUIStage extends Stage {
    /* components */
    private static final PaneTop paneTop = new PaneTop();
    private static final PaneLeft paneLeft = new PaneLeft();
    private static final PaneGallery paneGallery = new PaneGallery();
    private static final PanePreview panePreview = new PanePreview();
    private static final PaneRight paneRight = new PaneRight();

    private static final SplitPane paneSplit = new SplitPane(paneLeft, paneGallery, paneRight);
    private static final BorderPane paneMain = new BorderPane(paneSplit, paneTop, null, null, null);
    private static final Scene sceneMain = new Scene(paneMain);

    /* constructors */
    public GUIStage() {
        initializeComponents();
        initializeProperties();
        Keybinds.initialize(sceneMain);
        ChangeEventControl.requestReloadGlobal();
    }

    /* initialize */
    private void initializeComponents() {
        paneSplit.setDividerPositions(0.0, 1.0);
    }
    private void initializeProperties() {
        setTitle("JavaExplorer");
        setMinWidth(800);
        setMinHeight(600);
        setMaximized(true);
        setScene(sceneMain);
        setOnCloseRequest(event -> Serialization.writeToDisk());
        show();
    }

    /* get */
    public static PaneTop getPaneTop() {
        return paneTop;
    }
    public static PaneLeft getPaneLeft() {
        return paneLeft;
    }
    public static PaneGallery getPaneGallery() {
        return paneGallery;
    }
    public static PanePreview getPanePreview() {
        return panePreview;
    }
    public static PaneRight getPaneRight() {
        return paneRight;
    }
    public static SplitPane getPaneSplit() {
        return paneSplit;
    }
}
