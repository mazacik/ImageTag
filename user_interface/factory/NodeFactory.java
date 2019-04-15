package user_interface.factory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import system.CommonUtil;
import user_interface.factory.node.IntroWindowCell;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.side.GroupNode;

import java.util.ArrayList;

public abstract class NodeFactory {
    private static ArrayList<ColorData> nodeList = new ArrayList<>();
    public static ArrayList<ColorData> getNodeList() {
        return nodeList;
    }

    public static Label getLabel(String text) {
        return getLabel(text, ColorType.NULL, ColorType.NULL, ColorType.NULL, ColorType.NULL);
    }
    public static Label getLabel(String text, ColorData colorData) {
        return getLabel(text, colorData.getBackgroundDef(), colorData.getBackgroundAlt(), colorData.getTextFillDef(), colorData.getTextFillHov());
    }
    public static Label getLabel(String text, ColorType backgroundDef, ColorType textFillDef) {
        return getLabel(text, backgroundDef, ColorType.NULL, textFillDef, ColorType.NULL);
    }
    public static Label getLabel(String text, ColorType backgroundDef, ColorType backgroundAlt, ColorType textFillDef, ColorType textFillAlt) {
        Label label = new Label(text);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(CommonUtil.getFont());
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(5, 10, 5, 10));

        nodeList.add(new ColorData(label, backgroundDef, backgroundAlt, textFillDef, textFillAlt));

        return label;
    }

    public static Label getSeparator() {
        Label separator = new Label();
        separator.setFont(new Font(0));
        separator.setPrefHeight(0);
        separator.setBorder(NodeFactory.getBorder(0, 0, 1, 0));
        return separator;
    }

    public static GroupNode getGroupNode(VBox owner, String text, Color textFill) {
        GroupNode groupNode = new GroupNode(text, owner);
        groupNode.setTextFill(textFill);
        groupNode.setFont(CommonUtil.getFont());
        nodeList.add(new ColorData(groupNode, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL));

        return groupNode;
    }
    public static IntroWindowCell getIntroWindowCell(String path) {
        IntroWindowCell introWindowCell = new IntroWindowCell(path);
        nodeList.add(new ColorData(introWindowCell, ColorType.ALT, ColorType.DEF, ColorType.NULL, ColorType.NULL));
        return introWindowCell;
    }

    public static HBox getHBox(ColorType colorType, Node... children) {
        HBox hBox = new HBox(children);
        addNodeToManager(hBox, colorType);
        return hBox;
    }
    public static VBox getVBox(ColorType colorType, Node... children) {
        VBox vBox = new VBox(children);
        addNodeToManager(vBox, colorType);
        return vBox;
    }

    public static Tooltip getTooltip(String text) {
        Tooltip t = new Tooltip(text);
        t.setFont(CommonUtil.getFont());
        t.setShowDelay(new Duration(500));
        return t;
    }
    public static Border getBorder(int top, int right, int bottom, int left) {
        return new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(top, right, bottom, left)));
    }

    public static void addNodeToManager(Region region, ColorType backgroundDef) {
        addNodeToManager(region, backgroundDef, ColorType.NULL, ColorType.NULL, ColorType.NULL);
    }
    public static void addNodeToManager(Region region, ColorType backgroundDef, ColorType backgroundAlt, ColorType textFillDef, ColorType textFillAlt) {
        boolean match = false;
        for (ColorData colorData : nodeList) {
            if (colorData.getNode() == region) {
                match = true;
                break;
            }
        }

        if (!match) {
            nodeList.add(new ColorData(region, backgroundDef, backgroundAlt, textFillDef, textFillAlt));
        }
    }

    public static void removeNodesFromManager(Scene scene) {
        ArrayList<ColorData> colorDataList = new ArrayList<>();
        for (ColorData colorData : NodeFactory.getNodeList()) {
            if (colorData.getNode().getScene() != null && colorData.getNode().getScene().equals(scene)) {
                colorDataList.add(colorData);
            }
        }
        nodeList.removeAll(colorDataList);
    }
}
