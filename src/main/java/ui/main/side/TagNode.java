package ui.main.side;

import base.CustomList;
import base.tag.Tag;
import control.filter.Filter;
import control.reload.Reload;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import ui.custom.ClickMenu;
import ui.decorator.Decorator;
import ui.node.NodeText;
import ui.override.HBox;
import ui.override.VBox;

public class TagNode extends VBox {
	private CustomList<TagNode> subNodes;
	
	private NodeText nodeToggle;
	private NodeText nodeText;
	private HBox hBoxMain;
	
	private static final int PADDING_DEFAULT = 10;
	private static final int PADDING_INCREMENT = 10;
	
	private CustomList<String> levels;
	public CustomList<String> getLevels() {
		return levels;
	}
	
	public String getStringValue() {
		StringBuilder string = new StringBuilder();
		for (String level : levels) {
			string.append(level);
		}
		return string.toString();
	}
	
	public TagNode(SidePaneBase parentPane, Tag tag, int level) {
		this.subNodes = new CustomList<>();
		this.levels = new CustomList<>();
		
		for (int i = 0; i <= level; i++) {
			levels.add(tag.getLevel(i));
		}
		
		int paddingLeft = PADDING_DEFAULT + PADDING_INCREMENT * level;
		
		nodeToggle = new NodeText("+ ", false, false, false, false);
		nodeToggle.setPadding(new Insets(0, PADDING_DEFAULT, 0, paddingLeft));
		nodeToggle.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					if (!this.isOpen()) {
						this.open();
					} else {
						this.close();
					}
					break;
				case SECONDARY:
					break;
			}
		});
		
		nodeText = new NodeText(tag.getLevel(level), false, false, false, false);
		
		hBoxMain = new HBox(nodeToggle, nodeText);
		this.getChildren().add(hBoxMain);
		
		hBoxMain.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> this.setBackground(Decorator.getBackgroundSecondary()));
		hBoxMain.addEventFilter(MouseEvent.MOUSE_EXITED, event -> this.setBackground(Background.EMPTY));
		hBoxMain.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					if (event.getX() > nodeToggle.getWidth()) {
						if (parentPane == PaneFilter.getInstance()) {
							clickFilter();
						} else if (parentPane == PaneSelect.getInstance()) {
							clickSelect();
						}
						Reload.start();
					}
					break;
				case SECONDARY:
					ClickMenu.setTagNode(this);
					break;
			}
		});
		
		ClickMenu.install(hBoxMain, MouseButton.SECONDARY, ClickMenu.StaticInstance.TAG_GROUP);
	}
	
	private void clickFilter() {
		String stringValue = getStringValue();
		
		if (Filter.getListManager().isWhitelisted(stringValue)) {
			Filter.getListManager().blacklist(stringValue);
			nodeText.setTextFill(Decorator.getColorNegative());
		} else if (Filter.getListManager().isBlacklisted(stringValue)) {
			Filter.getListManager().unlist(stringValue);
			nodeText.setTextFill(Decorator.getColorPrimary());
		} else {
			Filter.getListManager().whitelist(stringValue);
			nodeText.setTextFill(Decorator.getColorPositive());
		}
	}
	private void clickSelect() {
		//todo maybe color changing here
	}
	
	public void open() {
		for (TagNode subNode : subNodes) {
			if (subNode.isLast()) {
				subNode.nodeToggle.setVisible(false);
			}
		}
		
		this.getChildren().retainAll(hBoxMain);
		this.getChildren().addAll(subNodes);
		nodeToggle.setText("− ");
	}
	public void close() {
		this.getChildren().retainAll(hBoxMain);
		nodeToggle.setText("+ ");
	}
	
	public CustomList<TagNode> getSubNodes() {
		return subNodes;
	}
	public int getLevel() {
		return levels.size() - 1;
	}
	public String getText() {
		return nodeText.getText();
	}
	public boolean isOpen() {
		return this.getChildren().size() > 1;
	}
	
	public boolean isLast() {
		return subNodes.isEmpty();
	}
}
