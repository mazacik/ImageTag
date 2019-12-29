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
				edtDirectorySourceCPS.setText(FileUtil.directoryChooser(this));
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
				StageManager.getStageMain().layoutMain();
				Project.setCurrent(project);
				Main.startDatabaseLoading();
			}
		});
		TextNode btnCancel = new TextNode("Cancel", true, true, true, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			edtProjectNameCPS.setText("");
			edtDirectorySourceCPS.setText("");
			nodeError.setText("");
			StageManager.getStageMain().setScene(StageManager.getStageMain().getSceneIntro());
		});
		HBox hBoxCreateCancel = new HBox(btnCancel, btnCreateProject);
		
		double width = SizeUtil.getStringWidth(lblDirectorySource.getText());
		lblProjectName.setPrefWidth(width);
		lblDirectorySource.setPrefWidth(width);
		
		VBox mainBox = new VBox(hBoxProjectName, hBoxDirectorySource, nodeError, hBoxCreateCancel);
		mainBox.setPadding(new Insets(10));
		mainBox.setSpacing(5);
		mainBox.setAlignment(Pos.CENTER);
		mainBox.setFillWidth(false);
		VBox.setVgrow(mainBox, Priority.ALWAYS);
		
		VBox vBox = new VBox(new TitleBar("Create a New Project"), mainBox);
		vBox.setBackground(ColorUtil.getBackgroundPrimary());
		vBox.setBorder(NodeUtil.getBorder(1));
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				if (this.checkEditValidity(edtProjectNameCPS, edtDirectorySourceCPS, nodeError)) {
					Project project = new Project(edtProjectNameCPS.getText(), edtDirectorySourceCPS.getText());
					project.writeToDisk();
					StageManager.getStageMain().layoutMain();
					Project.setCurrent(project);
					Main.startDatabaseLoading();
				}
			} else if (event.getCode() == KeyCode.ESCAPE) {
				StageManager.getStageMain().setScene(StageManager.getStageMain().getSceneIntro());
			}
		});
		this.setRoot(vBox);
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
