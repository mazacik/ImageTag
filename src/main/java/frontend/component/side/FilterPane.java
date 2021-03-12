package frontend.component.side;

import backend.BaseList;
import backend.misc.Direction;
import backend.reload.Notifier;
import backend.reload.Reload;
import frontend.UserInterface;
import frontend.decorator.DecoratorUtil;
import frontend.node.EditNode;
import frontend.node.ListBox;
import frontend.node.SeparatorNode;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import frontend.node.textnode.TextNodeTemplates;
import frontend.stage.primary.scene.MainSceneMode;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import main.Main;

public class FilterPane extends VBox {
	private boolean bHidden = false;
	
	private final TextNode nodeText;
	
	private final EditNode whitelistSearchNode;
	private final ListBox whitelistSearchBox;
	private final ListBox whitelistTagNodeBox = new ListBox();
	
	private final EditNode blacklistSearchNode;
	private final ListBox blacklistSearchBox;
	private final ListBox blacklistTagNodeBox = new ListBox();
	
	public FilterPane() {
		nodeText = new TextNode("", false, false, false, true);
		
		whitelistSearchNode = new EditNode();
		whitelistSearchNode.setPromptText("Whitelist Quick Search");
		whitelistSearchNode.setPadding(new Insets(3));
		
		whitelistSearchBox = new ListBox();
		whitelistSearchBox.setBorder(DecoratorUtil.getBorder(1));
		whitelistSearchBox.setBackground(DecoratorUtil.getBackgroundDefault());
		
		whitelistTagNodeBox.getBox().setSpacing(3);
		whitelistTagNodeBox.getBox().setAlignment(Pos.CENTER);
		whitelistTagNodeBox.getBox().setPadding(new Insets(3));
		
		blacklistSearchNode = new EditNode();
		blacklistSearchNode.setPromptText("Blacklist Quick Search");
		blacklistSearchNode.setPadding(new Insets(3));
		
		blacklistSearchBox = new ListBox();
		blacklistSearchBox.setBorder(DecoratorUtil.getBorder(1));
		blacklistSearchBox.setBackground(DecoratorUtil.getBackgroundDefault());
		
		blacklistTagNodeBox.getBox().setSpacing(3);
		blacklistTagNodeBox.getBox().setAlignment(Pos.CENTER);
		blacklistTagNodeBox.getBox().setPadding(new Insets(3));
		
		this.setMinWidth(UserInterface.SIDE_WIDTH);
		this.setMaxWidth(UserInterface.SIDE_WIDTH);
	}
	
