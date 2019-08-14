package application.gui.stage;

import application.database.loader.Project;
import application.gui.decorator.SizeUtil;
import application.gui.nodes.custom.IntroSceneNode;
import application.gui.nodes.simple.SeparatorNode;
import application.gui.nodes.simple.TextNode;
import application.main.Instances;
import application.main.LifeCycle;
import application.settings.Stack;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;

public class IntroStage extends StageBase {
	public IntroStage() {
		super("Welcome to Tagallery");
		
		double width = SizeUtil.getUsableScreenWidth() / 2;
		double height = SizeUtil.getUsableScreenHeight() / 2;
		this.setWidth(width);
		this.setHeight(height);
		this.setMinWidth(width);
		this.setMinHeight(height);
		
		VBox vBoxRecentProjects = new VBox();
		vBoxRecentProjects.setMinWidth(400);
		vBoxRecentProjects.setPadding(new Insets(5));
		
		VBox vBoxStartMenu = new VBox();
		vBoxStartMenu.setSpacing(10);
		vBoxStartMenu.setMinWidth(500);
		vBoxStartMenu.setAlignment(Pos.CENTER);
		vBoxStartMenu.setPrefWidth(SizeUtil.getUsableScreenWidth());
		
		HBox hBoxLayoutHelper = new HBox(vBoxRecentProjects, new SeparatorNode(), vBoxStartMenu);
		VBox.setVgrow(hBoxLayoutHelper, Priority.ALWAYS);
		
		TextNode btnOpenProject = new TextNode("Open Project", false, true, true);
		btnOpenProject.addEventFilter(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Project");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			File file = fileChooser.showOpenDialog(Stages.getMainStage());
			if (file != null) {
				String projectFilePath = file.getAbsolutePath();
				Instances.getSettings().getRecentProjects().push(projectFilePath);
				Project project = Project.readFromDisk(projectFilePath);
				LifeCycle.startLoading(project);
			}
		});
		
		TextNode btnNewProject = new TextNode("Create a New Project", false, true, true);
		btnNewProject.addEventFilter(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			Stages.getProjectStage()._show();
		});
		
		TextNode applicationNameNode = new TextNode("Tagallery", false, false, false, false);
		applicationNameNode.setFont(new Font(48));
		applicationNameNode.setPadding(new Insets(-20, 0, 20, 0));
		
		vBoxStartMenu.getChildren().add(new ImageView(new Image("application/gui/resources/logo-1.png")));
		vBoxStartMenu.getChildren().add(applicationNameNode);
		vBoxStartMenu.getChildren().add(btnNewProject);
		vBoxStartMenu.getChildren().add(btnOpenProject);
		//vBoxStartMenu.getChildren().add(new ColorModeSwitchNode());
		
		vBoxStartMenu.setPadding(new Insets(-40, 0, 40, 0));
		
		Stack<String> recentProjects = Instances.getSettings().getRecentProjects();
		if (recentProjects.size() != 0) {
			for (String recentProject : new ArrayList<>(recentProjects)) {
				File projectFile = new File(recentProject);
				if (projectFile.exists()) {
					Project project = Project.readFromDisk(recentProject);
					IntroSceneNode introSceneNode = new IntroSceneNode(recentProject, project.getSourceDirectory());
					introSceneNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
						if (event.getButton() == MouseButton.PRIMARY) {
							if (event.getPickResult().getIntersectedNode().getParent().equals(introSceneNode.getNodeRemove())) {
								recentProjects.remove(introSceneNode.getWorkingDirectory());
								vBoxRecentProjects.getChildren().remove(introSceneNode);
							} else {
								if (new File(project.getSourceDirectory()).exists()) {
									Instances.getSettings().getRecentProjects().push(recentProject);
									LifeCycle.startLoading(project);
								} else {
									Stages.getErrorStage()._show("The working directory of this project could not be found.");
								}
							}
						}
					});
					vBoxRecentProjects.getChildren().add(introSceneNode);
				}
			}
		}
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				if (!Instances.getSettings().getRecentProjects().isEmpty()) {
					LifeCycle.startLoading(Project.readFromDisk(Instances.getSettings().getRecentProjects().get(0)));
				} else {
					Stages.getProjectStage()._show();
				}
			}
		});
		
		this.setAlwaysOnTop(false);
		
		this.setRoot(hBoxLayoutHelper);
	}
	
	@Override
	public Object _show(String... args) {
		this.show();
		return null;
	}
}
