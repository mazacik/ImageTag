package application.gui.panes.side;

import application.controller.Filter;
import application.controller.Reload;
import application.controller.Select;
import application.controller.Target;
import application.database.object.DataObject;
import application.database.object.TagObject;
import application.gui.decorator.ColorUtil;
import application.gui.nodes.NodeUtil;
import application.gui.nodes.buttons.ButtonTemplates;
import application.gui.nodes.popup.ClickMenuLeft;
import application.gui.nodes.simple.EditNode;
import application.gui.nodes.simple.SeparatorNode;
import application.gui.nodes.simple.TextNode;
import application.main.Instances;
import application.misc.CompareUtil;
import application.misc.enums.Direction;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class SelectPane extends SidePaneBase {
	private final EditNode tfSearch;
	private String actualText = "";
	
	public SelectPane() {
		nodeTitle = new TextNode("", true, true, false, true);
		nodeTitle.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		tfSearch = new EditNode("Search tags to add to selection");
		tfSearch.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		
		TextNode nodeSelectAll = ButtonTemplates.SELECTION_SET_ALL.get();
		TextNode nodeSelectNone = ButtonTemplates.SELECTION_SET_NONE.get();
		TextNode nodeSelectMerge = ButtonTemplates.SELECTION_MERGE.get();
		ClickMenuLeft.install(nodeTitle, Direction.LEFT, nodeSelectAll, nodeSelectNone, new SeparatorNode(), nodeSelectMerge);
		
		this.getChildren().addAll(nodeTitle, tfSearch, scrollPane);
		
		tfSearchEvents();
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
		if (hiddenTilesCount > 0) text += "(+" + hiddenTilesCount + " hidden)";
		
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
	
	private void tfSearchEvents() {
		tfSearch.addEventFilter(KeyEvent.KEY_TYPED, Event::consume);
		tfSearch.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			/* first update the actualText variable */
			if (event.getCode().isLetterKey()) {
				actualText += event.getText().toLowerCase();
			} else if (event.getCode() == KeyCode.BACK_SPACE) {
				//todo only delete selected text (if there is any)
				if (!actualText.isEmpty()) {
					actualText = actualText.substring(0, actualText.length() - 1);
					event.consume();
				}
			} else if (event.getCode() == KeyCode.ESCAPE) {
				Instances.getGalleryPane().requestFocus();
			}
			
			/* second update the TextField text */
			if (actualText.isEmpty()) {
				tfSearch.clear();
			} else if (actualText.length() < 3) {
				tfSearch.setText(actualText);
				tfSearch.positionCaret(tfSearch.getLength());
			} else if (actualText.length() >= 3) {
				TagObject bestMatch = null;
				//tag's name starts with actualtext
				for (TagObject tagObject : Instances.getTagListMain()) {
					if (tagObject.getName().toLowerCase().startsWith(actualText)) {
						bestMatch = tagObject;
						break;
					}
				}
				if (bestMatch == null) {
					//tag's name contains actualtext
					for (TagObject tagObject : Instances.getTagListMain()) {
						if (tagObject.getName().toLowerCase().contains(actualText)) {
							bestMatch = tagObject;
							break;
						}
					}
				}
				if (bestMatch == null) {
					//custom string similarity algorithm
					//todo needs more work (probably)
					double bestMatchFactor = 0;
					for (TagObject tagObject : Instances.getTagListMain()) {
						double currentFactor = CompareUtil.getStringSimilarity(actualText, tagObject.getFull());
						if (currentFactor > bestMatchFactor) {
							bestMatchFactor = currentFactor;
							bestMatch = tagObject;
						}
					}
				}
				
				if (bestMatch != null) {
					String tagFull = bestMatch.getFull();
					tfSearch.setText(tagFull);
					int lastIndexOfActualText = tagFull.toLowerCase().lastIndexOf(actualText);
					if (lastIndexOfActualText == -1) {
						tfSearch.positionCaret(tfSearch.getText().length());
					} else {
						tfSearch.positionCaret(lastIndexOfActualText + actualText.length());
					}
				} else {
					tfSearch.setText(actualText);
					tfSearch.positionCaret(tfSearch.getLength());
				}
			}
		});
		tfSearch.setOnAction(event -> {
			if (!Instances.getSelect().isEmpty()) {
				TagObject tagObject = Instances.getTagListMain().getTagObject(tfSearch.getText());
				if (tagObject != null) {
					addTagObjectToSelection(tagObject);
					actualText = "";
					tfSearch.clear();
					Instances.getReload().notify(Reload.Control.TAG);
					Instances.getReload().doReload();
				}
			}
		});
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
				this.removeTagObjectFromSelection(tagObject);
			} else {
				nameNode.setTextFill(ColorUtil.getTextColorPos());
				this.addTagObjectToSelection(tagObject);
			}
			
			Instances.getReload().notify(Reload.Control.TAG);
			Instances.getReload().doReload();
		}
	}
	public void addTagObjectToSelection(TagObject tagObject) {
		if (Instances.getSelect().size() < 1) {
			DataObject currentTargetedItem = Instances.getTarget().getCurrentTarget();
			if (currentTargetedItem != null) {
				currentTargetedItem.getTagList().add(tagObject);
			}
		} else {
			Instances.getSelect().addTagObject(tagObject);
		}
	}
	public void removeTagObjectFromSelection(TagObject tagObject) {
		if (Instances.getSelect().size() < 1) {
			DataObject currentTargetedItem = Instances.getTarget().getCurrentTarget();
			if (currentTargetedItem != null) {
				currentTargetedItem.getTagList().remove(tagObject);
			}
		} else {
			Instances.getSelect().removeTagObject(tagObject);
		}
	}
	
	public TextField getTfSearch() {
		return tfSearch;
	}
}
