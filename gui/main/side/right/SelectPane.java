package gui.main.side.right;

import baseobject.CustomList;
import baseobject.entity.Entity;
import baseobject.tag.Tag;
import gui.component.clickmenu.ClickMenu;
import gui.component.simple.EditNode;
import gui.component.simple.TextNode;
import gui.component.simple.template.ButtonTemplates;
import gui.decorator.ColorUtil;
import gui.main.side.GroupNode;
import gui.main.side.SidePaneBase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;
import tools.NodeUtil;
import tools.enums.Direction;

import java.util.logging.Logger;

public class SelectPane extends SidePaneBase {
	private EditNode nodeSearch;
	
	public SelectPane() {
	
	}
	
	public void init() {
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
		
		collapseAll();
	}
	
	public boolean refresh() {
		Logger.getGlobal().info(this.toString());
		
		refreshTitle();
		
		Color textColorDefault = ColorUtil.getColorPrimary();
		Color textColorPositive = ColorUtil.getColorPositive();
		Color textColorShare = ColorUtil.getColorShare();
		
		CustomList<String> groupsInter;
		CustomList<String> groupsShare;
		
		if (select.size() == 0) {
			if (target.get() != null) {
				groupsInter = target.get().getTagList().getGroups();
				groupsShare = new CustomList<>();
			} else {
				return false;
			}
		} else {
			groupsInter = select.getTagsIntersect().getGroups();
			groupsShare = select.getTagsAll().getGroups();
		}
		
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
				CustomList<String> namesInter;
				CustomList<String> namesShare;
				if (select.size() == 0) {
					namesInter = target.get().getTagList().getNames(group);
					namesShare = new CustomList<>();
				} else {
					namesInter = select.getTagsIntersect().getNames(group);
					namesShare = select.getTagsAll().getNames(group);
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
	private void refreshTitle() {
		int hiddenTilesCount = 0;
		for (Entity entity : select) {
			if (!filter.contains(entity)) {
				hiddenTilesCount++;
			}
		}
		
		String text = "Selection: " + select.size();
		if (hiddenTilesCount > 0) text += " (" + hiddenTilesCount + " hidden)";
		
		nodeTitle.setText(text);
	}
	
	public void changeNodeState(GroupNode groupNode, TextNode nameNode) {
		if (nameNode == null) {
			if (groupNode.isExpanded()) {
				groupNode.hideNameNodes();
			} else {
				groupNode.showNameNodes();
			}
		} else {
			Tag tag = tagListMain.getTag(groupNode.getGroup(), nameNode.getText());
			if (nameNode.getTextFill().equals(ColorUtil.getColorPositive()) || nameNode.getTextFill().equals(ColorUtil.getColorShare())) {
				nameNode.setTextFill(ColorUtil.getColorPrimary());
				select.removeTag(tag);
			} else {
				nameNode.setTextFill(ColorUtil.getColorPositive());
				select.addTag(tag);
			}
			
			reload.doReload();
		}
	}
	
	private Tag bestMatch = null;
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
				if (previousMatchGroupNode != null) {
					previousMatchGroupNode = null;
				}
				if (previousMatchNameNode != null) {
					previousMatchNameNode.fireEvent(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
					previousMatchNameNode = null;
				}
			} else {
				bestMatch = this.getBestMatch(newValue);
				if (bestMatch != null) {
					for (GroupNode groupNode : getGroupNodes()) {
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
					}
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
			select.addTag(bestMatch);
			reload.doReload();
		};
	}
	private Tag getBestMatch(String query) {
		//	simple check if the name of any tag starts with query
		for (Tag tag : tagListMain) {
			if (tag.getName().toLowerCase().startsWith(query)) {
				return tag;
			}
		}
		
		//	more complex check for string similarity
		Tag bestMatch = null;
		double bestMatchFactor = 0;
		
		for (Tag tag : tagListMain) {
			SimilarityStrategy strategy = new JaroWinklerStrategy();
			StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
			double currentFactor = service.score(tag.getName(), query);
			
			if (currentFactor > bestMatchFactor) {
				bestMatch = tag;
				bestMatchFactor = currentFactor;
			}
		}
		
		return bestMatch;
	}
}
