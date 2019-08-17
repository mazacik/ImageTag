package application.gui.panes.side;

import application.database.list.TagListMain;
import application.database.object.TagObject;
import application.gui.nodes.simple.TextNode;
import application.gui.panes.NodeBase;
import application.main.Instances;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class SidePaneBase extends VBox implements NodeBase, SidePaneBaseInterface {
	protected TextNode nodeTitle;
	protected ScrollPane scrollPane;
	protected VBox groupNodes;
	
	public void refresh() {
		TagListMain tagListMain = Instances.getTagListMain();
		
		//	primary helpers
		ArrayList<String> groupsHere = new ArrayList<>();
		ArrayList<TagObject> tagsMain = new ArrayList<>(Instances.getTagListMain());
		ArrayList<TagObject> tagsHere = new ArrayList<>();
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				if (!groupsHere.contains(groupNode.getGroup())) groupsHere.add(groupNode.getGroup());
				for (TextNode nameNode : groupNode.getNameNodes()) {
					TagObject tagObject = tagListMain.getTagObject(groupNode.getGroup(), nameNode.getText());
					if (!tagsHere.contains(tagObject)) tagsHere.add(tagObject);
				}
			}
		}
		
		//	secondary helpers
		ArrayList<TagObject> tagsToAdd = new ArrayList<>();
		for (TagObject tagMain : tagsMain) {
			if (!tagsHere.contains(tagMain)) {
				tagsToAdd.add(tagMain);
			}
		}
		ArrayList<TagObject> tagsToRemove = new ArrayList<>();
		for (TagObject tagHere : tagsHere) {
			if (!tagsMain.contains(tagHere)) {
				tagsToRemove.add(tagHere);
			}
		}
		
		//	checkValues whether any changes are necessary (add)
		if (!tagsHere.containsAll(tagsMain)) {
			for (TagObject tagObject : tagsToAdd) {
				//	checkValues if the TagNode exists, if not, add it
				int index;
				if (!groupsHere.contains(tagObject.getGroup())) {
					groupsHere.add(tagObject.getGroup());
					groupsHere.sort(Comparator.naturalOrder());
					index = groupsHere.indexOf(tagObject.getGroup());
					GroupNode groupNode = new GroupNode(this, tagObject.getGroup());
					groupNodes.getChildren().add(index, groupNode);
				} else {
					index = groupsHere.indexOf(tagObject.getGroup());
				}
				//	add NameNode to the respective TagNode and sort
				Node node = groupNodes.getChildren().get(index);
				if (node instanceof GroupNode) {
					GroupNode groupNode = (GroupNode) node;
					groupNode.addNameNode(tagObject.getName());
					groupNode.sortNameNodes(); //todo this only has to be done once at the end
				}
			}
		}
		
		//	checkValues whether any changes are necessary (remove)
		if (!tagsMain.containsAll(tagsHere)) {
			for (TagObject tagObject : tagsToRemove) {
				//	use helper to find the TagNode
				//	remove NameNode from the TagNode
				Node node = groupNodes.getChildren().get(groupsHere.indexOf(tagObject.getGroup()));
				if (node instanceof GroupNode) {
					GroupNode groupNode = (GroupNode) node;
					groupNode.removeNameNode(tagObject.getName());
					//	if TagNode is empty, remove it
					if (groupNode.getNameNodes().isEmpty()) groupNodes.getChildren().remove(groupNode);
				}
			}
		}
	}
	
	public void updateGroupNode(String oldGroup, String newGroup) {
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				if (groupNode.getGroup().equals(oldGroup)) {
					groupNode.setGroup(newGroup);
				}
			}
		}
	}
	
	public void updateNameNode(String group, String oldName, String newName) {
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				if (groupNode.getGroup().equals(group)) {
					for (TextNode nameNode : groupNode.getNameNodes()) {
						if (nameNode.getText().equals(oldName)) {
							nameNode.setText(newName);
						}
					}
				}
			}
		}
	}
	public void removeNameNode(String group, String name) {
		GroupNode groupNode = null;
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				groupNode = (GroupNode) node;
				if (groupNode.getGroup().equals(group)) {
					groupNode.removeNameNode(name);
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
				groupNode.showNameNodes();
			}
		}
	}
	public void collapseAll() {
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				groupNode.hideNameNodes();
			}
		}
	}
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	public VBox getGroupNodes() {
		return groupNodes;
	}
}
