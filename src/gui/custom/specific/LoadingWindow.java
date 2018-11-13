package gui.custom.specific;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import utils.MainUtil;

public class LoadingWindow extends Stage implements MainUtil {
    private final BorderPane loadingPane = new BorderPane();
    private final Scene loadingScene = new Scene(loadingPane);
    private final Label progressLabel = new Label();

    public LoadingWindow() {
        setDefaultValuesChildren();
        setDefaultValues();
    }

    private void setDefaultValuesChildren() {
        loadingPane.setPadding(new Insets(10));
        loadingPane.setCenter(progressLabel);
        loadingPane.setPrefWidth(300);
    }
    private void setDefaultValues() {
        setTitle("ImageTag Loading");
        setScene(loadingScene);
        setResizable(false);
        centerOnScreen();
        show();
        customStage.init();
    }

    public Label getProgressLabel() {
        return progressLabel;
    }
}
