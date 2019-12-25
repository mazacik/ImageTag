package ui.stage.template;

import ui.component.simple.TextNode;
import ui.stage.base.StageBase;
import ui.stage.base.StageBaseInterface;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ErrorStage extends StageBase implements StageBaseInterface {
	private TextNode labelContent;
	
	public ErrorStage() {
		super("", false, true, true);
		
		labelContent = new TextNode("", false, false, false, true);
		
		TextNode btnOK = new TextNode("OK", true, true, false, true);
		btnOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		setRoot(labelContent);
		setButtons(btnOK);
	}
	
	@Override
	public Object show(String... args) {
		if (args.length > 0) {
			if (args.length == 1) {
				setTitle("Error");
				labelContent.setText(args[0]);
			} else {
				setTitle(args[0]);
				labelContent.setText(args[1]);
			}
			this.showAndWait();
		}
		return null;
	}
}
