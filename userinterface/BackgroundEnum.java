package userinterface;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;

public enum BackgroundEnum {
    NIGHT_1(new Background(new BackgroundFill(Paint.valueOf("#3C3F41"), null, null))),
    NIGHT_2(new Background(new BackgroundFill(Paint.valueOf("#313335"), null, null))),
    NIGHT_3(new Background(new BackgroundFill(Paint.valueOf("#2B2B2B"), null, null))),
    ;

    Background value;
    BackgroundEnum(Background value) {
        this.value = value;
    }

    public Background getValue() {
        return value;
    }
}
