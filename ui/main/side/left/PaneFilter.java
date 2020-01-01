package ui.main.side.left;

import base.tag.Tag;
import base.tag.TagList;
import control.filter.Filter;
import control.Select;
import control.reload.ChangeIn;
import control.reload.Reload;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import ui.NodeUtil;
import ui.component.simple.HBox;
import ui.component.simple.TextNode;
import ui.decorator.ColorUtil;
import ui.main.side.GroupNode;
import ui.main.side.SidePaneBase;
import ui.stage.StageManager;
import ui.stage.template.tageditstage.TagEditStageResult;

import java.util.logging.Logger;

public class PaneFilter extends SidePaneBase {
	public void init() {
		nodeTitle = new TextNode("", false, false, false, true);
		
		TextNode btnCreateNewTag = new TextNode("Create a New Tag", true, true, false, true);
		btnCreateNewTag.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		btnCreateNewTag.prefWidthProperty().bind(this.widthProperty());
		btnCreateNewTag.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			TagEditStageResult result = StageManager.getTagEditStage().show("");
			Tag tag = TagList.getMain().getTag(result.getGroup(), result.getName());
			TagList.getMain().add(tag);
			TagList.getMain().sort();
			
			if (result.isAddToSelect()) {
				Select.getEntities().addTag(tag);
			}
			
			Reload.notify(ChangeIn.TAG_LIST_MAIN);
			Reload.start();
		});
		
		TextNode btnReset = new TextNode("⟲", true, true, false, true);
		btnReset.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Filter.reset();
			Reload.start();
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
		
		nodeTitle.setText("Filter: " + Filter.getEntities().size());
		
		Color textColorDefault = ColorUtil.getColorPrimary();
		Color textColorPositive = ColorUtil.getColorPositive();
		Color textColorNegative = ColorUtil.getColorNegative();
		
		for (Node node : boxGroupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				String group = groupNode.getGroup();
				
				if (Filter.getListManager().isWhitelisted(group)) {
					groupNode.setTextFill(textColorPositive);
				} else if (Filter.getListManager().isBlacklisted(group)) {
					groupNode.setTextFill(textColorNegative);
				} else {
					groupNode.setTextFill(textColorDefault);
				}
				for (TextNode nameNode : groupNode.getNameNodes()) {
					String name = nameNode.getText();
					
					if (Filter.getListManager().isWhitelisted(group, name)) {
						nameNode.setTextFill(textColorPositive);
					} else if (Filter.getListManager().isBlacklisted(group, name)) {
						nameNode.setTextFill(textColorNegative);
					} else {
						nameNode.setTextFill(textColorDefault);
					}
				}
			}
		}
		
		return true;
	}
	
	private PaneFilter() {}
	private static class Loader {
		private static final PaneFilter INSTANCE = new PaneFilter();
	}
	public static PaneFilter getInstance() {
		return Loader.INSTANCE;
	}
}
