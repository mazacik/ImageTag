package frontend.node.project;

import backend.misc.FileUtil;
import backend.misc.Project;
import frontend.decorator.DecoratorUtil;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import frontend.stage.ConfirmationStage;
import frontend.stage.SimpleMessageStage;
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

import java.io.File;

public class ProjectNode extends BorderPane {
	public ProjectNode(Project project) {
		TextNode nodeProjectName = new TextNode(project.getProjectName(), false, false, false, false);
		TextNode nodeDirectorySource = new TextNode(project.getDirectory(), false, false, false, false);
		
		VBox vBoxLabels = new VBox(nodeProjectName, nodeDirectorySource);
		vBoxLabels.setAlignment(Pos.CENTER_LEFT);
		
		TextNode nodeEdit = new TextNode("···", false, true, false, false);
		nodeEdit.setFont(new Font(26));
		nodeEdit.setVisible(false);
		
		TextNode nodeRemove = new TextNode("✕", false, true, false, false);
		nodeRemove.setFont(new Font(20));
		nodeRemove.setVisible(false);
		
		VBox vBoxButtons = new VBox(nodeEdit, nodeRemove);
		vBoxButtons.setAlignment(Pos.CENTER);
		vBoxButtons.setPadding(new Insets(-10, 0, 0, 0));
		
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			this.setBackground(DecoratorUtil.getBackgroundSecondary());
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
					Main.STAGE.showProjectScene(project);
				} else if (pickResult.equals(nodeRemove)) {
					String message = "Delete project data? The source directory will not be affected";
					if (new ConfirmationStage(message).getResult()) {
						FileUtil.deleteFile(project.getProjectFile());
						FileUtil.deleteFile(FileUtil.getDirectoryCache(project.getProjectName()));
					}
				} else {
					if (new File(project.getDirectory()).exists()) {
						Main.startMain(project);
					} else {
						new SimpleMessageStage("Error", "The source directory of this project could not be found.\nDirectory: " + project.getDirectory()).show();
					}
				}
			}
		});
		
		this.setCenter(vBoxLabels);
		this.setRight(vBoxButtons);
		this.setPadding(new Insets(10));
	}
}
