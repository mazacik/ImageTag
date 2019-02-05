package userinterface.template.intro;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.CommonUtil;
import utils.InstanceRepo;

public class LoadingWindow extends Stage implements InstanceRepo {
    private final Label progressLabel = new Label();

    public LoadingWindow() {
        progressLabel.setTextFill(CommonUtil.getTextColorDefault());

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));
        borderPane.setCenter(progressLabel);
        borderPane.setBackground(CommonUtil.getBackgroundDefault());
        borderPane.setPrefWidth(300);
        borderPane.setBorder(new Border(new BorderStroke(CommonUtil.getNodeBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));

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
