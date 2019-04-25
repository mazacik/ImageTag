package user_interface.scene;

import database.loader.LoaderThread;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.ColorModeSwitch;
import user_interface.factory.node.IntroWindowCell;
import user_interface.factory.stage.DirectoryChooserStage;
import user_interface.factory.util.enums.ColorType;

import java.io.File;
import java.util.ArrayList;

public class IntroScene implements InstanceRepo {
    private final Scene introScene;

    IntroScene() {
        introScene = create();
    }

    private Scene create() {
        VBox vBoxL = NodeFactory.getVBox(ColorType.ALT);
        vBoxL.setMinWidth(300);
        vBoxL.setPadding(new Insets(5));
        vBoxL.setAlignment(Pos.CENTER);

        if (settings.getRecentDirList().size() == 0) {
            vBoxL.getChildren().add(NodeFactory.getLabel("No recent directories", ColorType.ALT, ColorType.DEF));
        } else {
            new ArrayList<>(settings.getRecentDirList()).forEach(item -> {
                if (new File(item).exists()) {
                    IntroWindowCell introWindowCell = NodeFactory.getIntroWindowCell(item);
                    introWindowCell.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            if (event.getPickResult().getIntersectedNode().getParent().equals(introWindowCell.getNodeRemove())) {
                                settings.getRecentDirList().remove(introWindowCell.getPath());
                                vBoxL.getChildren().remove(introWindowCell);
                            } else {
                                startLoading(introWindowCell.getPath());
                            }
                        }
                    });
                    vBoxL.getChildren().add(introWindowCell);
                } else {
                    logger.debug(this, item + " not found, removing it from recent directory list");
                    settings.getRecentDirList().remove(item);
                }
            });
        }

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
        vBoxR.setMinWidth(350);
        vBoxR.setAlignment(Pos.CENTER);
        vBoxR.setPrefWidth(SceneUtil.getUsableScreenWidth());

        HBox hBoxGrowHelper = NodeFactory.getHBox(ColorType.DEF, vBoxL, vBoxR);
        VBox.setVgrow(hBoxGrowHelper, Priority.ALWAYS);

        VBox vBoxMain = NodeFactory.getVBox(ColorType.DEF);
        vBoxMain.setAlignment(Pos.CENTER);
        mainStage.setTitle("Welcome");
        Scene introScene = new Scene(vBoxMain);
        vBoxMain.getChildren().add(hBoxGrowHelper);
        introScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!settings.getRecentDirList().isEmpty()) {
                    this.startLoading(((IntroWindowCell) vBoxL.getChildren().get(0)).getPath());
                } else {
                    this.directoryChooser();
                }
            }
        });

        CommonUtil.updateNodeProperties(introScene);
        return introScene;
    }
    void show() {
        double width = SceneUtil.getUsableScreenWidth() / 2.5;
        double height = SceneUtil.getUsableScreenHeight() / 2;
        mainStage.setWidth(width);
        mainStage.setHeight(height);
        mainStage.setMinWidth(width);
        mainStage.setMinHeight(height);

        mainStage.setScene(introScene);
        mainStage.centerOnScreen();
        SceneUtil.showMainStage();

        logger.debug(this, "waiting for directory");

        SceneUtil.createMainScene();
    }

    private void directoryChooser() {
        String sourcePath = new DirectoryChooserStage(mainStage, "Choose a Directory", "C:\\").getResultValue();
        if (!sourcePath.isEmpty()) {
            char lastchar = sourcePath.charAt(sourcePath.length() - 1);
            if (sourcePath.length() > 3 && (lastchar != '\\' || lastchar != '/')) {
                sourcePath += "\\";
            }
            startLoading(sourcePath);
        }
    }
    private void startLoading(String sourcePath) {
        settings.setCurrentDirectory(sourcePath);
        settings.writeToDisk();
        new LoaderThread().start();
        SceneUtil.createMainScene();
        SceneUtil.showMainScene();
    }
}