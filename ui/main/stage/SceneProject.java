package ui.main.stage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import main.Main;
import misc.FileUtil;
import misc.Project;
import ui.decorator.Decorator;
import ui.override.Scene;
import ui.node.NodeEdit;
import ui.override.HBox;
import ui.node.NodeText;
import ui.override.VBox;
import ui.custom.TitleBar;

import java.io.File;

public class SceneProject extends Scene {
	NodeEdit editProjectName;
	NodeEdit editDirectorySource;
	NodeText nodeError;
	TitleBar titleBar;
	
	Project project;
	
	public SceneProject() {
		NodeText lblProjectName = new NodeText("Project Name:");
		lblProjectName.setAlignment(Pos.CENTER_LEFT);
		editProjectName = new NodeEdit();
		editProjectName.setPrefWidth(400);
		HBox boxProjectName = new HBox(lblProjectName, editProjectName);
		boxProjectName.setSpacing(10);
		boxProjectName.setAlignment(Pos.CENTER);
		
		NodeText lblDirectorySource = new NodeText("Source Directory:");
		lblDirectorySource.setAlignment(Pos.CENTER_LEFT);
		editDirectorySource = new NodeEdit("");
		editDirectorySource.setPrefWidth(400);
		NodeText btnDirectorySource = new NodeText("Browse", true, true, true, true);
		btnDirectorySource.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				editDirectorySource.setText(FileUtil.directoryChooser(this));
			}
		});
		HBox boxDirectorySource = new HBox(lblDirectorySource, editDirectorySource, btnDirectorySource);
		boxDirectorySource.setSpacing(10);
		boxDirectorySource.setAlignment(Pos.CENTER);
		
		boxDirectorySource.layoutXProperty().addListener((observable, oldValue, newValue) -> {
			//todo shit doesn't work
			boxProjectName.setLayoutX((Double) newValue);
		});
		
		nodeError = new NodeText("");
		nodeError.setTextFill(Decorator.getColorNegative());
		
		NodeText btnFinish = new NodeText("Finish", true, true, true, true);
		btnFinish.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> this.initProject());
		NodeText btnCancel = new NodeText("Cancel", true, true, true, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> StageMain.getSceneIntro().show());
		HBox boxFinishCancel = new HBox(btnCancel, btnFinish);
		
		VBox boxMain = new VBox(boxProjectName, boxDirectorySource, nodeError, boxFinishCancel);
		boxMain.setPadding(new Insets(10));
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
		editDirectorySource.setText("");
		nodeError.setText("");
		
		titleBar.setTitle("Create a New Project");
		
		StageMain.getInstance().setScene(this);
	}
	public void show(Project project) {
		this.project = project;
		
		editProjectName.setText(project.getProjectName());
		editDirectorySource.setText(project.getDirectorySource());
		nodeError.setText("");
		
		titleBar.setTitle("Edit Project");
		
		StageMain.getInstance().setScene(this);
	}
	
	private void initProject() {
		if (this.checkEditValidity(editProjectName, editDirectorySource, nodeError)) {
			if (project == null) {
				Project newProject = new Project(editProjectName.getText(), editDirectorySource.getText());
				newProject.writeToDisk();
				StageMain.layoutMain();
				Project.setCurrent(newProject);
				Main.startDatabaseLoading();
			} else {
				project.updateProject(editProjectName.getText(), editDirectorySource.getText());
				
				StageMain.getSceneIntro().getProjectBox().refresh();
				StageMain.getSceneIntro().show();
			}
		}
	}
	
	private boolean checkEditValidity(NodeEdit editProjectName, NodeEdit editDirectorySource, NodeText nodeError) {
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
