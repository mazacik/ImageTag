package user_interface.nodes;

import javafx.scene.layout.Region;
import user_interface.style.enums.ColorType;

public class ColorData {
    private final Region node;

    private ColorType backgroundDef;
    private ColorType backgroundAlt;
    private ColorType textFillDef;
    private ColorType textFillAlt;

    public ColorData(ColorType backgroundDef, ColorType backgroundAlt, ColorType textFillDef, ColorType textFillAlt) {
        this(null, backgroundDef, backgroundAlt, textFillDef, textFillAlt);
    }
    public ColorData(Region node, ColorType backgroundDef, ColorType backgroundAlt, ColorType textFillDef, ColorType textFillAlt) {
        this.node = node;
        this.backgroundDef = backgroundDef;
        this.backgroundAlt = backgroundAlt;
        this.textFillDef = textFillDef;
        this.textFillAlt = textFillAlt;
    }

    public Region getRegion() {
        return node;
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
    public ColorType getTextFillAlt() {
        return textFillAlt;
    }
}
