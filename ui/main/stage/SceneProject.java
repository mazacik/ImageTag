package ui.main.stage;

import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import main.Main;
import misc.FileUtil;
import misc.Project;
import ui.custom.TitleBar;
import ui.decorator.Decorator;
import ui.node.NodeEdit;
import ui.node.NodeText;
import ui.override.HBox;
import ui.override.Scene;
import ui.override.VBox;

import java.io.File;

public class SceneProject extends Scene {
	private final NodeEdit editProjectName;
	private final NodeEdit editSourceDirectory;
	private final NodeText nodeError;
	private final TitleBar titleBar;
	
	private Project project;
	
	public SceneProject() {
		NodeText nodeProjectName = new NodeText("Project Name:");
		nodeProjectName.setAlignment(Pos.CENTER_LEFT);
		editProjectName = new NodeEdit();
		editProjectName.setPrefWidth(400);
		
		NodeText nodeSourceDirectory = new NodeText("Source Directory:");
		nodeSourceDirectory.setAlignment(Pos.CENTER_LEFT);
		editSourceDirectory = new NodeEdit("");
		editSourceDirectory.setPrefWidth(400);
		NodeText nodeBrowseForDirectory = new NodeText("Browse", true, true, true, true);
		nodeBrowseForDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				editSourceDirectory.setText(FileUtil.directoryChooser(this));
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
		
		nodeError = new NodeText("");
		nodeError.setTextFill(Decorator.getColorNegative());
		
		NodeText btnFinish = new NodeText("Finish", true, true, true, true);
		btnFinish.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> this.initProject());
		NodeText btnCancel = new NodeText("Cancel", true, true, true, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> StageMain.getSceneIntro().show());
		
		VBox boxMain = new VBox(gridPane, nodeError, new HBox(btnCancel, btnFinish));
		boxMain.setSpacing(5);
		boxMain.setAlignment(Pos.CENTER);
		boxMain.setFillWidth(false);
		VBox.setVgrow(boxMain, Priority.ALWAYS);
		
		titleBar = new TitleBar("Create a New Project");
		VBox vBox = new VBox(titleBar, boxMain);
		vBox.setBackground(Decorator.getBackgroundPrimary());
		vBox.setBorder(Decorator.getBorder(1));
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.initProject();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				StageMain.getSceneIntro().show();
			}
		});
		this.setRoot(vBox);
	}
	
	public void show() {
		editProjectName.setText("");
		editSourceDirectory.setText("");
		nodeError.setText("");
		
		titleBar.setTitle("Create a New Project");
		
		StageMain.getInstance().setScene(this);
	}
	public void show(Project project) {
		this.project = project;
		
		editProjectName.setText(project.getProjectName());
		editSourceDirectory.setText(project.getDirectorySource());
		nodeError.setText("");
		
		titleBar.setTitle("Edit Project");
		
		StageMain.getInstance().setScene(this);
	}
	
	private void initProject() {
		if (this.checkEditNodes(editProjectName, editSourceDirectory, nodeError)) {
			if (project == null) {
				Project newProject = new Project(editProjectName.getText(), editSourceDirectory.getText());
				newProject.writeToDisk();
				StageMain.layoutMain();
				Project.setCurrent(newProject);
				Main.startDatabaseLoading();
			} else {
				project.updateProject(editProjectName.getText(), editSourceDirectory.getText());
				
				StageMain.getSceneIntro().getProjectBox().refresh();
				StageMain.getSceneIntro().show();
			}
		}
	}
	private boolean checkEditNodes(NodeEdit editProjectName, NodeEdit editDirectorySource, NodeText nodeError) {
		if (editProjectName.getText().isEmpty()) {
			nodeError.setText("Project Name cannot be empty");
			return false;
		}
		if (editDirectorySource.getText().isEmpty()) {
			nodeError.setText("Source Directory cannot be empty");
			return false;
		}
		
		File directorySource = new File(editDirectorySource.getText());
		if (!directorySource.exists() || !directorySource.isDirectory()) {
			nodeError.setText("Project Directory invalid or not found");
			return false;
		}
		
		return true;
	}
}
