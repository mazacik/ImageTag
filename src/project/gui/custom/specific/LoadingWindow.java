package project.gui.custom.specific;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.database.loader.DataLoader;
import project.gui.GUIInstance;

public class LoadingWindow extends Stage {
    private final BorderPane loadingPane = new BorderPane();
    private final Scene loadingScene = new Scene(loadingPane);
    private final Label progressLabel = new Label();

    public LoadingWindow() {
        initializeComponents();
        initializeInstance();
    }

    private void initializeComponents() {
        loadingPane.setPadding(new Insets(10));
        loadingPane.setCenter(progressLabel);
        loadingPane.setPrefWidth(300);
    }
    private void initializeInstance() {
        setTitle("JavaExplorer Loading");
        setScene(loadingScene);
        setResizable(false);
        centerOnScreen();
        show();
        new DataLoader().start();
        GUIInstance.initialize();
    }

    public Label getProgressLabel() {
        return progressLabel;
    }
}
