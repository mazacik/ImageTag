package frontend.node;

import frontend.decorator.DecoratorUtil;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;

public class EditNode extends TextField {
	public EditNode() {
		this("", "");
	}
	public EditNode(EditNodeType type) {
		this("", "", type);
	}
	public EditNode(String text, String promptText) {
		this(text, promptText, EditNodeType.ANY_CHARACTERS);
	}
	public EditNode(String text, EditNodeType type) {
		this(text, "", type);
	}
	public EditNode(String text, String promptText, EditNodeType type) {
		this.setFont(DecoratorUtil.getFont());
		this.setBorder(DecoratorUtil.getBorder(1));
		if (!text.isEmpty()) this.setText(text);
		if (!promptText.isEmpty()) this.setPromptText(promptText);
		//this.skinProperty().addListener((observable, oldValue, newValue) -> setStyle("-fx-text-fill: " + Decorator.getColorAsStringForCss(ColorUtil.getColorPrimary()) + "; -fx-prompt-text-fill: gray;"));
		this.setBackground(Background.EMPTY);
		this.setStyle("-fx-text-fill: " + DecoratorUtil.getCssString(DecoratorUtil.getColorPrimary()) + ";");
		this.setPadding(new Insets(2));
		DecoratorUtil.getNodeList().add(this);
		
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
