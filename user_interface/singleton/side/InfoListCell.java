package user_interface.singleton.side;

import javafx.scene.paint.Color;

public class InfoListCell {
    private String text;
    private Color color;

    public InfoListCell(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
}
