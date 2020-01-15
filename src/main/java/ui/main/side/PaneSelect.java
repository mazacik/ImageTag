package ui.main.side;

import base.CustomList;
import base.entity.Entity;
import control.Select;
import control.filter.Filter;
import enums.Direction;
import ui.custom.ClickMenu;
import ui.decorator.Decorator;
import ui.node.EditNode;
import ui.node.NodeTemplates;
import ui.node.TextNode;

public class PaneSelect extends SidePaneBase {
	private EditNode nodeSearch;
	
	public void init() {
		nodeTitle.setBorder(Decorator.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		nodeSearch = new EditNode("Quick Search");
		nodeSearch.setBorder(Decorator.getBorder(0, 0, 1, 0));
		nodeSearch.prefWidthProperty().bind(this.widthProperty());
		//nodeSearch.textProperty().addListener(getSearchTextListener());
		//nodeSearch.setOnAction(getSearchOnAction());
		
		TextNode nodeSelectAll = NodeTemplates.SELECTION_SET_ALL.get();
		TextNode nodeSelectNone = NodeTemplates.SELECTION_SET_NONE.get();
		ClickMenu.install(nodeTitle, Direction.LEFT, nodeSelectAll, nodeSelectNone);
		
		this.setBorder(Decorator.getBorder(0, 0, 0, 1));
		this.getChildren().addAll(nodeTitle, nodeSearch, scrollPane);
	}
	
	public boolean refresh() {
		refreshTitle();
		
		if (Select.getEntities().isEmpty()) {
			Select.setTarget(Filter.getEntities().getFirst());
			Select.getEntities().set(Select.getTarget());
		}
		
		CustomList<String> stringListIntersect = new CustomList<>();
		Select.getEntities().getTagListIntersect().forEach(tag -> stringListIntersect.add(tag.getStringValue()));
		CustomList<String> stringListUnion = new CustomList<>();
		Select.getEntities().getTagList().forEach(tag -> stringListUnion.add(tag.getStringValue()));
		
		getTagNodes().forEach(tagNode -> refreshNodeColor(tagNode, stringListIntersect, stringListUnion));
		
		return true;
	}
	private void refreshNodeColor(TagNode tagNode, CustomList<String> stringListIntersect, CustomList<String> stringListUnion) {
		String stringNode = tagNode.getStringValue();
		for (String stringTag : stringListIntersect) {
			if (stringTag.startsWith(stringNode)) {
				tagNode.setTextFill(Decorator.getColorPositive());
				return;
			}
		}
		for (String stringTag : stringListUnion) {
			if (stringTag.startsWith(stringNode)) {
				tagNode.setTextFill(Decorator.getColorUnion());
				return;
			}
		}
		tagNode.setTextFill(Decorator.getColorPrimary());
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
	
	//	private Tag bestMatch = null;
	//	private TextNode previousMatchNameNode = null;
	//	//private GroupNode previousMatchGroupNode = null;
	//	private boolean wasPreviousGroupNodeExpanded = true;
	//
	//	//	private ChangeListener<? super String> getSearchTextListener() {
	//	//		return (ChangeListener<String>) (observable, oldValue, newValue) -> {
	//	//			if (newValue.length() < 3) {
	//	//				if (!wasPreviousGroupNodeExpanded) {
	//	//					previousMatchGroupNode.collapse();
	//	//					wasPreviousGroupNodeExpanded = true;
	//	//				}
	//	//				if (previousMatchGroupNode != null) {
	//	//					previousMatchGroupNode = null;
	//	//				}
	//	//				if (previousMatchNameNode != null) {
	//	//					previousMatchNameNode.fireEvent(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
	//	//					previousMatchNameNode = null;
	//	//				}
	//	//			} else {
	//	//				bestMatch = this.getBestMatch(newValue);
	//	//				if (bestMatch != null) {
	//	//					for (GroupNode groupNode : getGroupNodes()) {
	//	//						if (groupNode.getGroup().equals(bestMatch.getGroup())) {
	//	//							if (previousMatchGroupNode != groupNode) {
	//	//								if (!wasPreviousGroupNodeExpanded) {
	//	//									previousMatchGroupNode.collapse();
	//	//								}
	//	//
	//	//								if (previousMatchNameNode != null) {
	//	//									previousMatchNameNode.fireEvent(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
	//	//								}
	//	//
	//	//								previousMatchGroupNode = groupNode;
	//	//								wasPreviousGroupNodeExpanded = groupNode.isExpanded();
	//	//
	//	//								if (!groupNode.isExpanded()) {
	//	//									groupNode.expand();
	//	//								}
	//	//
	//	//								for (NodeText nameNode : groupNode.getNameNodes()) {
	//	//									if (nameNode.getText().equals(bestMatch.getName())) {
	//	//										previousMatchNameNode = nameNode;
	//	//										nameNode.fireEvent(new MouseEvent(MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
	//	//									}
	//	//								}
	//	//							}
	//	//						}
	//	//					}
	//	//				}
	//	//			}
	//	//		};
	//	//	}
	//	private EventHandler<ActionEvent> getSearchOnAction() {
	//		return event -> {
	//			if (previousMatchNameNode != null) {
	//				previousMatchNameNode.fireEvent(new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
	//			}
	//
	//			nodeSearch.clear();
	//			Select.getEntities().addTag(bestMatch.getID());
	//			Reload.start();
	//		};
	//	}
	//	private Tag getBestMatch(String query) {
	//		//	simple check if the name of any tag starts with query
	//		for (Tag tag : TagList.getMain()) {
	//			//if (tag.getName().toLowerCase().startsWith(query)) {
	//			return tag;
	//			//}
	//		}
	//
	//		//	more complex check for string similarity
	//		Tag bestMatch = null;
	//		double bestMatchFactor = 0;
	//
	//		for (Tag tag : TagList.getMain()) {
	//			SimilarityStrategy strategy = new JaroWinklerStrategy();
	//			StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
	//			//double currentFactor = service.score(tag.getName(), query);
	//
	//			//if (currentFactor > bestMatchFactor) {
	//			bestMatch = tag;
	//			//bestMatchFactor = currentFactor;
	//			//}
	//		}
	//
	//		return bestMatch;
	//	}
	
	private PaneSelect() {}
	private static class Loader {
		private static final PaneSelect INSTANCE = new PaneSelect();
	}
	public static PaneSelect getInstance() {
		return Loader.INSTANCE;
	}
	
	public EditNode getNodeSearch() {
		return nodeSearch;
	}
}
