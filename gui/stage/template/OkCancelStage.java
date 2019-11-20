package gui.stage.template;

import gui.component.simple.TextNode;
import gui.component.simple.template.ButtonTemplates;
import gui.stage.base.StageBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

@SuppressWarnings("FieldCanBeLocal")
public class OkCancelStage extends StageBase {
	private boolean result;
	
	private TextNode labelContent;
	private TextNode btnOK;
	private TextNode btnCancel;
	
	public OkCancelStage() {
		super("Error", false, true, true);
		
		labelContent = new TextNode("Content", false, false, false, true);
		
		btnOK = new TextNode("OK", true, true, false, true);
		btnOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = true;
			this.close();
		});
		
		btnCancel = new TextNode("Cancel", true, true, false, true);
		btnCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = false;
			this.close();
		});
		
		setRoot(labelContent);
		setButtons(btnOK, btnCancel);
	}
	
	@Override
	public Boolean show(String... args) {
		result = false;
		labelContent.setText(args[0]);
		this.showAndWait();
		return result;
	}
}
