package application.gui.nodes.simple;

import application.gui.decorator.Decorator;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.ColorData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class TextNode extends Label {
    public TextNode(String text) {
        this(text, ColorType.NULL, ColorType.NULL, ColorType.NULL, ColorType.NULL);
    }
    public TextNode(String text, ColorData colorData) {
        this(text, colorData.getBackgroundDef(), colorData.getBackgroundAlt(), colorData.getTextFillDef(), colorData.getTextFillAlt());
    }
    public TextNode(String text, ColorType background, ColorType textFill) {
        this(text, background, ColorType.NULL, textFill, ColorType.NULL);
    }
    public TextNode(String text, ColorType background, ColorType backgroundHover, ColorType textFill, ColorType textFillHover) {
        super(text);
        this.setTextAlignment(TextAlignment.CENTER);
		this.setFont(Decorator.getFont());
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(5, 10, 5, 10));
	
		Decorator.manage(this, background, backgroundHover, textFill, textFillHover);
    }
}
