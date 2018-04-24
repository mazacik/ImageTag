package project.frontend;

import javafx.scene.paint.Color;
import project.backend.DatabaseItem;

import java.io.Serializable;

public class ColoredText implements Serializable {

    private DatabaseItem owner;
    private String text;
    private Color color;

    ColoredText(String text, Color color) {
        this(text, color, null);
    }

    public ColoredText(String text, Color color, DatabaseItem owner) {
        this.text = text;

        this.color = color;
        this.owner = owner;
    }

    String getText() {
        return text;
    }

    Color getColor() {
        return color;
    }

    DatabaseItem getOwner() {
        return owner;
    }

    public void setText(String text) {
        this.text = text;
    }
}
