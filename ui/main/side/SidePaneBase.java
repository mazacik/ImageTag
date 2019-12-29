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
		//	populate primary helpers
		CustomList<String> groupsCurrent = new CustomList<>();
		CustomList<Tag> tagsMain = new CustomList<>(TagList.getMainInstance());
		CustomList<Tag> tagsCurrent = new CustomList<>();
		
		for (Node node : boxGroupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				groupsCurrent.add(groupNode.getGroup());
				for (TextNode nameNode : groupNode.getNameNodes()) {
					tagsCurrent.add(TagList.getMainInstance().getTag(groupNode.getGroup(), nameNode.getText()));
				}
			}
		}
		
		//	populate secondary helpers
		CustomList<Tag> tagsToAdd = new CustomList<>();
		for (Tag tagMain : tagsMain) {
			if (!tagsCurrent.contains(tagMain)) {
				tagsToAdd.add(tagMain);
			}
		}
		TagList tagsToRemove = new TagList();
		for (Tag tagCurrent : tagsCurrent) {
			if (!tagsMain.contains(tagCurrent)) {
				tagsToRemove.add(tagCurrent);
			}
		}
		
		//	check if any changes are necessary (add)
		if (!tagsCurrent.containsAll(tagsMain)) {
			for (Tag tag : tagsToAdd) {
				//	check if the GroupNode exists, if not, add it
				String group = tag.getGroup();
				int index;
				if (!groupsCurrent.contains(group)) {
					groupsCurrent.add(group);
					groupsCurrent.sort(Comparator.naturalOrder());
					index = groupsCurrent.indexOf(group);
					GroupNode groupNode = new GroupNode(this, group);
					boxGroupNodes.getChildren().add(index, groupNode);
				} else {
					index = groupsCurrent.indexOf(group);
				}
				//	add NameNode to the respective GroupNode and sort
				Node node = boxGroupNodes.getChildren().get(index);
				if (node instanceof GroupNode) {
					GroupNode groupNode = (GroupNode) node;
					groupNode.addNameNode(tag.getName());
					groupNode.sort();
				}
			}
		}
		
		//	check if any changes are necessary (remove)
		if (!tagsMain.containsAll(tagsCurrent)) {
			for (Tag tag : tagsToRemove) {
				//	use helper to find the GroupNode
				//	remove NameNode from the GroupNode
				Node node = boxGroupNodes.getChildren().get(groupsCurrent.indexOf(tag.getGroup()));
				if (node instanceof GroupNode) {
					GroupNode groupNode = (GroupNode) node;
					groupNode.removeNameNode(tag.getName());
					//	if GroupNode is empty, remove it
					if (groupNode.getNameNodes().isEmpty()) boxGroupNodes.getChildren().remove(groupNode);
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
		GroupNode groupNode;
		for (Node node : boxGroupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				groupNode = (GroupNode) node;
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
