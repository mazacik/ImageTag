package user_interface.scene;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import lifecycle.InstanceManager;
import user_interface.singleton.Events;
import user_interface.utils.NodeUtil;
import user_interface.utils.SizeUtil;
import user_interface.utils.StyleUtil;
import user_interface.utils.enums.ColorType;

public class MainScene {
    private ObservableList<Node> panes;
    private final Scene mainScene;

    public MainScene() {
        mainScene = create();
    }

    private Scene create() {
        HBox mainHBox = NodeUtil.getHBox(ColorType.DEF, InstanceManager.getFilterPane(), InstanceManager.getGalleryPane(), InstanceManager.getSelectPane());
        panes = mainHBox.getChildren();
        return new Scene(NodeUtil.getVBox(ColorType.DEF, InstanceManager.getToolbarPane(), mainHBox));
    }
    public void show() {
        InstanceManager.getMainStage().setOpacity(0);
        InstanceManager.getMainStage().setScene(mainScene);
        InstanceManager.getGalleryPane().requestFocus();

        SizeUtil.stageWidthChangeHandler();
        SizeUtil.stageHeightChangehandler();
        mainScene.widthProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageWidthChangeHandler());
        mainScene.heightProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageHeightChangehandler());

        Platform.runLater(() -> InstanceManager.getMainStage().setOpacity(1));

        Events.init();

        StyleUtil.applyScrollbarStyle(InstanceManager.getGalleryPane());
        StyleUtil.applyScrollbarStyle(InstanceManager.getFilterPane().getTagListScrollPane());
        StyleUtil.applyScrollbarStyle(InstanceManager.getSelectPane().getTagListScrollPane());
    }

    public ObservableList<Node> getPanes() {
        return panes;
    }
}
