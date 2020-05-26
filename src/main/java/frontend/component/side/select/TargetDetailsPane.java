package frontend.component.side.select;

import backend.misc.FileUtil;
import frontend.node.override.GridPane;
import frontend.node.textnode.TextNode;
import javafx.scene.image.Image;
import main.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;

public class TargetDetailsPane extends GridPane {
	private final TextNode sizeNode;
	private final TextNode resolutionNode;
	private final TextNode dateCreatedNode;
	private final TextNode dateModifiedNode;
	
	public TargetDetailsPane() {
		TextNode sizeNode1 = new TextNode("Size: ", false, false, false, true);
		TextNode resolutionNode1 = new TextNode("Resulution: ", false, false, false, true);
		TextNode dateCreatedNode1 = new TextNode("Date created: ", false, false, false, true);
		TextNode dateModifiedNode1 = new TextNode("Date modified: ", false, false, false, true);
		
		sizeNode = new TextNode("", false, false, false, true);
		resolutionNode = new TextNode("", false, false, false, true);
		dateCreatedNode = new TextNode("", false, false, false, true);
		dateModifiedNode = new TextNode("", false, false, false, true);
		
		this.add(sizeNode1, 0, 0);
		this.add(resolutionNode1, 0, 1);
		this.add(dateCreatedNode1, 0, 2);
		this.add(dateModifiedNode1, 0, 3);
		
		this.add(sizeNode, 1, 0);
		this.add(resolutionNode, 1, 1);
		this.add(dateCreatedNode, 1, 2);
		this.add(dateModifiedNode, 1, 3);
	}
	
	public void refresh() {
		if (Main.SELECT.getTarget() == null) {
			sizeNode.setText("-");
			resolutionNode.setText("-");
			dateCreatedNode.setText("-");
			dateModifiedNode.setText("-");
		} else {
			String path = FileUtil.getFileEntity(Main.SELECT.getTarget());
			
			Image entityImage = new Image("file:" + path);
			int width = (int) entityImage.getWidth();
			int height = (int) entityImage.getHeight();
			
			BasicFileAttributes attributes = null;
			try {
				attributes = Files.readAttributes(Paths.get(path), BasicFileAttributes.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			sizeNode.setText(Main.SELECT.getTarget().getSize() + "b");
			resolutionNode.setText(width + " x " + height);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			dateCreatedNode.setText(sdf.format(attributes.creationTime().toMillis()));
			dateModifiedNode.setText(sdf.format(attributes.lastModifiedTime().toMillis()));
		}
	}
}
