package userinterface.template.specific;

import database.object.DataLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import settings.SettingsNamespace;
import userinterface.BackgroundEnum;
import userinterface.template.generic.DirectoryChooserWindow;
import utils.MainUtil;

public class IntroWindow extends Stage implements MainUtil {
    private final VBox vboxL = new VBox();
    private final VBox vBoxR = new VBox();
    private final Button btnChoose = new Button("Choose a Directory");

    public IntroWindow() {
        setDefaultValuesChildren();
        setDefaultValues();
        logger.debug(this, "waiting for directory");
    }

    private void setDefaultValuesChildren() {
        settings.getRecentDirectoriesList().forEach(item -> vboxL.getChildren().add(new IntroWindowListCell(item)));
        vboxL.getChildren().forEach(node -> {
            if (node instanceof IntroWindowListCell) {
                node.setOnMouseClicked(event -> {
                    if (event.getPickResult().getIntersectedNode().getParent().equals(((IntroWindowListCell) node).getRemoveLabel())) {
                        settings.getRecentDirectoriesList().remove(((IntroWindowListCell) node).getPath());
                        vboxL.getChildren().remove(node);
                    } else {
                        settings.setCurrentDirectory(((IntroWindowListCell) node).getPath());
                        new DataLoader().start();
                        this.close();
                    }
                });
            }
        });
        vboxL.setPadding(new Insets(5));
        vboxL.setPrefWidth(200);
        vboxL.setBackground(BackgroundEnum.NIGHT_2.getValue());

        vBoxR.getChildren().add(btnChoose);
        vBoxR.setSpacing(10);
        vBoxR.setPrefWidth(500);
        vBoxR.setAlignment(Pos.CENTER);
        vBoxR.setBackground(BackgroundEnum.NIGHT_1.getValue());

        btnChoose.setBackground(BackgroundEnum.NIGHT_1.getValue());
        btnChoose.setFont(new Font(14));
        btnChoose.setTextFill(Color.LIGHTGRAY);
        btnChoose.setOnMouseEntered(event -> btnChoose.setTextFill(Color.ORANGE));
        btnChoose.setOnMouseExited(event -> btnChoose.setTextFill(Color.LIGHTGRAY));
        btnChoose.requestFocus();

        this.setBtnChooseListener();
    }
    private void setDefaultValues() {
        this.setTitle("Welcome to " + SettingsNamespace.APPNAME.getValue());

        this.setHeight(649); //todo dynamic calculation?
        this.setScene(new Scene(new HBox(vboxL, vBoxR)));
        this.setResizable(false);
        this.centerOnScreen();
        this.setOnCloseRequest(event -> logger.debug(this, "application exit"));
        this.show();
    }

    private void setBtnChooseListener() {
        btnChoose.setOnAction(event -> {
            String sourcePath = new DirectoryChooserWindow(this, "Choose Source Directory Path", "C:\\").getResultValue();
            if (!sourcePath.isEmpty()) {
                char lastchar = sourcePath.charAt(sourcePath.length() - 1);
                if (sourcePath.length() > 3 && (lastchar != '\\' || lastchar != '/')) {
                    sourcePath += "\\";
                }
                settings.setCurrentDirectory(sourcePath);
                new DataLoader().start();
                this.close();
            }
        });
    }
}
