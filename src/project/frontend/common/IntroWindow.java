package project.frontend.common;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.backend.Backend;
import project.backend.common.Settings;
import project.frontend.Frontend;
//todo: rework formatting
public class IntroWindow {
    private Stage introStage;
    private GridPane introPane = new GridPane();
    private Scene introScene = new Scene(introPane);

    private Label mainDirectoryLabel = new Label("Main Directory Path:");
    private Label imageCacheDirectoryLabel = new Label("Image Cache Directory Path:");
    private Label databaseCacheFileLabel = new Label("Database Cache File Path:");
    private TextField mainDirectoryTextField = new TextField();
    private TextField imageCacheDirectoryTextField = new TextField();
    private TextField databaseCacheFileTextField = new TextField();
    private Button mainDirectoryButton = new Button("...");
    private Button imageCacheDirectoryButton = new Button("...");
    private Button databaseCacheFileButton = new Button("...");

    private Button okButton = new Button("OK");


    public IntroWindow(Stage primaryStage) {
        introStage = primaryStage;
        introStage.setTitle("JavaExplorer Settings");
        introStage.setScene(introScene);
        okButton.setDisable(true);

        setListeners();

        mainDirectoryTextField.setPrefWidth(300);
        mainDirectoryButton.setPrefWidth(35);
        imageCacheDirectoryButton.setPrefWidth(35);
        databaseCacheFileButton.setPrefWidth(35);
        okButton.setPrefWidth(35);

        introPane.add(mainDirectoryLabel, 0, 0);
        introPane.add(imageCacheDirectoryLabel, 0, 1);
        introPane.add(databaseCacheFileLabel, 0, 2);
        introPane.add(mainDirectoryTextField, 1, 0);
        introPane.add(imageCacheDirectoryTextField, 1, 1);
        introPane.add(databaseCacheFileTextField, 1, 2);
        introPane.add(mainDirectoryButton, 2, 0);
        introPane.add(imageCacheDirectoryButton, 2, 1);
        introPane.add(databaseCacheFileButton, 2, 2);
        introPane.add(okButton, 2, 4);

        introPane.setPadding(new Insets(10));
        introPane.setHgap(5);
        introPane.setVgap(3);
        introStage.show();
        introStage.centerOnScreen();
        introStage.setResizable(false);
    }

    private void startLoading() {
        Frontend.initialize();
        Backend.initialize();
        introStage.close();
    }

    private void setListeners() {
        mainDirectoryButton.setOnAction(event -> {
            String mainDirectoryPath = new DirectoryChooserWindow("Choose Main Directory Path", "C:\\").getResultValue();
            mainDirectoryTextField.setText(mainDirectoryPath);
            imageCacheDirectoryTextField.setText(mainDirectoryPath + "\\imagecache");
            databaseCacheFileTextField.setText(mainDirectoryPath + "\\database.cache");
        });
        imageCacheDirectoryButton.setOnAction(event -> {
            String imageCacheDirectoryPath = new DirectoryChooserWindow("Choose Image Cache Directory Path", "C:\\").getResultValue();
            imageCacheDirectoryTextField.setText(imageCacheDirectoryPath);
        });
        databaseCacheFileButton.setOnAction(event -> {
            String databaseCacheFilePath = new DirectoryChooserWindow("Choose Database Cache File Path", "C:\\").getResultValue();
            databaseCacheFileTextField.setText(databaseCacheFilePath);
        });

        okButton.setOnAction(event -> {
            Settings.setMainDirectoryPath(mainDirectoryTextField.getText());
            Settings.setImageCacheDirectoryPath(imageCacheDirectoryTextField.getText());
            Settings.setDatabaseCacheFilePath(databaseCacheFileTextField.getText());
            startLoading();
        });

        ChangeListener textFieldChangeListener = (observable, oldValue, newValue) -> {
            String mainDirectoryPath = mainDirectoryTextField.getText();
            String imageCacheDirectoryPath = imageCacheDirectoryTextField.getText();
            String databaseCacheFilePath = databaseCacheFileTextField.getText();
            if (mainDirectoryPath.length() > 1 && mainDirectoryPath.charAt(1) == ':' && mainDirectoryPath.charAt(2) == '\\')
                if (imageCacheDirectoryPath.length() > 1 && imageCacheDirectoryPath.charAt(1) == ':' && imageCacheDirectoryPath.charAt(2) == '\\')
                    if (databaseCacheFilePath.length() > 1 && databaseCacheFilePath.charAt(1) == ':' && databaseCacheFilePath.charAt(2) == '\\')
                        okButton.setDisable(false);
                    else
                        okButton.setDisable(true);
        };
        mainDirectoryTextField.textProperty().addListener(textFieldChangeListener);
        imageCacheDirectoryTextField.textProperty().addListener(textFieldChangeListener);
        databaseCacheFileTextField.textProperty().addListener(textFieldChangeListener);
    }
}
