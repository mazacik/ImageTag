package frontend.stage.primary.project;

import backend.misc.FileUtil;
import backend.misc.Project;
import backend.misc.Settings;
import frontend.decorator.DecoratorUtil;
import frontend.node.EditNode;
import frontend.node.override.HBox;
import frontend.node.override.Scene;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import main.Root;

import java.awt.*;
import java.io.File;

public class ProjectStage extends Stage {
	public ProjectStage() {
		Rectangle usableScreenBounds = DecoratorUtil.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
		
		Scene projectScene = this.createProjectScene();
		projectScene.getRoot().requestFocus();
		
		this.getIcons().add(new Image("/logo-32px.png"));
		this.setScene(projectScene);
		this.setWidth(width);
		this.setHeight(height);
		this.setMinWidth(width);
		this.setMinHeight(height);
		this.centerOnScreen();
		
		this.setOnCloseRequest(event -> Settings.writeToDisk());
	}
	
	private EditNode editProjectName;
	private EditNode editSourceDirectory;
	private TextNode nodeError;
	//	private TitleBar titleBar;
	
	private Project project;
	
	private Scene createProjectScene() {
		TextNode nodeProjectName = new TextNode("Project Name:", false, false, false, false);
		nodeProjectName.setAlignment(Pos.CENTER_LEFT);
		editProjectName = new EditNode();
		editProjectName.setPrefWidth(400);
		
		TextNode nodeSourceDirectory = new TextNode("Source Directory:", false, false, false, false);
		nodeSourceDirectory.setAlignment(Pos.CENTER_LEFT);
		editSourceDirectory = new EditNode();
		editSourceDirectory.setPrefWidth(400);
		TextNode nodeBrowseForDirectory = new TextNode("Browse", true, true, true, true);
		nodeBrowseForDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				String path = FileUtil.directoryChooser(this.getScene());
				if (!path.isEmpty()) {
					if (editProjectName.getText().isEmpty()) {
						String name = new File(path).getName();
						editProjectName.setText(name);
						editProjectName.selectPositionCaret(name.length());
					}
					editSourceDirectory.setText(path);
				}
			}
		});
		
		GridPane gridPane = new GridPane();
		gridPane.add(nodeProjectName, 0, 0);
		gridPane.add(editProjectName, 1, 0);
		gridPane.add(nodeSourceDirectory, 0, 1);
		gridPane.add(editSourceDirectory, 1, 1);
		gridPane.add(nodeBrowseForDirectory, 2, 1);
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		
		nodeError = new TextNode("", false, false, false, false);
		nodeError.setTextFill(DecoratorUtil.getColorNegative());
		
		TextNode btnFinish = new TextNode("Finish", true, true, true, true);
		btnFinish.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> this.tryFinish());
		TextNode btnCancel = new TextNode("Cancel", true, true, true, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> Root.PSC.showIntroStage());
		
		VBox boxMain = new VBox(gridPane, nodeError, new HBox(btnCancel, btnFinish));
		boxMain.setSpacing(5);
		boxMain.setAlignment(Pos.CENTER);
		boxMain.setFillWidth(false);
		VBox.setVgrow(boxMain, Priority.ALWAYS);
		
		this.setTitle("Create a New Project");
		boxMain.setBackground(DecoratorUtil.getBackgroundPrimary());
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.tryFinish();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				Root.PSC.showIntroStage();
			}
		});
		
		return new Scene(boxMain);
	}
	
	public void initNodes() {
		project = null;
		
		editProjectName.setText("");
		editSourceDirectory.setText("");
		nodeError.setText("");
		
		this.setTitle("Create a New Project");
	}
	public void initNodes(Project project) {
		this.project = project;
		
		editProjectName.setText(project.getProjectName());
		editSourceDirectory.setText(project.getDirectorySource());
		nodeError.setText("");
		
		this.setTitle("Edit Project");
	}
	
	private void tryFinish() {
		if (this.checkUserInput()) {
			if (project == null) {
				project = new Project(editProjectName.getText(), editSourceDirectory.getText());
				project.writeToDisk();
				Root.PSC.showMainStage(project);
			} else {
				project.updateProject(editProjectName.getText(), editSourceDirectory.getText());
				Root.PSC.showIntroStage();
			}
		}
	}
	private boolean checkUserInput() {
		if (editProjectName.getText().isEmpty()) {
			nodeError.setText("Project Name cannot be empty");
			return false;
		}
		if (editSourceDirectory.getText().isEmpty()) {
			nodeError.setText("Source Directory cannot be empty");
			return false;
		}
		
		File directorySource = new File(editSourceDirectory.getText());
		if (!directorySource.exists() || !directorySource.isDirectory()) {
			nodeError.setText("Project Directory invalid or not found");
			return false;
		}
		
		return true;
	}
}
