package application.gui.nodes.custom;

import application.gui.decorator.ColorUtil;
import application.gui.nodes.simple.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.File;

public class IntroSceneNode extends BorderPane {
	private String workingDirectory;
	private TextNode nodeRemove;
	
	public IntroSceneNode(String projectFile, String workingDirectory) {
		this.workingDirectory = workingDirectory;
		
		String projectNameWithExtension = projectFile.substring(projectFile.lastIndexOf(File.separatorChar) + 1);
		String projectNameWithoutExtension = projectNameWithExtension.substring(0, projectNameWithExtension.lastIndexOf('.'));
		
		TextNode nodeProjectFile = new TextNode(projectNameWithoutExtension, false, false, false);
		TextNode nodeWorkingDirectory = new TextNode(workingDirectory, false, false, false);
		nodeRemove = new TextNode("✕", false, true, false);
		nodeRemove.setFont(new Font(20));
		nodeRemove.setVisible(false);
		
		VBox vBox = new VBox(nodeProjectFile, nodeWorkingDirectory);
		vBox.setAlignment(Pos.CENTER_LEFT);
		
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			this.setBackground(ColorUtil.getBackgroundAlt());
			this.setCursor(Cursor.HAND);
			nodeRemove.setVisible(true);
		});
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			this.setBackground(Background.EMPTY);
			this.setCursor(Cursor.DEFAULT);
			nodeRemove.setVisible(false);
		});
		
		this.setCenter(vBox);
		this.setRight(nodeRemove);
		this.setPadding(new Insets(10));
		setAlignment(nodeRemove, Pos.CENTER);
	}
	
	public String getWorkingDirectory() {
		return workingDirectory;
	}
	public TextNode getNodeRemove() {
		return nodeRemove;
	}
}
