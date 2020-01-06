package ui.main.side;

import base.CustomList;
import base.entity.Entity;
import base.tag.Tag;
import base.tag.TagList;
import control.Select;
import control.filter.Filter;
import control.reload.Reload;
import enums.Direction;
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
import ui.decorator.Decorator;
import ui.custom.ClickMenu;
import ui.node.NodeEdit;
import ui.node.NodeText;
import ui.node.NodeTemplates;

import java.util.logging.Logger;

public class PaneSelect extends SidePaneBase {
	private NodeEdit nodeSearch;
	
	public void init() {
		nodeTitle = new NodeText("", true, true, false, true);
		nodeTitle.setBorder(Decorator.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		nodeSearch = new NodeEdit("Quick Search");
		nodeSearch.setBorder(Decorator.getBorder(0, 0, 1, 0));
		nodeSearch.prefWidthProperty().bind(this.widthProperty());
		nodeSearch.textProperty().addListener(getSearchTextListener());
		nodeSearch.setOnAction(getSearchOnAction());
		
		NodeText nodeSelectAll = NodeTemplates.SELECTION_SET_ALL.get();
		NodeText nodeSelectNone = NodeTemplates.SELECTION_SET_NONE.get();
		ClickMenu.install(nodeTitle, Direction.LEFT, nodeSelectAll, nodeSelectNone);
		
		this.setBorder(Decorator.getBorder(0, 0, 0, 1));
		this.getChildren().addAll(nodeTitle, nodeSearch, scrollPane);
		
		collapseAll();
	}
	
	public boolean refresh() {
		Logger.getGlobal().info(this.toString());
		
		refreshTitle();
		
		Color textColorDefault = Decorator.getColorPrimary();
		Color textColorPositive = Decorator.getColorPositive();
		Color textColorShare = Decorator.getColorShare();
		
		CustomList<String> groupsInter;
		CustomList<String> groupsShare;
		
		if (Select.getEntities().size() == 0) {
			groupsInter = new CustomList<>();
			groupsShare = new CustomList<>();
		} else {
			groupsInter = Select.getEntities().getTagsIntersect().getGroups();
			groupsShare = Select.getEntities().getTags().getGroups();
		}
		
		for (Node node : boxGroupNodes.getChildren()) {
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
				if (Select.getEntities().size() == 0) {
					namesInter = Select.getTarget().getTagList().getNames(group);
					namesShare = new CustomList<>();
				} else {
					namesInter = Select.getEntities().getTagsIntersect().getNames(group);
					namesShare = Select.getEntities().getTags().getNames(group);
				}
				
				for (NodeText nameNode : groupNode.getNameNodes()) {
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
		for (Entity entity : Select.getEntities()) {
			if (!Filter.getEntities().contains(entity)) {
				hiddenTilesCount++;
			}
		}
		
		String text = "Selection: " + Select.getEntities().size();
		if (hiddenTilesCount > 0) text += " (" + hiddenTilesCount + " hidden)";
		
		nodeTitle.setText(text);
	}
	
	private Tag bestMatch = null;
	private NodeText previousMatchNameNode = null;
	private GroupNode previousMatchGroupNode = null;
	private boolean wasPreviousGroupNodeExpanded = true;
	
	private ChangeListener<? super String> getSearchTextListener() {
		return (ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (newValue.length() < 3) {
				if (!wasPreviousGroupNodeExpanded) {
					previousMatchGroupNode.collapse();
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
									previousMatchGroupNode.collapse();
								}
								
								if (previousMatchNameNode != null) {
									previousMatchNameNode.fireEvent(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
								}
								
								previousMatchGroupNode = groupNode;
								wasPreviousGroupNodeExpanded = groupNode.isExpanded();
								
								if (!groupNode.isExpanded()) {
									groupNode.expand();
								}
								
								for (NodeText nameNode : groupNode.getNameNodes()) {
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
			Select.getEntities().addTag(bestMatch);
			Reload.start();
		};
	}
	private Tag getBestMatch(String query) {
		//	simple check if the name of any tag starts with query
		for (Tag tag : TagList.getMain()) {
			if (tag.getName().toLowerCase().startsWith(query)) {
				return tag;
			}
		}
		
		//	more complex check for string similarity
		Tag bestMatch = null;
		double bestMatchFactor = 0;
		
		for (Tag tag : TagList.getMain()) {
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
	
	private PaneSelect() {}
	private static class Loader {
		private static final PaneSelect INSTANCE = new PaneSelect();
	}
	public static PaneSelect getInstance() {
		return Loader.INSTANCE;
	}
	
	public NodeEdit getNodeSearch() {
		return nodeSearch;
	}
}
