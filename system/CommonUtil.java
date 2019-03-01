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
import settings.SettingsNamespace;
import user_interface.node_factory.NodeColorData;
import user_interface.node_factory.NodeFactory;
import user_interface.node_factory.template.intro.IntroWindowCell;
import user_interface.node_factory.utils.ColorType;
import user_interface.node_factory.utils.ColorUtil;
import user_interface.single_instance.center.BaseTile;

import java.util.ArrayList;
import java.util.Random;

public abstract class CommonUtil implements InstanceRepo {
    private static boolean nightMode = false;
    /* Font */
    private static Font font = new Font(coreSettings.valueOf(SettingsNamespace.FONTSIZE));
    public static boolean isNightMode() {
        return nightMode;
    }
    public static void setNightMode(boolean value) {
        nightMode = value;
        updateNodeProperties();
    }
    private static Background getBackgroundDef(NodeColorData nodeColorData) {
        if (nodeColorData.getBackgroundDef() == ColorType.DEF) {
            return ColorUtil.getBackgroundDef();
        } else if (nodeColorData.getBackgroundDef() == ColorType.ALT) {
            return ColorUtil.getBackgroundAlt();
        } else {
            return null;
        }
    }
    private static Background getBackgroundHov(NodeColorData nodeColorData) {
        if (nodeColorData.getBackgroundHov() == ColorType.DEF) {
            return ColorUtil.getBackgroundDef();
        } else if (nodeColorData.getBackgroundHov() == ColorType.ALT) {
            return ColorUtil.getBackgroundAlt();
        } else {
            return null;
        }
    }
    private static Color getTextColorDef(NodeColorData nodeColorData) {
        if (nodeColorData.getTextFillDef() == ColorType.DEF) {
            return ColorUtil.getTextColorDef();
        } else if (nodeColorData.getTextFillDef() == ColorType.ALT) {
            return ColorUtil.getTextColorAlt();
        } else {
            return null;
        }
    }
    private static Color getTextColorHov(NodeColorData nodeColorData) {
        if (nodeColorData.getTextFillHov() == ColorType.DEF) {
            return ColorUtil.getTextColorDef();
        } else if (nodeColorData.getTextFillHov() == ColorType.ALT) {
            return ColorUtil.getTextColorAlt();
        } else {
            return null;
        }
    }
    public static void updateNodeProperties() {
        for (NodeColorData nodeColorData : NodeFactory.getNodeList()) {
            nodeColorData.getNode().setBackground(getBackgroundDef(nodeColorData));
            if (nodeColorData.getNodeType() == NodeColorData.NodeType.LABEL) {
                Label label = ((Label) nodeColorData.getNode());
                label.setTextFill(getTextColorDef(nodeColorData));
                if (nodeColorData.getBackgroundHov() != ColorType.NULL && nodeColorData.getTextFillHov() != ColorType.NULL) {
                    label.setOnMouseExited(event -> {
                        label.setBackground(getBackgroundDef(nodeColorData));
                        label.setTextFill(getTextColorDef(nodeColorData));
                    });
                    label.setOnMouseEntered(event -> {
                        label.setBackground(getBackgroundHov(nodeColorData));
                        label.setTextFill(getTextColorHov(nodeColorData));
                    });
                } else if (nodeColorData.getBackgroundHov() != ColorType.NULL) {
                    label.setOnMouseExited(event -> label.setBackground(getBackgroundDef(nodeColorData)));
                    label.setOnMouseEntered(event -> label.setBackground(getBackgroundHov(nodeColorData)));
                } else if (nodeColorData.getTextFillHov() != ColorType.NULL) {
                    label.setOnMouseExited(event -> label.setTextFill(getTextColorDef(nodeColorData)));
                    label.setOnMouseEntered(event -> label.setTextFill(getTextColorHov(nodeColorData)));
                }
            } else if (nodeColorData.getNodeType() == NodeColorData.NodeType.INTROWINDOWCELL) {
                IntroWindowCell introWindowCell = ((IntroWindowCell) nodeColorData.getNode());
                introWindowCell.setBackground(getBackgroundDef(nodeColorData));
                introWindowCell.setOnMouseEntered(event -> {
                    introWindowCell.setBackground(getBackgroundHov(nodeColorData));
                    introWindowCell.setCursor(Cursor.HAND);
                    introWindowCell.getNodeRemove().setVisible(true);
                });
                introWindowCell.setOnMouseExited(event -> {
                    introWindowCell.setBackground(getBackgroundDef(nodeColorData));
                    introWindowCell.setCursor(Cursor.DEFAULT);
                    introWindowCell.getNodeRemove().setVisible(false);
                });
            } else if (nodeColorData.getNodeType() == NodeColorData.NodeType.TEXTFIELD) {
                TextField textField = ((TextField) nodeColorData.getNode());
                Color color = getTextColorDef(nodeColorData);
                String colorString = String.format("#%02X%02X%02X",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255));
                textField.setStyle("-fx-text-fill: " + colorString + ";");
            }
        }
    }
    public static Font getFont() {
        return font;
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
        DataObject dataObject = tiles.get(index).getParentDataObject();

        if (dataObject.getMergeID() == 0) {
            return dataObject;
        } else {
            ArrayList<DataObject> dataObjectsSameMergeID = new ArrayList<>();
            for (DataObject dataObjectSameMergeID : filter) {
                if (dataObjectSameMergeID.getMergeID() == dataObject.getMergeID()) {
                    dataObjectsSameMergeID.add(dataObjectSameMergeID);
                }
            }
            return dataObjectsSameMergeID.get(new Random().nextInt(dataObjectsSameMergeID.size()));
        }
    }
    public static int getHash() {
        return new Random().nextInt();
    }
}
