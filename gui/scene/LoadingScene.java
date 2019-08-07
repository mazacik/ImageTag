package application.gui.scene;

import application.gui.decorator.Decorator;
import application.gui.decorator.SizeUtil;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.NodeUtil;
import application.gui.nodes.simple.TextNode;
import application.main.Instances;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import java.util.logging.Logger;

public class LoadingScene {
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
		Decorator.manage(borderPane, ColorType.DEF);

        return new Scene(borderPane);
    }
    void show() {
		Instances.getMainStage().setScene(loadingScene);
		Instances.getMainStage().setWidth(SizeUtil.getUsableScreenWidth() / 6);
		Instances.getMainStage().setHeight(SizeUtil.getUsableScreenHeight() / 16);
		Instances.getMainStage().centerOnScreen();
	
		Logger.getGlobal().info("waiting for directory");
    }

    public Scene getInstance() {
        return loadingScene;
    }
    public TextNode getProgressTextNode() {
        return progressTextNode;
    }
}
