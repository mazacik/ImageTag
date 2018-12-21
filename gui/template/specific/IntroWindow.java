package gui.template.specific;

import database.object.DataLoader;
import gui.template.generic.DirectoryChooserWindow;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import utils.MainUtil;

public class IntroWindow extends Stage implements MainUtil {
    private final GridPane introPane = new GridPane();
    private final Scene sceneIntro = new Scene(introPane);

    private final Label label = new Label("Source:");
    private final ComboBox<String> comboBox = new ComboBox<>();
    private final Button btnDots = new Button("...");
    private final Button btnOk = new Button("OK");

    public IntroWindow() {
        setDefaultValuesChildren();
        setDefaultValues();
        logger.debug(this, "waiting for input");
    }

    private void setDefaultValuesChildren() {
        introPane.add(label, 0, 0);
        introPane.add(comboBox, 1, 0);
        introPane.add(btnDots, 2, 0);
        introPane.add(btnOk, 2, 2);

        introPane.setPadding(new Insets(10));
        introPane.setHgap(5);
        introPane.setVgap(3);

        comboBox.setPrefWidth(300);
        btnDots.setPrefWidth(35);
        btnOk.setPrefWidth(35);

        if (!settings.getRecentDirectoriesList().isEmpty()) {
            this.refreshComboBoxItems();
            btnOk.requestFocus();
        } else {
            btnOk.setDisable(true);
            btnDots.requestFocus();
        }

        setBtnDotsListener();
        setBtnOkListener();
    }
    private void setDefaultValues() {
        setTitle("ImageTag");
        setScene(sceneIntro);
        setResizable(false);
        centerOnScreen();
        this.setOnCloseRequest(event -> logger.debug(this, "application exit"));
        show();
    }

    private void setBtnDotsListener() {
        btnDots.setOnAction(event -> {
            String sourcePath = new DirectoryChooserWindow(this, "Choose Source Directory Path", "C:\\").getResultValue();
            if (!sourcePath.isEmpty()) {
                int length = sourcePath.length() - 1;
                if (sourcePath.length() > 4 && (sourcePath.charAt(length) != '\\' || sourcePath.charAt(length) != '/')) {
                    sourcePath += "\\";
                }
                settings.setCurrentDirectory(sourcePath);
                this.refreshComboBoxItems();
                btnOk.setDisable(false);
                btnOk.requestFocus();
            }
        });
    }
    private void setBtnOkListener() {
        btnOk.setOnAction(event -> {
            settings.setCurrentDirectory(comboBox.getValue());
            btnOk.setDisable(true);
            new DataLoader().start();
            this.close();
        });
    }

    private void refreshComboBoxItems() {
        comboBox.getItems().setAll(settings.getRecentDirectoriesList());
        comboBox.getSelectionModel().selectFirst();
    }
}
