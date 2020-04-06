package ui.stage;

import base.CustomList;
import base.tag.TagList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.Root;
import ui.decorator.Decorator;
import ui.node.EditNode;
import ui.node.textnode.TextNode;
import ui.override.HBox;
import ui.override.VBox;

public class TagEditStage extends AbstractStageBase {
	private final CustomList<LevelNode> nodeList;
	private CustomList<String> returnList;
	
	private final VBox boxNodes;
	
	public TagEditStage() {
		super("Tag Editor", true);
		
		returnList = new CustomList<>();
		nodeList = new CustomList<>();
		
		boxNodes = new VBox();
		boxNodes.setSpacing(5);
		boxNodes.setAlignment(Pos.CENTER);
		boxNodes.setPadding(new Insets(5));
		boxNodes.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.returnValue();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				this.close();
			}
		});
		
		TextNode nodeOK = new TextNode("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::returnValue);
		
		TextNode nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		this.setWidth(640);
		this.setHeight(480);
		
		setRoot(boxNodes);
		setButtons(nodeOK, nodeCancel);
	}
	
	private TagList tagListToSearchIn;
	public CustomList<String> showCreate(CustomList<String> levels) {
		return show(Root.TAGLIST, levels, Mode.CREATE);
	}
	public CustomList<String> showEdit(TagList tagListToSearchIn, CustomList<String> levels) {
		return show(tagListToSearchIn, levels, Mode.EDIT);
	}
	private CustomList<String> show(TagList tagListToSearchIn, CustomList<String> levels, Mode mode) {
		nodeList.clear();
		if (levels != null) {
			for (String level : levels) {
				if (!level.isEmpty()) {
					nodeList.addImpl(new LevelNode(level));
				}
			}
		}
		nodeList.addImpl(new LevelNode(""));
		boxNodes.getChildren().setAll(nodeList);
		if (levels != null) {
			if (mode == Mode.CREATE) {
				nodeList.get(levels.size()).editNode.requestFocus();
			} else {
				EditNode editNode = nodeList.get(levels.size() - 1).editNode;
				editNode.selectAll();
				editNode.requestFocus();
			}
		}
		
		this.tagListToSearchIn = tagListToSearchIn;
		this.returnList = new CustomList<>();
		
		this.setErrorMessage("");
		this.showAndWait();
		
		return returnList;
	}
	private void returnValue() {
		CustomList<String> helperList = new CustomList<>();
		
		nodeList.forEach(levelNode -> {
			String string = levelNode.editNode.getText().trim();
			if (!string.isEmpty()) {
				helperList.addImpl(string);
			}
		});
		
		if (tagListToSearchIn.doesAnyTagStartWith(helperList)) {
			this.setErrorMessage("Tag already exists.");
		} else if (tagListToSearchIn.isAnyTagSubstringOf(helperList)) {
			this.setErrorMessage("Cannot extend existing tag.");
		} else {
			this.returnList = helperList;
			this.close();
		}
	}
	
	private class LevelNode extends HBox {
		private final EditNode editNode;
		
		public LevelNode(String startText) {
			editNode = new EditNode(startText, "");
			editNode.setPrefWidth(200);
			editNode.setBorder(Decorator.getBorder(1, 1, 1, 1));
			
			editNode.textProperty().addListener((observable, oldValue, newValue) -> {
				if (nodeList.getLastImpl() == this) {
					if (!newValue.isEmpty()) {
						LevelNode levelNode = new LevelNode("");
						nodeList.addImpl(levelNode);
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
					
					if (nodeList.getLastImpl() == this && !this.editNode.getText().isEmpty()) {
						LevelNode levelNode = new LevelNode("");
						nodeList.addImpl(levelNode);
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
			} else if (nodeList.getLastImpl() == this && !nodeList.get(nodeList.indexOf(this) - 1).editNode.getText().isEmpty()) {
				this.editNode.setText("");
			} else {
				nodeList.remove(this);
				boxNodes.getChildren().remove(this);
			}
		}
	}
	
	enum Mode {
		CREATE,
		EDIT
	}
}
