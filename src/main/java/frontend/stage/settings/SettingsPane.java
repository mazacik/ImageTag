package frontend.stage.settings;

import frontend.UserInterface;
import frontend.node.SeparatorNode;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import frontend.stage.primary.scene.MainSceneMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class SettingsPane extends BorderPane {
	private final HBox mainBox;
	
	public SettingsPane() {
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
		
		mainBox = new HBox(hBoxLeft, new SeparatorNode(), filterSettingsPane);
		
		TextNode nodeApply = new TextNode("Apply", true, true, false, true);
		nodeApply.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			filterSettingsPane.apply();
			UserInterface.getStage().getMainScene().setMode(MainSceneMode.DEFAULT);
		});
		TextNode nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> UserInterface.getStage().getMainScene().setMode(MainSceneMode.DEFAULT));
		
		this.setCenter(mainBox);
		
		HBox buttonBox = new HBox(nodeApply, nodeCancel);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(0, 3, 3, 3));
		this.setBottom(buttonBox);
		
		mainBox.setAlignment(Pos.CENTER);
	}
	
	private void setContent(Node content) {
		mainBox.getChildren().set(2, content);
	}
}
