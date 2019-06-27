package userinterface.stage;

import control.Filter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.InstanceManager;
import userinterface.nodes.ColorData;
import userinterface.nodes.NodeUtil;
import userinterface.nodes.base.CheckBoxNode;
import userinterface.nodes.base.EditNode;
import userinterface.nodes.base.EditNodeType;
import userinterface.nodes.base.TextNode;
import userinterface.nodes.node.SwitchNodeWithTitle;
import userinterface.nodes.node.TitleBar;
import userinterface.style.SizeUtil;
import userinterface.style.enums.ColorType;

public class StageFilterOptions extends Stage implements StageBase {
	CheckBoxNode cbImages = new CheckBoxNode("Images");
	CheckBoxNode cbGifs = new CheckBoxNode("Gifs");
	CheckBoxNode cbVideos = new CheckBoxNode("Videos");
	
	CheckBoxNode cbSession = new CheckBoxNode("Session");
	CheckBoxNode cbLimit = new CheckBoxNode("Limit");
	EditNode tfLimit = new EditNode("", EditNodeType.NUMERIC_POSITIVE);
	
	SwitchNodeWithTitle whitelistModeNode = new SwitchNodeWithTitle("Whitelist Mode", "All", "Any", 100);
	SwitchNodeWithTitle blacklistModeNode = new SwitchNodeWithTitle("Blacklist Mode", "All", "Any", 100);
	
	StageFilterOptions() {
		Filter filter = InstanceManager.getFilter();
		Border border = NodeUtil.getBorder(1, 1, 1, 1);
		double spacing = SizeUtil.getGlobalSpacing();
		
		double dinsets = 5 * SizeUtil.getGlobalSpacing();
		Insets insets = new Insets(dinsets, dinsets, 0, dinsets);
		
		cbLimit.getSelectedProperty().addListener((observable, oldValue, newValue) -> tfLimit.setDisable(!newValue));
		
		tfLimit.setPadding(new Insets(0, 1, -1, 1));
		tfLimit.setPrefWidth(100);
		tfLimit.setDisable(true);
		
		VBox vBoxLeft = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
		vBoxLeft.setSpacing(spacing);
		vBoxLeft.setPadding(insets);
		vBoxLeft.getChildren().addAll(cbImages, cbGifs, cbVideos, cbSession, cbLimit, tfLimit);
		
		VBox vBoxRight = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
		vBoxRight.setPadding(new Insets(spacing));
		vBoxRight.setSpacing(spacing);
		vBoxRight.setPadding(insets);
		vBoxRight.getChildren().addAll(
				whitelistModeNode,
				blacklistModeNode
		);
		
		TextNode whitelistNode1 = whitelistModeNode.getSwitchNode().getNode1();
		TextNode whitelistNode2 = whitelistModeNode.getSwitchNode().getNode2();
		TextNode blacklistNode1 = blacklistModeNode.getSwitchNode().getNode1();
		TextNode blacklistNode2 = blacklistModeNode.getSwitchNode().getNode2();
		
		whitelistNode1.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				filter.setWhitelistFactor(1.00);
				whitelistNode1.setBorder(border);
				whitelistNode2.setBorder(null);
			}
		});
		whitelistNode2.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				filter.setWhitelistFactor(0.01);
				whitelistNode1.setBorder(null);
				whitelistNode2.setBorder(border);
			}
		});
		blacklistNode1.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				filter.setBlacklistFactor(1.00);
				blacklistNode1.setBorder(border);
				blacklistNode2.setBorder(null);
			}
		});
		blacklistNode2.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				filter.setBlacklistFactor(0.01);
				blacklistNode1.setBorder(null);
				blacklistNode2.setBorder(border);
			}
		});
		
		ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		TextNode lblOK = new TextNode("OK", colorData);
		lblOK.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				filter.setShowImages(cbImages.isSelected());
				filter.setShowGifs(cbGifs.isSelected());
				filter.setShowVideos(cbVideos.isSelected());
				filter.setSessionOnly(cbSession.isSelected());
				filter.setEnableLimit(cbLimit.isSelected());
				filter.setLimit(Integer.valueOf(tfLimit.getText()));
				filter.refresh();
				InstanceManager.getReload().doReload();
				this.hide();
			}
		});
		TextNode lblCancel = new TextNode("Cancel", colorData);
		lblCancel.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				this.hide();
			}
		});
		HBox hBoxOkCancel = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, lblOK, lblCancel);
		hBoxOkCancel.setAlignment(Pos.CENTER);
		
		BorderPane borderPane = new BorderPane();
		Scene scene = new Scene(borderPane);
		borderPane.setTop(new TitleBar(scene, "Filter Settings"));
		borderPane.setLeft(vBoxLeft);
		borderPane.setRight(vBoxRight);
		borderPane.setBottom(hBoxOkCancel);
		borderPane.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		
		this.initStyle(StageStyle.UNDECORATED);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setScene(scene);
	}
	
	@Override
	public Object _show(String... args) {
		Filter filter = InstanceManager.getFilter();
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
