package ui.main.side;

import base.CustomList;
import base.entity.Entity;
import base.tag.Tag;
import base.tag.TagList;
import control.Select;
import control.filter.Filter;
import control.reload.Reload;
import enums.Direction;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import ui.custom.ClickMenu;
import ui.decorator.Decorator;
import ui.node.EditNode;
import ui.node.NodeTemplates;
import ui.node.TextNode;

public class SelectPane extends SidePaneBase {
	private EditNode nodeSearch;
	
	public boolean refresh() {
		refreshTitle();
		
		CustomList<String> stringListIntersect = new CustomList<>();
		Select.getEntities().getTagListIntersect().forEach(tag -> stringListIntersect.add(tag.getStringValue()));
		CustomList<String> stringListUnion = new CustomList<>();
		Select.getEntities().getTagList().forEach(tag -> stringListUnion.add(tag.getStringValue()));
		
		getTagNodesComplete().forEach(tagNode -> this.refreshNodeColor(tagNode, stringListIntersect, stringListUnion));
		
		return true;
	}
	private void refreshNodeColor(TagNode tagNode, CustomList<String> stringListIntersect, CustomList<String> stringListUnion) {
		String stringNode = tagNode.getStringValue();
		for (String stringTag : stringListIntersect) {
			if (stringTag.startsWith(stringNode)) {
				tagNode.setTextFill(Decorator.getColorPositive());
				return;
			}
		}
		for (String stringTag : stringListUnion) {
			if (stringTag.startsWith(stringNode)) {
				tagNode.setTextFill(Decorator.getColorUnion());
				return;
			}
		}
		tagNode.setTextFill(Decorator.getColorPrimary());
	}
	private void refreshTitle() {
		int hiddenTilesCount = 0;
		for (Entity entity : Select.getEntities()) {
			if (!Filter.getEntities().contains(entity)) {
				hiddenTilesCount++;
			}
		}
		
		String text = "Selection: " + Select.getEntities().size();
		if (hiddenTilesCount > 0) text += " (" + hiddenTilesCount + " hidden)";
		
		nodeTitle.setText(text);
	}
	
	private Tag bestMatch = null;
	private int previousLength = 0;
	private boolean enabled = true;
	private ChangeListener<? super String> getSearchTextListener() {
		return (ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (enabled) {
				bestMatch = null;
				if (newValue.length() > 3) {
					//	simple check if the last level of any tag starts with query
					newValue = newValue.toLowerCase();
					for (Tag tag : TagList.getMain()) {
						if (tag.getLevelLast().toLowerCase().startsWith(newValue)) {
							bestMatch = tag;
							break;
						}
					}
					if (bestMatch != null) {
						int caretPos = nodeSearch.getCaretPosition();
						enabled = false;
						nodeSearch.setText(bestMatch.getLevelLast());
						enabled = true;
						if (newValue.length() > previousLength) {
							Platform.runLater(() -> nodeSearch.selectRange(nodeSearch.getText().length(), caretPos + 1));
						} else {
							Platform.runLater(() -> nodeSearch.selectRange(nodeSearch.getText().length(), caretPos - 1));
						}
					}
				}
				previousLength = newValue.length();
			}
		};
	}
	private EventHandler<ActionEvent> getSearchOnAction() {
		return event -> {
			nodeSearch.clear();
			Select.getEntities().addTag(bestMatch.getID());
			Reload.start();
		};
	}
	
	private SelectPane() {
		nodeTitle.setBorder(Decorator.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		nodeSearch = new EditNode("Quick Search");
		nodeSearch.setBorder(Decorator.getBorder(0, 0, 1, 0));
		nodeSearch.prefWidthProperty().bind(this.widthProperty());
		nodeSearch.textProperty().addListener(this.getSearchTextListener());
		nodeSearch.setOnAction(this.getSearchOnAction());
		
		TextNode nodeSelectAll = NodeTemplates.SELECTION_SET_ALL.get();
		TextNode nodeSelectNone = NodeTemplates.SELECTION_SET_NONE.get();
		ClickMenu.install(nodeTitle, Direction.LEFT, nodeSelectAll, nodeSelectNone);
		
		this.setBorder(Decorator.getBorder(0, 0, 0, 1));
		this.getChildren().addAll(nodeTitle, nodeSearch, scrollPane);
	}
	private static class Loader {
		private static final SelectPane INSTANCE = new SelectPane();
	}
	public static SelectPane getInstance() {
		return Loader.INSTANCE;
	}
	
	public EditNode getNodeSearch() {
		return nodeSearch;
	}
}
