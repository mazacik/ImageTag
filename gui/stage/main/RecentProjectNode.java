package gui.stage.main;

import baseobject.Project;
import gui.component.simple.TextNode;
import gui.decorator.ColorPreset;
import gui.stage.StageManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import main.InstanceCollector;
import main.LifecycleManager;

import java.io.File;

public class RecentProjectNode extends BorderPane implements InstanceCollector {
	private TextNode nodeEdit;
	private TextNode nodeRemove;
	
	public RecentProjectNode(Project project) {
		TextNode nodeProjectFile = new TextNode(project.getProjectFileName(), false, false, false);
		TextNode nodeWorkingDirectory = new TextNode(project.getWorkingDirectory(), false, false, false);
		
		nodeEdit = new TextNode("···", false, true, false);
		nodeEdit.setFont(new Font(26));
		nodeEdit.setVisible(false);
		
		nodeRemove = new TextNode("✕", false, true, false);
		nodeRemove.setFont(new Font(20));
		nodeRemove.setVisible(false);
		
		VBox vBox = new VBox(nodeProjectFile, nodeWorkingDirectory);
		vBox.setAlignment(Pos.CENTER_LEFT);
		
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			this.setBackground(ColorPreset.getCurrent().getBackgroundSecondary());
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
				if (isClickOnNodeEdit(event)) {
					StageManager.getMainStage().showEditProjectScene(project);
				} else if (isClickOnNodeRemove(event)) {
					StageManager.getMainStage().removeProjectFromRecents(this, project);
				} else {
					if (new File(project.getWorkingDirectory()).exists()) {
						settings.getRecentProjects().add(0, project.getProjectFileFullPath());
						LifecycleManager.startLoading(project);
					} else {
						StageManager.getErrorStage().show("The working directory of this project could not be found.\nDirectory: " + project.getWorkingDirectory());
					}
				}
			}
		});
		
		VBox rightHelper = new VBox(nodeEdit, nodeRemove);
		rightHelper.setAlignment(Pos.CENTER);
		rightHelper.setPadding(new Insets(-10, 0, 0, 0));
		
		this.setCenter(vBox);
		this.setRight(rightHelper);
		this.setPadding(new Insets(10));
		setAlignment(nodeRemove, Pos.CENTER);
	}
	
	public boolean isClickOnNodeEdit(MouseEvent event) {
		return event.getPickResult().getIntersectedNode().getParent().equals(nodeEdit);
	}
	public boolean isClickOnNodeRemove(MouseEvent event) {
		return event.getPickResult().getIntersectedNode().getParent().equals(nodeRemove);
	}
}
