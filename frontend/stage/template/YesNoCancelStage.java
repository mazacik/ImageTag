package application.frontend.stage.template;

import application.frontend.component.simple.TextNode;
import application.frontend.stage.base.StageBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class YesNoCancelStage extends StageBase {
	private TextNode labelContent;
	private Result result;
	
	public YesNoCancelStage() {
		super("Confirmation");
		
		labelContent = new TextNode("CONTENT_STRING", false, false, false, true);
		
		TextNode buttonPositive = new TextNode("Yes", true, true, false, true);
		buttonPositive.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = Result.YES;
			this.close();
		});
		TextNode buttonNegative = new TextNode("No", true, true, false, true);
		buttonNegative.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = Result.NO;
			this.close();
		});
		TextNode buttonCancel = new TextNode("Cancel", true, true, false, true);
		buttonCancel.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = Result.CANCEL;
			this.close();
		});
		
		HBox hBox = new HBox(buttonPositive, buttonNegative, buttonCancel);
		hBox.setAlignment(Pos.CENTER);
		hBox.setSpacing(2);
		
		VBox vBoxMain = new VBox(labelContent, hBox);
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
		YES,
		NO,
		CANCEL
	}
}
