package application.gui.panes.side;

import application.controller.Filter;
import application.controller.Select;
import application.controller.Target;
import application.database.object.DataObject;
import application.database.object.TagObject;
import application.gui.decorator.ColorUtil;
import application.gui.nodes.ClickMenu;
import application.gui.nodes.NodeUtil;
import application.gui.nodes.buttons.ButtonTemplates;
import application.gui.nodes.simple.EditNode;
import application.gui.nodes.simple.TextNode;
import application.main.Instances;
import application.misc.CompareUtil;
import application.misc.enums.Direction;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class SelectPane extends SidePaneBase {
	private final EditNode nodeSearch;
	
	public SelectPane() {
		nodeTitle = new TextNode("", true, true, false, true);
		nodeTitle.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		nodeSearch = new EditNode("Quick Search");
		nodeSearch.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		nodeSearch.prefWidthProperty().bind(this.widthProperty());
		nodeSearch.textProperty().addListener(getSearchTextListener());
		nodeSearch.setOnAction(getSearchOnAction());
		
		TextNode nodeSelectAll = ButtonTemplates.SELECTION_SET_ALL.get();
		TextNode nodeSelectNone = ButtonTemplates.SELECTION_SET_NONE.get();
		ClickMenu.install(nodeTitle, Direction.LEFT, nodeSelectAll, nodeSelectNone);
		
		this.setBorder(NodeUtil.getBorder(0, 0, 0, 1));
		this.getChildren().addAll(nodeTitle, nodeSearch, scrollPane);
	}
	
	private void refreshTitle() {
		Filter filter = Instances.getFilter();
		Select select = Instances.getSelect();
		
		int hiddenTilesCount = 0;
		for (DataObject dataObject : select) {
			if (!filter.contains(dataObject)) {
				hiddenTilesCount++;
			}
		}
		
		String text = "Selection: " + select.size();
		if (hiddenTilesCount > 0) text += " (" + hiddenTilesCount + " hidden)";
		
		nodeTitle.setText(text);
	}
	public boolean reload() {
		Select select = Instances.getSelect();
		Target target = Instances.getTarget();
		
		refreshTitle();
		
		Color textColorDefault = ColorUtil.getTextColorDef();
		Color textColorPositive = ColorUtil.getTextColorPos();
		Color textColorShare = ColorUtil.getTextColorShr();
		
		ArrayList<String> groupsInter;
		ArrayList<String> groupsShare;
		if (select.size() == 0) {
			if (target.getCurrentTarget() != null) {
				groupsInter = target.getCurrentTarget().getTagList().getGroups();
				groupsShare = new ArrayList<>();
			} else {
				return false;
			}
		} else {
			groupsInter = select.getIntersectingTags().getGroups();
			groupsShare = select.getSharedTags().getGroups();
		}
		
		updateNodes();
		
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				String group = groupNode.getGroup();
				
				if (groupsInter.contains(group)) {
					groupNode.setTextFill(textColorPositive);
				} else if (groupsShare.contains(group)) {
					groupNode.setTextFill(textColorShare);
				} else {
					groupNode.setTextFill(textColorDefault);
				}
				ArrayList<String> namesInter;
				ArrayList<String> namesShare;
				if (select.size() == 0) {
					namesInter = target.getCurrentTarget().getTagList().getNames(group);
					namesShare = new ArrayList<>();
				} else {
					namesInter = select.getIntersectingTags().getNames(group);
					namesShare = select.getSharedTags().getNames(group);
				}
				for (TextNode nameNode : groupNode.getNameNodes()) {
					String name = nameNode.getText();
					
					if (namesInter.contains(name)) {
						nameNode.setTextFill(textColorPositive);
					} else if (namesShare.contains(name)) {
						nameNode.setTextFill(textColorShare);
					} else {
						nameNode.setTextFill(textColorDefault);
					}
				}
			}
		}
		return true;
	}
	
	public void changeNodeState(GroupNode groupNode, TextNode nameNode) {
		if (nameNode == null) {
			if (groupNode.isExpanded()) {
				groupNode.hideNameNodes();
			} else {
				groupNode.showNameNodes();
			}
		} else {
			TagObject tagObject = Instances.getTagListMain().getTagObject(groupNode.getGroup(), nameNode.getText());
			if (nameNode.getTextFill().equals(ColorUtil.getTextColorPos()) || nameNode.getTextFill().equals(ColorUtil.getTextColorShr())) {
				nameNode.setTextFill(ColorUtil.getTextColorDef());
				Instances.getSelect().removeTagObject(tagObject);
			} else {
				nameNode.setTextFill(ColorUtil.getTextColorPos());
				Instances.getSelect().addTagObject(tagObject);
			}
			
			Instances.getReload().doReload();
		}
	}
	
	private TagObject bestMatch = null;
	private TextNode previousMatchNameNode = null;
	private GroupNode previousMatchGroupNode = null;
	private boolean wasPreviousGroupNodeExpanded = true;
	
	private ChangeListener<? super String> getSearchTextListener() {
		return (ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (newValue.length() < 3) {
				if (!wasPreviousGroupNodeExpanded) {
					previousMatchGroupNode.hideNameNodes();
					wasPreviousGroupNodeExpanded = true;
				}
				if (previousMatchNameNode != null) {
					previousMatchNameNode.fireEvent(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
					previousMatchNameNode = null;
				}
			} else {
				bestMatch = this.getBestMatch(newValue);
				if (bestMatch != null) {
					getGroupNodes().forEach(groupNode -> {
						if (groupNode.getGroup().equals(bestMatch.getGroup())) {
							if (previousMatchGroupNode != groupNode) {
								if (!wasPreviousGroupNodeExpanded) {
									previousMatchGroupNode.hideNameNodes();
								}
								
								if (previousMatchNameNode != null) {
									previousMatchNameNode.fireEvent(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
								}
								
								previousMatchGroupNode = groupNode;
								wasPreviousGroupNodeExpanded = groupNode.isExpanded();
								
								if (!groupNode.isExpanded()) {
									groupNode.showNameNodes();
								}
								
								for (TextNode nameNode : groupNode.getNameNodes()) {
									if (nameNode.getText().equals(bestMatch.getName())) {
										previousMatchNameNode = nameNode;
										nameNode.fireEvent(new MouseEvent(MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
									}
								}
							}
						}
					});
				}
			}
		};
	}
	private EventHandler<ActionEvent> getSearchOnAction() {
		return event -> {
			if (previousMatchNameNode != null) {
				previousMatchNameNode.fireEvent(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
			}
			
			nodeSearch.clear();
			Instances.getSelect().addTagObject(bestMatch);
			Instances.getReload().doReload();
		};
	}
	private TagObject getBestMatch(String query) {
		//	simple check if any the name of any tag starts with query
		for (TagObject tagObject : Instances.getTagListMain()) {
			if (tagObject.getName().toLowerCase().startsWith(query)) {
				return tagObject;
			}
		}
		
		//	more complex check for string similarity
		double bestMatchFactor = 0;
		TagObject bestMatch = null;
		for (TagObject tagObject : Instances.getTagListMain()) {
			double currentFactor = CompareUtil.getStringSimilarity(query, tagObject.getFull());
			if (currentFactor > bestMatchFactor) {
				bestMatch = tagObject;
				bestMatchFactor = currentFactor;
			}
		}
		return bestMatch;
	}
}
