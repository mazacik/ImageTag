package application.frontend.stage.template;

import application.frontend.component.NodeUtil;
import application.frontend.component.simple.EditNode;
import application.frontend.component.simple.TextNode;
import application.frontend.stage.StageManager;
import application.frontend.stage.base.StageBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@SuppressWarnings("FieldCanBeLocal")
public class GroupEditStage extends StageBase {
	private final EditNode nodeGroupEdit;
	private final TextNode nodeGroup;
	private final TextNode nodeOK;
	private final TextNode nodeCancel;
	
	private String result = "";
	
	public GroupEditStage() {
		super("Edit Group");
		
		nodeGroup = new TextNode("Group", false, false, false, false);
		nodeGroup.setPrefWidth(80);
		
		nodeGroupEdit = new EditNode();
		nodeGroupEdit.setPrefWidth(200);
		nodeGroupEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		
		nodeOK = new TextNode("OK", true, true, false, true);
		nodeOK.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			String newGroup = nodeGroupEdit.getText();
			if (this.isValid(newGroup)) {
				result = newGroup;
				this.close();
			} else {
				StageManager.getErrorStage().show("Cannot contain the following character sequence: \" - \"");
			}
		});
		
		nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		HBox hBoxGroup = new HBox(nodeGroup, nodeGroupEdit);
		hBoxGroup.setAlignment(Pos.CENTER);
		hBoxGroup.setPadding(new Insets(5));
		
		HBox hBoxBottom = new HBox(nodeCancel, nodeOK);
		hBoxBottom.setAlignment(Pos.CENTER);
		
		VBox vBoxMain = new VBox(hBoxGroup, hBoxBottom);
		vBoxMain.setSpacing(5);
		vBoxMain.setPadding(new Insets(5));
		vBoxMain.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				String newGroup = nodeGroupEdit.getText();
				if (this.isValid(newGroup)) {
					result = newGroup;
					this.close();
				} else {
					StageManager.getErrorStage().show("Cannot contain the following character sequence: \" - \"");
				}
			} else if (event.getCode() == KeyCode.ESCAPE) {
				this.close();
			}
		});
		setRoot(vBoxMain);
	}
	
	@Override
	public String show(String... args) {
		result = args[0];
		nodeGroupEdit.setText(result);
		nodeGroupEdit.requestFocus();
		
		showAndWait();
		
		return result;
	}
	
	private boolean isValid(String group) {
		if (group.isEmpty()) return false;
		return !group.contains(" - ");
	}
}