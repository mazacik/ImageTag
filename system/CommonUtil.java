package system;

import database.object.DataObject;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import settings.SettingsEnum;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.IntroWindowCell;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.center.BaseTile;

import java.util.ArrayList;
import java.util.Random;

public abstract class CommonUtil implements InstanceRepo {
    private static boolean nightMode = userSettings.valueOf(SettingsEnum.COLORMODE);
    /* Font */
    private static Font font = new Font(coreSettings.valueOf(SettingsEnum.FONTSIZE));
    public static boolean isNightMode() {
        return nightMode;
    }
    public static void setNightMode(boolean value) {
        nightMode = value;
        userSettings.setValueOf(SettingsEnum.COLORMODE, value);
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
    private static Background getBackgroundHov(ColorData colorData) {
        if (colorData.getBackgroundHov() == ColorType.DEF) {
            return ColorUtil.getBackgroundDef();
        } else if (colorData.getBackgroundHov() == ColorType.ALT) {
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
    public static void updateNodeProperties() {
        for (ColorData colorData : NodeFactory.getNodeList()) {
            colorData.getNode().setBackground(getBackgroundDef(colorData));
            if (colorData.getNodeType() == ColorData.NodeType.LABEL) {
                Label label = ((Label) colorData.getNode());
                label.setTextFill(getTextColorDef(colorData));
                if (colorData.getBackgroundHov() != ColorType.NULL && colorData.getTextFillHov() != ColorType.NULL) {
                    label.setOnMouseExited(event -> {
                        label.setBackground(getBackgroundDef(colorData));
                        label.setTextFill(getTextColorDef(colorData));
                    });
                    label.setOnMouseEntered(event -> {
                        label.setBackground(getBackgroundHov(colorData));
                        label.setTextFill(getTextColorHov(colorData));
                    });
                } else if (colorData.getBackgroundHov() != ColorType.NULL) {
                    label.setOnMouseExited(event -> label.setBackground(getBackgroundDef(colorData)));
                    label.setOnMouseEntered(event -> label.setBackground(getBackgroundHov(colorData)));
                } else if (colorData.getTextFillHov() != ColorType.NULL) {
                    label.setOnMouseExited(event -> label.setTextFill(getTextColorDef(colorData)));
                    label.setOnMouseEntered(event -> label.setTextFill(getTextColorHov(colorData)));
                }
            } else if (colorData.getNodeType() == ColorData.NodeType.INTROWINDOWCELL) {
                IntroWindowCell introWindowCell = ((IntroWindowCell) colorData.getNode());
                introWindowCell.setBackground(getBackgroundDef(colorData));
                introWindowCell.setOnMouseEntered(event -> {
                    introWindowCell.setBackground(getBackgroundHov(colorData));
                    introWindowCell.setCursor(Cursor.HAND);
                    introWindowCell.getNodeRemove().setVisible(true);
                });
                introWindowCell.setOnMouseExited(event -> {
                    introWindowCell.setBackground(getBackgroundDef(colorData));
                    introWindowCell.setCursor(Cursor.DEFAULT);
                    introWindowCell.getNodeRemove().setVisible(false);
                });
            } else if (colorData.getNodeType() == ColorData.NodeType.TEXTFIELD) {
                TextField textField = ((TextField) colorData.getNode());
                Color color = getTextColorDef(colorData);
                String colorString = String.format("#%02X%02X%02X",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255));
                textField.setStyle("-fx-text-fill: " + colorString + ";");
            }
        }
    }
    public static void updateNodeProperties(Scene scene) {
        for (ColorData colorData : NodeFactory.getNodeList()) {
            if (colorData.getNode().getScene().equals(scene)) {
                colorData.getNode().setBackground(getBackgroundDef(colorData));
                if (colorData.getNodeType() == ColorData.NodeType.LABEL) {
                    Label label = ((Label) colorData.getNode());
                    label.setTextFill(getTextColorDef(colorData));
                    if (colorData.getBackgroundHov() != ColorType.NULL && colorData.getTextFillHov() != ColorType.NULL) {
                        label.setOnMouseExited(event -> {
                            label.setBackground(getBackgroundDef(colorData));
                            label.setTextFill(getTextColorDef(colorData));
                        });
                        label.setOnMouseEntered(event -> {
                            label.setBackground(getBackgroundHov(colorData));
                            label.setTextFill(getTextColorHov(colorData));
                        });
                    } else if (colorData.getBackgroundHov() != ColorType.NULL) {
                        label.setOnMouseExited(event -> label.setBackground(getBackgroundDef(colorData)));
                        label.setOnMouseEntered(event -> label.setBackground(getBackgroundHov(colorData)));
                    } else if (colorData.getTextFillHov() != ColorType.NULL) {
                        label.setOnMouseExited(event -> label.setTextFill(getTextColorDef(colorData)));
                        label.setOnMouseEntered(event -> label.setTextFill(getTextColorHov(colorData)));
                    }
                } else if (colorData.getNodeType() == ColorData.NodeType.INTROWINDOWCELL) {
                    IntroWindowCell introWindowCell = ((IntroWindowCell) colorData.getNode());
                    introWindowCell.setBackground(getBackgroundDef(colorData));
                    introWindowCell.setOnMouseEntered(event -> {
                        introWindowCell.setBackground(getBackgroundHov(colorData));
                        introWindowCell.setCursor(Cursor.HAND);
                        introWindowCell.getNodeRemove().setVisible(true);
                    });
                    introWindowCell.setOnMouseExited(event -> {
                        introWindowCell.setBackground(getBackgroundDef(colorData));
                        introWindowCell.setCursor(Cursor.DEFAULT);
                        introWindowCell.getNodeRemove().setVisible(false);
                    });
                } else if (colorData.getNodeType() == ColorData.NodeType.TEXTFIELD) {
                    TextField textField = ((TextField) colorData.getNode());
                    Color color = getTextColorDef(colorData);
                    String colorString = String.format("#%02X%02X%02X",
                            (int) (color.getRed() * 255),
                            (int) (color.getGreen() * 255),
                            (int) (color.getBlue() * 255));
                    textField.setStyle("-fx-text-fill: " + colorString + ";");
                }
            }
        }
    }
    public static Font getFont() {
        return font;
    }
    public static int getPadding() {
        return coreSettings.valueOf(SettingsEnum.GLOBAL_PADDING);
    }

    public static Image textToImage(String text) {
        Label label = new Label(text);
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

    public static void swapDisplayMode() {
        mainStage.swapDisplayMode();
    }
    public static boolean isFullView() {
        return mainStage.isFullView();
    }
    public static DataObject getRandomDataObject() {
        ArrayList<BaseTile> tiles = tileView.getTiles();
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
    }
    public static int getHash() {
        return new Random().nextInt();
    }
}
