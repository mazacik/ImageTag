package userinterface.style;

import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import main.InstanceManager;
import settings.SettingsEnum;
import userinterface.main.center.GalleryPane;
import userinterface.main.center.MediaPane;

import java.awt.*;

public abstract class SizeUtil {
    private static final double GLOBAL_SPACING = 2;
    private static final double PREF_HEIGHT_TOPMENU = 30;
	private static final double MIN_WIDTH_SIDELISTS = 250;
    private static final double GALLERY_ICON_SIZE = InstanceManager.getSettings().intValueOf(SettingsEnum.THUMBSIZE);

    public static double getUsableScreenWidth() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
    }
    public static double getUsableScreenHeight() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
    }

    public static void stageWidthChangeHandler() {
        GalleryPane galleryPane = InstanceManager.getGalleryPane();
        MediaPane mediaPane = InstanceManager.getMediaPane();
        TilePane tilePane = galleryPane.getTilePane();

        double sceneWidth = InstanceManager.getMainStage().getScene().getWidth();

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
        InstanceManager.getMediaPane().getCanvas().setHeight(InstanceManager.getMainStage().getScene().getHeight() - InstanceManager.getToolbarPane().getPrefHeight() - InstanceManager.getToolbarPane().getPadding().getBottom() - InstanceManager.getToolbarPane().getBorder().getInsets().getBottom());
    }

    public static double getStringWidth(String s) {
        Text t = new Text(s);
        t.setFont(StyleUtil.getFont());
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
