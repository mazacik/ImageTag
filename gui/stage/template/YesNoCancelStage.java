package gui.stage.template;

import gui.component.simple.TextNode;
import gui.stage.base.StageBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class YesNoCancelStage extends StageBase {
	private TextNode labelContent;
	private ButtonBooleanValue result;
	
	public YesNoCancelStage() {
		super("Confirmation", false, true, true);
		
		labelContent = new TextNode("CONTENT_STRING", false, false, false, false);
		
		TextNode buttonPositive = new TextNode("Yes", true, true, false, true);
		buttonPositive.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = ButtonBooleanValue.YES;
			this.close();
		});
		TextNode buttonNegative = new TextNode("No", true, true, false, true);
		buttonNegative.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = ButtonBooleanValue.NO;
			this.close();
		});
		TextNode buttonCancel = new TextNode("Cancel", true, true, false, true);
		buttonCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = ButtonBooleanValue.CANCEL;
			this.close();
		});
		
		setRoot(labelContent);
		setButtons(buttonPositive, buttonNegative, buttonCancel);
	}
	
	@Override
	public ButtonBooleanValue show(String... args) {
		result = ButtonBooleanValue.CANCEL;
		labelContent.setText(args[0]);
		this.showAndWait();
		return result;
	}
}
