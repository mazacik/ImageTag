package ui.main.side;

import base.tag.Tag;
import base.tag.TagList;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.decorator.Decorator;
import ui.node.NodeText;
import ui.override.HBox;
import ui.stage.StageEditTag;
import ui.stage.StageFilterOptions;

public class PaneFilter extends SidePaneBase {
	public void init() {
		NodeText btnCreateNewTag = new NodeText("Create a New Tag", true, true, false, true);
		btnCreateNewTag.setBorder(Decorator.getBorder(0, 0, 1, 0));
		btnCreateNewTag.prefWidthProperty().bind(this.widthProperty());
		btnCreateNewTag.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Tag result = new Tag(StageEditTag.show(null));
			if (result != null) {
				TagList.getMain().add(result);
				TagList.getMain().sort();
				
				//if (result.isAddToSelect()) Select.getEntities().addTag(tag.getID());
				
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
		
		//		Color textColorDefault = Decorator.getColorPrimary();
		//		Color textColorPositive = Decorator.getColorPositive();
		//		Color textColorNegative = Decorator.getColorNegative();
		//
		//		CustomList<TagNode> tagNodes = new CustomList<>();
		//		getTagNodes(tagNodes);
		//		for (TagNode tagNode : tagNodes) {
		//			if (tagNode.isLast()) {
		//				if (Filter.getListManager().isWhitelisted(tagNode.getStringValue())) {
		//					tagNode.setTextFill(textColorPositive);
		//				} else if (Filter.getListManager().isBlacklisted(tagNode.getStringValue())) {
		//					tagNode.setTextFill(textColorNegative);
		//				} else if (Filter.getListManager().isUnlisted(tagNode.getStringValue())) {
		//					tagNode.setTextFill(textColorDefault);
		//				}
		//			} else {
		//				if (tagNode.getListMode() == 0) {
		//					tagNode.setTextFill(textColorDefault);
		//				} else if (tagNode.getListMode() == 1) {
		//					tagNode.setTextFill(textColorPositive);
		//				} else if (tagNode.getListMode() == 2) {
		//					tagNode.setTextFill(textColorNegative);
		//				}
		//			}
		//		}
		
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
