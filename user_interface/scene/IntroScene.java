package user_interface.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
        VBox vBoxRecentProjects = NodeUtil.getVBox(ColorType.ALT);
        vBoxRecentProjects.setMinWidth(300);
        vBoxRecentProjects.setPadding(new Insets(5));

        VBox vBoxStartMenu = NodeUtil.getVBox(ColorType.DEF);
        vBoxStartMenu.setSpacing(10);
        vBoxStartMenu.setMinWidth(350);
        vBoxStartMenu.setAlignment(Pos.CENTER);
        vBoxStartMenu.setPrefWidth(SizeUtil.getUsableScreenWidth());

        HBox hBoxLayoutHelper = NodeUtil.getHBox(ColorType.DEF, vBoxRecentProjects, vBoxStartMenu);
        VBox.setVgrow(hBoxLayoutHelper, Priority.ALWAYS);

        TextNode btnOpenProject = new TextNode("Open Project", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.ALT);
        btnOpenProject.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                String projectFilePath = new FileChooserStage(mainStage, "Open Project", System.getProperty("user.dir")).getResultValue();
                if (!projectFilePath.isEmpty()) {
                    settings.addProjectPath(projectFilePath);
                    Project project = Project.readFromDisk(projectFilePath);
                    LoaderUtil.startLoading(project.getSourceDirectoryList().get(0));
                }
            }
        });

        TextNode btnNewProject = new TextNode("Create a New Project", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.ALT);
        btnNewProject.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                SceneUtil.showProjectScene();
            }
        });

        vBoxStartMenu.getChildren().add(btnOpenProject);
        vBoxStartMenu.getChildren().add(btnNewProject);
        vBoxStartMenu.getChildren().add(new ColorModeSwitch());

        if (settings.getRecentProjects().size() == 0) {
            vBoxRecentProjects.getChildren().add(new TextNode("No recent directories", ColorType.ALT, ColorType.DEF));
        } else {
            new ArrayList<>(settings.getRecentProjects()).forEach(recentProject -> {
                File projectFile = new File(recentProject);
                if (projectFile.exists()) {
                    Project project = Project.readFromDisk(recentProject);

                    IntroWindowCell introWindowCell = NodeUtil.getIntroWindowCell(recentProject, project.getSourceDirectoryList().get(0));
                    introWindowCell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            if (event.getPickResult().getIntersectedNode().getParent().equals(introWindowCell.getNodeRemove())) {
                                settings.getRecentProjects().remove(introWindowCell.getWorkingDirectory());
                                vBoxRecentProjects.getChildren().remove(introWindowCell);
                            } else {
                                LoaderUtil.startLoading(introWindowCell.getWorkingDirectory());
                            }
                        }
                    });
                    vBoxRecentProjects.getChildren().add(introWindowCell);
                } else {
                    logger.debug(this, recentProject + " not found, removing it from recent projects");
                    settings.getRecentProjects().remove(recentProject);
                }
            });
        }

        Scene introScene = new Scene(NodeUtil.getVBox(ColorType.DEF, hBoxLayoutHelper));
        introScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!settings.getRecentProjects().isEmpty()) {
                    IntroWindowCell cell = (IntroWindowCell) vBoxRecentProjects.getChildren().get(0);
                    LoaderUtil.startLoading(cell.getWorkingDirectory());
                } else {
                    SceneUtil.showProjectScene();
                }
            }
        });

        CommonUtil.updateNodeProperties(introScene);
        return introScene;
    }
    void show() {
        mainStage.setScene(introScene);
    }
}
