package user_interface.scene;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import system.CommonUtil;
import system.Instances;
import user_interface.factory.NodeUtil;
import user_interface.factory.base.TextNode;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.utils.SizeUtil;

public class LoadingScene implements Instances {
    private final Scene loadingScene;
    private final TextNode progressTextNode = new TextNode("", ColorType.DEF, ColorType.DEF);

    LoadingScene() {
        loadingScene = create();
    }

    private Scene create() {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));
        borderPane.setCenter(progressTextNode);
        borderPane.setPrefWidth(300);
        borderPane.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
        NodeUtil.addToManager(borderPane, ColorType.DEF);

        Scene loadingScene = new Scene(borderPane);
        CommonUtil.updateNodeProperties(loadingScene);
        return loadingScene;
    }
    void show() {
        mainStage.setScene(loadingScene);
        mainStage.setWidth(SizeUtil.getUsableScreenWidth() / 6);
        mainStage.setHeight(SizeUtil.getUsableScreenHeight() / 16);
        mainStage.centerOnScreen();

        logger.debug(this, "waiting for directory");
    }

    public Scene getInstance() {
        return loadingScene;
    }
    public TextNode getProgressTextNode() {
        return progressTextNode;
    }
}
