package client.ui.stage.primary;

import client.ui.custom.SeparatorNode;
import client.ui.custom.TitleBar;
import client.ui.custom.textnode.TextNode;
import client.ui.decorator.ColorPreset;
import client.ui.decorator.Decorator;
import client.ui.override.HBox;
import client.ui.override.Scene;
import client.ui.override.VBox;
import client.ui.stage.primary.project.ProjectBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Root;
import server.base.CustomList;
import server.misc.FileUtil;
import server.misc.Project;
import server.misc.Settings;

import java.awt.*;
import java.io.File;

public class IntroStage extends Stage {
	public IntroStage() {
		Rectangle usableScreenBounds = Decorator.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
		
		Scene introScene = this.createIntroScene();
		introScene.getRoot().requestFocus();
		
		this.setScene(introScene);
		this.setWidth(width);
		this.setHeight(height);
		this.setMinWidth(width);
		this.setMinHeight(height);
		this.initStyle(StageStyle.UNDECORATED);
		this.centerOnScreen();
		
		this.setOnCloseRequest(event -> Settings.writeToDisk());
	}
	
	private ProjectBox projectBox = new ProjectBox();
	
	private Scene createIntroScene() {
		TextNode applicationNameNode = new TextNode("Tagallery", false, false, false, true);
		applicationNameNode.setFont(new Font(48));
		applicationNameNode.setPadding(new javafx.geometry.Insets(-20, 0, 20, 0));
		
		TextNode btnNewProject = new TextNode("Create a New Project", true, false, true, true);
		btnNewProject.setMaxWidth(Double.MAX_VALUE);
		//noinspection Convert2MethodRef
		btnNewProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> Root.PSC.showProjectStage());
		
		TextNode btnOpenProject = new TextNode("Open Project", true, false, true, true);
		btnOpenProject.setMaxWidth(Double.MAX_VALUE);
		btnOpenProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Project");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			File file = fileChooser.showOpenDialog(Root.PSC.MAIN_STAGE);
			if (file != null) {
				Root.PSC.showMainStage(Project.readFromDisk(file.getAbsolutePath()));//todo check if valid
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
		
		VBox vBox = new VBox(new TitleBar("Welcome to " + FileUtil.APP_NAME), mainBox);
		vBox.setBackground(Decorator.getBackgroundPrimary());
		vBox.setBorder(Decorator.getBorder(1));
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
				case ENTER:
					CustomList<Project> projects = FileUtil.getProjects();
					if (!projects.isEmpty()) {
						projects.sort(Project.getComparator());
						Root.PSC.showMainStage(projects.getFirstImpl());
					} else {
						Root.PSC.showProjectStage();
					}
					break;
				default:
					break;
			}
		});
		
		return new Scene(vBox);
	}
	
	public void initNodes() {
		projectBox.refresh();
	}
}
