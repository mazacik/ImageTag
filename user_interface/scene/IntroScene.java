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
import main.InstanceManager;
import main.LifeCycleManager;
import settings.Settings;
import user_interface.nodes.NodeUtil;
import user_interface.nodes.base.TextNode;
import user_interface.nodes.node.ColorModeSwitchNode;
import user_interface.nodes.node.IntroStageNode;
import user_interface.stage.StageUtil;
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
		Settings settings = InstanceManager.getSettings();
		
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
				if (file != null) {
					String projectFilePath = file.getAbsolutePath();
					settings.addProjectPath(projectFilePath);
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
		vBoxStartMenu.getChildren().add(new ColorModeSwitchNode());
		
		ArrayList<String> recentProjects = settings.getRecentProjects();
		if (recentProjects.size() != 0) {
			for (String recentProject : new ArrayList<>(recentProjects)) {
				File projectFile = new File(recentProject);
				if (projectFile.exists()) {
					Project project = Project.readFromDisk(recentProject);
					IntroStageNode introStageNode = NodeUtil.getIntroStageNode(recentProject, project.getSourceDirectoryList().get(0));
					introStageNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
						if (event.getButton() == MouseButton.PRIMARY) {
							if (event.getPickResult().getIntersectedNode().getParent().equals(introStageNode.getNodeRemove())) {
								recentProjects.remove(introStageNode.getWorkingDirectory());
								vBoxRecentProjects.getChildren().remove(introStageNode);
							} else {
								if (new File(project.getSourceDirectoryList().get(0)).exists()) {
									settings.addProjectPath(recentProject);
									LifeCycleManager.startLoading(project);
								} else {
									StageUtil.showStageError("The working directory of this project could not be found.");
								}
							}
						}
					});
					vBoxRecentProjects.getChildren().add(introStageNode);
				}
			}
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
		InstanceManager.getMainStage().show();
	}
}
