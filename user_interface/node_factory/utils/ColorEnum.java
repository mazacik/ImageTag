package user_interface.node_factory.utils;

import javafx.scene.paint.Color;

public enum ColorEnum {
    TEXTDAYDEF(Color.BLACK),
    TEXTDAYALT(Color.RED),
    TEXTDAYPOS(Color.GREEN),
    TEXTDAYNEG(Color.RED),
    TEXTDAYINT(Color.BLUE),

    TEXTNIGHTDEF(Color.LIGHTGRAY),
    TEXTNIGHTALT(Color.ORANGE),
    TEXTNIGHTPOS(Color.LIGHTGREEN),
    TEXTNIGHTNEG(Color.ORANGERED),
    TEXTNIGHTINT(Color.BLUE),

    BORDERDAY(Color.GRAY),
    BORDERNIGHT(Color.GRAY),
    ;

    private Color value;

    ColorEnum(Color value) {
        this.value = value;
    }

    public Color getValue() {
        return value;
    }
}
