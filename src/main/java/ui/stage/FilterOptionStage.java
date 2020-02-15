package ui.stage;

import control.filter.Filter;
import control.filter.FilterSettings;
import control.reload.Notifier;
import control.reload.Reload;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.node.CheckboxNode;
import ui.node.EditNode;
import ui.node.SwitchNode;
import ui.node.TextNode;
import ui.override.HBox;
import ui.override.VBox;

public class FilterOptionStage extends AbstractStage {
	private static final CheckboxNode nodeImages;
	private static final CheckboxNode nodeGifs;
	private static final CheckboxNode nodeVideos;
	
	private static final CheckboxNode nodeSession;
	private static final CheckboxNode nodeLimit;
	private static final EditNode nodeLimitValue;
	
	private static final HBox boxContent;
	
	private static final TextNode nodeOK;
	private static final TextNode nodeCancel;
	
	static {
		nodeImages = new CheckboxNode("Images");
		nodeGifs = new CheckboxNode("Gifs");
		nodeVideos = new CheckboxNode("Videos");
		nodeSession = new CheckboxNode("Session");
		
		nodeLimit = new CheckboxNode("Limit");
		nodeLimitValue = new EditNode(EditNode.EditNodeType.NUMERIC_POSITIVE);
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
		
		SwitchNode nodeWhitelistMode = new SwitchNode("Whitelist Mode", "AND", "OR", 125);
		nodeWhitelistMode.selectLeft();
		//nodeWhitelistMode.getLeft().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Filter.getSettings().setWhitelistFactor(1.00));
		//nodeWhitelistMode.getRight().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Filter.getSettings().setWhitelistFactor(0.01));
		
		SwitchNode nodeBlacklistMode = new SwitchNode("Blacklist Mode", "AND", "OR", 125);
		nodeBlacklistMode.selectRight();
		//nodeBlacklistMode.getLeft().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Filter.getSettings().setBlacklistFactor(1.00));
		//nodeBlacklistMode.getRight().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Filter.getSettings().setBlacklistFactor(0.01));
		
		VBox boxRight = new VBox(nodeWhitelistMode, nodeBlacklistMode);
		boxRight.setSpacing(5);
		
		boxContent = new HBox(boxLeft, boxRight);
		boxContent.setSpacing(100);
		boxContent.setPadding(new Insets(5));
		
		nodeOK = new TextNode("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			FilterSettings filterSettings = Filter.getSettings();
			filterSettings.setShowImages(nodeImages.isSelected());
			filterSettings.setShowGifs(nodeGifs.isSelected());
			filterSettings.setShowVideos(nodeVideos.isSelected());
			filterSettings.setShowOnlyNewEntities(nodeSession.isSelected());
			filterSettings.setEnableLimit(nodeLimit.isSelected());
			filterSettings.setLimit(Integer.parseInt(nodeLimitValue.getText()));
			
			Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
			Reload.start();
			getInstance().close();
		});
		
		nodeCancel = new TextNode("Cancel", true, true, false, true);
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
	
	private FilterOptionStage() {
		super("Filter Settings", false);
		
		setRoot(boxContent);
		setButtons(nodeOK, nodeCancel);
	}
	private static class Loader {
		private static final FilterOptionStage INSTANCE = new FilterOptionStage();
	}
	public static FilterOptionStage getInstance() {
		return FilterOptionStage.Loader.INSTANCE;
	}
}
