package ui.main.stage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import main.Main;
import misc.FileUtil;
import misc.Project;
import ui.component.simple.TextNode;
import ui.component.simple.VBox;
import ui.decorator.ColorUtil;
import ui.stage.StageManager;

import java.io.File;

public class ProjectNode extends BorderPane {
	public ProjectNode(Project project) {
		TextNode nodeProjectName = new TextNode(project.getProjectName(), false, false, false);
		TextNode nodeDirectorySource = new TextNode(project.getDirectorySource(), false, false, false);
		
		VBox vBoxLabels = new VBox(nodeProjectName, nodeDirectorySource);
		vBoxLabels.setAlignment(Pos.CENTER_LEFT);
		
		TextNode nodeEdit = new TextNode("···", false, true, false);
		nodeEdit.setFont(new Font(26));
		nodeEdit.setVisible(false);
		
		TextNode nodeRemove = new TextNode("✕", false, true, false);
		nodeRemove.setFont(new Font(20));
		nodeRemove.setVisible(false);
		
		VBox vBoxButtons = new VBox(nodeEdit, nodeRemove);
		vBoxButtons.setAlignment(Pos.CENTER);
		vBoxButtons.setPadding(new Insets(-10, 0, 0, 0));
		
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			this.setBackground(ColorUtil.getBackgroundSecondary());
			this.setCursor(Cursor.HAND);
			
			nodeEdit.setVisible(true);
			nodeRemove.setVisible(true);
		});
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			this.setBackground(Background.EMPTY);
			this.setCursor(Cursor.DEFAULT);
			
			nodeEdit.setVisible(false);
			nodeRemove.setVisible(false);
		});
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				StageMain stageMain = StageManager.getStageMain();
				Node pickResult = event.getPickResult().getIntersectedNode().getParent();
				
				if (pickResult.equals(nodeEdit)) {
					StageManager.getStageMain().getSceneProject().show(project);
				} else if (pickResult.equals(nodeRemove)) {
					String message = "Delete project data? The source directory will not be affected";
					if (StageManager.getYesNoStage().show(message)) {
						FileUtil.deleteFile(project.getProjectFile());
						FileUtil.deleteFile(FileUtil.getDirectoryCache(project.getProjectName()));
						stageMain.getSceneIntro().getProjectBox().refresh();
					}
				} else {
					if (new File(project.getDirectorySource()).exists()) {
						StageManager.getStageMain().layoutMain();
						Project.setCurrent(project);
						Main.startDatabaseLoading();
					} else {
						StageManager.getErrorStage().show("The source directory of this project could not be found.\nDirectory: " + project.getDirectorySource());
					}
				}
			}
		});
		
		this.setCenter(vBoxLabels);
		this.setRight(vBoxButtons);
		this.setPadding(new Insets(10));
	}
}
