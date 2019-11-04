package application.frontend.stage.template;

import application.frontend.component.simple.TextNode;
import application.frontend.stage.base.StageBase;
import application.frontend.stage.base.StageBaseInterface;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ErrorStage extends StageBase implements StageBaseInterface {
	private TextNode labelContent;
	
	public ErrorStage() {
		super("Error");
		
		labelContent = new TextNode("Error", false, false, false, true);
		
		TextNode btnOK = new TextNode("OK", true, true, false, true);
		btnOK.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		VBox vBoxMain = new VBox(labelContent, btnOK);
		setRoot(vBoxMain);
	}
	
	@Override
	public Object _show(String... args) {
		labelContent.setText(args[0]);
		this.showAndWait();
		return null;
	}
}
