package user_interface.scene;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;

public class LoadingScene implements InstanceRepo {
    private final Scene loadingScene;
    private final Label progressLabel = NodeFactory.getLabel("", ColorType.DEF, ColorType.DEF);

    LoadingScene() {
        loadingScene = create();
    }

    private Scene create() {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));
        borderPane.setCenter(progressLabel);
        borderPane.setPrefWidth(300);
        borderPane.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));
        NodeFactory.addNodeToBackgroundManager(borderPane, ColorType.DEF);

        Scene loadingScene = new Scene(borderPane);
        CommonUtil.updateNodeProperties(loadingScene);
        return loadingScene;
    }
    void show() {
        mainStage.setScene(loadingScene);
        mainStage.setWidth(CommonUtil.getUsableScreenWidth() / 6);
        mainStage.setHeight(CommonUtil.getUsableScreenHeight() / 16);
        mainStage.centerOnScreen();

        logger.debug(this, "waiting for directory");
    }

    public Label getProgressLabel() {
        return progressLabel;
    }
}
