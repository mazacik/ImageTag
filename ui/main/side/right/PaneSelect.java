package ui.main.side.right;

import base.CustomList;
import base.entity.Entity;
import base.tag.Tag;
import base.tag.TagList;
import control.Select;
import control.Target;
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
import ui.NodeUtil;
import ui.component.clickmenu.ClickMenu;
import ui.component.simple.EditNode;
import ui.component.simple.TextNode;
import ui.component.simple.template.ButtonTemplates;
import ui.decorator.ColorUtil;
import ui.main.side.GroupNode;
import ui.main.side.SidePaneBase;

import java.util.logging.Logger;

public class PaneSelect extends SidePaneBase {
	private EditNode nodeSearch;
	
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
		
		if (Select.getEntities().size() == 0) {
			if (Target.get() != null) {
				groupsInter = Target.get().getTagList().getGroups();
				groupsShare = new CustomList<>();
			} else {
				return false;
			}
		} else {
			groupsInter = Select.getEntities().getTagsIntersect().getGroups();
			groupsShare = Select.getEntities().getTagsAll().getGroups();
		}
		
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				String group = groupNode.getText();
				
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
					namesInter = Target.get().getTagList().getNames(group);
					namesShare = new CustomList<>();
				} else {
					namesInter = Select.getEntities().getTagsIntersect().getNames(group);
					namesShare = Select.getEntities().getTagsAll().getNames(group);
				}
				for (TextNode nameNode : groupNode.getNodes()) {
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
	private TextNode previousMatchNameNode = null;
	private GroupNode previousMatchGroupNode = null;
	private boolean wasPreviousGroupNodeExpanded = true;
	
	private ChangeListener<? super String> getSearchTextListener() {
		return (ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (newValue.length() < 3) {
				if (!wasPreviousGroupNodeExpanded) {
					previousMatchGroupNode.hide();
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
						if (groupNode.getText().equals(bestMatch.getGroup())) {
							if (previousMatchGroupNode != groupNode) {
								if (!wasPreviousGroupNodeExpanded) {
									previousMatchGroupNode.hide();
								}
								
								if (previousMatchNameNode != null) {
									previousMatchNameNode.fireEvent(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
								}
								
								previousMatchGroupNode = groupNode;
								wasPreviousGroupNodeExpanded = groupNode.isExpanded();
								
								if (!groupNode.isExpanded()) {
									groupNode.show();
								}
								
								for (TextNode nameNode : groupNode.getNodes()) {
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
			Select.addTag(bestMatch);
			Reload.start();
		};
	}
	private Tag getBestMatch(String query) {
		//	simple check if the name of any tag starts with query
		for (Tag tag : TagList.getMainInstance()) {
			if (tag.getName().toLowerCase().startsWith(query)) {
				return tag;
			}
		}
		
		//	more complex check for string similarity
		Tag bestMatch = null;
		double bestMatchFactor = 0;
		
		for (Tag tag : TagList.getMainInstance()) {
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
	public static PaneSelect get() {
		return Loader.INSTANCE;
	}
}
