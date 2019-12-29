package ui.stage.template;

import javafx.scene.Scene;
import ui.component.simple.TextNode;
import ui.component.simple.VBox;
import ui.stage.TitleBar;
import ui.stage.base.StageBase;
import ui.stage.base.StageBaseInterface;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ErrorStage extends StageBase implements StageBaseInterface {
	private VBox mainBox = new VBox();
	
	private TextNode labelContent;
	
	public ErrorStage() {
		super("", false, true, true);
		
		labelContent = new TextNode("", false, false, false, true);
		
		TextNode btnOK = new TextNode("OK", true, true, false, true);
		btnOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		mainBox.getChildren().addAll(new TitleBar(this), labelContent, btnOK);
		
		this.setScene(new Scene(mainBox));
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
