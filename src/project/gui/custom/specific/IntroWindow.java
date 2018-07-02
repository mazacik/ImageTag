package project.gui.custom.specific;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import project.Main;
import project.gui.custom.generic.DirectoryChooserWindow;
import project.helper.Settings;

public class IntroWindow extends Stage {
    /* components */
    private final GridPane paneIntro = new GridPane();
    private final Scene sceneIntro = new Scene(paneIntro);

    private final Label lblMainDirectory = new Label("Main Directory Path:");
    private final Label lblImageCacheDirectory = new Label("Image Cache Directory Path:");
    private final Label lblDatabaseCacheFile = new Label("Database Cache File Path:");
    private final TextField tfMainDirectory = new TextField();
    private final TextField tfImageCacheDirectory = new TextField();
    private final TextField tfDatabaseCacheFile = new TextField();
    private final Button btnMainDirectory = new Button("...");
    private final Button btnImageCacheDirectory = new Button("...");
    private final Button btnDatabaseCacheFile = new Button("...");

    private final Button buttonOK = new Button("OK");

    /* constructors */
    public IntroWindow() {
        initializeComponents();
        initializeInstance();
    }

    /* initialize */
    private void initializeComponents() {
        setListeners();
        addComponentsToGrid();

        paneIntro.setPadding(new Insets(10));
        paneIntro.setHgap(5);
        paneIntro.setVgap(3);

        tfMainDirectory.setPrefWidth(300);
        btnMainDirectory.setPrefWidth(35);
        btnImageCacheDirectory.setPrefWidth(35);
        btnDatabaseCacheFile.setPrefWidth(35);
        buttonOK.setPrefWidth(35);
        buttonOK.setDisable(true);
    }
    private void initializeInstance() {
        setTitle("JavaExplorer Settings");
        setScene(sceneIntro);
        setResizable(false);
        centerOnScreen();
        show();
        btnMainDirectory.requestFocus();
    }
    private void addComponentsToGrid() {
        paneIntro.add(lblMainDirectory, 0, 0);
        paneIntro.add(lblImageCacheDirectory, 0, 1);
        paneIntro.add(lblDatabaseCacheFile, 0, 2);
        paneIntro.add(tfMainDirectory, 1, 0);
        paneIntro.add(tfImageCacheDirectory, 1, 1);
        paneIntro.add(tfDatabaseCacheFile, 1, 2);
        paneIntro.add(btnMainDirectory, 2, 0);
        paneIntro.add(btnImageCacheDirectory, 2, 1);
        paneIntro.add(btnDatabaseCacheFile, 2, 2);
        paneIntro.add(buttonOK, 2, 4);
    }
    private void setListeners() {
        btnMainDirectory.setOnAction(event -> {
            String mainDirectoryPath = new DirectoryChooserWindow(this, "Choose Main Directory Path", "C:\\").getResultValue();
            tfMainDirectory.setText(mainDirectoryPath);
            tfImageCacheDirectory.setText(mainDirectoryPath + "\\imagecache");
            tfDatabaseCacheFile.setText(mainDirectoryPath + "\\databasecache.json");
        });
        btnImageCacheDirectory.setOnAction(event -> {
            String imageCacheDirectoryPath = new DirectoryChooserWindow(this, "Choose Image Cache Directory Path", "C:\\").getResultValue();
            tfImageCacheDirectory.setText(imageCacheDirectoryPath);
        });
        btnDatabaseCacheFile.setOnAction(event -> {
            String databaseCacheFilePath = new DirectoryChooserWindow(this, "Choose Database Cache File Path", "C:\\").getResultValue();
            tfDatabaseCacheFile.setText(databaseCacheFilePath);
        });

        buttonOK.setOnAction(event -> {
            buttonOK.setDisable(true);
            Settings.setMainDirectoryPath(tfMainDirectory.getText());
            Settings.setImageCacheDirectoryPath(tfImageCacheDirectory.getText());
            Settings.setDatabaseCacheFilePath(tfDatabaseCacheFile.getText());
            Settings.writeToFile();
            Main.getLoadingWindow().close();
            Main.setStage(new LoadingWindow());
        });

        ChangeListener textFieldChangeListener = (observable, oldValue, newValue) -> {
            String mainDirectoryPath = tfMainDirectory.getText();
            String imageCacheDirectoryPath = tfImageCacheDirectory.getText();
            String databaseCacheFilePath = tfDatabaseCacheFile.getText();
            if (mainDirectoryPath.length() > 1 && mainDirectoryPath.charAt(1) == ':' && mainDirectoryPath.charAt(2) == '\\')
                if (imageCacheDirectoryPath.length() > 1 && imageCacheDirectoryPath.charAt(1) == ':' && imageCacheDirectoryPath.charAt(2) == '\\')
                    if (databaseCacheFilePath.length() > 1 && databaseCacheFilePath.charAt(1) == ':' && databaseCacheFilePath.charAt(2) == '\\')
                        buttonOK.setDisable(false);
                    else
                        buttonOK.setDisable(true);
        };

        tfMainDirectory.textProperty().addListener(textFieldChangeListener);
        tfImageCacheDirectory.textProperty().addListener(textFieldChangeListener);
        tfDatabaseCacheFile.textProperty().addListener(textFieldChangeListener);
    }
}
