package gui.stage.base;

import gui.component.simple.TextNode;
import gui.decorator.ColorUtil;
import gui.stage.TitleBar;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tools.NodeUtil;

public abstract class StageBase extends Stage implements StageBaseInterface {
	private VBox vBoxMain;
	
	private TitleBar titleBar;
	private TextNode errorNode;
	private HBox buttonBox;
	
	public StageBase() {
		this("");
	}
	public StageBase(String title) {
		vBoxMain = new VBox();
		vBoxMain.setAlignment(Pos.TOP_CENTER);
		vBoxMain.setBackground(ColorUtil.getBackgroundPrimary());
		vBoxMain.setBorder(NodeUtil.getBorder(1));
		
		titleBar = new TitleBar(title);
		errorNode = new TextNode("", false, false, false, false);
		errorNode.setTextFill(ColorUtil.getColorNegative());
		buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		
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
		vBoxMain.getChildren().add(errorNode);
		vBoxMain.getChildren().add(buttonBox);
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
