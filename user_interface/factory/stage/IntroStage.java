package user_interface.factory.stage;

import database.object.DataLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.ColorModeSwitch;
import user_interface.factory.node.IntroWindowCell;
import user_interface.factory.node.TitleBar;
import user_interface.factory.util.enums.ColorType;

import java.io.File;
import java.util.ArrayList;

public class IntroStage extends Stage implements InstanceRepo {
    public IntroStage() {
        VBox vBoxL = NodeFactory.getVBox(ColorType.ALT);
        vBoxL.setPrefWidth(250);
        vBoxL.setPadding(new Insets(5));
        new ArrayList<>(coreSettings.getRecentDirectoriesList()).forEach(item -> {
            if (new File(item).exists()) {
                IntroWindowCell introWindowCell = NodeFactory.getIntroWindowCell(item);
                introWindowCell.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (event.getPickResult().getIntersectedNode().getParent().equals(introWindowCell.getNodeRemove())) {
                            coreSettings.getRecentDirectoriesList().remove(introWindowCell.getPath());
                            vBoxL.getChildren().remove(introWindowCell);
                        } else {
                            startLoading(introWindowCell);
                        }
                    }
                });
                vBoxL.getChildren().add(introWindowCell);
            } else {
                logger.debug(this, item + " not found, removing it from recent directory list");
                coreSettings.getRecentDirectoriesList().remove(item);
            }
        });

        Label btnChoose = NodeFactory.getLabel("Choose a Directory", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.ALT);
        btnChoose.setFont(CommonUtil.getFont());
        btnChoose.setFocusTraversable(false);
        btnChoose.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.directoryChooser();
            }
        });

        VBox vBoxR = NodeFactory.getVBox(ColorType.DEF);
        vBoxR.getChildren().add(btnChoose);
        vBoxR.getChildren().add(new ColorModeSwitch());
        vBoxR.setSpacing(10);
        vBoxR.setPrefWidth(500);
        vBoxR.setAlignment(Pos.CENTER);

        HBox hBoxGrowHelper = NodeFactory.getHBox(ColorType.DEF, vBoxL, vBoxR);
        VBox.setVgrow(hBoxGrowHelper, Priority.ALWAYS);

        VBox vBoxMain = NodeFactory.getVBox(ColorType.DEF);
        vBoxMain.getChildren().add(new TitleBar(this, "Welcome"));
        vBoxMain.getChildren().add(hBoxGrowHelper);

        Scene introScene = new Scene(vBoxMain);
        introScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!coreSettings.getRecentDirectoriesList().isEmpty()) {
                    this.startLoading((IntroWindowCell) vBoxL.getChildren().get(0));
                } else {
                    this.directoryChooser();
                }
            }
        });

        CommonUtil.updateNodeProperties();

        this.setScene(introScene);
        this.setOnShowing(event -> {
            if (vBoxL.getChildren().isEmpty()) {
                vBoxL.getChildren().add(NodeFactory.getIntroWindowCell("Stage Height Helper"));
            }
        });
        this.setOnShown(event -> {
            double newHeight;
            if (coreSettings.getRecentDirectoriesList().size() > 0) {
                newHeight = this.getHeight() + ((6 - vBoxL.getChildren().size()) * ((IntroWindowCell) vBoxL.getChildren().get(0)).getHeight());
            } else {
                newHeight = 6 * ((IntroWindowCell) vBoxL.getChildren().get(0)).getHeight();
                vBoxL.getChildren().clear();
            }
            this.setHeight(newHeight);
            this.centerOnScreen();
        });
        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);
        this.setOnCloseRequest(event -> {
            userSettings.writeToDisk();
            logger.debug(this, "application exit");
        });
        this.show();
        logger.debug(this, "waiting for directory");
    }

    private void directoryChooser() {
        String sourcePath = new DirectoryChooserStage(this, "Choose a Directory", "C:\\").getResultValue();
        if (!sourcePath.isEmpty()) {
            char lastchar = sourcePath.charAt(sourcePath.length() - 1);
            if (sourcePath.length() > 3 && (lastchar != '\\' || lastchar != '/')) {
                sourcePath += "\\";
            }
            coreSettings.setCurrentDirectory(sourcePath);
            new DataLoader().start();
            this.close();
        }
    }
    private void startLoading(IntroWindowCell introWindowCell) {
        coreSettings.setCurrentDirectory(introWindowCell.getPath());
        userSettings.writeToDisk();
        new DataLoader().start();
        this.close();
    }
}
