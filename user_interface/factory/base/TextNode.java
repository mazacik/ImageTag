package user_interface.factory.base;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import user_interface.factory.ColorData;
import user_interface.utils.NodeUtil;
import user_interface.utils.StyleUtil;
import user_interface.utils.enums.ColorType;

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
        this.setFont(StyleUtil.getFont());
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(5, 10, 5, 10));

        NodeUtil.addToManager(this, background, backgroundHover, textFill, textFillHover);
    }
}