	public void initialize() {
		TextNode btnReset = new TextNode("⟲", true, true, false, true);
		btnReset.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Main.FILTER.getFilterListManager().clear();
			Reload.notify(Notifier.FILTER_CHANGED);
			Reload.start();
		});
		
		nodeText.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(nodeText, Priority.ALWAYS);
		
		TextNode btnSettings = new TextNode("⁝", true, true, false, true);
		btnSettings.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> UserInterface.getStage().getMainScene().setMode(MainSceneMode.SETTINGS));
		
		HBox boxButtons = new HBox();
		boxButtons.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		boxButtons.getChildren().add(btnReset);
		boxButtons.getChildren().add(TextNodeTemplates.TAG_CREATE.get());
		boxButtons.getChildren().add(nodeText);
		boxButtons.getChildren().add(btnSettings);
		
		String cHide = "←";
		String cShow = "→";
		TextNode btnHide = new TextNode(cHide, true, true, false, true);
		btnHide.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			bHidden = !bHidden;
			if (bHidden) {
				btnHide.setText(cShow);
				this.getChildren().setAll(btnHide);
				this.setMinWidth(btnHide.getWidth() + 1);
				this.setMaxWidth(btnHide.getWidth() + 1);
			} else {
				btnHide.setText(cHide);
				this.getChildren().setAll(boxButtons, whitelistTagNodeBox, new SeparatorNode(), blacklistTagNodeBox);
				boxButtons.getChildren().add(btnHide);
				this.setMinWidth(UserInterface.SIDE_WIDTH);
				this.setMaxWidth(UserInterface.SIDE_WIDTH);
			}
		});
		
		boxButtons.getChildren().add(btnHide);
		
		initWhitelistEvents();
		initBlacklistEvents();
		this.getChildren().addAll(boxButtons, whitelistTagNodeBox, new SeparatorNode(), blacklistTagNodeBox);
	}
	
	public boolean reload() {
		nodeText.setText("Filter: " + Main.FILTER.size());
		
		BaseList<TagNode> whitelistTagNodes = new BaseList<>();
		Main.FILTER.getFilterListManager().getWhitelist().forEach(tag -> whitelistTagNodes.add(new FilterTagNode(tag)));
		
		whitelistTagNodeBox.getBox().getChildren().clear();
		whitelistTagNodeBox.getBox().getChildren().add(whitelistSearchNode);
		whitelistTagNodeBox.getBox().getChildren().addAll(whitelistTagNodes);
		
		whitelistSearchBox.getBox().getChildren().clear();
		for (String tag : Main.TAGLIST) this.whitelistSearchBoxAddTextNode(tag);
		whitelistSearchBox.moveFocus(Direction.DOWN);
		
		BaseList<TagNode> blacklistTagNodes = new BaseList<>();
		Main.FILTER.getFilterListManager().getBlacklist().forEach(tag -> blacklistTagNodes.add(new FilterTagNode(tag)));
		
		blacklistTagNodeBox.getBox().getChildren().clear();
		blacklistTagNodeBox.getBox().getChildren().add(blacklistSearchNode);
		blacklistTagNodeBox.getBox().getChildren().addAll(blacklistTagNodes);
		
		blacklistSearchBox.getBox().getChildren().clear();
		for (String tag : Main.TAGLIST) this.blacklistSearchBoxAddTextNode(tag);
		blacklistSearchBox.moveFocus(Direction.DOWN);
		
		return true;
	}
	
	private void initWhitelistEvents() {
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			if (!event.isPrimaryButtonDown()) {
				hideSearchPopup();
			}
		});
		
		whitelistSearchNode.textProperty().addListener((observable, oldValue, newValue) -> {
			whitelistSearchBox.getBox().getChildren().clear();
			
			String newValueLowerCase = newValue.toLowerCase();
			for (String tag : Main.TAGLIST) {
				String tagLowerCase = tag.toLowerCase();
				if (tagLowerCase.contains(newValueLowerCase)) {
					whitelistSearchBoxAddTextNode(tag);
				}
			}
			whitelistSearchBox.moveFocus(Direction.DOWN);
			
			showWhitelistSearchPopup();
		});
		EventHandler<KeyEvent> keyPressEvent = event -> {
			switch (event.getCode()) {
				case ESCAPE:
					event.consume();
					UserInterface.getCenterPane().requestFocus();
					break;
				case ENTER:
					event.consume();
					Region region = whitelistSearchBox.getCurrentFocus();
					if (region instanceof TextNode) {
						String tag = ((TextNode) region).getText();
						if (Main.TAGLIST.contains(tag)) {
							Main.FILTER.getFilterListManager().whitelist(tag);
							whitelistSearchNode.clear();
							Reload.start();
						}
					}
					break;
				case UP:
					event.consume();
					whitelistSearchBox.moveFocus(Direction.UP);
					break;
				case TAB:
				case DOWN:
					event.consume();
					whitelistSearchBox.moveFocus(Direction.DOWN);
					break;
				default:
					break;
			}
		};
		
		whitelistSearchNode.addEventFilter(KeyEvent.KEY_PRESSED, keyPressEvent);
		whitelistSearchBox.addEventFilter(KeyEvent.KEY_PRESSED, keyPressEvent);
		
		whitelistSearchNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			whitelistSearchNode.setText("");
			showWhitelistSearchPopup();
		});
	}
	private void initBlacklistEvents() {
		blacklistSearchNode.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				hideSearchPopup();
			}
		});
		
		blacklistSearchNode.textProperty().addListener((observable, oldValue, newValue) -> {
			blacklistSearchBox.getBox().getChildren().clear();
			
			String newValueLowerCase = newValue.toLowerCase();
			for (String tag : Main.TAGLIST) {
				String tagLowerCase = tag.toLowerCase();
				if (tagLowerCase.contains(newValueLowerCase)) {
					blacklistSearchBoxAddTextNode(tag);
				}
			}
			blacklistSearchBox.moveFocus(Direction.DOWN);
			
			showBlacklistSearchPopup();
		});
		EventHandler<KeyEvent> keyPressEvent = event -> {
			switch (event.getCode()) {
				case ENTER:
					event.consume();
					Region region = blacklistSearchBox.getCurrentFocus();
					if (region instanceof TextNode) {
						String tag = ((TextNode) region).getText();
						if (Main.TAGLIST.contains(tag)) {
							Main.FILTER.getFilterListManager().blacklist(tag);
							blacklistSearchNode.clear();
							Reload.start();
						}
					}
					break;
				case UP:
					event.consume();
					blacklistSearchBox.moveFocus(Direction.UP);
					break;
				case TAB:
				case DOWN:
					event.consume();
					blacklistSearchBox.moveFocus(Direction.DOWN);
					break;
				default:
					break;
			}
		};
		
		blacklistSearchNode.addEventFilter(KeyEvent.KEY_PRESSED, keyPressEvent);
		blacklistSearchBox.addEventFilter(KeyEvent.KEY_PRESSED, keyPressEvent);
		
		blacklistSearchNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			blacklistSearchNode.setText("");
			showBlacklistSearchPopup();
		});
	}
	
	private void whitelistSearchBoxAddTextNode(String tag) {
		TextNode textNode = new TextNode(tag);
		textNode.setAlignment(Pos.CENTER_LEFT);
		textNode.setMaxWidth(Double.MAX_VALUE);
		textNode.setPadding(new Insets(0, 5, 0, 5));
		
		textNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				hideSearchPopup();
				if (Main.TAGLIST.contains(tag)) {
					Main.FILTER.getFilterListManager().whitelist(tag);
					whitelistSearchNode.clear();
					Reload.start();
				}
			}
		});
		textNode.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
			if (textNode != whitelistSearchBox.getCurrentFocus()) {
				if (whitelistSearchBox.getCurrentFocus() != null) {
					whitelistSearchBox.getCurrentFocus().setBackground(DecoratorUtil.getBackgroundDefault());
				}
				whitelistSearchBox.setCurrentFocus(textNode);
				textNode.setBackground(DecoratorUtil.getBackgroundDefaultDark());
			}
		});
		
		whitelistSearchBox.getBox().getChildren().add(textNode);
	}
	private void blacklistSearchBoxAddTextNode(String tag) {
		TextNode textNode = new TextNode(tag);
		textNode.setAlignment(Pos.CENTER_LEFT);
		textNode.setMaxWidth(Double.MAX_VALUE);
		textNode.setPadding(new Insets(0, 5, 0, 5));
		
		textNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				hideSearchPopup();
				if (Main.TAGLIST.contains(tag)) {
					Main.FILTER.getFilterListManager().blacklist(tag);
					blacklistSearchNode.clear();
					Reload.start();
				}
			}
		});
		textNode.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
			if (textNode != blacklistSearchBox.getCurrentFocus()) {
				if (blacklistSearchBox.getCurrentFocus() != null) {
					blacklistSearchBox.getCurrentFocus().setBackground(DecoratorUtil.getBackgroundDefault());
				}
				blacklistSearchBox.setCurrentFocus(textNode);
				textNode.setBackground(DecoratorUtil.getBackgroundDefaultDark());
			}
		});
		
		blacklistSearchBox.getBox().getChildren().add(textNode);
	}
	
	private void showWhitelistSearchPopup() {
		whitelistTagNodeBox.getBox().getChildren().clear();
		whitelistTagNodeBox.getBox().getChildren().add(whitelistSearchNode);
		whitelistTagNodeBox.getBox().getChildren().add(whitelistSearchBox);
	}
	private void showBlacklistSearchPopup() {
		blacklistTagNodeBox.getBox().getChildren().clear();
		blacklistTagNodeBox.getBox().getChildren().add(blacklistSearchNode);
		blacklistTagNodeBox.getBox().getChildren().add(blacklistSearchBox);
	}
	private void hideSearchPopup() {
		reload();
	}
	
	public boolean isHidden() {
		return bHidden;
	}
}
