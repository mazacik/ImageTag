package user_interface.singleton.utils;

import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import settings.SettingsEnum;
import system.CommonUtil;
import system.Instances;

import java.awt.*;

public class SizeUtil implements Instances {
    private static final double GLOBAL_SPACING = 2;
    private static final double PREF_HEIGHT_TOPMENU = 30;
    private static final double MIN_WIDTH_SIDELISTS = 200;
    private static final double GALLERY_ICON_SIZE = settings.intValueOf(SettingsEnum.THUMBSIZE);
    public static double getUsableScreenWidth() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
    }
    public static double getUsableScreenHeight() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
    }
    public static void stageWidthChangeHandler() {
        TilePane tilePane = tileView.getTilePane();

        int availableWidth = (int) (mainStage.getScene().getWidth() - 2 * SizeUtil.getMinWidthSideLists());
        int prefColumnsNew = (int) ((availableWidth - 50) / tilePane.getPrefTileWidth());
        int prefColumnsOld = tilePane.getPrefColumns();

        if (prefColumnsNew != prefColumnsOld) {
            tilePane.setPrefColumns(prefColumnsNew);

            double width = tilePane.getPrefColumns() * tilePane.getPrefTileWidth() + (tilePane.getPrefColumns() - 1) * tilePane.getHgap();
            tileView.setMinViewportWidth(width);
            tileView.setPrefViewportWidth(width);
        }

        mediaView.getCanvas().setWidth(availableWidth - 10);
    }
    public static void stageHeightChangehandler() {
        mediaView.getCanvas().setHeight(mainStage.getScene().getHeight() - topMenu.getPrefHeight() - topMenu.getPadding().getBottom() - topMenu.getBorder().getInsets().getBottom());
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
