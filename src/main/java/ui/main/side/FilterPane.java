package ui.main.side;

import control.filter.Filter;
import control.reload.Reload;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import ui.decorator.Decorator;
import ui.node.textnode.TextNode;
import ui.node.textnode.TextNodeTemplates;
import ui.stage.FilterOptionStage;

public class FilterPane extends SidePaneBase {
	private final TextNode nodeText;
	
	public boolean refresh() {
		nodeText.setText("Filter: " + Filter.getEntities().size());
		
		return true;
	}
	
	private FilterPane() {
		TextNode btnReset = new TextNode("⟲", true, true, false, true);
		btnReset.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Filter.reset();
			Reload.start();
		});
		
		nodeText = new TextNode("", false, false, false, true);
		nodeText.setMaxWidth(Double.MAX_VALUE);
		
		TextNode btnSettings = new TextNode("⁝", true, true, false, true);
		btnSettings.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> FilterOptionStage.show(""));
		
		BorderPane paneTitle = new BorderPane();
		paneTitle.setBorder(Decorator.getBorder(0, 0, 1, 0));
		paneTitle.setLeft(btnReset);
		paneTitle.setCenter(nodeText);
		paneTitle.setRight(btnSettings);
		
		TextNode btnCreateNewTag = TextNodeTemplates.TAG_CREATE.get();
		btnCreateNewTag.setBorder(Decorator.getBorder(0, 0, 1, 0));
		btnCreateNewTag.prefWidthProperty().bind(this.widthProperty());
		
		this.setBorder(Decorator.getBorder(0, 1, 0, 0));
		this.getChildren().addAll(paneTitle, btnCreateNewTag, scrollPane);
	}
	private static class Loader {
		private static final FilterPane INSTANCE = new FilterPane();
	}
	public static FilterPane getInstance() {
		return Loader.INSTANCE;
	}
}
