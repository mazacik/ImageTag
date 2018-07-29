package project.gui;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.control.ReloadControl;
import project.database.loader.Serialization;
import project.gui.component.*;
import project.userinput.UserInputGlobal;

public abstract class GUIMain extends Stage {
    /* components */
    private static Stage _this;
    private static final SplitPane splitPane = new SplitPane(LeftPane.getInstance(), GalleryPane.getInstance(), RightPane.getInstance());
    private static final BorderPane mainPane = new BorderPane(splitPane, TopPane.getInstance(), null, null, null);
    private static final Scene mainScene = new Scene(mainPane);

    /* initialize */
    public static void initialize() {
        _this = new Stage();
        initializeComponents();
        initializeInstance();
        UserInputGlobal.initialize();
        ReloadControl.requestGlobalReload(true);
    }
    private static void initializeComponents() {
        TopPane.initialize();
        LeftPane.initialize();
        GalleryPane.initialize();
        PreviewPane.initialize();
        RightPane.initialize();

        splitPane.setDividerPositions(0.0, 1.0);
    }
    private static void initializeInstance() {
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
        return _this;
    }
}
