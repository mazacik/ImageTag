package user_interface.factory.util;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import user_interface.factory.node.IntroWindowCell;
import user_interface.factory.util.enums.ColorType;

public class ColorData {
    private final NodeType nodeType;

    private final Region node;

    private ColorType backgroundDef;
    private ColorType backgroundAlt;
    private ColorType textFillDef;
    private ColorType textFillAlt;

    public ColorData(ColorType backgroundDef, ColorType backgroundAlt, ColorType textFillDef, ColorType textFillAlt) {
        this(null, backgroundDef, backgroundAlt, textFillDef, textFillAlt);
    }
    public ColorData(Region node, ColorType backgroundDef, ColorType backgroundAlt, ColorType textFillDef, ColorType textFillAlt) {
        if (node instanceof Label) {
            nodeType = NodeType.LABEL;
        } else if (node instanceof IntroWindowCell) {
            nodeType = NodeType.INTROWINDOWCELL;
        } else if (node instanceof TextField) {
            nodeType = NodeType.TEXTFIELD;
        } else {
            nodeType = NodeType.GENERIC;
        }

        this.node = node;
        this.backgroundDef = backgroundDef;
        this.backgroundAlt = backgroundAlt;
        this.textFillDef = textFillDef;
        this.textFillAlt = textFillAlt;
    }

    public Region getNode() {
        return node;
    }
    public NodeType getNodeType() {
        return nodeType;
    }
    public ColorType getBackgroundDef() {
        return backgroundDef;
    }
    public ColorType getBackgroundAlt() {
        return backgroundAlt;
    }
    public ColorType getTextFillDef() {
        return textFillDef;
    }
    public ColorType getTextFillHov() {
        return textFillAlt;
    }

    public enum NodeType {
        LABEL,
        GENERIC,
        INTROWINDOWCELL,
        TEXTFIELD,
        ;
    }
}
