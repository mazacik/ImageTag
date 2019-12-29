package ui.main.stage;

import base.CustomList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import main.Main;
import misc.FileUtil;
import misc.Project;
import ui.NodeUtil;
import ui.component.simple.BoxSeparatorNode;
import ui.component.simple.HBox;
import ui.component.simple.TextNode;
import ui.component.simple.VBox;
import ui.decorator.ColorPreset;
import ui.decorator.ColorUtil;
import ui.stage.Scene;
import ui.stage.StageManager;
import ui.stage.TitleBar;

import java.io.File;

public class SceneIntro extends Scene {
	private ProjectBox projectBox = new ProjectBox();
	
	public SceneIntro() {
		TextNode applicationNameNode = new TextNode("Tagallery", false, false, false, true);
		applicationNameNode.setFont(new Font(48));
		applicationNameNode.setPadding(new Insets(-20, 0, 20, 0));
		
		TextNode btnNewProject = new TextNode("Create a New Project", true, false, true, true);
		btnNewProject.setMaxWidth(Double.MAX_VALUE);
		btnNewProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> StageManager.getStageMain().setScene(StageManager.getStageMain().getSceneProject()));
		
		TextNode btnOpenProject = new TextNode("Open Project", true, false, true, true);
		btnOpenProject.setMaxWidth(Double.MAX_VALUE);
		btnOpenProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Project");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			File file = fileChooser.showOpenDialog(StageManager.getStageMain());
			if (file != null) {
				StageManager.getStageMain().layoutMain();
				Project.setCurrent(Project.readFromDisk(file.getAbsolutePath()));
				Main.startDatabaseLoading();
			}
		});
		
		TextNode btnColorPreset = new TextNode("Color Preset: " + ColorUtil.getColorPreset().getDisplayName(), true, false, true, true);
		btnColorPreset.setMaxWidth(Double.MAX_VALUE);
		btnColorPreset.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			int presetIndex = ColorUtil.getColorPreset().ordinal();
			presetIndex++;
			if (presetIndex >= ColorPreset.values().length) presetIndex = 0;
			ColorUtil.setColorPreset(ColorPreset.values()[presetIndex]);
			btnColorPreset.setText("Color Preset: " + ColorUtil.getColorPreset().getDisplayName());
		});
		
		VBox vBoxStartMenu = new VBox();
		vBoxStartMenu.setAlignment(Pos.CENTER);
		vBoxStartMenu.setPadding(new Insets(-40, 0, 20, 0));
		HBox.setHgrow(vBoxStartMenu, Priority.ALWAYS);
		
		vBoxStartMenu.getChildren().add(new ImageView(getClass().getResource("/logo-1.png").toExternalForm()));
		vBoxStartMenu.getChildren().add(applicationNameNode);
		vBoxStartMenu.getChildren().add(btnNewProject);
		vBoxStartMenu.getChildren().add(btnOpenProject);
		vBoxStartMenu.getChildren().add(btnColorPreset);
		
		HBox mainBox = new HBox(projectBox, new BoxSeparatorNode(), vBoxStartMenu);
		mainBox.setAlignment(Pos.TOP_CENTER);
		VBox.setVgrow(mainBox, Priority.ALWAYS);
		
		VBox vBox = new VBox(new TitleBar("Welcome to " + FileUtil.getApplicationName()), mainBox);
		vBox.setBackground(ColorUtil.getBackgroundPrimary());
		vBox.setBorder(NodeUtil.getBorder(1));
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
				case ENTER:
					CustomList<Project> projects = FileUtil.getProjects();
					if (!projects.isEmpty()) {
						StageManager.getStageMain().layoutMain();
						projects.sort(Project.getComparator());
						Project.setCurrent(projects.getFirst());
						Main.startDatabaseLoading();
					} else {
						StageManager.getStageMain().setScene(StageManager.getStageMain().getSceneProject());
					}
					break;
				default:
					break;
			}
		});
		this.setRoot(vBox);
	}
	
	public ProjectBox getProjectBox() {
		return projectBox;
	}
}
