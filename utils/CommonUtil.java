package utils;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lifecycle.InstanceManager;
import settings.SettingsEnum;
import user_interface.factory.base.TextNode;
import user_interface.scene.MainScene;
import user_interface.utils.StyleUtil;

import java.util.Random;

public abstract class CommonUtil {
    private static int nightMode = InstanceManager.getSettings().intValueOf(SettingsEnum.COLORMODE);

    private static Font font = new Font(InstanceManager.getSettings().intValueOf(SettingsEnum.FONTSIZE));
    public static boolean isNightMode() {
        return nightMode == 1;
    }
    public static void setNightMode(boolean value) {
        if (!value) {
            nightMode = 0;
        } else {
            nightMode = 1;
        }
        InstanceManager.getSettings().setValueOf(SettingsEnum.COLORMODE, nightMode);
        StyleUtil.applyStyle();
    }

    //todo stop changing font when using factories (it's changed there)
    public static Font getFont() {
        return font;
    }

    public static Image textToImage(String text) {
        TextNode label = new TextNode(text);
        label.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        label.setTextFill(Color.RED);
        label.setWrapText(true);
        label.setFont(CommonUtil.getFont());
        label.setPadding(new Insets(0, 0, 0, 0));

        Text theText = new Text(label.getText());
        theText.setFont(label.getFont());
        int width = (int) theText.getBoundsInLocal().getWidth();
        int height = (int) theText.getBoundsInLocal().getHeight();

        WritableImage img = new WritableImage(width, height);
        Scene scene = new Scene(new Group(label));
        scene.setFill(Color.TRANSPARENT);
        scene.snapshot(img);
        return img;
    }
    public static void swapViewMode() {
        MainScene.swapViewMode();
    }
    public static int getRandomHash() {
        return new Random().nextInt();
    }
}
