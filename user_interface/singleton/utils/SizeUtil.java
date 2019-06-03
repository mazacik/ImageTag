package user_interface.singleton.utils;

import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import lifecycle.InstanceManager;
import settings.SettingsEnum;
import system.CommonUtil;

import java.awt.*;

public class SizeUtil {
    private static final double GLOBAL_SPACING = 2;
    private static final double PREF_HEIGHT_TOPMENU = 30;
    private static final double MIN_WIDTH_SIDELISTS = 200;
    private static final double GALLERY_ICON_SIZE = InstanceManager.getSettings().intValueOf(SettingsEnum.THUMBSIZE);

    public static double getUsableScreenWidth() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
    }
    public static double getUsableScreenHeight() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
    }
    public static void stageWidthChangeHandler() {
        TilePane tilePane = InstanceManager.getGalleryPane().getTilePane();

        //int availableWidth = (int) (SizeUtil.getUsableScreenWidth() - 2 * SizeUtil.getMinWidthSideLists());
        int availableWidth = (int) (InstanceManager.getMainStage().getScene().getWidth() - 2 * SizeUtil.getMinWidthSideLists());
        int prefColumnsNew = (int) ((availableWidth - 50) / tilePane.getPrefTileWidth());
        int prefColumnsOld = tilePane.getPrefColumns();

        if (prefColumnsNew != prefColumnsOld) {
            tilePane.setPrefColumns(prefColumnsNew);

            double width = tilePane.getPrefColumns() * tilePane.getPrefTileWidth() + (tilePane.getPrefColumns() - 1) * tilePane.getHgap();
            InstanceManager.getGalleryPane().setMinViewportWidth(width);
            InstanceManager.getGalleryPane().setPrefViewportWidth(width);
        }

        InstanceManager.getMediaPane().getCanvas().setWidth(availableWidth - 10);
    }
    public static void stageHeightChangehandler() {
        InstanceManager.getMediaPane().getCanvas().setHeight(InstanceManager.getMainStage().getScene().getHeight() - InstanceManager.getToolbarPane().getPrefHeight() - InstanceManager.getToolbarPane().getPadding().getBottom() - InstanceManager.getToolbarPane().getBorder().getInsets().getBottom());
    }
    public static double getStringWidth(String s) {
        Text t = new Text(s);
        t.setFont(CommonUtil.getFont());
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
