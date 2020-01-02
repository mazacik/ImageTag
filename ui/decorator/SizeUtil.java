package ui.decorator;

import ui.main.center.PaneGallery;
import ui.stage.StageManager;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;

import java.awt.*;

public abstract class SizeUtil {
	private static final double PREF_HEIGHT_TOPMENU = 30;
	private static final double MIN_WIDTH_SIDELISTS = 250;
	
	public static Rectangle getUsableScreenBounds() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	}
	public static double getUsableScreenWidth() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
	}
	public static double getUsableScreenHeight() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
	}
	
	public static void stageWidthChangeHandler() {
		PaneGallery paneGallery = PaneGallery.getInstance();
		TilePane tiles = PaneGallery.getTilePane();
		
		double galleryTileSize = tiles.getPrefTileWidth();
		double sceneWidth = StageManager.getStageMain().getScene().getWidth();
		
		double availableWidth = sceneWidth - 2 * SizeUtil.getMinWidthSideLists();
		double width = getWidth(availableWidth, galleryTileSize, tiles.getHgap());
		int prefColumnsNew = (int) (width / galleryTileSize);
		int prefColumnsOld = tiles.getPrefColumns();
		
		if (prefColumnsNew != prefColumnsOld) {
			tiles.setPrefColumns(prefColumnsNew);
			
			paneGallery.setMinViewportWidth(width);
			paneGallery.setPrefViewportWidth(width);
		}
	}
	private static double getWidth(double availableWidth, double galleryTileSize, double tileHgap) {
		double result = 0;
		double increment = galleryTileSize + tileHgap;
		
		while (result + increment <= availableWidth) {
			result += increment;
		}
		return result;
	}
	
	public static double getStringWidth(String s) {
		Text t = new Text(s);
		t.setFont(Decorator.getFont());
		return t.getLayoutBounds().getWidth();
	}
	
	public static double getPrefHeightTopMenu() {
		return PREF_HEIGHT_TOPMENU;
	}
	public static double getMinWidthSideLists() {
		return MIN_WIDTH_SIDELISTS;
	}
}
