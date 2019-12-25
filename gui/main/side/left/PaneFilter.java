package gui.main.side.left;

import baseobject.tag.Tag;
import control.reload.ChangeIn;
import gui.component.simple.HBox;
import gui.component.simple.TextNode;
import gui.decorator.ColorUtil;
import gui.main.side.GroupNode;
import gui.main.side.SidePaneBase;
import gui.stage.StageManager;
import gui.stage.template.tageditstage.TagEditStageResult;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import tools.NodeUtil;

import java.util.logging.Logger;

public class PaneFilter extends SidePaneBase {
	public PaneFilter() {
	
	}
	
	public void init() {
		nodeTitle = new TextNode("", false, false, false, true);
		
		TextNode btnCreateNewTag = new TextNode("Create a New Tag", true, true, false, true);
		btnCreateNewTag.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		btnCreateNewTag.prefWidthProperty().bind(this.widthProperty());
		btnCreateNewTag.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			TagEditStageResult result = StageManager.getTagEditStage().show("");
			Tag tag = mainTagList.getTag(result.getGroup(), result.getName());
			mainTagList.add(tag);
			mainTagList.sort();
			
			if (result.isAddToSelect()) {
				select.addTag(tag);
			}
			
			reload.notify(ChangeIn.TAG_LIST_MAIN);
			reload.doReload();
		});
		
		TextNode btnReset = new TextNode("⟲", true, true, false, true);
		btnReset.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			filter.reset();
			reload.doReload();
		});
		
		TextNode btnSettings = new TextNode("⁝", true, true, false, true);
		btnSettings.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> StageManager.getFilterSettingsStage().show(""));
		
		HBox hBoxTitle = new HBox(btnReset, nodeTitle, btnSettings);
		hBoxTitle.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		this.setBorder(NodeUtil.getBorder(0, 1, 0, 0));
		this.getChildren().addAll(hBoxTitle, btnCreateNewTag, scrollPane);
	}
	
	public boolean refresh() {
		Logger.getGlobal().info(this.toString());
		
		nodeTitle.setText("Filter: " + filter.size());
		
		Color textColorDefault = ColorUtil.getColorPrimary();
		Color textColorPositive = ColorUtil.getColorPositive();
		Color textColorNegative = ColorUtil.getColorNegative();
		
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				String group = groupNode.getText();
				
				if (filter.isWhitelisted(group)) {
					groupNode.setTextFill(textColorPositive);
				} else if (filter.isBlacklisted(group)) {
					groupNode.setTextFill(textColorNegative);
				} else {
					groupNode.setTextFill(textColorDefault);
				}
				for (TextNode nameNode : groupNode.getNodes()) {
					String name = nameNode.getText();
					
					if (filter.isWhitelisted(group, name)) {
						nameNode.setTextFill(textColorPositive);
					} else if (filter.isBlacklisted(group, name)) {
						nameNode.setTextFill(textColorNegative);
					} else {
						nameNode.setTextFill(textColorDefault);
					}
				}
			}
		}
		
		return true;
	}
}