package application.gui.scene;

import application.database.loader.Project;
import application.gui.decorator.SizeUtil;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.NodeUtil;
import application.gui.nodes.custom.ColorModeSwitchNode;
import application.gui.nodes.custom.IntroStageNode;
import application.gui.nodes.custom.Tooltip;
import application.gui.nodes.simple.TextNode;
import application.gui.stage.StageUtil;
import application.main.Instances;
import application.main.LifeCycle;
import application.settings.Settings;
import application.settings.Stack;
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

import java.io.File;
import java.util.ArrayList;

public class IntroScene {
	private final Scene introScene;
	
	public IntroScene() {
		introScene = create();
	}
	
	private Scene create() {
		Settings settings = Instances.getSettings();
		
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
				File file = fileChooser.showOpenDialog(Instances.getMainStage());
				if (file != null) {
					String projectFilePath = file.getAbsolutePath();
					settings.getRecentProjects().push(projectFilePath);
					Project project = Project.readFromDisk(projectFilePath);
					LifeCycle.startLoading(project);
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
		
		Stack<String> recentProjects = settings.getRecentProjects();
		if (recentProjects.size() != 0) {
			for (String recentProject : new ArrayList<>(recentProjects)) {
				File projectFile = new File(recentProject);
				if (projectFile.exists()) {
					Project project = Project.readFromDisk(recentProject);
					IntroStageNode introStageNode = NodeUtil.getIntroStageNode(recentProject, project.getSourceDirectory());
					introStageNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
						if (event.getButton() == MouseButton.PRIMARY) {
							if (event.getPickResult().getIntersectedNode().getParent().equals(introStageNode.getNodeRemove())) {
								recentProjects.remove(introStageNode.getWorkingDirectory());
								vBoxRecentProjects.getChildren().remove(introStageNode);
							} else {
								if (new File(project.getSourceDirectory()).exists()) {
									settings.getRecentProjects().push(recentProject);
									LifeCycle.startLoading(project);
								} else {
									StageUtil.showStageError("The working directory of this project could not be found.");
								}
							}
						}
					});
					Tooltip t = new Tooltip(project.getSourceDirectory(), 200);
					Tooltip.install(introStageNode, t);
					vBoxRecentProjects.getChildren().add(introStageNode);
				}
			}
		}
		
		Scene introScene = new Scene(NodeUtil.getVBox(ColorType.DEF, ColorType.DEF, hBoxLayoutHelper));
		introScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				if (!Instances.getSettings().getRecentProjects().isEmpty()) {
					LifeCycle.startLoading(Project.readFromDisk(Instances.getSettings().getRecentProjects().get(0)));
				} else {
					SceneUtil.showProjectScene();
				}
			}
		});
		
		return introScene;
	}
	public void show() {
		Instances.getMainStage().setScene(introScene);
		Instances.getMainStage().show();
	}
}
