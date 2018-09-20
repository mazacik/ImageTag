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
import project.settings.Settings;

public class IntroWindow extends Stage {
    /* components */
    private final GridPane introPane = new GridPane();
    private final Scene sceneIntro = new Scene(introPane);

    private final Label lblSource = new Label("Path - Source:");
    private final Label lblCache = new Label("Path - Cache:");
    private final Label lblData = new Label("Path - Database:");
    private final TextField tfSource = new TextField();
    private final TextField tfCache = new TextField();
    private final TextField tfData = new TextField();
    private final Button btnSource = new Button("...");
    private final Button btnCache = new Button("...");
    private final Button btnData = new Button("...");
    private final Button btnOk = new Button("OK");

    /* constructors */
    public IntroWindow() {
        initializeComponents();
        initializeInstance();
    }

    /* initialize */
    private void initializeComponents() {
        setListeners();
        addComponentsToGrid();

        introPane.setPadding(new Insets(10));
        introPane.setHgap(5);
        introPane.setVgap(3);

        tfSource.setPrefWidth(300);
        btnSource.setPrefWidth(35);
        btnCache.setPrefWidth(35);
        btnData.setPrefWidth(35);
        btnOk.setPrefWidth(35);

        if (Settings.readFromFile(getClass())) {
            tfSource.setText(Settings.getPath_source());
            tfCache.setText(Settings.getPath_cache());
            tfData.setText(Settings.getPath_data());
            btnOk.requestFocus();
        } else {
            btnOk.setDisable(true);
            btnSource.requestFocus();
        }
    }
    private void initializeInstance() {
        setTitle("JavaExplorer Settings");
        setScene(sceneIntro);
        setResizable(false);
        centerOnScreen();
        show();
    }
    private void addComponentsToGrid() {
        introPane.add(lblSource, 0, 0);
        introPane.add(lblCache, 0, 1);
        introPane.add(lblData, 0, 2);
        introPane.add(tfSource, 1, 0);
        introPane.add(tfCache, 1, 1);
        introPane.add(tfData, 1, 2);
        introPane.add(btnSource, 2, 0);
        introPane.add(btnCache, 2, 1);
        introPane.add(btnData, 2, 2);
        introPane.add(btnOk, 2, 4);
    }
    private void setListeners() {
        btnSource.setOnAction(event -> {
            String sourcePath = new DirectoryChooserWindow(this, "Choose Source Directory Path", "C:\\").getResultValue();
            if (!sourcePath.isEmpty()) {
                int length = sourcePath.length() - 1;
                if (sourcePath.length() > 4 && (sourcePath.charAt(length) != '\\' || sourcePath.charAt(length) != '/')) {
                    sourcePath += "\\";
                }
                tfSource.setText(sourcePath);
                tfCache.setText(sourcePath + "cache");
                tfData.setText(sourcePath + "database");
            }
        });
        btnCache.setOnAction(event -> {
            String cachePath = new DirectoryChooserWindow(this, "Choose Image Cache Directory Path", "C:\\").getResultValue();
            tfCache.setText(cachePath);
        });
        btnData.setOnAction(event -> {
            String dataPath = new DirectoryChooserWindow(this, "Choose Database Directory Path", "C:\\").getResultValue();
            tfData.setText(dataPath);
        });

        btnOk.setOnAction(event -> {
            btnOk.setDisable(true);
            Settings.setPath_source(tfSource.getText());
            Settings.setPath_cache(tfCache.getText());
            Settings.setPath_data(tfData.getText());
            Settings.writeToFile();
            Main.getStage().close();
            Main.setStage(new LoadingWindow());
        });

        ChangeListener textFieldChangeListener = (observable, oldValue, newValue) -> {
            String sourcePath = tfSource.getText();
            String cachePath = tfCache.getText();
            String dataPath = tfData.getText();
            if (sourcePath.length() > 3 && sourcePath.charAt(1) == ':' && sourcePath.charAt(2) == '\\')
                if (!sourcePath.equals(cachePath) && cachePath.length() > 4 && cachePath.charAt(1) == ':' && cachePath.charAt(2) == '\\')
                    if (!sourcePath.equals(dataPath) && dataPath.length() > 4 && dataPath.charAt(1) == ':' && dataPath.charAt(2) == '\\')
                        btnOk.setDisable(false);
                    else
                        btnOk.setDisable(true);
        };

        tfSource.textProperty().addListener(textFieldChangeListener);
        tfCache.textProperty().addListener(textFieldChangeListener);
        tfData.textProperty().addListener(textFieldChangeListener);
    }
}
