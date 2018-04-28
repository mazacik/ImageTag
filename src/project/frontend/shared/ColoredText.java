package project.frontend.shared;

import javafx.scene.paint.Color;
import project.backend.shared.DatabaseItem;

public class ColoredText {

    private DatabaseItem owner;
    private String text;
    private Color color;

    public ColoredText(String text, Color color) {
        this(text, color, null);
    }

    public ColoredText(String text, Color color, DatabaseItem owner) {
        this.text = text;

        this.color = color;
        this.owner = owner;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }

    public DatabaseItem getOwner() {
        return owner;
    }

    public void setText(String text) {
        this.text = text;
    }
}
