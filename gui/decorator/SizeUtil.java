package application.gui.decorator;

import application.gui.panes.center.GalleryPane;
import application.gui.stage.Stages;
import application.main.Instances;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;

import java.awt.*;

public abstract class SizeUtil {
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
		TilePane tilePane = galleryPane.getTilePane();
		
		double sceneWidth = Stages.getMainStage().getScene().getWidth();
		
		int availableWidth = (int) (sceneWidth - 2 * SizeUtil.getMinWidthSideLists());
		int prefColumnsNew = (int) (availableWidth / tilePane.getPrefTileWidth());
		int prefColumnsOld = tilePane.getPrefColumns();
		
		if (prefColumnsNew != prefColumnsOld) {
			tilePane.setPrefColumns(prefColumnsNew);
			
			double width = tilePane.getPrefColumns() * tilePane.getPrefTileWidth() + (tilePane.getPrefColumns() - 1) * tilePane.getHgap();
			
			galleryPane.setMinViewportWidth(width);
			galleryPane.setPrefViewportWidth(width);
		}
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
	public static double getGalleryIconSize() {
		return GALLERY_ICON_SIZE;
	}
}
