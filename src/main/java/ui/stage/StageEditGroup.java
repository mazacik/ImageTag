package ui.stage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.decorator.Decorator;
import ui.node.NodeEdit;
import ui.node.NodeText;
import ui.override.HBox;

public class StageEditGroup extends StageBase {
	private static final NodeText nodeGroup;
	private static final NodeEdit nodeGroupEdit;
	
	private static final HBox boxContent;
	
	private static final NodeText nodeOK;
	private static final NodeText nodeCancel;
	
	private static String result;
	
	static {
		nodeGroup = new NodeText("Group", false, false, false, false);
		nodeGroup.setPrefWidth(80);
		
		nodeGroupEdit = new NodeEdit();
		nodeGroupEdit.setPrefWidth(200);
		nodeGroupEdit.setBorder(Decorator.getBorder(1, 1, 1, 1));
		
		boxContent = new HBox(nodeGroup, nodeGroupEdit);
		boxContent.setAlignment(Pos.CENTER);
		boxContent.setPadding(new Insets(5));
		boxContent.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				String newGroup = nodeGroupEdit.getText();
				if (checkEditNodes(newGroup)) {
					result = newGroup;
					getInstance().close();
				}
			} else if (event.getCode() == KeyCode.ESCAPE) {
				getInstance().close();
			}
		});
		
		nodeOK = new NodeText("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			String newGroup = nodeGroupEdit.getText();
			if (checkEditNodes(newGroup)) {
				result = newGroup;
				getInstance().close();
			}
		});
		
		nodeCancel = new NodeText("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, getInstance()::close);
	}
	
	public static boolean checkEditNodes(String... fields) {
		if (fields[0].trim().isEmpty()) {
			getInstance().setErrorMessage("Field \"" + nodeGroup.getText() + "\" cannot be empty.");
			return false;
		}
		return true;
	}
	
	public static String show(String... args) {
		result = args[0];
		
		nodeGroupEdit.setText(result);
		nodeGroupEdit.selectAll();
		nodeGroupEdit.requestFocus();
		
		getInstance().setErrorMessage("");
		getInstance().showAndWait();
		
		return result;
	}
	
	private StageEditGroup() {
		super("Edit Group", true, true, true);
		setRoot(boxContent);
		setButtons(nodeOK, nodeCancel);
	}
	private static class Loader {
		private static final StageEditGroup INSTANCE = new StageEditGroup();
	}
	public static StageEditGroup getInstance() {
		return Loader.INSTANCE;
	}
}
