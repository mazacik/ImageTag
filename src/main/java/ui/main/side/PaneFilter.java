package ui.main.side;

import base.CustomList;
import base.tag.Tag;
import base.tag.TagList;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.decorator.Decorator;
import ui.node.TextNode;
import ui.override.HBox;
import ui.stage.TagEditStage;
import ui.stage.FilterOptionStage;

public class PaneFilter extends SidePaneBase {
	public void init() {
		TextNode btnCreateNewTag = new TextNode("Create a New Tag", true, true, false, true);
		btnCreateNewTag.setBorder(Decorator.getBorder(0, 0, 1, 0));
		btnCreateNewTag.prefWidthProperty().bind(this.widthProperty());
		btnCreateNewTag.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			CustomList<String> levels = TagEditStage.show(null);
			if (levels != null) {
				TagList.getMain().add(new Tag(levels));
				TagList.getMain().sort();
				
				Reload.notify(Notifier.TAG_LIST_MAIN);
				Reload.start();
			}
		});
		
		TextNode btnReset = new TextNode("⟲", true, true, false, true);
		btnReset.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Filter.reset();
			Reload.start();
		});
		
		TextNode btnSettings = new TextNode("⁝", true, true, false, true);
		btnSettings.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> FilterOptionStage.show(""));
		
		HBox hBoxTitle = new HBox(btnReset, nodeTitle, btnSettings);
		hBoxTitle.setBorder(Decorator.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		this.setBorder(Decorator.getBorder(0, 1, 0, 0));
		this.getChildren().addAll(hBoxTitle, btnCreateNewTag, scrollPane);
	}
	
	public boolean refresh() {
		nodeTitle.setText("Filter: " + Filter.getEntities().size());
		
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
