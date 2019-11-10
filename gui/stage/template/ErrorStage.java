package gui.stage.template;

import gui.component.simple.TextNode;
import gui.stage.base.StageBase;
import gui.stage.base.StageBaseInterface;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ErrorStage extends StageBase implements StageBaseInterface {
	private TextNode labelContent;
	
	public ErrorStage() {
		super("Error");
		
		labelContent = new TextNode("Error", false, false, false, true);
		
		TextNode btnOK = new TextNode("OK", true, true, false, true);
		btnOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		VBox vBoxMain = new VBox(labelContent, btnOK);
		setRoot(vBoxMain);
	}
	
	@Override
	public Object show(String... args) {
		labelContent.setText(args[0]);
		this.showAndWait();
		return null;
	}
}
