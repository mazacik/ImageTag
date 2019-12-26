package ui.main.stage;

import base.CustomList;
import misc.Project;
import ui.component.simple.BoxSeparatorNode;
import ui.component.simple.HBox;
import ui.component.simple.TextNode;
import ui.component.simple.VBox;
import ui.decorator.ColorPreset;
import ui.decorator.ColorUtil;
import ui.stage.StageManager;
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

import java.io.File;

public class SceneIntro extends HBox {
	private ProjectBox projectBox;
	
	public SceneIntro() {
		projectBox = new ProjectBox();
		
		TextNode applicationNameNode = new TextNode("Tagallery", false, false, false, true);
		applicationNameNode.setFont(new Font(48));
		applicationNameNode.setPadding(new Insets(-20, 0, 20, 0));
		
		TextNode btnNewProject = new TextNode("Create a New Project", true, false, true, true);
		btnNewProject.setMaxWidth(Double.MAX_VALUE);
		btnNewProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> StageManager.getStageMain().getSceneProject().show());
		
		TextNode btnOpenProject = new TextNode("Open Project", true, false, true, true);
		btnOpenProject.setMaxWidth(Double.MAX_VALUE);
		btnOpenProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Project");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			File file = fileChooser.showOpenDialog(StageManager.getStageMain());
			if (file != null) {
				StageManager.getStageMain().layoutMain();
				Main.startDatabaseLoading(Project.readFromDisk(file.getAbsolutePath()));
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
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
				case ENTER:
					CustomList<Project> projects = FileUtil.getProjects();
					if (!projects.isEmpty()) {
						StageManager.getStageMain().layoutMain();
						projects.sort(Project.getComparator());
						Main.startDatabaseLoading(projects.getFirst());
					} else {
						StageManager.getStageMain().getSceneProject().show();
					}
					break;
				default:
					break;
			}
		});
		
		VBox.setVgrow(this, Priority.ALWAYS);
		
		this.getChildren().addAll(projectBox, new BoxSeparatorNode(), vBoxStartMenu);
	}
	
	public void show() {
		StageManager.getStageMain().setTitle("Welcome to Tagallery");
		StageManager.getStageMain().setRoot(this);
		this.requestFocus();
	}
	
	public ProjectBox getProjectBox() {
		return projectBox;
	}
}
