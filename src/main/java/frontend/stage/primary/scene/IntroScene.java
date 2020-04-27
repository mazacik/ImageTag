package frontend.stage.primary.scene;

import backend.misc.Project;
import frontend.decorator.DecoratorTemplate;
import frontend.decorator.DecoratorUtil;
import frontend.node.SeparatorNode;
import frontend.node.override.HBox;
import frontend.node.override.Scene;
import frontend.node.override.VBox;
import frontend.node.project.ProjectBox;
import frontend.node.textnode.TextNode;
import frontend.stage.SimpleMessageStage;
import frontend.stage.primary.PrimaryStage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import main.Root;

import java.io.File;

public class IntroScene extends Scene {
	private final ProjectBox projectBox = new ProjectBox();
	
	public IntroScene(PrimaryStage primaryStage) {
		TextNode applicationNameNode = new TextNode("Tagallery", false, false, false, true);
		applicationNameNode.setFont(new Font(48));
		applicationNameNode.setPadding(new javafx.geometry.Insets(-20, 0, 20, 0));
		
		TextNode btnNewProject = new TextNode("Create a New Project", true, false, true, true);
		btnNewProject.setMaxWidth(Double.MAX_VALUE);
		//noinspection Convert2MethodRef
		btnNewProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> primaryStage.showProjectScene());
		
		TextNode btnOpenProject = new TextNode("Open Project", true, false, true, true);
		btnOpenProject.setMaxWidth(Double.MAX_VALUE);
		btnOpenProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Project");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			File file = fileChooser.showOpenDialog(Root.PRIMARY_STAGE);
			
			Project project = null;
			if (file != null) {
				project = Project.readFromDisk(file.getAbsolutePath());
			}
			
			if (project != null) {
				primaryStage.showMainScene(project);
			} else {
				new SimpleMessageStage("Error", "Error opening project file.").showAndWait();
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
		
		mainBox.setBackground(DecoratorUtil.getBackgroundPrimary());
		
		this.setRoot(mainBox);
	}
	
	public void refreshIntroBox() {
		projectBox.refresh();
	}
}
