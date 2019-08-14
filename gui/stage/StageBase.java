package application.gui.stage;

import application.gui.decorator.ColorUtil;
import application.gui.nodes.custom.TitleBar;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class StageBase extends Stage implements StageBaseInterface {
	private TitleBar titleBar;
	private VBox vBoxMain;
	
	public StageBase() {
		this("");
	}
	public StageBase(TitleBar titleBar) {
		this.titleBar = titleBar;
		init();
	}
	public StageBase(String title) {
		titleBar = new TitleBar(title);
		init();
	}
	private void init() {
		vBoxMain = new VBox();
		vBoxMain.setAlignment(Pos.CENTER);
		vBoxMain.setBackground(ColorUtil.getBackgroundDef());
		
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
	
	@Override
	public void showAndWait() {
		if (this.isIconified() && this.isShowing()) this.setIconified(false);
		else super.showAndWait();
	}
}
