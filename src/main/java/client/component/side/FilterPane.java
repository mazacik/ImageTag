package client.component.side;

import client.decorator.DecoratorUtil;
import client.node.textnode.TextNode;
import client.node.textnode.TextNodeTemplates;
import client.stage.options.SettingsStage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import main.Root;
import server.control.reload.Reload;

public class FilterPane extends SidePaneBase {
	private final TextNode nodeText;
	
	public FilterPane() {
		TextNode btnReset = new TextNode("⟲", true, true, false, true);
		btnReset.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Root.FILTER.reset();
			Reload.start();
		});
		
		nodeText = new TextNode("", false, false, false, true);
		nodeText.setMaxWidth(Double.MAX_VALUE);
		
		TextNode btnSettings = new TextNode("⁝", true, true, false, true);
		btnSettings.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> new SettingsStage().showAndWait());
		
		BorderPane paneTitle = new BorderPane();
		paneTitle.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		paneTitle.setLeft(btnReset);
		paneTitle.setCenter(nodeText);
		paneTitle.setRight(btnSettings);
		
		TextNode btnCreateNewTag = TextNodeTemplates.TAG_CREATE.get();
		btnCreateNewTag.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		
		this.setBorder(DecoratorUtil.getBorder(0, 1, 0, 0));
		this.getChildren().addAll(paneTitle, btnCreateNewTag, scrollPane);
	}
	
	public boolean refresh() {
		nodeText.setText("Filter: " + Root.FILTER.size());
		
		return true;
	}
}
