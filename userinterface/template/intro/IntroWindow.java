package userinterface.template.intro;

import database.object.DataLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import settings.SettingsNamespace;
import userinterface.template.generic.WindowDirectoryChooser;
import utils.CommonUtil;
import utils.InstanceRepo;

public class IntroWindow extends Stage implements InstanceRepo {
    private final VBox vboxL = new VBox();
    private final VBox vBoxR = new VBox();
    private final Button btnChoose = new Button("Choose a Directory");

    public IntroWindow() {
        setDefaultValuesChildren();
        setDefaultValues();
        logger.debug(this, "waiting for directory");
    }

    private void setDefaultValuesChildren() {
        coreSettings.getRecentDirectoriesList().forEach(item -> vboxL.getChildren().add(new IntroWindowListCell(item)));
        vboxL.getChildren().forEach(node -> {
            if (node instanceof IntroWindowListCell) {
                node.setOnMouseClicked(event -> {
                    if (event.getPickResult().getIntersectedNode().getParent().equals(((IntroWindowListCell) node).getRemoveLabel())) {
                        coreSettings.getRecentDirectoriesList().remove(((IntroWindowListCell) node).getPath());
                        vboxL.getChildren().remove(node);
                    } else {
                        startLoading((IntroWindowListCell) node);
                    }
                });
            }
        });
        vboxL.setPadding(new Insets(5));
        vboxL.setPrefWidth(200);
        vboxL.setBackground(CommonUtil.getBackgroundAlternative());

        vBoxR.getChildren().add(btnChoose);
        vBoxR.setSpacing(10);
        vBoxR.setPrefWidth(500);
        vBoxR.setAlignment(Pos.CENTER);
        vBoxR.setBackground(CommonUtil.getBackgroundDefault());

        btnChoose.setBackground(CommonUtil.getButtonBackgroundDefault());
        btnChoose.setFont(new Font(14));
        btnChoose.setTextFill(Color.LIGHTGRAY);
        btnChoose.setFocusTraversable(false);
        btnChoose.setOnMouseEntered(event -> {
            btnChoose.setCursor(Cursor.HAND);
            btnChoose.setTextFill(Color.ORANGE);
        });
        btnChoose.setOnMouseExited(event -> {
            btnChoose.setCursor(Cursor.DEFAULT);
            btnChoose.setTextFill(Color.LIGHTGRAY);
        });

        this.setBtnChooseListener();
    }
    private void setDefaultValues() {
        this.setTitle("Welcome to " + SettingsNamespace.APPNAME.getValue());

        this.setHeight(649); //todo dynamic calculation?
        this.setScene(new Scene(new HBox(vboxL, vBoxR)));
        this.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!coreSettings.getRecentDirectoriesList().isEmpty()) {
                    startLoading((IntroWindowListCell) vboxL.getChildren().get(0));
                } else {
                    btnChoose.fire();
                }
            }
        });
        this.setResizable(false);
        this.centerOnScreen();
        this.setOnCloseRequest(event -> logger.debug(this, "application exit"));
        this.show();
    }

    private void setBtnChooseListener() {
        btnChoose.setOnAction(event -> {
            String sourcePath = new WindowDirectoryChooser(this, "Choose a Directory", "C:\\").getResultValue();
            if (!sourcePath.isEmpty()) {
                char lastchar = sourcePath.charAt(sourcePath.length() - 1);
                if (sourcePath.length() > 3 && (lastchar != '\\' || lastchar != '/')) {
                    sourcePath += "\\";
                }
                coreSettings.setCurrentDirectory(sourcePath);
                new DataLoader().start();
                this.close();
            }
        });
    }

    private void startLoading(IntroWindowListCell introWindowListCell) {
        coreSettings.setCurrentDirectory(introWindowListCell.getPath());
        new DataLoader().start();
        this.close();
    }
}
