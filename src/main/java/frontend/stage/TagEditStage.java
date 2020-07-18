package frontend.stage;

import frontend.node.EditNode;
import frontend.node.override.HBox;
import frontend.node.textnode.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.Main;

public class TagEditStage extends BaseStage {
	private final EditNode editNode;
	private String returnValue;
	
	public TagEditStage() {
		super("Tag Editor", 0.15);
		
		editNode = new EditNode();
		returnValue = "";
		
		HBox boxMain = new HBox(editNode);
		boxMain.setAlignment(Pos.CENTER);
		boxMain.setPadding(new Insets(5));
		boxMain.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				closeOk();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				closeCancel();
			}
		});
		
		TextNode nodeOK = new TextNode("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::closeOk);
		
		TextNode nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::closeCancel);
		
		setRoot(boxMain);
		setButtons(nodeOK, nodeCancel);
	}
	
	public String create() {
		return edit("");
	}
	public String edit(String tag) {
		editNode.setText(tag);
		
		this.showAndWait();
		
		return returnValue;
	}
	
	private void closeOk() {
		String value = editNode.getText().trim();
		
		// can't be empty
		if (value.isEmpty()) {
			return;
		}
		
		// can't already exist
		String valueLowerCase = value.toLowerCase();
		for (String tag : Main.TAGLIST) {
			if (valueLowerCase.equals(tag.toLowerCase())) {
				return;
			}
		}
		
		this.returnValue = value;
		this.close();
	}
	private void closeCancel() {
		this.returnValue = "";
		this.close();
	}
}
