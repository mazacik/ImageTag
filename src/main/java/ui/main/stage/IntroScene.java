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
import ui.custom.TitleBar;
import ui.decorator.ColorPreset;
import ui.decorator.Decorator;
import ui.node.SeparatorNode;
import ui.node.textnode.TextNode;
import ui.override.HBox;
import ui.override.Scene;
import ui.override.VBox;

import java.io.File;

public class IntroScene extends Scene {
	private ProjectBox projectBox = new ProjectBox();
	
	public IntroScene() {
		TextNode applicationNameNode = new TextNode("Tagallery", false, false, false, true);
		applicationNameNode.setFont(new Font(48));
		applicationNameNode.setPadding(new Insets(-20, 0, 20, 0));
		
		TextNode btnNewProject = new TextNode("Create a New Project", true, false, true, true);
		btnNewProject.setMaxWidth(Double.MAX_VALUE);
		btnNewProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> MainStage.getProjectScene().show());
		
		TextNode btnOpenProject = new TextNode("Open Project", true, false, true, true);
		btnOpenProject.setMaxWidth(Double.MAX_VALUE);
		btnOpenProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Project");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			File file = fileChooser.showOpenDialog(MainStage.getInstance());
			if (file != null) {
				MainStage.layoutMain();
				Project.setCurrent(Project.readFromDisk(file.getAbsolutePath()));
				Main.startProjectDatabaseLoading();
			}
		});
		
		TextNode btnColorPreset = new TextNode("Color Preset: " + Decorator.getColorPreset().getDisplayName(), true, false, true, true);
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
		
		HBox mainBox = new HBox(projectBox, new SeparatorNode(), vBoxStartMenu);
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
						MainStage.layoutMain();
						projects.sort(Project.getComparator());
						Project.setCurrent(projects.getFirst());
						Main.startProjectDatabaseLoading();
					} else {
						MainStage.getProjectScene().show();
					}
					break;
				default:
					break;
			}
		});
		this.setRoot(vBox);
	}
	
	public void show() {
		MainStage.getInstance().setScene(this);
	}
	
	public ProjectBox getProjectBox() {
		return projectBox;
	}
}
