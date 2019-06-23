package user_interface.scene;

import database.loader.Project;
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
import javafx.stage.FileChooser;
import lifecycle.InstanceManager;
import lifecycle.LifeCycleManager;
import user_interface.nodes.NodeUtil;
import user_interface.nodes.base.TextNode;
import user_interface.nodes.node.ColorModeNode;
import user_interface.nodes.node.IntroWindowCell;
import user_interface.style.SizeUtil;
import user_interface.style.enums.ColorType;

import java.io.File;
import java.util.ArrayList;

public class IntroScene {
    private final Scene introScene;

    public IntroScene() {
        introScene = create();
    }

    private Scene create() {
        VBox vBoxRecentProjects = NodeUtil.getVBox(ColorType.ALT, ColorType.ALT);
        vBoxRecentProjects.setMinWidth(300);
        vBoxRecentProjects.setPadding(new Insets(5));

        VBox vBoxStartMenu = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
        vBoxStartMenu.setSpacing(10);
        vBoxStartMenu.setMinWidth(350);
        vBoxStartMenu.setAlignment(Pos.CENTER);
        vBoxStartMenu.setPrefWidth(SizeUtil.getUsableScreenWidth());

        HBox hBoxLayoutHelper = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, vBoxRecentProjects, vBoxStartMenu);
        VBox.setVgrow(hBoxLayoutHelper, Priority.ALWAYS);

        TextNode btnOpenProject = new TextNode("Open Project", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.ALT);
        btnOpenProject.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Project");
				fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
				File file = fileChooser.showOpenDialog(InstanceManager.getMainStage());
				String projectFilePath = file.getAbsolutePath();

                if (!projectFilePath.isEmpty()) {
                    InstanceManager.getSettings().addProjectPath(projectFilePath);
                    Project project = Project.readFromDisk(projectFilePath);
                    LifeCycleManager.startLoading(project);
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
		vBoxStartMenu.getChildren().add(new ColorModeNode());

        if (InstanceManager.getSettings().getRecentProjects().size() == 0) {
            vBoxRecentProjects.getChildren().add(new TextNode("No recent directories", ColorType.ALT, ColorType.DEF));
        } else {
            new ArrayList<>(InstanceManager.getSettings().getRecentProjects()).forEach(recentProject -> {
                File projectFile = new File(recentProject);
                if (projectFile.exists()) {
                    Project project = Project.readFromDisk(recentProject);

                    IntroWindowCell introWindowCell = NodeUtil.getIntroWindowCell(recentProject, project.getSourceDirectoryList().get(0));
                    introWindowCell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            if (event.getPickResult().getIntersectedNode().getParent().equals(introWindowCell.getNodeRemove())) {
                                InstanceManager.getSettings().getRecentProjects().remove(introWindowCell.getWorkingDirectory());
                                vBoxRecentProjects.getChildren().remove(introWindowCell);
                            } else {
                                LifeCycleManager.startLoading(project);
                            }
                        }
                    });
                    vBoxRecentProjects.getChildren().add(introWindowCell);
                } else {
                    InstanceManager.getLogger().debug(recentProject + " not found, removing it from recent projects");
                    InstanceManager.getSettings().getRecentProjects().remove(recentProject);
                }
            });
        }

        Scene introScene = new Scene(NodeUtil.getVBox(ColorType.DEF, ColorType.DEF, hBoxLayoutHelper));
        introScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!InstanceManager.getSettings().getRecentProjects().isEmpty()) {
                    LifeCycleManager.startLoading(Project.readFromDisk(InstanceManager.getSettings().getRecentProjects().get(0)));
                } else {
                    SceneUtil.showProjectScene();
                }
            }
        });

        return introScene;
    }
    public void show() {
        InstanceManager.getMainStage().setScene(introScene);
    }
}
