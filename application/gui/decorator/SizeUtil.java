package application.gui.decorator;

import application.gui.panes.center.GalleryPane;
import application.gui.panes.center.MediaPane;
import application.main.Instances;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;

import java.awt.*;

public abstract class SizeUtil {
	private static final double GLOBAL_SPACING = 2;
	private static final double PREF_HEIGHT_TOPMENU = 30;
	private static final double MIN_WIDTH_SIDELISTS = 250;
	private static final double GALLERY_ICON_SIZE = Instances.getSettings().getGalleryTileSize();
	
	public static double getUsableScreenWidth() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
	}
	public static double getUsableScreenHeight() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
	}
	
	public static void stageWidthChangeHandler() {
		GalleryPane galleryPane = Instances.getGalleryPane();
		MediaPane mediaPane = Instances.getMediaPane();
		TilePane tilePane = galleryPane.getTilePane();
		
		double sceneWidth = Instances.getMainStage().getScene().getWidth();
		
		int availableWidth = (int) (sceneWidth - 2 * SizeUtil.getMinWidthSideLists());
		int prefColumnsNew = (int) (availableWidth / tilePane.getPrefTileWidth());
		int prefColumnsOld = tilePane.getPrefColumns();
		
		if (prefColumnsNew != prefColumnsOld) {
			tilePane.setPrefColumns(prefColumnsNew);
			
			double width = tilePane.getPrefColumns() * tilePane.getPrefTileWidth() + (tilePane.getPrefColumns() - 1) * tilePane.getHgap();
			
			galleryPane.setMinViewportWidth(width);
			galleryPane.setPrefViewportWidth(width);
			
			mediaPane.getCanvas().setWidth(availableWidth - 10);
		}
	}
	public static void stageHeightChangehandler() {
		Instances.getMediaPane().getCanvas().setHeight(Instances.getMainStage().getScene().getHeight() - Instances.getToolbarPane().getPrefHeight() - Instances.getToolbarPane().getPadding().getBottom() - Instances.getToolbarPane().getBorder().getInsets().getBottom());
	}
	
	public static double getStringWidth(String s) {
		Text t = new Text(s);
		t.setFont(Decorator.getFont());
		return t.getLayoutBounds().getWidth();
	}
	
	public static double getGlobalSpacing() {
		return GLOBAL_SPACING;
	}
	public static double getPrefHeightTopMenu() {
		return PREF_HEIGHT_TOPMENU;
	}
	public static double getMinWidthSideLists() {
		return MIN_WIDTH_SIDELISTS;
	}
	public static double getGalleryIconSize() {
		return GALLERY_ICON_SIZE;
	}
}
