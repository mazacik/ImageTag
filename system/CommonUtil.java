package system;

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import settings.SettingsNamespace;
import user_interface.node_factory.NodeColorData;
import user_interface.node_factory.NodeFactory;
import user_interface.node_factory.template.intro.IntroWindowCell;
import user_interface.node_factory.utils.ColorType;
import user_interface.node_factory.utils.ColorUtil;

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

    public static void swapDisplayMode() {
        mainStage.swapDisplayMode();
    }
    public static boolean isFullView() {
        return mainStage.isFullView();
    }
}
