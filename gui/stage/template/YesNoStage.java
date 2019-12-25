package gui.stage.template;

import gui.component.simple.TextNode;
import gui.stage.base.StageBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class YesNoStage extends StageBase {
	private TextNode labelContent;
	private boolean result;
	
	public YesNoStage() {
		super("Confirmation", false, true, true);
		
		labelContent = new TextNode("CONTENT_STRING", false, false, false, false);
		
		TextNode buttonPositive = new TextNode("Yes", true, true, false, true);
		buttonPositive.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = true;
			this.close();
		});
		TextNode buttonNegative = new TextNode("No", true, true, false, true);
		buttonNegative.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = false;
			this.close();
		});
		
		setRoot(labelContent);
		setButtons(buttonPositive, buttonNegative);
	}
	
	@Override
	public Boolean show(String... args) {
		result = false;
		labelContent.setText(args[0]);
		this.showAndWait();
		return result;
	}
}
