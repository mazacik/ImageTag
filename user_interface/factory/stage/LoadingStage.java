package user_interface.factory.stage;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;

public class LoadingStage extends Stage implements InstanceRepo {
    private final Label progressLabel = NodeFactory.getLabel("", ColorType.DEF, ColorType.DEF);

    public LoadingStage() {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));
        borderPane.setCenter(progressLabel);
        borderPane.setPrefWidth(300);
        borderPane.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));
        NodeFactory.addNodeToBackgroundManager(borderPane, ColorType.DEF);

        this.setScene(new Scene(borderPane));
        this.setResizable(false);
        this.initStyle(StageStyle.UNDECORATED);
        this.setAlwaysOnTop(true);
        this.setOnShown(event -> this.centerOnScreen());
        this.show();
    }

    public Label getProgressLabel() {
        return progressLabel;
    }
}
