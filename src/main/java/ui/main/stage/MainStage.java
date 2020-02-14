package ui.main.stage;

import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import misc.Settings;
import ui.decorator.Decorator;
import ui.main.display.DisplayPane;
import ui.main.gallery.GalleryPane;
import ui.main.side.FilterPane;
import ui.main.side.SelectPane;
import ui.main.side.SidePaneBase;
import ui.main.top.ToolbarPane;

import java.awt.*;

public class MainStage extends Stage {
	private static IntroScene introScene;
	private static ProjectScene projectScene;
	private static MainScene mainScene;
	
	public static void layoutIntro() {
		setVisible(false);
		
		introScene = new IntroScene();
		projectScene = new ProjectScene();
		
		Rectangle usableScreenBounds = Decorator.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
		
		getInstance().setScene(introScene);
		getInstance().setWidth(width);
		getInstance().setHeight(height);
		getInstance().setMinWidth(width);
		getInstance().setMinHeight(height);
		getInstance().setOnCloseRequest(event -> Settings.writeToDisk());
		getInstance().centerOnScreen();
		
		introScene.getRoot().requestFocus();
		
		setVisible(true);
	}
	public static void layoutMain() {
		setVisible(false);
		
		mainScene = new MainScene();
		
		Decorator.setScrollbarStyle(GalleryPane.getInstance());
		Decorator.setScrollbarStyle(FilterPane.getInstance().getScrollPane());
		Decorator.setScrollbarStyle(SelectPane.getInstance().getScrollPane());
		
		getInstance().setScene(mainScene);
		getInstance().getScene().widthProperty().addListener((observable, oldValue, newValue) -> onStageWidthChange());
		
		getInstance().setMinWidth(100 + SidePaneBase.MIN_WIDTH_SIDELISTS * 2 + Settings.TILE_SIZE.getValue());
		getInstance().setMinHeight(100 + ToolbarPane.PREF_HEIGHT + Settings.TILE_SIZE.getValue());
		getInstance().setWidth(Decorator.getUsableScreenWidth());
		getInstance().setHeight(Decorator.getUsableScreenHeight());
		
		getInstance().centerOnScreen();
		getInstance().setOnCloseRequest(event -> Main.exitApplication());
		getInstance().focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				DisplayPane.getInstance().getControls().hide();
			}
		});
		
		mainScene.getRoot().requestFocus();
		
		setVisible(true);
	}
	
	private static void onStageWidthChange() {
		GalleryPane galleryPane = GalleryPane.getInstance();
		TilePane tiles = GalleryPane.getTilePane();
		
		double galleryTileSize = tiles.getPrefTileWidth();
		double sceneWidth = MainStage.getInstance().getScene().getWidth();
		
		double width = 0;
		double availableWidth = sceneWidth - 2 * SidePaneBase.MIN_WIDTH_SIDELISTS;
		
		double increment = galleryTileSize + tiles.getHgap();
		while (width + increment <= availableWidth) width += increment;
		
		int prefColumnsNew = (int) (width / galleryTileSize);
		int prefColumnsOld = tiles.getPrefColumns();
		
		if (prefColumnsNew != prefColumnsOld) {
			tiles.setPrefColumns(prefColumnsNew);
			
			galleryPane.setMinViewportWidth(width);
			galleryPane.setPrefViewportWidth(width);
		}
	}
	private static void setVisible(boolean value) {
		if (value) {
			getInstance().setOpacity(1);
		} else {
			getInstance().setOpacity(0);
		}
	}
	
	public static IntroScene getIntroScene() {
		return introScene;
	}
	public static ProjectScene getProjectScene() {
		return projectScene;
	}
	public static MainScene getMainScene() {
		return mainScene;
	}
	
	private MainStage() {
		this.initStyle(StageStyle.UNDECORATED);
		this.show();
	}
	private static class Loader {
		private static final MainStage INSTANCE = new MainStage();
	}
	public static MainStage getInstance() {
		return Loader.INSTANCE;
	}
}
