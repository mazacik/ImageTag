package gui.stage.main;

import baseobject.Project;
import gui.component.simple.EditNode;
import gui.component.simple.HBox;
import gui.component.simple.TextNode;
import gui.component.simple.VBox;
import gui.decorator.ColorUtil;
import gui.decorator.SizeUtil;
import gui.stage.StageManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import main.InstanceCollector;
import main.Main;
import tools.FileUtil;

import java.io.File;

public class SceneProject extends VBox implements InstanceCollector {
	public SceneProject() {
		TextNode lblProjectName = new TextNode("Project Name:");
		lblProjectName.setAlignment(Pos.CENTER_LEFT);
		EditNode edtProjectNameCPS = new EditNode("Project1");
		edtProjectNameCPS.setPrefWidth(400);
		TextNode btnHelperProjectName = new TextNode("Browse", true, true, true, true);
		btnHelperProjectName.setVisible(false);
		HBox hBoxProjectName = new HBox(lblProjectName, edtProjectNameCPS, btnHelperProjectName);
		hBoxProjectName.setSpacing(10);
		hBoxProjectName.setAlignment(Pos.CENTER);
		
		TextNode lblDirectorySource = new TextNode("Source Directory:");
		lblDirectorySource.setAlignment(Pos.CENTER_LEFT);
		EditNode edtDirectorySourceCPS = new EditNode("");
		edtDirectorySourceCPS.setPrefWidth(400);
		TextNode btnDirectorySource = new TextNode("Browse", true, true, true, true);
		btnDirectorySource.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				edtDirectorySourceCPS.setText(FileUtil.directoryChooser(this.getScene()));
			}
		});
		HBox hBoxDirectorySource = new HBox(lblDirectorySource, edtDirectorySourceCPS, btnDirectorySource);
		hBoxDirectorySource.setSpacing(10);
		hBoxDirectorySource.setAlignment(Pos.CENTER);
		
		TextNode nodeError = new TextNode("");
		nodeError.setTextFill(ColorUtil.getColorNegative());
		
		TextNode btnCreateProject = new TextNode("Create Project", true, true, true, true);
		btnCreateProject.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (this.checkEditValidity(edtProjectNameCPS, edtDirectorySourceCPS, nodeError)) {
				Project project = new Project(edtProjectNameCPS.getText(), edtDirectorySourceCPS.getText());
				project.writeToDisk();
				Main.startDatabaseLoading(project);
			}
		});
		TextNode btnCancel = new TextNode("Cancel", true, true, true, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			edtProjectNameCPS.setText("");
			edtDirectorySourceCPS.setText("");
			nodeError.setText("");
			StageManager.getStageMain().getSceneIntro().show();
		});
		HBox hBoxCreateCancel = new HBox(btnCancel, btnCreateProject);
		
		double width = SizeUtil.getStringWidth(lblDirectorySource.getText());
		lblProjectName.setPrefWidth(width);
		lblDirectorySource.setPrefWidth(width);
		
		this.getChildren().addAll(hBoxProjectName, hBoxDirectorySource, nodeError, hBoxCreateCancel);
		this.setPadding(new Insets(10));
		this.setSpacing(5);
		this.setAlignment(Pos.CENTER);
		this.setFillWidth(false);
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				if (this.checkEditValidity(edtProjectNameCPS, edtDirectorySourceCPS, nodeError)) {
					Project project = new Project(edtProjectNameCPS.getText(), edtDirectorySourceCPS.getText());
					project.writeToDisk();
					Main.startDatabaseLoading(project);
				}
			} else if (event.getCode() == KeyCode.ESCAPE) {
				StageManager.getStageMain().getSceneIntro().show();
			}
		});
		VBox.setVgrow(this, Priority.ALWAYS);
	}
	
	public void show() {
		StageManager.getStageMain().setTitle("Create a New Project");
		StageManager.getStageMain().setRoot(this);
		this.requestFocus();
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
