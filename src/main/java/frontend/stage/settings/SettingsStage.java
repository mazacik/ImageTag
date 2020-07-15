package frontend.stage.settings;

import frontend.node.SeparatorNode;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import frontend.stage.BaseStage;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class SettingsStage extends BaseStage {
	private final HBox hBoxMain;
	
	public SettingsStage() {
		super("Settings", 0.75);
		
		FilterSettingsPane filterSettingsPane = new FilterSettingsPane();
		filterSettingsPane.setPrefWidth(600);
		
		TextNode nodeFilterSettings = new TextNode("Filter Settings", true, true, false, true);
		nodeFilterSettings.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> this.setContent(filterSettingsPane));
		nodeFilterSettings.setMaxWidth(Double.MAX_VALUE);
		
		FilterPresetsPane filterPresetsPane = new FilterPresetsPane();
		filterPresetsPane.setPrefWidth(600);
		
		TextNode nodeFilterPresets = new TextNode("Filter Presets", true, true, false, true);
		nodeFilterPresets.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> this.setContent(filterPresetsPane));
		nodeFilterPresets.setMaxWidth(Double.MAX_VALUE);
		
		VBox hBoxLeft = new VBox(nodeFilterSettings, nodeFilterPresets);
		hBoxLeft.setPrefWidth(150);
		
		hBoxMain = new HBox(hBoxLeft, new SeparatorNode(), filterSettingsPane);
		
		TextNode nodeApply = new TextNode("Apply", true, true, false, true);
		nodeApply.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			filterSettingsPane.apply();
			this.close();
		});
		TextNode nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		this.setRoot(hBoxMain);
		this.setButtons(nodeApply, nodeCancel);
	}
	
	private void setContent(Node content) {
		hBoxMain.getChildren().set(2, content);
	}
}
