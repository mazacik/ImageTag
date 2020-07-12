package frontend.component.side.select;

import backend.BaseList;
import backend.misc.Direction;
import backend.reload.Reload;
import frontend.component.side.SidePaneBase;
import frontend.component.side.TagNode;
import frontend.decorator.DecoratorUtil;
import frontend.node.EditNode;
import javafx.scene.layout.Background;
import main.Main;

public class SelectionTagsPane extends SidePaneBase {
	private final EditNode nodeSearch;
	
	private TagNode match;
	private String query;
	private boolean searchLock = false;
	
	public SelectionTagsPane() {
		nodeSearch = new EditNode("", "Quick Search");
		nodeSearch.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		nodeSearch.textProperty().addListener((observable, oldValue, newValue) -> searchTextChangeHandler(newValue));
		nodeSearch.setOnAction(event -> this.searchOnActionHandler());
		
		getChildren().addAll(nodeSearch, listBox);
	}
	
	private void searchTextChangeHandler(String newValue) {
		if (!searchLock) {
			if (match != null) {
				match.setBackground(Background.EMPTY);
				match.setBackgroundLock(false);
				match = null;
			}
			
			if (!newValue.isEmpty()) {
				query = newValue.toLowerCase();
				for (TagNode tagNode : tagNodes) {
					if (tagNode.getText().toLowerCase().contains(query)) {
						match = tagNode;
						match.setBackground(DecoratorUtil.getBackgroundSecondary());
						match.setBackgroundLock(true);
						break;
					}
				}
			}
		}
	}
	private void searchOnActionHandler() {
		if (match != null) {
			match.setBackground(Background.EMPTY);
			match.setBackgroundLock(false);
			
			searchLock = true;
			nodeSearch.clear();
			searchLock = false;
			
			if (Main.SELECT.getTagListIntersect().contains(match.getText())) {
				Main.SELECT.removeTag(match.getText());
			} else {
				Main.SELECT.addTag(match.getText());
			}
			
			Reload.start();
		}
	}
	
	public void refresh() {
		BaseList<String> stringListIntersect = new BaseList<>(Main.SELECT.getTagListIntersect());
		BaseList<String> stringListUnion = new BaseList<>(Main.SELECT.getTagList());
		
		tagNodes.forEach(tagNode -> refreshNodeColor(tagNode, stringListIntersect, stringListUnion));
	}
	private void refreshNodeColor(TagNode tagNode, BaseList<String> stringListIntersect, BaseList<String> stringListUnion) {
		for (String stringTag : stringListIntersect) {
			if (stringTag.startsWith(tagNode.getText())) {
				tagNode.setTextFill(DecoratorUtil.getColorPositive());
				return;
			}
		}
		for (String stringTag : stringListUnion) {
			if (stringTag.startsWith(tagNode.getText())) {
				tagNode.setTextFill(DecoratorUtil.getColorUnion());
				return;
			}
		}
		tagNode.setTextFill(DecoratorUtil.getColorPrimary());
	}
	
	public void nextMatch(Direction searchDirection, boolean isControlDown) {
		if (match != null) {
			int i = tagNodes.indexOf(match);
			TagNode tagNode;
			
			do {
				switch (searchDirection) {
					case UP:
						i--;
						break;
					case DOWN:
						i++;
						break;
					default:
						throw new IllegalArgumentException("Invalid search direction: " + searchDirection.name());
				}
				
				if (i < 0) {
					i = tagNodes.size() - 1;
				} else if (i == tagNodes.size()) {
					i = 0;
				}
				
				tagNode = tagNodes.get(i);
				
			} while (isControlDown && !tagNode.getText().toLowerCase().contains(query));
			
			match.setBackground(Background.EMPTY);
			match.setBackgroundLock(false);
			
			match = tagNode;
			match.setBackground(DecoratorUtil.getBackgroundSecondary());
			match.setBackgroundLock(true);
		}
	}
	
	public EditNode getNodeSearch() {
		return nodeSearch;
	}
}
