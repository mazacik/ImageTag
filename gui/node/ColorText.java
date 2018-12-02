package gui.node;

import javafx.scene.paint.Color;

public class ColorText {
    private String text;
    private Color color;

    public ColorText(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }
    public Color getColor() {
        return color;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setColor(Color color) {
        this.color = color;
    }
}
