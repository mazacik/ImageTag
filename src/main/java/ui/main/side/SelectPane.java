package ui.main.side;

import base.CustomList;
import base.entity.Entity;
import base.tag.TagList;
import control.Select;
import control.filter.Filter;
import control.reload.Reload;
import enums.Direction;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Background;
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
		
		getTagNodesAll().forEach(tagNode -> this.refreshNodeColor(tagNode, stringListIntersect, stringListUnion));
		
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
	
	private TagNode bestMatch = null;
	private TagNode getBestMatch(CustomList<TagNode> tagNodes, String query) {
		for (TagNode tagNode : tagNodes) {
			if (tagNode.isLast() && tagNode.getText().toLowerCase().startsWith(query)) {
				return tagNode;
			}
		}
		return null;
	}
	private ChangeListener<? super String> getSearchTextListener() {
		return (ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (bestMatch != null) bestMatch.setBackground(Background.EMPTY);
			
			CustomList<TagNode> tagNodesAll = getTagNodesAll();
			tagNodesAll.forEach(TagNode::close);
			
			if (newValue.length() > 0) {
				bestMatch = getBestMatch(tagNodesAll, newValue.toLowerCase());
				if (bestMatch != null) {
					for (int i = 0; i < bestMatch.getLevels().size(); i++) {
						getTagNode(bestMatch.getLevels().get(i).toLowerCase(), i).open();
					}
					bestMatch.setBackground(Decorator.getBackgroundSecondary());
				}
			}
		};
	}
	private EventHandler<ActionEvent> getSearchOnAction() {
		return event -> {
			bestMatch.setBackground(Background.EMPTY);
			getTagNodesAll().forEach(TagNode::close);
			nodeSearch.clear();
			Select.getEntities().addTag(TagList.getMain().getTag(bestMatch.getStringValue()).getID());
			
			Reload.start();
		};
	}
	
	private SelectPane() {
		nodeTitle.setBorder(Decorator.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		nodeSearch = new EditNode("", "Quick Search");
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
