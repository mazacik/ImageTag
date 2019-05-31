package system;

import database.object.DataObject;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import settings.SettingsEnum;
import user_interface.factory.NodeUtil;
import user_interface.factory.base.CheckBoxNode;
import user_interface.factory.base.TextNode;
import user_interface.factory.node.IntroWindowCell;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.scene.MainScene;
import user_interface.singleton.center.BaseTile;
import user_interface.singleton.side.GroupNode;

import java.util.ArrayList;
import java.util.Random;

public abstract class CommonUtil implements Instances {
    private static int nightMode = settings.intValueOf(SettingsEnum.COLORMODE);

    private static Font font = new Font(settings.intValueOf(SettingsEnum.FONTSIZE));
    public static boolean isNightMode() {
        return nightMode == 1;
    }
    public static void setNightMode(boolean value) {
        if (!value) {
            nightMode = 0;
        } else {
            nightMode = 1;
        }
        settings.setValueOf(SettingsEnum.COLORMODE, nightMode);
        updateNodeProperties();
    }
    private static Background getBackgroundDef(ColorData colorData) {
        if (colorData.getBackgroundDef() == ColorType.DEF) {
            return ColorUtil.getBackgroundDef();
        } else if (colorData.getBackgroundDef() == ColorType.ALT) {
            return ColorUtil.getBackgroundAlt();
        } else {
            return null;
        }
    }
    private static Background getBackgroundAlt(ColorData colorData) {
        if (colorData.getBackgroundAlt() == ColorType.DEF) {
            return ColorUtil.getBackgroundDef();
        } else if (colorData.getBackgroundAlt() == ColorType.ALT) {
            return ColorUtil.getBackgroundAlt();
        } else {
            return null;
        }
    }
    private static Color getTextColorDef(ColorData colorData) {
        if (colorData.getTextFillDef() == ColorType.DEF) {
            return ColorUtil.getTextColorDef();
        } else if (colorData.getTextFillDef() == ColorType.ALT) {
            return ColorUtil.getTextColorAlt();
        } else {
            return null;
        }
    }
    private static Color getTextColorHov(ColorData colorData) {
        if (colorData.getTextFillHov() == ColorType.DEF) {
            return ColorUtil.getTextColorDef();
        } else if (colorData.getTextFillHov() == ColorType.ALT) {
            return ColorUtil.getTextColorAlt();
        } else {
            return null;
        }
    }

