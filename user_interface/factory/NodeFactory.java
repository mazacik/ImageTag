package user_interface.factory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import system.CommonUtil;
import user_interface.factory.node.IntroWindowCell;
import user_interface.factory.util.ColorData;
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

    public static GroupNode getGroupNode(String text, Color textFill) {
        GroupNode groupNode = new GroupNode(text);
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
        hBox.setAlignment(Pos.CENTER);
        addNodeToBackgroundManager(hBox, colorType);
        return hBox;
    }
    public static VBox getVBox(ColorType colorType, Node... children) {
        VBox vBox = new VBox(children);
        vBox.setAlignment(Pos.CENTER);
        addNodeToBackgroundManager(vBox, colorType);
        return vBox;
    }

    public static void addNodeToBackgroundManager(Region region, ColorType backgroundDef) {
        addNodeToBackgroundManager(region, backgroundDef, ColorType.NULL, ColorType.NULL, ColorType.NULL);
    }
    public static void addNodeToBackgroundManager(Region region, ColorType backgroundDef, ColorType backgroundAlt, ColorType textFillDef, ColorType textFillAlt) {
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
}
