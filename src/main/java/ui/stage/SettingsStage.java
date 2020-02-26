package ui.stage;

import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import misc.Settings;
import ui.node.EditNode;
import ui.node.TextNode;
import ui.override.GridPane;

public class SettingsStage extends AbstractStage {
	private static final GridPane gridPane;
	private static final TextNode nodeApply;
	private static final TextNode nodeCancel;
	
	static {
		gridPane = new GridPane();
		gridPane.setVgap(3);
		gridPane.setHgap(3);
		gridPane.setPadding(new Insets(3));
		
		for (Settings setting : Settings.values()) {
			if (setting.isModifiable()) {
				TextNode textNode = new TextNode(setting.name(), false, false, false, true);
				EditNode editNode = new EditNode(String.valueOf(setting.getValue()), "", EditNode.EditNodeType.NUMERIC_POSITIVE);
				TextNode resetNode = new TextNode("⟲", true, true, false, true);
				resetNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> editNode.setText(String.valueOf(setting.getDefaultValue())));
				
				gridPane.add(textNode, 0, setting.ordinal());
				gridPane.add(editNode, 1, setting.ordinal());
				gridPane.add(resetNode, 2, setting.ordinal());
			}
		}
		
		nodeApply = new TextNode("Apply", true, true, false, true);
		nodeApply.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			for (Settings setting : Settings.values()) {
				EditNode editNode = (EditNode) gridPane.getNode(1, setting.ordinal());
				setting.setValue(editNode.getText());
			}
			getInstance().close();
		});
		
		nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			getInstance().close();
		});
	}
	
	public static void show(String... args) {
		for (Settings setting : Settings.values()) {
			if (setting.isModifiable()) {
				EditNode editNode = (EditNode) gridPane.getNode(1, setting.ordinal());
				editNode.setText(String.valueOf(setting.getValue()));
			}
		}
		
		getInstance().showAndWait();
	}
	
	private SettingsStage() {
		super("Settings", false);
		setRoot(gridPane);
		setButtons(nodeApply, nodeCancel);
	}
	private static class Loader {
		private static final SettingsStage INSTANCE = new SettingsStage();
	}
	public static SettingsStage getInstance() {
		return Loader.INSTANCE;
	}
}
