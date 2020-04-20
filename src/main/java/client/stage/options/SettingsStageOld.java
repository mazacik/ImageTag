package client.stage.options;

import client.node.EditNode;
import client.node.override.GridPane;
import client.node.textnode.TextNode;
import client.stage.AbstractStageBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import server.misc.Settings;

public class SettingsStageOld extends AbstractStageBase {
	public SettingsStageOld() {
		super("Settings", false);
		
		GridPane gridPane = new GridPane();
		gridPane.setVgap(3);
		gridPane.setHgap(3);
		gridPane.setPadding(new Insets(3));
		gridPane.setAlignment(Pos.CENTER);
		
		for (Settings setting : Settings.values()) {
			if (setting.isUserModifiable()) {
				TextNode textNode = new TextNode(setting.getName(), false, false, false, true);
				EditNode editNode = new EditNode(String.valueOf(setting.getValue()), "", EditNode.EditNodeType.NUMERIC_POSITIVE);
				TextNode resetNode = new TextNode("⟲", true, true, false, true);
				resetNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> editNode.setText(String.valueOf(setting.getDefault())));
				
				gridPane.add(textNode, 0, setting.ordinal());
				gridPane.add(editNode, 1, setting.ordinal());
				gridPane.add(resetNode, 2, setting.ordinal());
			}
		}
		
		TextNode nodeApply = new TextNode("Apply", true, true, false, true);
		nodeApply.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			for (Settings setting : Settings.values()) {
				if (setting.isUserModifiable()) {
					EditNode editNode = (EditNode) gridPane.getNode(1, setting.ordinal());
					setting.setValue(editNode.getText());
				}
			}
			this.close();
		});
		
		TextNode nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		this.setRoot(gridPane);
		this.setButtons(nodeApply, nodeCancel);
	}
}
