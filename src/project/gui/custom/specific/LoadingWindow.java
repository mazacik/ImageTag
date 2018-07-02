package project.gui.custom.specific;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.database.loader.DataLoader;

public class LoadingWindow extends Stage {
    /* components */
    private final BorderPane loadingPane = new BorderPane();
    private final Scene loadingScene = new Scene(loadingPane);
    private final Label progressLabel = new Label();

    /* constructors */
    public LoadingWindow() {
        initializeComponents();
        initializeInstance();
    }

    /* initialize */
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
    }

    /* get */
    public Label getProgressLabel() {
        return progressLabel;
    }
}
