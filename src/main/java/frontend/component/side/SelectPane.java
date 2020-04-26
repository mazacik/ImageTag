package frontend.component.side;

import backend.control.reload.Reload;
import backend.list.BaseList;
import backend.list.entity.Entity;
import backend.list.tag.Tag;
import backend.misc.Direction;
import frontend.decorator.DecoratorUtil;
import frontend.node.EditNode;
import frontend.node.menu.ClickMenu;
import frontend.node.textnode.TextNode;
import frontend.node.textnode.TextNodeTemplates;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import main.Root;

public class SelectPane extends SidePaneBase {
	private final TextNode nodeText;
	private final EditNode nodeSearch;
	
	private TagNode match;
	private String query;
	private boolean searchLock = false;
	
	public SelectPane() {
		nodeText = new TextNode("", true, true, false, true);
		nodeText.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		nodeText.setMaxWidth(Double.MAX_VALUE);
		
		nodeSearch = new EditNode("", "Quick Search");
		nodeSearch.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		nodeSearch.textProperty().addListener((observable, oldValue, newValue) -> {
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
		});
		nodeSearch.setOnAction(event -> {
			if (match != null) {
				match.setBackground(Background.EMPTY);
				match.setBackgroundLock(false);
				
				searchLock = true;
				nodeSearch.clear();
				searchLock = false;
				
				Tag tag = Root.TAGLIST.getTag(match.getStringValue());
				if (Root.SELECT.getTagListIntersect().contains(tag)) {
					Root.SELECT.removeTag(tag.getID());
				} else {
					Root.SELECT.addTag(tag.getID());
				}
				
				Reload.start();
			}
		});
		
		ClickMenu.install(nodeText, Direction.LEFT, MouseButton.PRIMARY, 0, -1,
		                  TextNodeTemplates.SELECTION_SET_ALL.get(),
		                  TextNodeTemplates.SELECTION_SET_NONE.get()
		);
		
		this.setBorder(DecoratorUtil.getBorder(0, 0, 0, 1));
		this.getChildren().addAll(nodeText, nodeSearch, listBox);
	}
	
	public boolean refresh() {
		refreshTitle();
		
		BaseList<String> stringListIntersect = new BaseList<>();
		Root.SELECT.getTagListIntersect().forEach(tag -> stringListIntersect.addImpl(tag.getStringValue()));
		BaseList<String> stringListUnion = new BaseList<>();
		Root.SELECT.getTagList().forEach(tag -> stringListUnion.addImpl(tag.getStringValue()));
		
		getTagNodes().forEach(tagNode -> this.refreshNodeColor(tagNode, stringListIntersect, stringListUnion));
		
		return true;
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
	private void refreshTitle() {
		int hiddenTilesCount = 0;
		for (Entity entity : Root.SELECT) {
			if (!Root.FILTER.contains(entity)) {
				hiddenTilesCount++;
			}
		}
		
		String text = "Selection: " + Root.SELECT.size();
		if (hiddenTilesCount > 0) text += " (" + hiddenTilesCount + " hidden)";
		
		nodeText.setText(text);
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