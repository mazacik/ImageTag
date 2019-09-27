package application.gui.stage;

import application.controller.Filter;
import application.gui.nodes.custom.SwitchNode;
import application.gui.nodes.custom.SwitchNodeWithTitle;
import application.gui.nodes.simple.CheckboxNode;
import application.gui.nodes.simple.EditNode;
import application.gui.nodes.simple.TextNode;
import application.main.Instances;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FilterSettingsStage extends StageBase {
	CheckboxNode cbImages;
	CheckboxNode cbGifs;
	CheckboxNode cbVideos;
	
	CheckboxNode cbSession;
	CheckboxNode cbLimit;
	EditNode tfLimit;
	
	SwitchNodeWithTitle whitelistModeNode;
	SwitchNodeWithTitle blacklistModeNode;
	
	FilterSettingsStage() {
		super("Filter Settings");
		
		cbLimit = new CheckboxNode("Limit");
		tfLimit = new EditNode("", EditNode.EditNodeType.NUMERIC_POSITIVE);
		cbLimit.getSelectedProperty().addListener((observable, oldValue, newValue) -> tfLimit.setDisable(!newValue));
		
		tfLimit.setPadding(new Insets(0, 1, -1, 1));
		tfLimit.setPrefWidth(100);
		tfLimit.setDisable(true);
		
		VBox vBoxLeft = new VBox();
		vBoxLeft.setSpacing(5);
		cbImages = new CheckboxNode("Images");
		cbGifs = new CheckboxNode("Gifs");
		cbVideos = new CheckboxNode("Videos");
		cbSession = new CheckboxNode("Session");
		vBoxLeft.getChildren().addAll(cbImages, cbGifs, cbVideos, cbSession, cbLimit, tfLimit);
		
		VBox vBoxRight = new VBox();
		vBoxRight.setSpacing(5);
		whitelistModeNode = new SwitchNodeWithTitle("Whitelist Mode", "All", "Any", 100);
		blacklistModeNode = new SwitchNodeWithTitle("Blacklist Mode", "All", "Any", 100);
		vBoxRight.getChildren().addAll(whitelistModeNode, blacklistModeNode);
		
		whitelistModeNode.setSelectedNode(SwitchNode.SwitchNodeEnum.LEFT);
		blacklistModeNode.setSelectedNode(SwitchNode.SwitchNodeEnum.RIGHT);
		
		whitelistModeNode.getSwitchNode().getNode1().addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () ->
				Instances.getFilter().setWhitelistFactor(1.00));
		whitelistModeNode.getSwitchNode().getNode2().addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () ->
				Instances.getFilter().setWhitelistFactor(0.01));
		blacklistModeNode.getSwitchNode().getNode1().addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () ->
				Instances.getFilter().setBlacklistFactor(1.00));
		blacklistModeNode.getSwitchNode().getNode2().addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () ->
				Instances.getFilter().setBlacklistFactor(0.01));
		
		TextNode btnOK = new TextNode("OK", true, true, false, true);
		btnOK.addEventFilter(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			Filter filter = Instances.getFilter();
			filter.setShowImages(cbImages.isSelected());
			filter.setShowGifs(cbGifs.isSelected());
			filter.setShowVideos(cbVideos.isSelected());
			filter.setSessionOnly(cbSession.isSelected());
			filter.setEnableLimit(cbLimit.isSelected());
			filter.setLimit(Integer.parseInt(tfLimit.getText()));
			filter.refresh();
			Instances.getReload().doReload();
			Instances.getGalleryPane().updateViewportTilesVisibility();
			this.hide();
		});
		TextNode btnCancel = new TextNode("Cancel", true, true, false, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, this::hide);
		
		HBox hBoxOkCancel = new HBox(btnOK, btnCancel);
		hBoxOkCancel.setAlignment(Pos.CENTER);
		
		HBox hBoxContent = new HBox(vBoxLeft, vBoxRight);
		hBoxContent.setSpacing(100);
		
		VBox vBoxMain = new VBox(hBoxContent, hBoxOkCancel);
		vBoxMain.setSpacing(5);
		vBoxMain.setPadding(new Insets(5));
		
		setRoot(vBoxMain);
	}
	
	@Override
	public Object _show(String... args) {
		Filter filter = Instances.getFilter();
		cbImages.setSelected(filter.isShowImages());
		cbGifs.setSelected(filter.isShowGifs());
		cbVideos.setSelected(filter.isShowVideos());
		cbSession.setSelected(filter.isSessionOnly());
		cbLimit.setSelected(filter.isEnableLimit());
		tfLimit.setText(String.valueOf(filter.getLimit()));
		
		this.show();
		return null;
	}
}
