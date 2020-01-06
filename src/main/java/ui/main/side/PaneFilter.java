package ui.main.side;

import base.tag.Tag;
import base.tag.TagList;
import control.Select;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import ui.decorator.Decorator;
import ui.node.NodeText;
import ui.override.HBox;
import ui.stage.StageEditTag;
import ui.stage.StageFilterOptions;

public class PaneFilter extends SidePaneBase {
	public void init() {
		nodeTitle = new NodeText("", false, false, false, true);
		
		NodeText btnCreateNewTag = new NodeText("Create a New Tag", true, true, false, true);
		btnCreateNewTag.setBorder(Decorator.getBorder(0, 0, 1, 0));
		btnCreateNewTag.prefWidthProperty().bind(this.widthProperty());
		btnCreateNewTag.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			StageEditTag.Result result = StageEditTag.show("");
			if (result != null) {
				Tag tag = new Tag(result.getGroup(), result.getName());
				TagList.getMain().add(tag);
				TagList.getMain().sort();
				
				if (result.isAddToSelect()) Select.getEntities().addTag(tag);
				
				Reload.notify(Notifier.TAG_LIST_MAIN);
				Reload.start();
			}
		});
		
		NodeText btnReset = new NodeText("⟲", true, true, false, true);
		btnReset.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Filter.reset();
			Reload.start();
		});
		
		NodeText btnSettings = new NodeText("⁝", true, true, false, true);
		btnSettings.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> StageFilterOptions.show(""));
		
		HBox hBoxTitle = new HBox(btnReset, nodeTitle, btnSettings);
		hBoxTitle.setBorder(Decorator.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		this.setBorder(Decorator.getBorder(0, 1, 0, 0));
		this.getChildren().addAll(hBoxTitle, btnCreateNewTag, scrollPane);
	}
	
	public boolean refresh() {
		nodeTitle.setText("Filter: " + Filter.getEntities().size());
		
		Color textColorDefault = Decorator.getColorPrimary();
		Color textColorPositive = Decorator.getColorPositive();
		Color textColorNegative = Decorator.getColorNegative();
		Color textColorShare = Decorator.getColorShare();
		
		for (Node node : boxGroupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				String group = groupNode.getGroup();
				
				if (Filter.getListManager().isWhitelisted(group)) {
					groupNode.setTextFill(textColorPositive);
				} else if (Filter.getListManager().isBlacklisted(group)) {
					groupNode.setTextFill(textColorNegative);
				} else if (Filter.getListManager().isUnlisted(group)) {
					groupNode.setTextFill(textColorDefault);
				} else {
					groupNode.setTextFill(textColorShare);
				}
				
				for (NodeText nameNode : groupNode.getNameNodes()) {
					if (Filter.getListManager().isWhitelisted(group, nameNode.getText())) {
						nameNode.setTextFill(textColorPositive);
					} else if (Filter.getListManager().isBlacklisted(group, nameNode.getText())) {
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
