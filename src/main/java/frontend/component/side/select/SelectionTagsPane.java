package frontend.component.side.select;

import backend.BaseList;
import backend.misc.Direction;
import backend.reload.Reload;
import backend.tag.Tag;
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
		nodeSearch.textProperty().addListener((observable, oldValue, newValue) -> this.searchTextChangeHandler(newValue));
		nodeSearch.setOnAction(event -> this.searchOnActionHandler());
		
		this.getChildren().addAll(nodeSearch, listBox);
	}
	
	private void searchTextChangeHandler(String newValue) {
		if (!searchLock) {
			if (match != null) {
				match.setBackground(Background.EMPTY);
				match.setBackgroundLock(false);
				match = null;
			}
			
			closeNodes();
			
			if (newValue.length() > 0) {
				query = newValue.toLowerCase();
				
				for (TagNode tagNode : this.getTagNodes()) {
					if (tagNode.getText().toLowerCase().contains(query)) {
						if (tagNode.isLast()) {
							match = tagNode;
							break;
						} else if (match == null) {
							match = tagNode;
						}
					}
				}
				
				if (match != null) {
					match.setBackground(DecoratorUtil.getBackgroundSecondary());
					match.setBackgroundLock(true);
					match.getParentNodes().forEach(TagNode::open);
				}
			} else {
				match = null;
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
			
			Tag tag = Main.DB_TAG.getTag(match.getStringValue());
			if (Main.SELECT.getTagListIntersect().contains(tag)) {
				Main.SELECT.removeTag(tag.getID());
			} else {
				Main.SELECT.addTag(tag.getID());
			}
			
			Reload.start();
		}
	}
	
	public void refresh() {
		BaseList<String> stringListIntersect = new BaseList<>();
		Main.SELECT.getTagListIntersect().forEach(tag -> stringListIntersect.add(tag.getStringValue()));
		BaseList<String> stringListUnion = new BaseList<>();
		Main.SELECT.getTagList().forEach(tag -> stringListUnion.add(tag.getStringValue()));
		
		getTagNodes().forEach(tagNode -> this.refreshNodeColor(tagNode, stringListIntersect, stringListUnion));
	}
	private void refreshNodeColor(TagNode tagNode, BaseList<String> stringListIntersect, BaseList<String> stringListUnion) {
		String stringNode = tagNode.getStringValue();
		for (String stringTag : stringListIntersect) {
			if (stringTag.startsWith(stringNode)) {
				tagNode.setTextFill(DecoratorUtil.getColorPositive());
				return;
			}
		}
		for (String stringTag : stringListUnion) {
			if (stringTag.startsWith(stringNode)) {
				tagNode.setTextFill(DecoratorUtil.getColorUnion());
				return;
			}
		}
		tagNode.setTextFill(DecoratorUtil.getColorPrimary());
	}
	
	public void nextMatch(Direction searchDirection, boolean isControlDown) {
		if (match != null) {
			BaseList<TagNode> tagNodes = getTagNodes();
			int i = tagNodes.indexOf(match);
			TagNode tagNode;
			
			while (true) {
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
				
				if (i == 0) {
					i = tagNodes.size() - 1;
				} else if (i == tagNodes.size()) {
					i = 0;
				}
				
				tagNode = tagNodes.get(i);
				
				if (isControlDown) {
					if (tagNode == match || (tagNode.isLast() && tagNode.getText().toLowerCase().contains(query))) {
						break;
					}
				} else {
					if (tagNode == match || tagNode.isLast()) {
						break;
					}
				}
			}
			
			closeNodes();
			match.setBackground(Background.EMPTY);
			match.setBackgroundLock(false);
			
			match = tagNode;
			match.setBackground(DecoratorUtil.getBackgroundSecondary());
			match.setBackgroundLock(true);
			match.getParentNodes().forEach(TagNode::open);
		}
	}
	public void closeNodes() {
		new BaseList<>(openNodes).forEach(TagNode::close);
	}
	
	public EditNode getNodeSearch() {
		return nodeSearch;
	}
}
