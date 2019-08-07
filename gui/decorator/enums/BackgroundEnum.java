package application.gui.decorator.enums;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;

public enum BackgroundEnum {
    DAYDEF(new Background(new BackgroundFill(Paint.valueOf("#DDDEEE"), null, null))),
    DAYALT(new Background(new BackgroundFill(Paint.valueOf("#CCCDDD"), null, null))),
    NIGHTDEF(new Background(new BackgroundFill(Paint.valueOf("#3C3F41"), null, null))),
    NIGHTALT(new Background(new BackgroundFill(Paint.valueOf("#313335"), null, null))),
    ;

    private Background value;
    BackgroundEnum(Background value) {
        this.value = value;
    }

    public Background getValue() {
        return value;
    }
}
