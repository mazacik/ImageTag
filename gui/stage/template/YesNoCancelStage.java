package gui.stage.template;

import gui.component.simple.TextNode;
import gui.stage.base.StageBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class YesNoCancelStage extends StageBase {
	protected TextNode labelContent;
	protected VBox vBoxMain;
	protected Result result;
	
	public YesNoCancelStage() {
		super("Confirmation");
		
		labelContent = new TextNode("CONTENT_STRING", false, false, false, true);
		
		TextNode buttonPositive = new TextNode("Yes", true, true, false, true);
		buttonPositive.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = Result.YES;
			this.close();
		});
		TextNode buttonNegative = new TextNode("No", true, true, false, true);
		buttonNegative.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = Result.NO;
			this.close();
		});
		TextNode buttonCancel = new TextNode("Cancel", true, true, false, true);
		buttonCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = Result.CANCEL;
			this.close();
		});
		
		HBox hBoxButtons = new HBox(buttonPositive, buttonNegative, buttonCancel);
		hBoxButtons.setAlignment(Pos.CENTER);
		hBoxButtons.setSpacing(2);
		
		vBoxMain = new VBox(labelContent, hBoxButtons);
		vBoxMain.setSpacing(5);
		vBoxMain.setPadding(new Insets(5));
		vBoxMain.setAlignment(Pos.CENTER);
		
		setRoot(vBoxMain);
	}
	
	@Override
	public Result show(String... args) {
		result = Result.CANCEL;
		labelContent.setText(args[0]);
		this.showAndWait();
		return result;
	}
	
	public enum Result {
		YES(true),
		NO(false),
		CANCEL(false);
		
		private boolean booleanValue;
		
		Result(boolean booleanValue) {
			this.booleanValue = booleanValue;
		}
		
		public boolean getBooleanValue() {
			return booleanValue;
		}
	}
}
