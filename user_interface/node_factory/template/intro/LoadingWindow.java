package user_interface.node_factory.template.intro;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.InstanceRepo;
import user_interface.node_factory.NodeFactory;
import user_interface.node_factory.utils.ColorType;
import user_interface.node_factory.utils.ColorUtil;

public class LoadingWindow extends Stage implements InstanceRepo {
    private final Label progressLabel = NodeFactory.getLabel("", ColorType.DEF, ColorType.DEF);

    public LoadingWindow() {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));
        borderPane.setCenter(progressLabel);
        borderPane.setPrefWidth(300);
        borderPane.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));
        NodeFactory.addNodeToBackgroundManager(borderPane, ColorType.DEF);

        this.setScene(new Scene(borderPane));
        this.setResizable(false);
        this.initStyle(StageStyle.UNDECORATED);
        this.centerOnScreen();
        this.setAlwaysOnTop(true);
        this.show();
    }

    public Label getProgressLabel() {
        return progressLabel;
    }
}
