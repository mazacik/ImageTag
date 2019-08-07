package application.gui.nodes.custom;

import application.gui.decorator.enums.ColorType;
import application.gui.nodes.ColorData;
import application.gui.nodes.simple.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.File;

public class IntroStageNode extends BorderPane {
	private String projectDirectory;
	private String workingDirectory;
	
	private TextNode nodeRemove;
	
	public IntroStageNode(String projectFile, String workingDirectory) {
		ColorData colorData = new ColorData(ColorType.NULL, ColorType.NULL, ColorType.DEF, ColorType.DEF);
		
		this.projectDirectory = new File(projectFile).getParentFile().getAbsolutePath();
		this.workingDirectory = workingDirectory;
		
		String projectNameWithExtension = projectFile.substring(projectFile.lastIndexOf(File.separatorChar) + 1);
		String projectNameWithoutExtension = projectNameWithExtension.substring(0, projectNameWithExtension.lastIndexOf('.'));
		
		TextNode nodeProjectFile = new TextNode(projectNameWithoutExtension, colorData);
		TextNode nodeWorkingDirectory = new TextNode(workingDirectory, colorData);
		
		VBox vBox = new VBox(nodeProjectFile, nodeWorkingDirectory);
		vBox.setAlignment(Pos.CENTER_LEFT);
		
		this.setCenter(vBox);
		nodeRemove = new TextNode("✕", ColorType.NULL, ColorType.NULL, ColorType.DEF, ColorType.ALT);
		this.setRight(nodeRemove);
		setAlignment(nodeRemove, Pos.CENTER);
		
		nodeRemove.setFont(new Font(20));
		nodeRemove.setVisible(false);
		
		this.setPadding(new Insets(10));
	}
	
	public String getProjectDirectory() {
		return projectDirectory;
	}
	public String getWorkingDirectory() {
		return workingDirectory;
	}
	
	public TextNode getNodeRemove() {
		return nodeRemove;
	}
}
