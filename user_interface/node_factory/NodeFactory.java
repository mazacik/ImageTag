package user_interface.node_factory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import system.CommonUtil;
import user_interface.node_factory.template.intro.IntroWindowCell;
import user_interface.node_factory.utils.ColorType;

import java.util.ArrayList;

public abstract class NodeFactory {
    private static ArrayList<NodeColorData> nodeList = new ArrayList<>();
    public static ArrayList<NodeColorData> getNodeList() {
        return nodeList;
    }

    public static Label getLabel(String text, NodeColorData nodeColorData) {
        return getLabel(text, nodeColorData.getBackgroundDef(), nodeColorData.getBackgroundHov(), nodeColorData.getTextFillDef(), nodeColorData.getTextFillHov());
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

        nodeList.add(new NodeColorData(label, backgroundDef, backgroundAlt, textFillDef, textFillAlt));

        return label;
    }
    public static IntroWindowCell getIntroWindowCell(String path) {
        IntroWindowCell introWindowCell = new IntroWindowCell(path);
        nodeList.add(new NodeColorData(introWindowCell, ColorType.ALT, ColorType.DEF, ColorType.NULL, ColorType.NULL));
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
        for (NodeColorData nodeColorData : nodeList) {
            if (nodeColorData.getNode() == region) {
                match = true;
                break;
            }
        }

        if (!match) {
            nodeList.add(new NodeColorData(region, backgroundDef, backgroundAlt, textFillDef, textFillAlt));
        }
    }
}
