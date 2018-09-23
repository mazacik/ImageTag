package project.gui.custom.specific;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.MainUtils;
import project.database.loader.DataLoader;

public class LoadingWindow extends Stage implements MainUtils {
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
        setTitle("JavaExplorer Loading");
        setScene(loadingScene);
        setResizable(false);
        centerOnScreen();
        show();
        new DataLoader().start(this);
        customStage.init();
    }

    public Label getProgressLabel() {
        return progressLabel;
    }
}
