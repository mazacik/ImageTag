package application.gui.panes.side;

import application.database.list.TagListMain;
import application.database.object.TagObject;
import application.gui.decorator.Decorator;
import application.gui.nodes.simple.TextNode;
import application.gui.panes.NodeBase;
import application.main.Instances;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class SidePaneBase extends VBox implements NodeBase {
	protected TextNode nodeTitle;
	protected ScrollPane scrollPane;
	protected VBox tagNodesBox;
	
	public void refresh() {
		TagListMain tagListMain = Instances.getTagListMain();
		
		//	primary helpers
		ArrayList<String> groupsHere = new ArrayList<>();
		ArrayList<TagObject> tagsMain = new ArrayList<>(Instances.getTagListMain());
		ArrayList<TagObject> tagsHere = new ArrayList<>();
		for (Node node : tagNodesBox.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				if (!groupsHere.contains(tagNode.getGroup())) groupsHere.add(tagNode.getGroup());
				for (TextNode nameNode : tagNode.getNameNodes()) {
					TagObject tagObject = tagListMain.getTagObject(tagNode.getGroup(), nameNode.getText());
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
					TagNode tagNode = new TagNode(this, tagObject.getGroup());
					tagNodesBox.getChildren().add(index, tagNode);
				} else {
					index = groupsHere.indexOf(tagObject.getGroup());
				}
				//	add NameNode to the respective TagNode and sort
				Node node = tagNodesBox.getChildren().get(index);
				if (node instanceof TagNode) {
					TagNode tagNode = (TagNode) node;
					tagNode.addNameNode(tagObject.getName());
					tagNode.sortNameNodes();
				}
			}
			Decorator.applyStyle(tagNodesBox);
		}
		
		//	checkValues whether any changes are necessary (remove)
		if (!tagsMain.containsAll(tagsHere)) {
			for (TagObject tagObject : tagsToRemove) {
				//	use helper to find the TagNode
				//	remove NameNode from the TagNode
				Node node = tagNodesBox.getChildren().get(groupsHere.indexOf(tagObject.getGroup()));
				if (node instanceof TagNode) {
					TagNode tagNode = (TagNode) node;
					tagNode.removeNameNode(tagObject.getName());
					//	if TagNode is empty, remove it
					if (tagNode.getNameNodes().isEmpty()) tagNodesBox.getChildren().remove(tagNode);
				}
			}
		}
	}
	
	public void updateGroupNode(String oldGroup, String newGroup) {
		for (Node node : tagNodesBox.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				if (tagNode.getGroup().equals(oldGroup)) {
					tagNode.setGroup(newGroup);
				}
			}
		}
	}
	
	public void updateNameNode(String group, String oldName, String newName) {
		for (Node node : tagNodesBox.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				if (tagNode.getGroup().equals(group)) {
					for (TextNode nameNode : tagNode.getNameNodes()) {
						if (nameNode.getText().equals(oldName)) {
							nameNode.setText(newName);
						}
					}
				}
			}
		}
	}
	public void removeNameNode(String group, String name) {
		ArrayList<TagNode> emptyNodes = new ArrayList<>();
		for (Node node : tagNodesBox.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				if (tagNode.getGroup().equals(group)) {
					String nameToRemove = "";
					for (TextNode nameNode : tagNode.getNameNodes()) {
						if (nameNode.getText().equals(name)) {
							nameToRemove = nameNode.getText();
							break;
						}
					}
					tagNode.removeNameNode(nameToRemove);
				}
				if (tagNode.getNameNodes().isEmpty()) emptyNodes.add(tagNode);
			}
		}
		tagNodesBox.getChildren().removeAll(emptyNodes);
	}
	public void changeNodeState(TagNode tagNode, TextNode nameNode) {
	
	}
	
	public void expandAll() {
		for (Node node : tagNodesBox.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				tagNode.showNameNodes();
			}
		}
	}
	public void collapseAll() {
		for (Node node : tagNodesBox.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				tagNode.hideNameNodes();
			}
		}
	}
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	public VBox getTagNodesBox() {
		return tagNodesBox;
	}
}
