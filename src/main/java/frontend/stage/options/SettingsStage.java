package frontend.stage.options;

import frontend.node.SeparatorNode;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import frontend.stage.BaseStage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class SettingsStage extends BaseStage {
	public SettingsStage() {
		super("Settings");
		
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
