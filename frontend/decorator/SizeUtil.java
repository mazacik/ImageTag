package application.frontend.decorator;

import application.frontend.stage.StageManager;
import application.main.InstanceCollector;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;

import java.awt.*;

public abstract class SizeUtil implements InstanceCollector {
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
		TilePane tilePane = galleryPane.getTilePane();
		
		double galleryTileSize = tilePane.getPrefTileWidth();
		double sceneWidth = StageManager.getMainStage().getScene().getWidth();
		
		double availableWidth = sceneWidth - 2 * SizeUtil.getMinWidthSideLists();
		double width = getWidth(availableWidth, galleryTileSize, tilePane.getHgap());
		int prefColumnsNew = (int) (width / galleryTileSize);
		int prefColumnsOld = tilePane.getPrefColumns();
		
		if (prefColumnsNew != prefColumnsOld) {
			tilePane.setPrefColumns(prefColumnsNew);
			
			galleryPane.setMinViewportWidth(width);
			galleryPane.setPrefViewportWidth(width);
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
	public static double getGalleryTileSize() {
		return settings.getGalleryTileSize();
	}
}
