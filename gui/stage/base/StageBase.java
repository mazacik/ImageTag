package gui.stage.base;

import gui.decorator.ColorUtil;
import gui.stage.TitleBar;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tools.NodeUtil;

public abstract class StageBase extends Stage implements StageBaseInterface {
	private TitleBar titleBar;
	private VBox vBoxMain;
	
	public StageBase() {
		this("");
	}
	public StageBase(String title) {
		this(new TitleBar(title));
	}
	public StageBase(TitleBar titleBar) {
		this.titleBar = titleBar;
		
		vBoxMain = new VBox();
		vBoxMain.setAlignment(Pos.TOP_CENTER);
		vBoxMain.setBackground(ColorUtil.getBackgroundDef());
		vBoxMain.setBorder(NodeUtil.getBorder(1));
		
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
	}
	public void setBorder(Border border) {
		vBoxMain.setBorder(border);
	}
	public void setTitleBar(TitleBar titleBar) {
		this.titleBar = titleBar;
	}
	
	@Override
	public void showAndWait() {
		if (this.isIconified() || this.isShowing()) this.setIconified(false);
		else super.showAndWait();
	}
}
