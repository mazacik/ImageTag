package user_interface.style.enums;

import javafx.scene.paint.Color;

public enum ColorEnum {
    TEXTDAYDEF(Color.BLACK),
    TEXTDAYALT(Color.DARKORANGE),
    TEXTDAYPOS(Color.GREEN),
    TEXTDAYNEG(Color.RED),
    TEXTDAYSHR(Color.CORNFLOWERBLUE),

    TEXTNIGHTDEF(Color.LIGHTGRAY),
    TEXTNIGHTALT(Color.ORANGE),
    TEXTNIGHTPOS(Color.LIGHTGREEN),
    TEXTNIGHTNEG(Color.ORANGERED),
    TEXTNIGHTSHR(Color.CORNFLOWERBLUE),

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
