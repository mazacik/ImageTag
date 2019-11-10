package application.stage.template;

import application.component.simple.TextNode;
import application.stage.base.StageBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@SuppressWarnings("FieldCanBeLocal")
public class OkCancelStage extends StageBase {
	private boolean result;
	
	private TextNode labelContent;
	private TextNode btnOK;
	private TextNode btnCancel;
	
	public OkCancelStage() {
		super("Error");
		
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
		
		HBox hBox = new HBox(btnOK, btnCancel);
		hBox.setAlignment(Pos.CENTER);
		
		VBox vBoxMain = new VBox(labelContent, hBox);
		vBoxMain.setSpacing(5);
		vBoxMain.setPadding(new Insets(5));
		
		setRoot(vBoxMain);
	}
	
	@Override
	public Boolean show(String... args) {
		result = false;
		labelContent.setText(args[0]);
		this.showAndWait();
		return result;
	}
}
