package frontend.component.side.select;

import backend.BaseList;
import backend.misc.Direction;
import backend.reload.Reload;
import frontend.component.side.TagNode;
import frontend.decorator.DecoratorUtil;
import frontend.node.EditNode;
import frontend.node.ListBox;
import frontend.node.textnode.TextNode;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import main.Main;

import java.util.Comparator;

public class SelectionTagsPane extends VBox {
	private final EditNode searchNode;
	private boolean textPropertyLock = false;
	private final Popup searchPopup;
	private final ListBox searchBox;
	
	private final TilePane tilePane = new TilePane();
	
	public SelectionTagsPane() {
		searchNode = new EditNode();
		searchNode.setPromptText("Quick Search");
		searchNode.setPadding(new Insets(2));
		
		searchBox = new ListBox();
		searchBox.setMaxHeight(300);
		searchBox.setBorder(DecoratorUtil.getBorder(1));
		searchBox.setBackground(DecoratorUtil.getBackgroundPrimary());
		searchBox.minWidthProperty().bind(searchNode.widthProperty());
		
		searchPopup = new Popup();
		searchPopup.getContent().add(searchBox);
		
		tilePane.setVgap(3);
		tilePane.setHgap(3);
		tilePane.setAlignment(Pos.CENTER);
		tilePane.setPadding(new Insets(3));
		
		initEvents();
		getChildren().addAll(tilePane);
	}
	private void initEvents() {
		searchNode.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
			// TODO when main window gets moved around, popup doesn't follow it
			Bounds boundsOnScreen = searchNode.localToScreen(searchNode.parentToLocal(newValue));
			searchPopup.setAnchorX(boundsOnScreen.getMinX());
			searchPopup.setAnchorY(boundsOnScreen.getMaxY() - 1);
		});
		
		searchNode.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				searchPopup.hide();
			}
		});
		
		searchNode.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!textPropertyLock) {
				searchBox.getNodes().clear();
				
				String newValueLowerCase = newValue.toLowerCase();
				for (String tag : Main.TAGLIST) {
					String tagLowerCase = tag.toLowerCase();
					if (tagLowerCase.contains(newValueLowerCase)) {
						addTextNode(tag);
					}
				}
				searchBox.moveFocus(Direction.DOWN);
				
				showSearchPopup();
			}
		});
		EventHandler<KeyEvent> keyPressEvent = event -> {
			switch (event.getCode()) {
				case ENTER:
					event.consume();
					Region region = searchBox.getCurrentFocus();
					if (region instanceof TextNode) {
						TextNode currentFocus = (TextNode) region;
						if (searchPopup.isShowing()) {
							textPropertyLock = true;
							searchPopup.hide();
							searchNode.setText(currentFocus.getText());
							textPropertyLock = false;
						} else {
							String tag = currentFocus.getText();
							if (Main.TAGLIST.contains(tag)) {
								Main.SELECT.addTag(tag);
								searchNode.clear();
								Reload.start();
							}
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
		
		textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			textPropertyLock = true;
			searchPopup.hide();
			searchNode.setText(textNode.getText());
			textPropertyLock = false;
		});
		textNode.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
			if (textNode != searchBox.getCurrentFocus()) {
				if (searchBox.getCurrentFocus() != null) {
					searchBox.getCurrentFocus().setBackground(DecoratorUtil.getBackgroundPrimary());
				}
				searchBox.setCurrentFocus(textNode);
				textNode.setBackground(DecoratorUtil.getBackgroundSecondary());
			}
		});
		
		searchBox.getNodes().add(textNode);
	}
	
	public void refresh() {
		reload();
	}
	public boolean reload() {
		BaseList<TagNode> tagNodes = new BaseList<>();
		
		Main.SELECT.getTagListWithCount().forEach(pair -> tagNodes.add(new TagNode(pair.getKey(), pair.getValue())));
		tagNodes.sort(Comparator.comparing(TagNode::getText));
		
		tilePane.getChildren().clear();
		tilePane.getChildren().add(searchNode);
		tilePane.getChildren().addAll(tagNodes);
		
		searchBox.getNodes().clear();
		for (String tag : Main.TAGLIST) {
			addTextNode(tag);
		}
		searchBox.moveFocus(Direction.DOWN);
		
		return true;
	}
	
	private void showSearchPopup() {
		Bounds bounds = searchNode.localToScreen(searchNode.getBoundsInLocal());
		searchPopup.show(searchNode, bounds.getMinX(), bounds.getMaxY() - 1);
	}
	
	public EditNode getSearchNode() {
		return searchNode;
	}
}
