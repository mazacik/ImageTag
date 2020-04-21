package frontend.stage.primary;

import backend.list.BaseList;
import backend.misc.FileUtil;
import backend.misc.Project;
import backend.misc.Settings;
import frontend.decorator.DecoratorTemplate;
import frontend.decorator.DecoratorUtil;
import frontend.node.SeparatorNode;
import frontend.node.override.HBox;
import frontend.node.override.Scene;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import frontend.stage.primary.project.ProjectBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Root;

import java.awt.*;
import java.io.File;

public class IntroStage extends Stage {
	public IntroStage() {
		Rectangle usableScreenBounds = DecoratorUtil.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
		
		Scene introScene = this.createIntroScene();
		introScene.getRoot().requestFocus();
		
		this.getIcons().add(new Image("/logo-32px.png"));
		this.setScene(introScene);
		this.setWidth(width);
		this.setHeight(height);
		this.setMinWidth(width);
		this.setMinHeight(height);
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
		
		TextNode btnColorPreset = new TextNode("Color Preset: " + DecoratorUtil.getDecoratorTemplate().getDisplayName(), true, false, true, true);
		btnColorPreset.setMaxWidth(Double.MAX_VALUE);
		btnColorPreset.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			int presetIndex = DecoratorUtil.getDecoratorTemplate().ordinal();
			presetIndex++;
			if (presetIndex >= DecoratorTemplate.values().length) presetIndex = 0;
			DecoratorUtil.setDecoratorTemplate(DecoratorTemplate.values()[presetIndex]);
			btnColorPreset.setText("Color Preset: " + DecoratorUtil.getDecoratorTemplate().getDisplayName());
		});
		
		VBox vBoxStartMenu = new VBox();
		vBoxStartMenu.setAlignment(Pos.CENTER);
		vBoxStartMenu.setPadding(new Insets(-40, 0, 20, 0));
		HBox.setHgrow(vBoxStartMenu, Priority.ALWAYS);
		
		vBoxStartMenu.getChildren().add(new ImageView(getClass().getResource("/logo-128px.png").toExternalForm()));
		vBoxStartMenu.getChildren().add(applicationNameNode);
		vBoxStartMenu.getChildren().add(btnNewProject);
		vBoxStartMenu.getChildren().add(btnOpenProject);
		vBoxStartMenu.getChildren().add(btnColorPreset);
		
		HBox mainBox = new HBox(projectBox, new SeparatorNode(), vBoxStartMenu);
		mainBox.setAlignment(Pos.TOP_CENTER);
		VBox.setVgrow(mainBox, Priority.ALWAYS);
		
		this.setTitle("Welcome to " + FileUtil.APP_NAME);
		mainBox.setBackground(DecoratorUtil.getBackgroundPrimary());
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
				case ENTER:
					BaseList<Project> projects = FileUtil.getProjects();
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
		
		return new Scene(mainBox);
	}
	
	public void initNodes() {
		projectBox.refresh();
	}
}
