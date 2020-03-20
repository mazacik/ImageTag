package ui.stage;

import control.filter.FilterSettings;
import control.reload.Notifier;
import control.reload.Reload;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.Root;
import ui.node.CheckboxNode;
import ui.node.EditNode;
import ui.node.textnode.TextNode;
import ui.override.HBox;
import ui.override.VBox;

public class FilterOptionStage extends AbstractStage {
	public FilterOptionStage() {
		super("Filter Settings", false);
		
		CheckboxNode nodeImages = new CheckboxNode("Images");
		CheckboxNode nodeGifs = new CheckboxNode("Gifs");
		CheckboxNode nodeVideos = new CheckboxNode("Videos");
		CheckboxNode nodeSession = new CheckboxNode("Session");//todo maybe rename to "Last Import"?
		
		CheckboxNode nodeLimit = new CheckboxNode("Limit");
		EditNode nodeLimitValue = new EditNode(EditNode.EditNodeType.NUMERIC_POSITIVE);
		nodeLimit.getCheckedProperty().addListener((observable, oldValue, newValue) -> nodeLimitValue.setDisable(!newValue));
		
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
		
		//SwitchNode nodeWhitelistMode = new SwitchNode("Whitelist Mode", "AND", "OR", 125);
		//nodeWhitelistMode.selectLeft();
		//nodeWhitelistMode.getLeft().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Root.FILTER.getSettings().setWhitelistFactor(1.00));
		//nodeWhitelistMode.getRight().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Root.FILTER.getSettings().setWhitelistFactor(0.01));
		
		//SwitchNode nodeBlacklistMode = new SwitchNode("Blacklist Mode", "AND", "OR", 125);
		//nodeBlacklistMode.selectRight();
		//nodeBlacklistMode.getLeft().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Root.FILTER.getSettings().setBlacklistFactor(1.00));
		//nodeBlacklistMode.getRight().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Root.FILTER.getSettings().setBlacklistFactor(0.01));
		
		//VBox boxRight = new VBox(nodeWhitelistMode, nodeBlacklistMode);
		//boxRight.setSpacing(5);
		
		HBox boxContent = new HBox(boxLeft);//, boxRight);
		boxContent.setSpacing(100);
		boxContent.setMinWidth(300);
		boxContent.setPadding(new Insets(5));
		
		TextNode nodeOK = new TextNode("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			this.close();
			
			FilterSettings fs = Root.FILTER.getSettings();
			fs.setShowImages(nodeImages.isChecked());
			fs.setShowGifs(nodeGifs.isChecked());
			fs.setShowVideos(nodeVideos.isChecked());
			fs.setShowOnlyNewEntities(nodeSession.isChecked());
			fs.setEnableLimit(nodeLimit.isChecked());
			fs.setLimit(Integer.parseInt(nodeLimitValue.getText()));
			
			Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
			Reload.start();
		});
		
		TextNode nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, this::close);
		
		this.onShowingProperty().addListener((observable, oldValue, newValue) -> {
			FilterSettings fs = Root.FILTER.getSettings();
			nodeImages.setChecked(fs.isShowImages());
			nodeGifs.setChecked(fs.isShowGifs());
			nodeVideos.setChecked(fs.isShowVideos());
			nodeSession.setChecked(fs.isShowOnlyNewEntities());
			nodeLimit.setChecked(fs.isEnableLimit());
			nodeLimitValue.setText(String.valueOf(fs.getLimit()));
		});
		
		setRoot(boxContent);
		setButtons(nodeOK, nodeCancel);
	}
}
