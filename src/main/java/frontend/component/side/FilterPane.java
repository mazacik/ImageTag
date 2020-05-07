package frontend.component.side;

import backend.control.reload.Reload;
import frontend.decorator.DecoratorUtil;
import frontend.node.textnode.TextNode;
import frontend.node.textnode.TextNodeTemplates;
import frontend.stage.settings.SettingsStage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import main.Main;

public class FilterPane extends SidePaneBase {
	private final TextNode nodeText;
	
	public FilterPane() {
		TextNode btnReset = new TextNode("⟲", true, true, false, true);
		btnReset.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Main.FILTER.reset();
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
		this.getChildren().addAll(paneTitle, btnCreateNewTag, listBox);
	}
	
	public boolean refresh() {
		nodeText.setText("Filter: " + Main.FILTER.size());
		
		return true;
	}
}
