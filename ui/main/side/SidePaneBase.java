package ui.main.side;

import base.CustomList;
import base.tag.Tag;
import base.tag.TagList;
import ui.component.simple.TextNode;
import ui.component.simple.VBox;
import ui.decorator.SizeUtil;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;

import java.util.Comparator;

public abstract class SidePaneBase extends VBox {
	protected TextNode nodeTitle;
	protected VBox groupNodes;
	protected ScrollPane scrollPane;
	
	public SidePaneBase() {
		groupNodes = new VBox();
		
		scrollPane = new ScrollPane();
		scrollPane.setContent(groupNodes);
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
		
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				groupsCurrent.add(groupNode.getText());
				for (TextNode nameNode : groupNode.getNameNodes()) {
					tagsCurrent.add(TagList.getMainInstance().getTag(groupNode.getText(), nameNode.getText()));
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
					groupNodes.getChildren().add(index, groupNode);
				} else {
					index = groupsCurrent.indexOf(group);
				}
				//	add NameNode to the respective GroupNode and sort
				Node node = groupNodes.getChildren().get(index);
				if (node instanceof GroupNode) {
					GroupNode groupNode = (GroupNode) node;
					groupNode.add(tag.getName());
					groupNode.sort();
				}
			}
		}
		
		//	check if any changes are necessary (remove)
		if (!tagsMain.containsAll(tagsCurrent)) {
			for (Tag tag : tagsToRemove) {
				//	use helper to find the GroupNode
				//	remove NameNode from the GroupNode
				Node node = groupNodes.getChildren().get(groupsCurrent.indexOf(tag.getGroup()));
				if (node instanceof GroupNode) {
					GroupNode groupNode = (GroupNode) node;
					groupNode.remove(tag.getName());
					//	if GroupNode is empty, remove it
					if (groupNode.getNameNodes().isEmpty()) groupNodes.getChildren().remove(groupNode);
				}
			}
		}
		
		return true;
	}
	
	public void updateGroupNode(String groupBefore, String groupAfter) {
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				if (groupNode.getText().equals(groupBefore)) {
					groupNode.setText(groupAfter);
					break;
				}
			}
		}
	}
	
	//todo move to GroupNode
	public void updateNameNode(String group, String nameBefore, String nameAfter) {
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				if (groupNode.getText().equals(group)) {
					groupNode.update(nameBefore, nameAfter);
					break;
				}
			}
		}
	}
	public void removeNameNode(String group, String name) {
		GroupNode groupNode = null;
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				groupNode = (GroupNode) node;
				if (groupNode.getText().equals(group)) {
					groupNode.remove(name);
					break;
				}
			}
		}
		if (groupNode != null && groupNode.getNameNodes().isEmpty()) groupNodes.getChildren().remove(groupNode);
	}
	
	public void expandAll() {
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				groupNode.expand();
			}
		}
	}
	public void collapseAll() {
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				groupNode.collapse();
			}
		}
	}
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	public CustomList<GroupNode> getGroupNodes() {
		CustomList<GroupNode> customList = new CustomList<>();
		groupNodes.getChildren().forEach(node -> {
			if (node instanceof GroupNode) {
				customList.add((GroupNode) node);
			}
		});
		return customList;
	}
}
