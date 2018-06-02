package project.customdialog;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.database.DatabaseLoader;

public class LoadingWindow extends Stage {
    /* components */
    private BorderPane loadingPane = new BorderPane();
    private Scene loadingScene = new Scene(loadingPane);
    private Label progressLabel = new Label();

    /* constructos */
    public LoadingWindow() {
        setTitle("JavaExplorer Loading");
        setScene(loadingScene);

        loadingPane.setPadding(new Insets(10));
        loadingPane.setCenter(progressLabel);
        loadingPane.setPrefWidth(300);

        show();
        centerOnScreen();
        setResizable(false);

        new DatabaseLoader().start();
    }

    /* getters */
    public Label getProgressLabel() {
        return progressLabel;
    }
}
