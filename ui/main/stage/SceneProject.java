package ui.main.stage;

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
import ui.NodeUtil;
import ui.component.simple.EditNode;
import ui.component.simple.HBox;
import ui.component.simple.TextNode;
import ui.component.simple.VBox;
import ui.decorator.ColorUtil;
import ui.decorator.SizeUtil;
import ui.stage.Scene;
import ui.stage.StageManager;
import ui.stage.TitleBar;

import java.io.File;

public class SceneProject extends Scene {
	EditNode editProjectName;
	EditNode editDirectorySource;
	TextNode nodeError;
	TitleBar titleBar;
	
	Project project;
	
	public SceneProject() {
		TextNode lblProjectName = new TextNode("Project Name:");
		lblProjectName.setAlignment(Pos.CENTER_LEFT);
		editProjectName = new EditNode("Project1");
		editProjectName.setPrefWidth(400);
		TextNode btnHelperProjectName = new TextNode("Browse", true, true, true, true);
		btnHelperProjectName.setVisible(false);
		HBox boxProjectName = new HBox(lblProjectName, editProjectName, btnHelperProjectName);
		boxProjectName.setSpacing(10);
		boxProjectName.setAlignment(Pos.CENTER);
		
		TextNode lblDirectorySource = new TextNode("Source Directory:");
		lblDirectorySource.setAlignment(Pos.CENTER_LEFT);
		editDirectorySource = new EditNode("");
		editDirectorySource.setPrefWidth(400);
		TextNode btnDirectorySource = new TextNode("Browse", true, true, true, true);
		btnDirectorySource.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				editDirectorySource.setText(FileUtil.directoryChooser(this));
			}
		});
		HBox boxDirectorySource = new HBox(lblDirectorySource, editDirectorySource, btnDirectorySource);
		boxDirectorySource.setSpacing(10);
		boxDirectorySource.setAlignment(Pos.CENTER);
		
		nodeError = new TextNode("");
		nodeError.setTextFill(ColorUtil.getColorNegative());
		
		TextNode btnFinish = new TextNode("Finish", true, true, true, true);
		btnFinish.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> this.initProject());
		TextNode btnCancel = new TextNode("Cancel", true, true, true, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> StageManager.getStageMain().getSceneIntro().show());
		HBox boxFinishCancel = new HBox(btnCancel, btnFinish);
		
		double width = SizeUtil.getStringWidth(lblDirectorySource.getText());
		lblProjectName.setPrefWidth(width);
		lblDirectorySource.setPrefWidth(width);
		
		VBox boxMain = new VBox(boxProjectName, boxDirectorySource, nodeError, boxFinishCancel);
		boxMain.setPadding(new Insets(10));
		boxMain.setSpacing(5);
		boxMain.setAlignment(Pos.CENTER);
		boxMain.setFillWidth(false);
		VBox.setVgrow(boxMain, Priority.ALWAYS);
		
		titleBar = new TitleBar("Create a New Project");
		VBox vBox = new VBox(titleBar, boxMain);
		vBox.setBackground(ColorUtil.getBackgroundPrimary());
		vBox.setBorder(NodeUtil.getBorder(1));
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.initProject();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				StageManager.getStageMain().getSceneIntro().show();
			}
		});
		this.setRoot(vBox);
	}
	
	public void show() {
		editProjectName.setText("");
		editDirectorySource.setText("");
		nodeError.setText("");
		
		titleBar.setTitle("Create a New Project");
		
		StageManager.getStageMain().setScene(this);
	}
	public void show(Project project) {
		this.project = project;
		
		editProjectName.setText(project.getProjectName());
		editDirectorySource.setText(project.getDirectorySource());
		nodeError.setText("");
		
		titleBar.setTitle("Edit Project");
		
		StageManager.getStageMain().setScene(this);
	}
	
	private void initProject() {
		if (this.checkEditValidity(editProjectName, editDirectorySource, nodeError)) {
			if (project == null) {
				Project newProject = new Project(editProjectName.getText(), editDirectorySource.getText());
				newProject.writeToDisk();
				StageManager.getStageMain().layoutMain();
				Project.setCurrent(newProject);
				Main.startDatabaseLoading();
			} else {
				project.updateProject(editProjectName.getText(), editDirectorySource.getText());
				
				SceneIntro sceneIntro = StageManager.getStageMain().getSceneIntro();
				sceneIntro.getProjectBox().refresh();
				sceneIntro.show();
			}
		}
	}
	
	private boolean checkEditValidity(EditNode editProjectName, EditNode editDirectorySource, TextNode nodeError) {
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
