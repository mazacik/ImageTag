package ui.main.stage;

import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import misc.Settings;
import ui.decorator.Decorator;
import ui.main.display.PaneDisplay;
import ui.main.gallery.PaneGallery;
import ui.main.side.PaneFilter;
import ui.main.side.PaneSelect;
import ui.main.side.SidePaneBase;
import ui.main.top.PaneToolbar;
import ui.stage.StageEditTag;

import java.awt.*;

public class StageMain extends Stage {
	private static SceneIntro sceneIntro;
	private static SceneProject sceneProject;
	private static SceneMain sceneMain;
	
	public static void layoutIntro() {
		setVisible(false);
		
		sceneIntro = new SceneIntro();
		sceneProject = new SceneProject();
		
		Rectangle usableScreenBounds = Decorator.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
		
		getInstance().setScene(sceneIntro);
		getInstance().setWidth(width);
		getInstance().setHeight(height);
		getInstance().setMinWidth(width);
		getInstance().setMinHeight(height);
		getInstance().setOnCloseRequest(event -> Settings.writeToDisk());
		getInstance().centerOnScreen();
		
		sceneIntro.getRoot().requestFocus();
		
		setVisible(true);
	}
	public static void layoutMain() {
		setVisible(false);
		
		sceneMain = new SceneMain();
		
		Decorator.setScrollbarStyle(StageEditTag.getScrollPane());
		Decorator.setScrollbarStyle(PaneGallery.getInstance());
		Decorator.setScrollbarStyle(PaneFilter.getInstance().getScrollPane());
		Decorator.setScrollbarStyle(PaneSelect.getInstance().getScrollPane());
		
		getInstance().setScene(sceneMain);
		getInstance().getScene().widthProperty().addListener((observable, oldValue, newValue) -> onStageWidthChange());
		
		getInstance().setMinWidth(100 + SidePaneBase.MIN_WIDTH_SIDELISTS * 2 + Settings.getTileSize());
		getInstance().setMinHeight(100 + PaneToolbar.PREF_HEIGHT + Settings.getTileSize());
		getInstance().setWidth(Decorator.getUsableScreenWidth());
		getInstance().setHeight(Decorator.getUsableScreenHeight());
		
		getInstance().centerOnScreen();
		getInstance().setOnCloseRequest(event -> Main.exitApplication());
		getInstance().focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				PaneDisplay.getInstance().getControls().hide();
			}
		});
		
		sceneMain.getRoot().requestFocus();
		
		setVisible(true);
	}
	
	private static void onStageWidthChange() {
		PaneGallery paneGallery = PaneGallery.getInstance();
		TilePane tiles = PaneGallery.getTilePane();
		
		double galleryTileSize = tiles.getPrefTileWidth();
		double sceneWidth = StageMain.getInstance().getScene().getWidth();
		
		double width = 0;
		double availableWidth = sceneWidth - 2 * SidePaneBase.MIN_WIDTH_SIDELISTS;
		
		double increment = galleryTileSize + tiles.getHgap();
		while (width + increment <= availableWidth) width += increment;
		
		int prefColumnsNew = (int) (width / galleryTileSize);
		int prefColumnsOld = tiles.getPrefColumns();
		
		if (prefColumnsNew != prefColumnsOld) {
			tiles.setPrefColumns(prefColumnsNew);
			
			paneGallery.setMinViewportWidth(width);
			paneGallery.setPrefViewportWidth(width);
		}
	}
	private static void setVisible(boolean value) {
		if (value) {
			getInstance().setOpacity(1);
		} else {
			getInstance().setOpacity(0);
		}
	}
	
	public static SceneIntro getSceneIntro() {
		return sceneIntro;
	}
	public static SceneProject getSceneProject() {
		return sceneProject;
	}
	public static SceneMain getSceneMain() {
		return sceneMain;
	}
	
	private StageMain() {
		this.initStyle(StageStyle.UNDECORATED);
		this.show();
	}
	private static class Loader {
		private static final StageMain INSTANCE = new StageMain();
	}
	public static StageMain getInstance() {
		return Loader.INSTANCE;
	}
}
