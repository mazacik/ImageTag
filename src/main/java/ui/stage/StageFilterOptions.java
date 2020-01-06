package ui.stage;

import control.filter.Filter;
import control.filter.FilterSettings;
import control.reload.Notifier;
import control.reload.Reload;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.node.NodeCheckbox;
import ui.node.NodeEdit;
import ui.node.NodeText;
import ui.override.HBox;
import ui.override.VBox;
import ui.node.NodeSwitch;

public class StageFilterOptions extends StageBase {
	private static final NodeCheckbox nodeImages;
	private static final NodeCheckbox nodeGifs;
	private static final NodeCheckbox nodeVideos;
	
	private static final NodeCheckbox nodeSession;
	private static final NodeCheckbox nodeLimit;
	private static final NodeEdit nodeLimitValue;
	
	private static final HBox boxContent;
	
	private static final NodeText nodeOK;
	private static final NodeText nodeCancel;
	
	static {
		nodeImages = new NodeCheckbox("Images");
		nodeGifs = new NodeCheckbox("Gifs");
		nodeVideos = new NodeCheckbox("Videos");
		nodeSession = new NodeCheckbox("Session");
		
		nodeLimit = new NodeCheckbox("Limit");
		nodeLimitValue = new NodeEdit("", NodeEdit.EditNodeType.NUMERIC_POSITIVE);
		nodeLimit.getSelectedProperty().addListener((observable, oldValue, newValue) -> nodeLimitValue.setDisable(!newValue));
		
		nodeLimitValue.setPadding(new Insets(0, 1, -1, 1));
		nodeLimitValue.setPrefWidth(100);
		nodeLimitValue.setDisable(true);
		
		nodeImages.setAlignment(Pos.CENTER_LEFT);
		nodeGifs.setAlignment(Pos.CENTER_LEFT);
		nodeVideos.setAlignment(Pos.CENTER_LEFT);
		nodeSession.setAlignment(Pos.CENTER_LEFT);
		nodeLimit.setAlignment(Pos.CENTER_LEFT);
		nodeLimitValue.setAlignment(Pos.CENTER_LEFT);
		
		VBox boxLeft = new VBox(nodeImages, nodeGifs, nodeVideos, nodeSession, nodeLimit, nodeLimitValue);
		boxLeft.setSpacing(5);
		
		NodeSwitch nodeWhitelistMode = new NodeSwitch("Whitelist Mode", "AND", "OR", 125);
		nodeWhitelistMode.selectLeft();
		nodeWhitelistMode.getLeft().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Filter.getSettings().setWhitelistFactor(1.00));
		nodeWhitelistMode.getRight().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Filter.getSettings().setWhitelistFactor(0.01));
		
		NodeSwitch nodeBlacklistMode = new NodeSwitch("Blacklist Mode", "AND", "OR", 125);
		nodeBlacklistMode.selectRight();
		nodeBlacklistMode.getLeft().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Filter.getSettings().setBlacklistFactor(1.00));
		nodeBlacklistMode.getRight().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Filter.getSettings().setBlacklistFactor(0.01));
		
		VBox boxRight = new VBox(nodeWhitelistMode, nodeBlacklistMode);
		boxRight.setSpacing(5);
		
		boxContent = new HBox(boxLeft, boxRight);
		boxContent.setSpacing(100);
		boxContent.setPadding(new Insets(5));
		
		nodeOK = new NodeText("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			FilterSettings filterSettings = Filter.getSettings();
			filterSettings.setShowImages(nodeImages.isSelected());
			filterSettings.setShowGifs(nodeGifs.isSelected());
			filterSettings.setShowVideos(nodeVideos.isSelected());
			filterSettings.setShowOnlyNewEntities(nodeSession.isSelected());
			filterSettings.setEnableLimit(nodeLimit.isSelected());
			filterSettings.setLimit(Integer.parseInt(nodeLimitValue.getText()));
			
			Reload.notify(Notifier.FILTER);
			Reload.start();
			getInstance().close();
		});
		
		nodeCancel = new NodeText("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, getInstance()::close);
	}
	
	public static void show(String... args) {
		if (!getInstance().isShowing()) {
			FilterSettings filterSettings = Filter.getSettings();
			
			nodeImages.setSelected(filterSettings.isShowImages());
			nodeGifs.setSelected(filterSettings.isShowGifs());
			nodeVideos.setSelected(filterSettings.isShowVideos());
			nodeSession.setSelected(filterSettings.isShowOnlyNewEntities());
			nodeLimit.setSelected(filterSettings.isEnableLimit());
			nodeLimitValue.setText(String.valueOf(filterSettings.getLimit()));
			
			getInstance().show();
		}
	}
	
	private StageFilterOptions() {
		super("Filter Settings", false, true, true);
		
		setRoot(boxContent);
		setButtons(nodeOK, nodeCancel);
	}
	private static class Loader {
		private static final StageFilterOptions INSTANCE = new StageFilterOptions();
	}
	public static StageFilterOptions getInstance() {
		return StageFilterOptions.Loader.INSTANCE;
	}
}
