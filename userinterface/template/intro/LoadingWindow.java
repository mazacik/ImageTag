package userinterface.template.intro;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utils.CommonUtil;
import utils.InstanceRepo;

public class LoadingWindow extends Stage implements InstanceRepo {
    private final BorderPane borderPane = new BorderPane();
    private final Label progressLabel = new Label();

    public LoadingWindow() {
        setDefaultValuesChildren();
        setDefaultValues();
    }

    private void setDefaultValuesChildren() {
        progressLabel.setTextFill(Color.LIGHTGRAY);

        borderPane.setPadding(new Insets(10));
        borderPane.setCenter(progressLabel);
        borderPane.setBackground(CommonUtil.getBackgroundDefault());
        borderPane.setPrefWidth(300);
    }
    private void setDefaultValues() {
        this.setTitle("PLACEHOLDER");
        this.setScene(new Scene(borderPane));
        this.setResizable(false);
        this.centerOnScreen();
        this.show();
    }

    public Label getProgressLabel() {
        return progressLabel;
    }
}
