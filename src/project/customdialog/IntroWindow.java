package project.customdialog;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import project.Main;
import project.common.Settings;
import project.customdialog.generic.DirectoryChooserWindow;

public class IntroWindow extends Stage {
    /* components */
    private GridPane introPane = new GridPane();
    private Scene introScene = new Scene(introPane);

    private Label labelMainDirectory = new Label("Main Directory Path:");
    private Label labelImageCacheDirectory = new Label("Image Cache Directory Path:");
    private Label labelDatabaseCacheFile = new Label("Database Cache File Path:");
    private TextField textFieldMainDirectory = new TextField();
    private TextField textFieldImageCacheDirectory = new TextField();
    private TextField textFieldDatabaseCacheFile = new TextField();
    private Button buttonMainDirectory = new Button("...");
    private Button buttonImageCacheDirectory = new Button("...");
    private Button buttonDatabaseCacheFile = new Button("...");

    private Button buttonOK = new Button("OK");

    /* constructors */
    public IntroWindow() {
        /* stage */
        setTitle("JavaExplorer Settings");
        setScene(introScene);
        setResizable(false);

        /* pane */
        introPane.setPadding(new Insets(10));
        introPane.setHgap(5);
        introPane.setVgap(3);

        /* components */
        textFieldMainDirectory.setPrefWidth(300);
        buttonMainDirectory.setPrefWidth(35);
        buttonImageCacheDirectory.setPrefWidth(35);
        buttonDatabaseCacheFile.setPrefWidth(35);
        buttonOK.setPrefWidth(35);
        buttonOK.setDisable(true);

        setListeners();
        addComponentsToGrid();

        show();
        centerOnScreen();
        buttonMainDirectory.requestFocus();
    }

    /* private methods */
    private void addComponentsToGrid() {
        introPane.add(labelMainDirectory, 0, 0);
        introPane.add(labelImageCacheDirectory, 0, 1);
        introPane.add(labelDatabaseCacheFile, 0, 2);
        introPane.add(textFieldMainDirectory, 1, 0);
        introPane.add(textFieldImageCacheDirectory, 1, 1);
        introPane.add(textFieldDatabaseCacheFile, 1, 2);
        introPane.add(buttonMainDirectory, 2, 0);
        introPane.add(buttonImageCacheDirectory, 2, 1);
        introPane.add(buttonDatabaseCacheFile, 2, 2);
        introPane.add(buttonOK, 2, 4);
    }

    private void setListeners() {
        buttonMainDirectory.setOnAction(event -> {
            String mainDirectoryPath = new DirectoryChooserWindow(this, "Choose Main Directory Path", "C:\\").getResultValue();
            textFieldMainDirectory.setText(mainDirectoryPath);
            textFieldImageCacheDirectory.setText(mainDirectoryPath + "\\imagecache");
            textFieldDatabaseCacheFile.setText(mainDirectoryPath + "\\databasecache.json");
        });
        buttonImageCacheDirectory.setOnAction(event -> {
            String imageCacheDirectoryPath = new DirectoryChooserWindow(this, "Choose Image Cache Directory Path", "C:\\").getResultValue();
            textFieldImageCacheDirectory.setText(imageCacheDirectoryPath);
        });
        buttonDatabaseCacheFile.setOnAction(event -> {
            String databaseCacheFilePath = new DirectoryChooserWindow(this, "Choose Database Cache File Path", "C:\\").getResultValue();
            textFieldDatabaseCacheFile.setText(databaseCacheFilePath);
        });

        buttonOK.setOnAction(event -> {
            buttonOK.setDisable(true);
            Settings.setMainDirectoryPath(textFieldMainDirectory.getText());
            Settings.setImageCacheDirectoryPath(textFieldImageCacheDirectory.getText());
            Settings.setDatabaseCacheFilePath(textFieldDatabaseCacheFile.getText());
            Settings.writeToFile();
            Main.setLoadingWindow(new LoadingWindow());
            close();
        });

        ChangeListener textFieldChangeListener = (observable, oldValue, newValue) -> {
            String mainDirectoryPath = textFieldMainDirectory.getText();
            String imageCacheDirectoryPath = textFieldImageCacheDirectory.getText();
            String databaseCacheFilePath = textFieldDatabaseCacheFile.getText();
            if (mainDirectoryPath.length() > 1 && mainDirectoryPath.charAt(1) == ':' && mainDirectoryPath.charAt(2) == '\\')
                if (imageCacheDirectoryPath.length() > 1 && imageCacheDirectoryPath.charAt(1) == ':' && imageCacheDirectoryPath.charAt(2) == '\\')
                    if (databaseCacheFilePath.length() > 1 && databaseCacheFilePath.charAt(1) == ':' && databaseCacheFilePath.charAt(2) == '\\')
                        buttonOK.setDisable(false);
                    else
                        buttonOK.setDisable(true);
        };

        textFieldMainDirectory.textProperty().addListener(textFieldChangeListener);
        textFieldImageCacheDirectory.textProperty().addListener(textFieldChangeListener);
        textFieldDatabaseCacheFile.textProperty().addListener(textFieldChangeListener);
    }
}
