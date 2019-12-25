package ui.stage.base;

import ui.component.simple.HBox;
import ui.component.simple.TextNode;
import ui.component.simple.VBox;
import ui.decorator.ColorUtil;
import ui.stage.TitleBar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.NodeUtil;

public abstract class StageBase extends Stage implements StageBaseInterface {
	private VBox vBoxMain;
	
	private TitleBar titleBar;
	
	private TextNode errorNode;
	private boolean showErrorNode;
	
	private HBox buttonBox;
	private boolean showButtonBox;
	
	private boolean defaultPadding;
	
	public StageBase(String title, boolean showErrorNode, boolean showButtonBox, boolean defaultPadding) {
		vBoxMain = new VBox();
		vBoxMain.setAlignment(Pos.TOP_CENTER);
		vBoxMain.setBackground(ColorUtil.getBackgroundPrimary());
		vBoxMain.setBorder(NodeUtil.getBorder(1));
		
		titleBar = new TitleBar(title);
		errorNode = new TextNode("", false, false, false, false);
		errorNode.setTextFill(ColorUtil.getColorNegative());
		buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		
		this.showErrorNode = showErrorNode;
		this.showButtonBox = showButtonBox;
		
		this.defaultPadding = defaultPadding;
		
		this.setScene(new Scene(vBoxMain));
		this.initStyle(StageStyle.UNDECORATED);
		this.setOnShown(event -> this.centerOnScreen());
		this.setAlwaysOnTop(true);
		this.titleProperty().addListener((observable, oldValue, newValue) -> titleBar.setTitle(newValue));
	}
	
	public void setRoot(Region root) {
		vBoxMain.getChildren().clear();
		vBoxMain.getChildren().add(titleBar);
		vBoxMain.getChildren().add(root);
		if (showErrorNode) vBoxMain.getChildren().add(errorNode);
		if (showButtonBox) vBoxMain.getChildren().add(buttonBox);
		
		if (defaultPadding) {
			root.setPadding(new Insets(5, 5, 5, 5));
		}
	}
	public void setTitleBar(TitleBar titleBar) {
		this.titleBar = titleBar;
	}
	public void setErrorMessage(String errorMessage) {
		errorNode.setText(errorMessage);
	}
	public void setButtons(TextNode... buttons) {
		buttonBox.getChildren().clear();
		buttonBox.getChildren().addAll(buttons);
	}
	
	public void setBorder(Border border) {
		vBoxMain.setBorder(border);
	}
	
	@Override
	public void showAndWait() {
		if (this.isIconified() || this.isShowing()) this.setIconified(false);
		else super.showAndWait();
	}
}
