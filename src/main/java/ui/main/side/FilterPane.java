package ui.main.side;

import control.filter.Filter;
import control.reload.Reload;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.decorator.Decorator;
import ui.node.NodeTemplates;
import ui.node.TextNode;
import ui.override.HBox;
import ui.stage.FilterOptionStage;

public class FilterPane extends SidePaneBase {
	public boolean refresh() {
		nodeTitle.setText("Filter: " + Filter.getEntities().size());
		
		return true;
	}
	
	private FilterPane() {
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
		
		TextNode btnCreateNewTag = NodeTemplates.TAG_CREATE.get();
		btnCreateNewTag.setBorder(Decorator.getBorder(0, 0, 1, 0));
		btnCreateNewTag.prefWidthProperty().bind(this.widthProperty());
		
		this.setBorder(Decorator.getBorder(0, 1, 0, 0));
		this.getChildren().addAll(hBoxTitle, btnCreateNewTag, scrollPane);
	}
	private static class Loader {
		private static final FilterPane INSTANCE = new FilterPane();
	}
	public static FilterPane getInstance() {
		return Loader.INSTANCE;
	}
}
