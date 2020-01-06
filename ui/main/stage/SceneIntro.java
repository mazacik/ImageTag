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
import ui.decorator.Decorator;
import ui.decorator.ColorPreset;
import ui.override.Scene;
import ui.node.NodeBoxSeparator;
import ui.override.HBox;
import ui.node.NodeText;
import ui.override.VBox;
import ui.custom.TitleBar;

import java.io.File;

public class SceneIntro extends Scene {
	private ProjectBox projectBox = new ProjectBox();
	
	public SceneIntro() {
		NodeText applicationNameNode = new NodeText("Tagallery", false, false, false, true);
		applicationNameNode.setFont(new Font(48));
		applicationNameNode.setPadding(new Insets(-20, 0, 20, 0));
		
		NodeText btnNewProject = new NodeText("Create a New Project", true, false, true, true);
		btnNewProject.setMaxWidth(Double.MAX_VALUE);
		btnNewProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> StageMain.getSceneProject().show());
		
		NodeText btnOpenProject = new NodeText("Open Project", true, false, true, true);
		btnOpenProject.setMaxWidth(Double.MAX_VALUE);
		btnOpenProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Project");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			File file = fileChooser.showOpenDialog(StageMain.getInstance());
			if (file != null) {
				StageMain.layoutMain();
				Project.setCurrent(Project.readFromDisk(file.getAbsolutePath()));
				Main.startDatabaseLoading();
			}
		});
		
		NodeText btnColorPreset = new NodeText("Color Preset: " + Decorator.getColorPreset().getDisplayName(), true, false, true, true);
		btnColorPreset.setMaxWidth(Double.MAX_VALUE);
		btnColorPreset.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			int presetIndex = Decorator.getColorPreset().ordinal();
			presetIndex++;
			if (presetIndex >= ColorPreset.values().length) presetIndex = 0;
			Decorator.setColorPreset(ColorPreset.values()[presetIndex]);
			btnColorPreset.setText("Color Preset: " + Decorator.getColorPreset().getDisplayName());
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
		
		HBox mainBox = new HBox(projectBox, new NodeBoxSeparator(), vBoxStartMenu);
		mainBox.setAlignment(Pos.TOP_CENTER);
		VBox.setVgrow(mainBox, Priority.ALWAYS);
		
		VBox vBox = new VBox(new TitleBar("Welcome to " + FileUtil.getApplicationName()), mainBox);
		vBox.setBackground(Decorator.getBackgroundPrimary());
		vBox.setBorder(Decorator.getBorder(1));
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
				case ENTER:
					CustomList<Project> projects = FileUtil.getProjects();
					if (!projects.isEmpty()) {
						StageMain.layoutMain();
						projects.sort(Project.getComparator());
						Project.setCurrent(projects.getFirst());
						Main.startDatabaseLoading();
					} else {
						StageMain.getSceneProject().show();
					}
					break;
				default:
					break;
			}
		});
		this.setRoot(vBox);
	}
	
	public void show() {
		StageMain.getInstance().setScene(this);
	}
	
	public ProjectBox getProjectBox() {
		return projectBox;
	}
}
