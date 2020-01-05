package ui.stage.template;

import ui.component.simple.EditNode;
import ui.component.simple.TextNode;
import ui.stage.base.StageBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.component.simple.HBox;
import ui.NodeUtil;

@SuppressWarnings("FieldCanBeLocal")
public class GroupEditStage extends StageBase {
	private final EditNode nodeGroupEdit;
	private final TextNode nodeGroup;
	private final TextNode nodeOK;
	private final TextNode nodeCancel;
	
	private String result = "";
	
	public GroupEditStage() {
		super("Edit Group", true, true, true);
		
		nodeGroup = new TextNode("Group", false, false, false, false);
		nodeGroup.setPrefWidth(80);
		
		nodeGroupEdit = new EditNode();
		nodeGroupEdit.setPrefWidth(200);
		nodeGroupEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		
		nodeOK = new TextNode("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			String newGroup = nodeGroupEdit.getText();
			if (this.checkFieldValidity(newGroup)) {
				result = newGroup;
				this.close();
			}
		});
		
		nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		HBox hBoxMain = new HBox(nodeGroup, nodeGroupEdit);
		hBoxMain.setAlignment(Pos.CENTER);
		hBoxMain.setPadding(new Insets(5));
		hBoxMain.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				String newGroup = nodeGroupEdit.getText();
				if (this.checkFieldValidity(newGroup)) {
					result = newGroup;
					this.close();
				}
			} else if (event.getCode() == KeyCode.ESCAPE) {
				this.close();
			}
		});
		
		setRoot(hBoxMain);
		setButtons(nodeOK, nodeCancel);
	}
	
	public boolean checkFieldValidity(String... fields) {
		if (fields[0].trim().isEmpty()) {
			this.setErrorMessage("Field \"" + nodeGroup.getText() + "\" cannot be empty.");
			return false;
		}
		return true;
	}
	
	@Override
	public String show(String... args) {
		result = args[0];
		
		nodeGroupEdit.setText(result);
		nodeGroupEdit.selectAll();
		nodeGroupEdit.requestFocus();
		
		this.setErrorMessage("");
		this.showAndWait();
		
		return result;
	}
}
