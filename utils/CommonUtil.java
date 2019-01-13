package utils;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public class CommonUtil implements InstanceRepo {
    private static boolean nightMode = true;
    /* Color */
    private static Color textColorDayDefault = Color.BLACK;
    private static Color textColorDayPositive = Color.GREEN;

    private static Font font = new Font(14);
    public static Font getFont() {
        return font;
    }
    private static Color textColorDayNegative = Color.RED;
    private static Color textColorDayIntersect = Color.BLUE;
    private static Color textColorNightDefault = Color.LIGHTGRAY;
    private static Color textColorNightPositive = Color.LIGHTGREEN;
    private static Color textColorNightNegative = Color.ORANGERED;
    private static Color textColorNightIntersect = Color.BLUE;
    private static Color nodeBorderColorNight = Color.GRAY;
    /* Background */
    private static Background backgroundNight1 = new Background(new BackgroundFill(Paint.valueOf("#3C3F41"), null, null));
    private static Background backgroundNight2 = new Background(new BackgroundFill(Paint.valueOf("#313335"), null, null));
    private static Background backgroundNight3 = new Background(new BackgroundFill(Paint.valueOf("#2B2B2B"), null, null));
    public static void swapDisplayMode() {
        mainStage.swapDisplayMode();
    }
    public static boolean isFullView() {
        return mainStage.isFullView();
    }
    public static Color getTextColorDefault() {
        if (nightMode) return textColorNightDefault;
        else return textColorDayDefault;
    }
    public static Color getTextColorPositive() {
        if (nightMode) return textColorNightPositive;
        else return textColorDayPositive;
    }
    public static Color getTextColorNegative() {
        if (nightMode) return textColorNightNegative;
        else return textColorDayNegative;
    }
    public static Color getTextColorIntersect() {
        if (nightMode) return textColorNightIntersect;
        else return textColorDayIntersect;
    }
    public static Color getNodeBorderColor() {
        if (nightMode) return nodeBorderColorNight;
        else return Color.PINK; //todo
    }
    public static Background getBackgroundDefault() {
        return backgroundNight1;
    }
    public static Background getBackgroundAlternative() {
        return backgroundNight2;
    }

    public static Background getButtonBackgroundDefault() {
        return backgroundNight1;
    }
    public static Background getButtonBackgroundHover() {
        return backgroundNight2;
    }
}
