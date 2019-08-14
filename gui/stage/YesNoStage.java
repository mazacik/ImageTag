package application.gui.stage;

import application.gui.nodes.simple.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class YesNoStage extends StageBase {
	private TextNode labelContent;
	private boolean result;
	
	YesNoStage() {
		super("Confirmation");
		
		labelContent = new TextNode("Content", false, false, false, true);
		
		TextNode buttonPositive = new TextNode("Yes", true, true, false, true);
		buttonPositive.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = true;
			this.close();
		});
		TextNode buttonNegative = new TextNode("No");
		buttonNegative.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = false;
			this.close();
		});
		
		HBox hBox = new HBox(buttonPositive, buttonNegative);
		hBox.setAlignment(Pos.CENTER);
		hBox.setSpacing(2);
		
		VBox vBoxMain = new VBox(labelContent, hBox);
		vBoxMain.setSpacing(5);
		vBoxMain.setPadding(new Insets(5));
		
		setRoot(vBoxMain);
	}
	
	@Override
	public Boolean _show(String... args) {
		result = false;
		labelContent.setText(args[0]);
		this.showAndWait();
		return result;
	}
}
