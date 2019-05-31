package user_interface.scene;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import loader.LoaderUtil;
import loader.Project;
import system.CommonUtil;
import system.Instances;
import user_interface.factory.NodeUtil;
import user_interface.factory.base.TextNode;
import user_interface.factory.node.ColorModeSwitch;
import user_interface.factory.node.IntroWindowCell;
import user_interface.factory.stage.FileChooserStage;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.utils.SizeUtil;

import java.io.File;
import java.util.ArrayList;

public class IntroScene implements Instances {
    private final Scene introScene;

    IntroScene() {
        introScene = create();
    }

    private Scene create() {
        VBox vBoxL = NodeUtil.getVBox(ColorType.ALT);
        vBoxL.setMinWidth(300);
        vBoxL.setPadding(new Insets(5));
        vBoxL.setAlignment(Pos.CENTER);

        if (settings.getRecentProjects().size() == 0) {
            vBoxL.getChildren().add(new TextNode("No recent directories", ColorType.ALT, ColorType.DEF));
        } else {
            new ArrayList<>(settings.getRecentProjects()).forEach(recentProject -> {
                File projectFile = new File(recentProject);
                if (projectFile.exists()) {
                    Project project = Project.readFromDisk(recentProject);

                    IntroWindowCell introWindowCell = NodeUtil.getIntroWindowCell(recentProject, project.getSourceDirectoryList().get(0));
                    introWindowCell.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            if (event.getPickResult().getIntersectedNode().getParent().equals(introWindowCell.getNodeRemove())) {
                                settings.getRecentProjects().remove(introWindowCell.getWorkingDirectory());
                                vBoxL.getChildren().remove(introWindowCell);
                            } else {
                                LoaderUtil.startLoading(introWindowCell.getWorkingDirectory());
                            }
                        }
                    });
                    vBoxL.getChildren().add(introWindowCell);
                } else {
                    logger.debug(this, recentProject + " not found, removing it from recent directory list");
                    settings.getRecentProjects().remove(recentProject);
                }
            });
        }

        TextNode btnNewProject = new TextNode("Create a New Project", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.ALT);
        btnNewProject.setFocusTraversable(false);
        btnNewProject.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                SceneUtil.showProjectScene();
            }
        });
        TextNode btnOpenProject = new TextNode("Open Project", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.ALT);
        btnOpenProject.setFocusTraversable(false);
        btnOpenProject.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                String projectFilePath = new FileChooserStage(mainStage, "Open Project", System.getProperty("user.dir")).getResultValue();
                if (!projectFilePath.isEmpty()) {
                    settings.addProjectPath(projectFilePath);
                    Project project = Project.readFromDisk(projectFilePath);
                    LoaderUtil.startLoading(project.getSourceDirectoryList().get(0));
                }
            }
        });

        VBox vBoxR = NodeUtil.getVBox(ColorType.DEF);
        vBoxR.getChildren().add(btnOpenProject);
        vBoxR.getChildren().add(btnNewProject);
        vBoxR.getChildren().add(new ColorModeSwitch());
        vBoxR.setSpacing(10);
        vBoxR.setMinWidth(350);
        vBoxR.setAlignment(Pos.CENTER);
        vBoxR.setPrefWidth(SizeUtil.getUsableScreenWidth());

        HBox hBoxGrowHelper = NodeUtil.getHBox(ColorType.DEF, vBoxL, vBoxR);
        VBox.setVgrow(hBoxGrowHelper, Priority.ALWAYS);

        VBox vBoxMain = NodeUtil.getVBox(ColorType.DEF);
        vBoxMain.setAlignment(Pos.CENTER);
        mainStage.setTitle("Welcome");
        Scene introScene = new Scene(vBoxMain);
        vBoxMain.getChildren().add(hBoxGrowHelper);
        introScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!settings.getRecentProjects().isEmpty()) {
                    LoaderUtil.startLoading((((IntroWindowCell) vBoxL.getChildren().get(0)).getWorkingDirectory()));
                } else {
                    SceneUtil.showProjectScene();
                }
            }
        });

        CommonUtil.updateNodeProperties(introScene);
        return introScene;
    }
    void show() {
        mainStage.setOpacity(0);
        mainStage.setScene(introScene);
        mainStage.show();

        double width = SizeUtil.getUsableScreenWidth() / 2.5;
        double height = SizeUtil.getUsableScreenHeight() / 2;
        mainStage.setWidth(width);
        mainStage.setHeight(height);
        mainStage.setMinWidth(width);
        mainStage.setMinHeight(height);
        mainStage.centerOnScreen();

        Platform.runLater(() -> mainStage.setOpacity(1));

        logger.debug(this, "waiting for directory");
    }
}
