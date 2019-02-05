package userinterface.template.intro;

import database.object.DataLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import userinterface.template.generic.TitleBar;
import userinterface.template.generic.WindowDirectoryChooser;
import utils.CommonUtil;
import utils.InstanceRepo;

public class IntroWindow extends Stage implements InstanceRepo {
    public IntroWindow() {
        VBox vboxL = new VBox();
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

        Button btnChoose = new Button("Choose a Directory");
        btnChoose.setBackground(CommonUtil.getButtonBackgroundDefault());
        btnChoose.setFont(CommonUtil.getFont());
        btnChoose.setTextFill(CommonUtil.getTextColorDefault());
        btnChoose.setFocusTraversable(false);
        btnChoose.setOnMouseEntered(event -> {
            btnChoose.setCursor(Cursor.HAND);
            btnChoose.setTextFill(CommonUtil.getTextColorHighlight());
        });
        btnChoose.setOnMouseExited(event -> {
            btnChoose.setCursor(Cursor.DEFAULT);
            btnChoose.setTextFill(CommonUtil.getTextColorDefault());
        });
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

        VBox vBoxR = new VBox();
        vBoxR.getChildren().add(btnChoose);
        vBoxR.setSpacing(10);
        vBoxR.setPrefWidth(500);
        vBoxR.setAlignment(Pos.CENTER);
        vBoxR.setBackground(CommonUtil.getBackgroundDefault());

        HBox hBox = new HBox(vboxL, vBoxR);
        VBox.setVgrow(hBox, Priority.ALWAYS);

        VBox vBoxMain = new VBox();
        vBoxMain.getChildren().add(new TitleBar(this, "Welcome"));
        vBoxMain.getChildren().add(hBox);
        vBoxMain.setBackground(CommonUtil.getBackgroundDefault());

        this.setHeight(649); //todo dynamic calculation?
        this.setScene(new Scene(vBoxMain));
        this.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!coreSettings.getRecentDirectoriesList().isEmpty()) {
                    startLoading((IntroWindowListCell) vboxL.getChildren().get(0));
                } else {
                    btnChoose.fire();
                }
            }
        });
        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);
        this.centerOnScreen();
        this.setOnCloseRequest(event -> logger.debug(this, "application exit"));
        this.show();
        logger.debug(this, "waiting for directory");
    }

    private void startLoading(IntroWindowListCell introWindowListCell) {
        coreSettings.setCurrentDirectory(introWindowListCell.getPath());
        new DataLoader().start();
        this.close();
    }
}
