package client.stage.options;

import client.node.SeparatorNode;
import client.node.override.HBox;
import client.node.override.VBox;
import client.node.textnode.TextNode;
import client.stage.AbstractStageBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class SettingsStage extends AbstractStageBase {
	public SettingsStage() {
		super("Settings", false);
		
		TextNode settingsFilterNode = new TextNode("Filter", false, false, false, true);
		
		VBox hBoxLeftList = new VBox(settingsFilterNode);
		hBoxLeftList.setPrefWidth(150);
		
		FilterPreferences filterPreferences = new FilterPreferences();
		filterPreferences.setPrefWidth(600);
		
		HBox hBoxMain = new HBox(hBoxLeftList, new SeparatorNode(), filterPreferences);
		
		TextNode nodeApply = new TextNode("Apply", true, true, false, true);
		nodeApply.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			filterPreferences.apply();
			this.close();
		});
		TextNode nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		this.setRoot(hBoxMain);
		this.setButtons(nodeApply, nodeCancel);
	}
}
