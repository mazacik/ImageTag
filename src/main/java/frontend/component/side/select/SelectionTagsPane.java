package frontend.component.side.select;

import backend.BaseList;
import backend.misc.Direction;
import backend.reload.Reload;
import frontend.UserInterface;
import frontend.component.side.SelectTagNode;
import frontend.component.side.TagNode;
import frontend.decorator.DecoratorUtil;
import frontend.node.EditNode;
import frontend.node.ListBox;
import frontend.node.textnode.TextNode;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import main.Main;

import java.util.Comparator;

public class SelectionTagsPane extends VBox {
	private final EditNode searchNode;
	private final ListBox searchBox;
	
	private final ListBox tagNodeBox = new ListBox();
	
	public SelectionTagsPane() {
		searchNode = new EditNode();
		searchNode.setPromptText("Quick Search");
		searchNode.setPadding(new Insets(3));
		
		searchBox = new ListBox();
		searchBox.setBorder(DecoratorUtil.getBorder(1));
		searchBox.setBackground(DecoratorUtil.getBackgroundDefault());
		
		tagNodeBox.getBox().setSpacing(3);
		tagNodeBox.getBox().setAlignment(Pos.CENTER);
		tagNodeBox.getBox().setPadding(new Insets(3));
		
		initEvents();
		getChildren().addAll(tagNodeBox);
	}
	private void initEvents() {
		searchNode.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				hideSearchPopup();
			}
		});
		
		searchNode.textProperty().addListener((observable, oldValue, newValue) -> {
			searchBox.getBox().getChildren().clear();
			
			String newValueLowerCase = newValue.toLowerCase();
			for (String tag : Main.TAGLIST) {
				String tagLowerCase = tag.toLowerCase();
				if (tagLowerCase.contains(newValueLowerCase)) {
					this.addTextNode(tag);
				}
			}
			searchBox.moveFocus(Direction.DOWN);
			
			showSearchPopup();
		});
		EventHandler<KeyEvent> keyPressEvent = event -> {
			switch (event.getCode()) {
				case ESCAPE:
					event.consume();
					UserInterface.getCenterPane().requestFocus();
					break;
				case ENTER:
					event.consume();
					Region region = searchBox.getCurrentFocus();
					if (searchBox.getBox().getChildren().isEmpty()) {
						String tag = searchNode.getText();
						if (tag.length() > 0) {
							Main.TAGLIST.add(tag);
							Main.SELECT.addTag(tag);
							searchNode.clear();
							Reload.start();
						}
					} else if (region instanceof TextNode) {
						String tag = ((TextNode) region).getText();
						if (Main.TAGLIST.contains(tag)) {
							Main.SELECT.addTag(tag);
							searchNode.clear();
							Reload.start();
						}
					}
					break;
				case UP:
					event.consume();
					searchBox.moveFocus(Direction.UP);
					break;
				case TAB:
				case DOWN:
					event.consume();
					searchBox.moveFocus(Direction.DOWN);
					break;
				default:
					break;
			}
		};
		
		searchNode.addEventFilter(KeyEvent.KEY_PRESSED, keyPressEvent);
		searchBox.addEventFilter(KeyEvent.KEY_PRESSED, keyPressEvent);
		
		searchNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			searchNode.setText("");
			showSearchPopup();
		});
	}
	
	private void addTextNode(String tag) {
		TextNode textNode = new TextNode(tag);
		textNode.setAlignment(Pos.CENTER_LEFT);
		textNode.setMaxWidth(Double.MAX_VALUE);
		textNode.setPadding(new Insets(0, 5, 0, 5));
		
		textNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (Main.TAGLIST.contains(tag)) {
					Main.SELECT.addTag(tag);
					searchNode.clear();
					Reload.start();
				}
			}
		});
		textNode.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
			if (textNode != searchBox.getCurrentFocus()) {
				if (searchBox.getCurrentFocus() != null) {
					searchBox.getCurrentFocus().setBackground(DecoratorUtil.getBackgroundDefault());
				}
				searchBox.setCurrentFocus(textNode);
				textNode.setBackground(DecoratorUtil.getBackgroundDefaultDark());
			}
		});
		
		searchBox.getBox().getChildren().add(textNode);
	}
	
	public boolean reload() {
		BaseList<TagNode> tagNodes = new BaseList<>();
		
		BaseList<String> union = Main.SELECT.getTagList();
		BaseList<String> intersection = Main.SELECT.getTagListIntersect();
		
		Main.SELECT.getTagListWithCount().forEach(pair -> {
			String tag = pair.getKey();
			SelectTagNode tagNode = new SelectTagNode(tag);
			
			if (union.contains(tag)) tagNode.getTextNode().setTextFill(DecoratorUtil.getColorUnion());
			if (intersection.contains(tag)) tagNode.getTextNode().setTextFill(DecoratorUtil.getColorPositive());
			
			tagNodes.add(tagNode);
		});
		tagNodes.sort(Comparator.comparing(TagNode::getText));
		
		tagNodeBox.getBox().getChildren().clear();
		tagNodeBox.getBox().getChildren().add(searchNode);
		tagNodeBox.getBox().getChildren().addAll(tagNodes);
		
		searchBox.getBox().getChildren().clear();
		for (String tag : Main.TAGLIST) this.addTextNode(tag);
		searchBox.moveFocus(Direction.DOWN);
		
		return true;
	}
	
	private void showSearchPopup() {
		tagNodeBox.getBox().getChildren().setAll(searchNode, searchBox);
	}
	private void hideSearchPopup() {
		reload();
	}
	
	public EditNode getSearchNode() {
		return searchNode;
	}
}
