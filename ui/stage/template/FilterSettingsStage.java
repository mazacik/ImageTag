package ui.stage.template;

import control.filter.Filter;
import control.filter.FilterSettings;
import control.reload.Reload;
import ui.component.SwitchNode;
import ui.component.SwitchNodeWithTitle;
import ui.component.simple.*;
import ui.stage.base.StageBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class FilterSettingsStage extends StageBase {
	CheckboxNode cbImages;
	CheckboxNode cbGifs;
	CheckboxNode cbVideos;
	
	CheckboxNode cbSession;
	CheckboxNode cbLimit;
	EditNode tfLimit;
	
	SwitchNodeWithTitle whitelistModeNode;
	SwitchNodeWithTitle blacklistModeNode;
	
	public FilterSettingsStage() {
		super("Filter Settings", false, true, true);
		
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
		
		cbImages.setAlignment(Pos.CENTER_LEFT);
		cbGifs.setAlignment(Pos.CENTER_LEFT);
		cbVideos.setAlignment(Pos.CENTER_LEFT);
		cbSession.setAlignment(Pos.CENTER_LEFT);
		cbLimit.setAlignment(Pos.CENTER_LEFT);
		tfLimit.setAlignment(Pos.CENTER_LEFT);
		
		vBoxLeft.getChildren().addAll(cbImages, cbGifs, cbVideos, cbSession, cbLimit, tfLimit);
		
		VBox vBoxRight = new VBox();
		vBoxRight.setSpacing(5);
		whitelistModeNode = new SwitchNodeWithTitle("Whitelist Mode", "AND", "OR", 125);
		blacklistModeNode = new SwitchNodeWithTitle("Blacklist Mode", "AND", "OR", 125);
		vBoxRight.getChildren().addAll(whitelistModeNode, blacklistModeNode);
		
		whitelistModeNode.setSelectedNode(SwitchNode.SwitchNodeEnum.LEFT);
		blacklistModeNode.setSelectedNode(SwitchNode.SwitchNodeEnum.RIGHT);
		
		whitelistModeNode.getSwitchNode().getNode1().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () ->
				Filter.getSettings().setWhitelistFactor(1.00));
		whitelistModeNode.getSwitchNode().getNode2().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () ->
				Filter.getSettings().setWhitelistFactor(0.01));
		blacklistModeNode.getSwitchNode().getNode1().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () ->
				Filter.getSettings().setBlacklistFactor(1.00));
		blacklistModeNode.getSwitchNode().getNode2().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () ->
				Filter.getSettings().setBlacklistFactor(0.01));
		
		TextNode btnOK = new TextNode("OK", true, true, false, true);
		btnOK.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			FilterSettings filterSettings = Filter.getSettings();
			filterSettings.setShowImages(cbImages.isSelected());
			filterSettings.setShowGifs(cbGifs.isSelected());
			filterSettings.setShowVideos(cbVideos.isSelected());
			filterSettings.setShowOnlyNewEntities(cbSession.isSelected());
			filterSettings.setEnableLimit(cbLimit.isSelected());
			filterSettings.setLimit(Integer.parseInt(tfLimit.getText()));
			
			Filter.refresh();
			Reload.start();
			this.close();
		});
		
		TextNode btnCancel = new TextNode("Cancel", true, true, false, true);
		btnCancel.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, this::close);
		
		HBox hBoxContent = new HBox(vBoxLeft, vBoxRight);
		hBoxContent.setSpacing(100);
		hBoxContent.setPadding(new Insets(5));
		
		setRoot(hBoxContent);
		setButtons(btnOK, btnCancel);
	}
	
	@Override
	public Object show(String... args) {
		cbImages.setSelected(Filter.getSettings().isShowImages());
		cbGifs.setSelected(Filter.getSettings().isShowGifs());
		cbVideos.setSelected(Filter.getSettings().isShowVideos());
		cbSession.setSelected(Filter.getSettings().isShowOnlyNewEntities());
		cbLimit.setSelected(Filter.getSettings().isEnableLimit());
		tfLimit.setText(String.valueOf(Filter.getSettings().getLimit()));
		
		super.show();
		return null;
	}
}
