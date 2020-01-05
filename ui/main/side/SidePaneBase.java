package ui.main.side;

import base.CustomList;
import base.tag.Tag;
import base.tag.TagList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import ui.component.simple.TextNode;
import ui.component.simple.VBox;
import ui.decorator.SizeUtil;

import java.util.Comparator;

public abstract class SidePaneBase extends VBox {
	protected TextNode nodeTitle;
	protected VBox boxGroupNodes;
	protected ScrollPane scrollPane;
	
	public SidePaneBase() {
		boxGroupNodes = new VBox();
		
		scrollPane = new ScrollPane(boxGroupNodes);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setBackground(Background.EMPTY);
		
		this.setPrefWidth(SizeUtil.getUsableScreenWidth());
		this.setMinWidth(SizeUtil.getMinWidthSideLists());
	}
	
	public boolean reload() {
		//	populate primary lists
		CustomList<String> groupsWithNodes = new CustomList<>();
		TagList tagsWithNodes = new TagList();
		
		for (Node node : boxGroupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				groupsWithNodes.add(groupNode.getGroup(), true);
				for (TextNode nameNode : groupNode.getNameNodes()) {
					Tag tag = TagList.getMain().getTag(groupNode.getGroup(), nameNode.getText());
					if (tag == null) {
						//  it's possible the Tag is not in TagList.getMain()
						tag = new Tag(groupNode.getGroup(), nameNode.getText());
					}
					tagsWithNodes.add(tag, true);
				}
			}
		}
		
		//	populate secondary lists
		CustomList<Tag> tagsWithoutNodes = new CustomList<>();
		for (Tag tagMain : TagList.getMain()) {
			if (!tagsWithNodes.contains(tagMain)) {
				tagsWithoutNodes.add(tagMain);
			}
		}
		TagList nodesWithoutTags = new TagList();
		for (Tag tagCurrent : tagsWithNodes) {
			if (!TagList.getMain().contains(tagCurrent)) {
				nodesWithoutTags.add(tagCurrent);
			}
		}
		
		//	check if any changes are necessary (add)
		if (!tagsWithNodes.containsAll(TagList.getMain())) {
			for (Tag tag : tagsWithoutNodes) {
				//	check if the GroupNode exists, if not, add it
				String group = tag.getGroup();
				int index;
				if (!groupsWithNodes.contains(group)) {
					groupsWithNodes.add(group);
					groupsWithNodes.sort(Comparator.naturalOrder());
					index = groupsWithNodes.indexOf(group);
					GroupNode groupNode = new GroupNode(this, group);
					boxGroupNodes.getChildren().add(index, groupNode);
				} else {
					index = groupsWithNodes.indexOf(group);
				}
				
				//	add NameNode to the respective GroupNode and sort
				Node node = boxGroupNodes.getChildren().get(index);
				if (node instanceof GroupNode) {
					GroupNode groupNode = (GroupNode) node;
					groupNode.addNameNode(tag.getName());
					groupNode.sort();
					groupNode.expand();
				}
			}
		}
		
		//	check if any changes are necessary (remove)
		if (!TagList.getMain().containsAll(tagsWithNodes)) {
			for (Tag tag : nodesWithoutTags) {
				//	use helper to find the GroupNode
				//	remove NameNode from the GroupNode
				Node node = boxGroupNodes.getChildren().get(groupsWithNodes.indexOf(tag.getGroup()));
				if (node instanceof GroupNode) {
					GroupNode groupNode = (GroupNode) node;
					groupNode.removeNameNode(tag.getName());
				}
			}
		}
		
		return true;
	}
	
	public void expandAll() {
		for (Node node : boxGroupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				groupNode.expand();
			}
		}
	}
	public void collapseAll() {
		for (Node node : boxGroupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				groupNode.collapse();
			}
		}
	}
	
	public GroupNode getGroupNode(String group) {
		for (Node node : boxGroupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				if (groupNode.getGroup().equals(group)) {
					return groupNode;
				}
			}
		}
		return null;
	}
	public CustomList<GroupNode> getGroupNodes() {
		CustomList<GroupNode> customList = new CustomList<>();
		boxGroupNodes.getChildren().forEach(node -> {
			if (node instanceof GroupNode) {
				customList.add((GroupNode) node);
			}
		});
		return customList;
	}
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
}
