package project.gui.component.LeftPane;

import javafx.scene.paint.Color;

public class ColoredText {
    /* vars */
    private String text;
    private Color color;

    /* constructors */
    public ColoredText(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    /* get */
    public String getText() {
        return text;
    }
    public Color getColor() {
        return color;
    }

    /* set */
    public void setColor(Color color) {
        this.color = color;
    }
}
