package project.gui.control;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.control.ReloadControl;
import project.database.loader.Serialization;
import project.gui.component.*;
import project.helper.Keybinds;

public abstract class GUIStage extends Stage {
    /* components */
    private static Stage _this;
    private static final SplitPane splitPane = new SplitPane(LeftPane.getInstance(), GalleryPane.getInstance(), RightPane.getInstance());
    private static final BorderPane mainPane = new BorderPane(splitPane, TopPane.getInstance(), null, null, null);
    private static final Scene mainScene = new Scene(mainPane);

    /* initialize */
    public static void initialize() {
        _this = new Stage();
        initializeComponents();
        initializeProperties();
        Keybinds.initialize(mainScene);
        ReloadControl.requestReloadGlobal(true);
    }
    private static void initializeComponents() {
        TopPane.initialize();
        LeftPane.initialize();
        GalleryPane.initialize();
        PreviewPane.initialize();
        RightPane.initialize();

        splitPane.setDividerPositions(0.0, 1.0);
    }
    private static void initializeProperties() {
        _this.setTitle("JavaExplorer");
        _this.setMinWidth(800);
        _this.setMinHeight(600);
        _this.setMaximized(true);
        _this.setScene(mainScene);
        _this.setOnCloseRequest(event -> Serialization.writeToDisk());
        _this.show();
    }

    /* get */
    public static SplitPane getSplitPane() {
        return splitPane;
    }
    public static Stage getInstance() {
        if (_this == null) initialize();
        return _this;
    }
}