    public static void updateNodeProperties(ArrayList<ColorData> colorDataList) {
        for (ColorData colorData : colorDataList) {
            colorData.getRegion().setBackground(getBackgroundDef(colorData));
            if (colorData.getRegion() instanceof TextNode) {
                TextNode label = ((TextNode) colorData.getRegion());
                if (colorData.getTextFillDef() != ColorType.NULL) {
                    label.setTextFill(getTextColorDef(colorData));
                }
                if (colorData.getBackgroundAlt() != ColorType.NULL && colorData.getTextFillHov() != ColorType.NULL) {
                    label.setOnMouseExited(event -> {
                        label.setBackground(getBackgroundDef(colorData));
                        label.setTextFill(getTextColorDef(colorData));
                        //label.setCursor(Cursor.DEFAULT);
                    });
                    label.setOnMouseEntered(event -> {
                        label.setBackground(getBackgroundAlt(colorData));
                        label.setTextFill(getTextColorHov(colorData));
                        //label.setCursor(Cursor.HAND);
                    });
                } else if (colorData.getBackgroundAlt() != ColorType.NULL) {
                    label.setOnMouseExited(event -> {
                        label.setBackground(getBackgroundDef(colorData));
                        //label.setCursor(Cursor.DEFAULT);
                    });
                    label.setOnMouseEntered(event -> {
                        label.setBackground(getBackgroundAlt(colorData));
                        //label.setCursor(Cursor.HAND);
                    });
                } else if (colorData.getTextFillHov() != ColorType.NULL) {
                    label.setOnMouseExited(event -> {
                        label.setTextFill(getTextColorDef(colorData));
                        //label.setCursor(Cursor.DEFAULT);
                    });
                    label.setOnMouseEntered(event -> {
                        label.setTextFill(getTextColorHov(colorData));
                        //label.setCursor(Cursor.HAND);
                    });
                }
            } else if (colorData.getRegion() instanceof GroupNode) {
                if (colorData.getBackgroundAlt() != ColorType.NULL) {
                    colorData.getRegion().setOnMouseExited(event -> {
                        colorData.getRegion().setBackground(getBackgroundDef(colorData));
                        //colorData.getRegion().setCursor(Cursor.DEFAULT);
                    });
                    colorData.getRegion().setOnMouseEntered(event -> {
                        colorData.getRegion().setBackground(getBackgroundAlt(colorData));
                        //colorData.getRegion().setCursor(Cursor.HAND);
                    });
                }
            } else if (colorData.getRegion() instanceof IntroWindowCell) {
                IntroWindowCell introWindowCell = ((IntroWindowCell) colorData.getRegion());
                introWindowCell.setBackground(getBackgroundDef(colorData));
                introWindowCell.setOnMouseEntered(event -> {
                    introWindowCell.setBackground(getBackgroundAlt(colorData));
                    introWindowCell.setCursor(Cursor.HAND);
                    introWindowCell.getNodeRemove().setVisible(true);
                });
                introWindowCell.setOnMouseExited(event -> {
                    introWindowCell.setBackground(getBackgroundDef(colorData));
                    introWindowCell.setCursor(Cursor.DEFAULT);
                    introWindowCell.getNodeRemove().setVisible(false);
                });
            } else if (colorData.getRegion() instanceof TextField) {
                TextField textField = ((TextField) colorData.getRegion());
                Color color = getTextColorDef(colorData);
                String colorString = String.format("#%02X%02X%02X",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255));
                textField.setStyle("-fx-text-fill: " + colorString + ";");
            } else if (colorData.getRegion() instanceof CheckBoxNode) {
                CheckBoxNode checkBoxNode = ((CheckBoxNode) colorData.getRegion());
                if (colorData.getTextFillDef() != ColorType.NULL) {
                    checkBoxNode.setTextFill(getTextColorDef(colorData));
                }
            }
        }
    }
    public static void updateNodeProperties(Scene scene) {
        ArrayList<ColorData> colorDataList = new ArrayList<>();
        for (ColorData colorData : NodeUtil.getNodeList()) {
            if (colorData.getRegion().getScene() != null && colorData.getRegion().getScene().equals(scene)) {
                colorDataList.add(colorData);
            }
        }
        updateNodeProperties(colorDataList);
    }
    public static void updateNodeProperties(Pane pane) {
        ArrayList<ColorData> colorDataList = new ArrayList<>();
        for (ColorData colorData : NodeUtil.getNodeList()) {
            Region region = colorData.getRegion();
            Parent parent = region.getParent();
            if ((parent != null && parent.equals(pane)) || region == pane) {
                colorDataList.add(colorData);
            }
        }
        updateNodeProperties(colorDataList);
    }
    public static void updateNodeProperties() {
        updateNodeProperties(NodeUtil.getNodeList());
    }

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
    public static boolean isCenterFullscreen() {
        return MainScene.isFullView();
    }
    public static DataObject getRandomDataObject() {
        ArrayList<BaseTile> tiles = tileView.getVisibleTiles();
        if (tiles.size() >= 1) {
            int index = new Random().nextInt(tiles.size());
            DataObject chosenDataObject = tiles.get(index).getParentDataObject();

            if (chosenDataObject.getMergeID() == 0) {
                return chosenDataObject;
            } else {
                ArrayList<DataObject> dataObjectsSameMergeID = new ArrayList<>();
                for (DataObject dataObjectFromFilter : filter) {
                    if (dataObjectFromFilter.getMergeID() == chosenDataObject.getMergeID()) {
                        dataObjectsSameMergeID.add(dataObjectFromFilter);
                    }
                }
                return dataObjectsSameMergeID.get(new Random().nextInt(dataObjectsSameMergeID.size()));
            }
        } else {
            return null;
        }
    }
    public static int getRandomHash() {
        return new Random().nextInt();
    }
}
