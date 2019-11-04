package application.frontend.component.simple;

import application.frontend.component.NodeUtil;
import application.frontend.decorator.ColorUtil;
import application.frontend.decorator.Decorator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;

public class EditNode extends TextField {
	public EditNode(String promptText, EditNodeType type) {
		this.setFont(Decorator.getFont());
		this.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		this.setPromptText(promptText);
		//this.skinProperty().addListener((observable, oldValue, newValue) -> setStyle("-fx-text-fill: " + Decorator.getColorAsStringForCss(ColorUtil.getTextColorDef()) + "; -fx-prompt-text-fill: gray;"));
		this.setBackground(Background.EMPTY);
		this.setStyle("-fx-text-fill: " + Decorator.getColorAsStringForCss(ColorUtil.getTextColorDef()) + ";");
		
		switch (type) {
			case ANY_CHARACTERS:
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
	}
	public EditNode(String promptText) {
		this(promptText, EditNodeType.ANY_CHARACTERS);
	}
	public EditNode() {
		this("", EditNodeType.ANY_CHARACTERS);
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
		ANY_CHARACTERS,
		NUMERIC,
		NUMERIC_POSITIVE,
	}
}
