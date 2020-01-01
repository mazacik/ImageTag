package ui.main.stage;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import misc.Settings;
import ui.decorator.Decorator;
import ui.decorator.SizeUtil;
import ui.main.center.PaneEntity;
import ui.main.center.PaneGallery;
import ui.main.side.left.PaneFilter;
import ui.main.side.right.PaneSelect;

import java.awt.*;

public class StageMain extends Stage {
	private SceneIntro sceneIntro;
	private SceneProject sceneProject;
	private SceneMain sceneMain;
	
	public StageMain() {
		this.initStyle(StageStyle.UNDECORATED);
		this.show();
	}
	
	public void layoutIntro() {
		setVisible(false);
		
		sceneIntro = new SceneIntro();
		sceneProject = new SceneProject();
		
		Rectangle usableScreenBounds = SizeUtil.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
		
		this.setScene(sceneIntro);
		this.setWidth(width);
		this.setHeight(height);
		this.setMinWidth(width);
		this.setMinHeight(height);
		this.setOnCloseRequest(event -> Settings.writeToDisk());
		this.centerOnScreen();
		
		sceneIntro.getRoot().requestFocus();
		
		this.setVisible(true);
	}
	public void layoutMain() {
		setVisible(false);
		
		sceneMain = new SceneMain();
		
		Decorator.applyScrollbarStyle(PaneGallery.getInstance());
		Decorator.applyScrollbarStyle(PaneFilter.get().getScrollPane());
		Decorator.applyScrollbarStyle(PaneSelect.get().getScrollPane());
		
		this.setScene(sceneMain);
		this.getScene().widthProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageWidthChangeHandler());
		
		this.setMinWidth(100 + SizeUtil.getMinWidthSideLists() * 2 + Settings.getTileSize());
		this.setMinHeight(100 + SizeUtil.getPrefHeightTopMenu() + Settings.getTileSize());
		this.setWidth(SizeUtil.getUsableScreenWidth());
		this.setHeight(SizeUtil.getUsableScreenHeight());
		
		this.centerOnScreen();
		this.setOnCloseRequest(event -> Main.exitApplication());
		this.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) PaneEntity.get().getControls().hide();
		});
		
		sceneMain.getRoot().requestFocus();
		
		this.setVisible(true);
	}
	
	private void setVisible(boolean value) {
		if (value) {
			this.setOpacity(1);
		} else {
			this.setOpacity(0);
		}
	}
	
	public SceneIntro getSceneIntro() {
		return sceneIntro;
	}
	public SceneProject getSceneProject() {
		return sceneProject;
	}
	public SceneMain getSceneMain() {
		return sceneMain;
	}
}
