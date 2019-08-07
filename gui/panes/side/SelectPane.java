package application.gui.panes.side;

import application.controller.Filter;
import application.controller.Reload;
import application.controller.Select;
import application.controller.Target;
import application.database.object.DataObject;
import application.database.object.TagObject;
import application.gui.decorator.ColorUtil;
import application.gui.decorator.Decorator;
import application.gui.decorator.SizeUtil;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.ColorData;
import application.gui.nodes.NodeUtil;
import application.gui.nodes.buttons.ButtonFactory;
import application.gui.nodes.buttons.ButtonTemplates;
import application.gui.nodes.popup.ClickMenuLeft;
import application.gui.nodes.simple.EditNode;
import application.gui.nodes.simple.SeparatorNode;
import application.gui.nodes.simple.TextNode;
import application.main.Instances;
import application.misc.enums.Direction;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class SelectPane extends SidePaneBase {
	private final EditNode tfSearch;
	private String actualText = "";
	
	private boolean needsReload;
	
	public SelectPane() {
		needsReload = false;
		
		ColorData colorDataSimple = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		
		nodeTitle = new TextNode("", colorDataSimple);
		nodeTitle.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		tfSearch = new EditNode("Search tags to add to selection");
		tfSearch.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		
		ButtonFactory bf = ButtonFactory.getInstance();
		TextNode nodeSelectAll = bf.get(ButtonTemplates.SEL_ALL);
		TextNode nodeSelectNone = bf.get(ButtonTemplates.SEL_NONE);
		TextNode nodeSelectMerge = bf.get(ButtonTemplates.SEL_MERGE);
		ClickMenuLeft.install(nodeTitle, Direction.LEFT, nodeSelectAll, nodeSelectNone, new SeparatorNode(), nodeSelectMerge);
		
		tagNodesBox = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
		scrollPane = new ScrollPane();
		scrollPane.setContent(tagNodesBox);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		Decorator.manage(scrollPane, ColorType.DEF);
		
		this.setPrefWidth(SizeUtil.getUsableScreenWidth());
		this.setMinWidth(SizeUtil.getMinWidthSideLists());
		this.getChildren().addAll(nodeTitle, tfSearch, scrollPane);
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
		
		String text = "Selection: " + select.size() + " file(s)";
		if (hiddenTilesCount > 0) {
			text += ", " + hiddenTilesCount + " hidden by filter";
		}
		
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
		
		refresh();
		for (Node node : tagNodesBox.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				String group = tagNode.getGroup();
				
				if (groupsInter.contains(group)) {
					tagNode.setTextFill(textColorPositive);
				} else if (groupsShare.contains(group)) {
					tagNode.setTextFill(textColorShare);
				} else {
					tagNode.setTextFill(textColorDefault);
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
				for (TextNode nameNode : tagNode.getNameNodes()) {
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
	
	public void changeNodeState(TagNode tagNode, TextNode nameNode) {
		if (nameNode != null) {
			TagObject tagObject = Instances.getTagListMain().getTagObject(tagNode.getGroup(), nameNode.getText());
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
	
	@Override
	public boolean getNeedsReload() {
		return needsReload;
	}
	@Override
	public void setNeedsReload(boolean needsReload) {
		this.needsReload = needsReload;
	}
	
	public TextField getTfSearch() {
		return tfSearch;
	}
	public String getActualText() {
		return actualText;
	}
	
	public void setActualText(String actualText) {
		this.actualText = actualText;
	}
}
