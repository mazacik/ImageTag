package ui.main.stage;

import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import main.Root;
import misc.Settings;
import ui.decorator.Decorator;
import ui.main.side.SidePaneBase;

import java.awt.*;

public class MainStage extends Stage {
	private IntroScene introScene;
	private ProjectScene projectScene;
	private MainScene mainScene;
	
	public MainStage() {
		this.initStyle(StageStyle.UNDECORATED);
		this.show();
	}
	
	public void layoutIntro() {
		this.setOpacity(0);
		
		introScene = new IntroScene();
		projectScene = new ProjectScene();
		
		Rectangle usableScreenBounds = Decorator.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
		
		this.setScene(introScene);
		this.setWidth(width);
		this.setHeight(height);
		this.setMinWidth(width);
		this.setMinHeight(height);
		this.setOnCloseRequest(event -> Settings.writeToDisk());
		this.centerOnScreen();
		
		introScene.getRoot().requestFocus();
		
		this.setOpacity(1);
	}
	public void layoutMain() {
		this.setOpacity(0);//todo this doesn't even seem necessary
		
		mainScene = new MainScene();
		mainScene.getRoot().requestFocus();
		mainScene.widthProperty().addListener((observable, oldValue, newValue) -> onStageWidthChange());
		
		Root.GALLERY_PANE.widthProperty().addListener((observable, oldValue, newValue) -> {
			double availableWidth = MainStage.this.getScene().getWidth() - newValue.doubleValue();
			double width = availableWidth / 2;
			Root.FILTER_PANE.setPrefWidth(width);
			Root.SELECT_PANE.setPrefWidth(width);
		});
		
		Decorator.setScrollbarStyle(Root.GALLERY_PANE);
		Decorator.setScrollbarStyle(Root.FILTER_PANE.getScrollPane());
		Decorator.setScrollbarStyle(Root.SELECT_PANE.getScrollPane());
		
		this.setScene(mainScene);
		
		this.setMinWidth(100 + SidePaneBase.MIN_WIDTH * 2 + Settings.GALLERY_TILE_SIZE.getValueInteger());
		this.setMinHeight(100 + Settings.GALLERY_TILE_SIZE.getValueInteger());
		this.setWidth(Decorator.getUsableScreenWidth());
		this.setHeight(Decorator.getUsableScreenHeight());
		
		this.centerOnScreen();
		this.setOnCloseRequest(event -> Main.exitApplication());
		
		this.setOpacity(1);//todo move this ALL THE WAY to the back
	}
	
	private void onStageWidthChange() {
		TilePane tiles = Root.GALLERY_PANE.getTilePane();
		
		double galleryTileSize = tiles.getPrefTileWidth();
		double sceneWidth = MainStage.this.getScene().getWidth();
		
		double width = 0;
		double availableWidth = sceneWidth - 2 * SidePaneBase.MIN_WIDTH;
		
		double increment = galleryTileSize + tiles.getHgap();
		while (width + increment <= availableWidth) width += increment;
		
		int prefColumnsNew = (int) (width / galleryTileSize);
		int prefColumnsOld = tiles.getPrefColumns();
		
		if (prefColumnsNew != prefColumnsOld) {
			tiles.setPrefColumns(prefColumnsNew);
			Root.GALLERY_PANE.setPrefViewportWidth(width);
		}
	}
	
	public IntroScene getIntroScene() {
		return introScene;
	}
	public ProjectScene getProjectScene() {
		return projectScene;
	}
	public MainScene getMainScene() {
		return mainScene;
	}
}
