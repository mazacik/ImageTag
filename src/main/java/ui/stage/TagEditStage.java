package ui.stage;

import base.CustomList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.decorator.Decorator;
import ui.node.EditNode;
import ui.node.TextNode;
import ui.override.HBox;
import ui.override.VBox;

public class TagEditStage extends AbstractStage {
	private static final CustomList<LevelNode> nodeList;
	private static final CustomList<String> returnList;
	
	private static final VBox boxNodes;
	
	private static final TextNode nodeOK;
	private static final TextNode nodeCancel;
	
	static {
		returnList = new CustomList<>();
		nodeList = new CustomList<>();
		
		boxNodes = new VBox();
		boxNodes.setSpacing(5);
		boxNodes.setAlignment(Pos.CENTER);
		boxNodes.setPadding(new Insets(5));
		boxNodes.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				returnValue();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				getInstance().close();
			}
		});
		
		nodeOK = new TextNode("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			if (checkEditNodes()) {
				returnValue();
			}
		});
		
		nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, getInstance()::close);
	}
	
	private static void returnValue() {
		if (checkEditNodes()) {
			nodeList.forEach(levelNode -> returnList.add(levelNode.editNode.getText()));
			getInstance().close();
		}
	}
	public static boolean checkEditNodes() {
		for (LevelNode levelNode : nodeList) {
			if (levelNode.editNode.getText().trim().isEmpty()) {
				getInstance().setErrorMessage("Level " + (nodeList.indexOf(levelNode) + 1) + " is empty.");
				return false;
			}
		}
		return true;
	}
	
	public static CustomList<String> show(CustomList<String> levelsBefore) {
		nodeList.clear();
		if (levelsBefore == null) {
			nodeList.add(new LevelNode(""));
		} else {
			for (String string : levelsBefore) {
				nodeList.add(new LevelNode(string));
			}
		}
		boxNodes.getChildren().setAll(nodeList);
		
		returnList.clear();
		
		getInstance().setErrorMessage("");
		getInstance().showAndWait();
		
		if (!returnList.isEmpty()) {
			return returnList;
		} else {
			return null;
		}
	}
	
	private TagEditStage() {
		super("Tag Editor", true);
		
		this.setWidth(640);
		this.setHeight(480);
		
		setRoot(boxNodes);
		setButtons(nodeOK, nodeCancel);
	}
	private static class Loader {
		private static final TagEditStage INSTANCE = new TagEditStage();
	}
	public static TagEditStage getInstance() {
		return Loader.INSTANCE;
	}
	
	private static class LevelNode extends HBox {
		private final EditNode editNode;
		
		public LevelNode(String edit) {
			editNode = new EditNode(edit);
			editNode.setPrefWidth(200);
			editNode.setBorder(Decorator.getBorder(1, 1, 1, 1));
			
			editNode.textProperty().addListener((observable, oldValue, newValue) -> {
				if (nodeList.getLast() == this) {
					if (!newValue.isEmpty()) {
						LevelNode levelNode = new LevelNode("");
						nodeList.add(levelNode);
						boxNodes.getChildren().add(levelNode);
					}
				}
			});
			
			TextNode nodeMoveUp = new TextNode("V", true, true, false, true);
			nodeMoveUp.setRotate(180);
			nodeMoveUp.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
				int currentIndex = nodeList.indexOf(this);
				if (currentIndex > 0) {
					nodeList.remove(this);
					nodeList.add(currentIndex - 1, this);
					
					boxNodes.getChildren().remove(this);
					boxNodes.getChildren().add(currentIndex - 1, this);
				}
			});
			
			TextNode nodeRemove = new TextNode("X", true, true, false, true);
			nodeRemove.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, this::removeLevelNode);
			
			TextNode nodeMoveDown = new TextNode("V", true, true, false, true);
			nodeMoveDown.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
				int currentIndex = nodeList.indexOf(this);
				if (currentIndex < nodeList.size() - 1) {
					nodeList.remove(this);
					nodeList.add(currentIndex + 1, this);
					
					boxNodes.getChildren().remove(this);
					boxNodes.getChildren().add(currentIndex + 1, this);
					
					if (nodeList.getLast() == this && !this.editNode.getText().isEmpty()) {
						LevelNode levelNode = new LevelNode("");
						nodeList.add(levelNode);
						boxNodes.getChildren().add(levelNode);
					}
				}
			});
			
			HBox boxButtons = new HBox(nodeMoveUp, nodeRemove, nodeMoveDown);
			boxButtons.setVisible(false);
			
			this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> boxButtons.setVisible(true));
			this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> boxButtons.setVisible(false));
			
			this.setAlignment(Pos.CENTER);
			this.getChildren().addAll(editNode, boxButtons);
		}
		
		private void removeLevelNode() {
			if (nodeList.size() == 1) {
				this.editNode.setText("");
			} else if (nodeList.getLast() == this && !nodeList.get(nodeList.indexOf(this) - 1).editNode.getText().isEmpty()) {
				this.editNode.setText("");
			} else {
				nodeList.remove(this);
				boxNodes.getChildren().remove(this);
			}
		}
	}
}
