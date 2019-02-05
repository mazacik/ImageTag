package utils;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import settings.SettingsNamespace;
import userinterface.node.topmenu.CustomCM;

public class CommonUtil implements InstanceRepo {
    private static boolean nightMode = true;
    public static boolean isNightMode() {
        return nightMode;
    }

    /* Font */
    private static Font font = new Font(coreSettings.valueOf(SettingsNamespace.FONTSIZE));
    public static Font getFont() {
        return font;
    }

    /* Color */
    private static Color textColorDayDefault = Color.BLACK;
    private static Color textColorDayHighlight = Color.RED;
    private static Color textColorDayPositive = Color.GREEN;
    private static Color textColorDayNegative = Color.RED;
    private static Color textColorDayIntersect = Color.BLUE;
    private static Color textColorNightDefault = Color.LIGHTGRAY;
    private static Color textColorNightHighlight = Color.ORANGE;
    private static Color textColorNightPositive = Color.LIGHTGREEN;
    private static Color textColorNightNegative = Color.ORANGERED;
    private static Color textColorNightIntersect = Color.BLUE;
    public static Color getTextColorDefault() {
        if (nightMode) return textColorNightDefault;
        else return textColorDayDefault;
    }
    public static Color getTextColorHighlight() {
        if (nightMode) return textColorNightHighlight;
        else return textColorDayHighlight;
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

    private static Color nodeBorderColorNight = Color.GRAY;
    private static Color nodeBorderColorDay = Color.GRAY;
    /* Background */
    private static Background backgroundDay1 = new Background(new BackgroundFill(Paint.valueOf("#DDDEEE"), null, null));
    private static Background backgroundDay2 = new Background(new BackgroundFill(Paint.valueOf("#CCCDDD"), null, null));
    private static Background backgroundNight1 = new Background(new BackgroundFill(Paint.valueOf("#3C3F41"), null, null));
    private static Background backgroundNight2 = new Background(new BackgroundFill(Paint.valueOf("#313335"), null, null));
    private static Insets nodePadding = new Insets(5, 10, 5, 10);
    public static Color getNodeBorderColor() {
        if (nightMode) return nodeBorderColorNight;
        else return nodeBorderColorDay;
    }
    public static Background getBackgroundDefault() {
        if (nightMode) return backgroundNight1;
        else return backgroundDay1;
    }
    public static Background getBackgroundAlternative() {
        if (nightMode) return backgroundNight2;
        else return backgroundDay2;
    }
    public static Background getButtonBackgroundDefault() {
        if (nightMode) return backgroundNight1;
        else return backgroundDay1;
    }
    public static Background getButtonBackgroundHover() {
        if (nightMode) return backgroundNight2;
        else return backgroundDay2;
    }
    public static void swapDisplayMode() {
        mainStage.swapDisplayMode();
    }
    public static boolean isFullView() {
        return mainStage.isFullView();
    }
    public static Label createNode(String text) {
        Label node = new Label(text);
        node.setFont(CommonUtil.getFont());
        node.setTextFill(CommonUtil.getTextColorDefault());
        node.setTextAlignment(TextAlignment.CENTER);
        node.setOnMouseEntered(event -> node.setBackground(CommonUtil.getButtonBackgroundHover()));
        node.setOnMouseExited(event -> node.setBackground(CommonUtil.getButtonBackgroundDefault()));
        node.setPadding(nodePadding);

        return node;
    }
    public static Label createRoot(String text, Label... children) {
        CustomCM customCM = new CustomCM(children);

        Label root = new Label(text);
        root.setFont(CommonUtil.getFont());
        root.setTextFill(CommonUtil.getTextColorDefault());
        root.setTextAlignment(TextAlignment.CENTER);
        root.setOnMouseEntered(event -> root.setBackground(CommonUtil.getButtonBackgroundHover()));
        root.setOnMouseExited(event -> root.setBackground(CommonUtil.getButtonBackgroundDefault()));
        root.setOnMouseClicked(event -> {
            Bounds boundsInScene = root.getBoundsInParent();
            customCM.show(root, boundsInScene.getMinX(), boundsInScene.getMaxY() + 1);
            CommonUtil.setLabelGroupWidth(children);
        });
        root.setPadding(nodePadding);
        return root;
    }
    private static void setLabelGroupWidth(Label... labels) {
        double width = 0;
        for (Label label : labels) {
            if (width < label.getWidth()) width = label.getWidth();
        }
        for (Label label : labels) {
            label.setPrefWidth(width);
        }
    }
}
