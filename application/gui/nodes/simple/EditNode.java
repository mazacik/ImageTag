package application.gui.nodes.simple;

import application.gui.decorator.ColorUtil;
import application.gui.decorator.Decorator;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.NodeUtil;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class EditNode extends TextField {
	public EditNode(String promptText, EditNodeType type) {
		this.setFont(Decorator.getFont());
		this.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		this.setPromptText(promptText);
		this.skinProperty().addListener((observable, oldValue, newValue) -> {
			Color color = ColorUtil.getTextColorDef();
			String colorString = String.format("#%02X%02X%02X",
					(int) (color.getRed() * 255),
					(int) (color.getGreen() * 255),
					(int) (color.getBlue() * 255));
			setStyle("-fx-text-fill: " + colorString + "; -fx-prompt-text-fill: gray;");
		});
		
		switch (type) {
			case DEFAULT:
				break;
			case NUMERIC:
				this.textProperty().addListener((observable, oldValue, newValue) -> {
					if (!newValue.equals("") && !isInteger(newValue)) {
						this.setText(oldValue);
					}
				});
				break;
			case NUMERIC_POSITIVE:
				this.textProperty().addListener((observable, oldValue, newValue) -> {
					if (!newValue.equals("") && !isIntegerPositive(newValue)) {
						this.setText(oldValue);
					}
				});
				break;
		}
		
		Decorator.manage(this, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
	}
	public EditNode(String promptText) {
		this(promptText, EditNodeType.DEFAULT);
	}
	public EditNode() {
		this("", EditNodeType.DEFAULT);
	}
	
	public boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}
	public boolean isIntegerPositive(String str) {
		if (!isInteger(str)) return false;
		return !(str.charAt(0) == '-');
	}
	
	public enum EditNodeType {
		DEFAULT,
		NUMERIC,
		NUMERIC_POSITIVE,
	}
}
