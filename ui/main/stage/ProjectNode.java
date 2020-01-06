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
import ui.decorator.Decorator;
import ui.node.NodeText;
import ui.override.VBox;
import ui.stage.StageSimpleMessage;
import ui.stage.StageConfirmation;

import java.io.File;

public class ProjectNode extends BorderPane {
	public ProjectNode(Project project) {
		NodeText nodeProjectName = new NodeText(project.getProjectName(), false, false, false);
		NodeText nodeDirectorySource = new NodeText(project.getDirectorySource(), false, false, false);
		
		VBox vBoxLabels = new VBox(nodeProjectName, nodeDirectorySource);
		vBoxLabels.setAlignment(Pos.CENTER_LEFT);
		
		NodeText nodeEdit = new NodeText("···", false, true, false);
		nodeEdit.setFont(new Font(26));
		nodeEdit.setVisible(false);
		
		NodeText nodeRemove = new NodeText("✕", false, true, false);
		nodeRemove.setFont(new Font(20));
		nodeRemove.setVisible(false);
		
		VBox vBoxButtons = new VBox(nodeEdit, nodeRemove);
		vBoxButtons.setAlignment(Pos.CENTER);
		vBoxButtons.setPadding(new Insets(-10, 0, 0, 0));
		
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			this.setBackground(Decorator.getBackgroundSecondary());
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
				Node pickResult = event.getPickResult().getIntersectedNode().getParent();
				
				if (pickResult.equals(nodeEdit)) {
					StageMain.getSceneProject().show(project);
				} else if (pickResult.equals(nodeRemove)) {
					String message = "Delete project data? The source directory will not be affected";
					if (StageConfirmation.show(message)) {
						FileUtil.deleteFile(project.getProjectFile());
						FileUtil.deleteFile(FileUtil.getDirectoryCache(project.getProjectName()));
						StageMain.getSceneIntro().getProjectBox().refresh();
					}
				} else {
					if (new File(project.getDirectorySource()).exists()) {
						StageMain.layoutMain();
						Project.setCurrent(project);
						Main.startDatabaseLoading();
					} else {
						StageSimpleMessage.show("The source directory of this project could not be found.\nDirectory: " + project.getDirectorySource());
					}
				}
			}
		});
		
		this.setCenter(vBoxLabels);
		this.setRight(vBoxButtons);
		this.setPadding(new Insets(10));
	}
}
