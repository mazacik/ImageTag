package ui.stage.template.tageditstage;

import ui.component.simple.*;
import ui.stage.base.StageBase;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.NodeUtil;
import enums.Direction;

@SuppressWarnings("FieldCanBeLocal")
public class TagEditStage extends StageBase {
	private final EditNode nodeGroupEdit;
	private final EditNode nodeNameEdit;
	private final TextNode nodeGroup;
	private final TextNode nodeName;
	private final TextNode nodeOK;
	private final TextNode nodeCancel;
	private final CheckboxNode nodeApply;
	
	private String group;
	private String name;
	
	public TagEditStage() {
		super("", true, true, true);
		
		nodeGroup = new TextNode("Group", false, false, false, false);
		nodeGroup.setPrefWidth(80);
		nodeGroupEdit = new EditNode();
		nodeGroupEdit.setPrefWidth(200);
		nodeName = new TextNode("Name", false, false, false, false);
		nodeName.setPrefWidth(80);
		nodeNameEdit = new EditNode();
		nodeNameEdit.setPrefWidth(200);
		
		nodeGroupEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		nodeNameEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		
		nodeOK = new TextNode("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			String _group = nodeGroupEdit.getText();
			String _name = nodeNameEdit.getText();
			if (this.checkFieldValidity(_group, _name)) {
				group = _group;
				name = _name;
				this.close();
			}
		});
		
		nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		HBox hBoxGroup = new HBox(nodeGroup, nodeGroupEdit);
		hBoxGroup.setAlignment(Pos.CENTER);
		HBox hBoxName = new HBox(nodeName, nodeNameEdit);
		hBoxName.setAlignment(Pos.CENTER);
		
		nodeApply = new CheckboxNode("Apply to selection?", Direction.RIGHT);
		
		VBox vBoxMain = new VBox(hBoxGroup, hBoxName, nodeApply);
		vBoxMain.setSpacing(5);
		vBoxMain.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				String _group = nodeGroupEdit.getText();
				String _name = nodeNameEdit.getText();
				if (this.checkFieldValidity(_group, _name)) {
					group = _group;
					name = _name;
					this.close();
				}
			} else if (event.getCode() == KeyCode.ESCAPE) {
				this.close();
			}
		});
		
		setRoot(vBoxMain);
		setButtons(nodeOK, nodeCancel);
	}
	
	public boolean checkFieldValidity(String... fields) {
		if (fields[0].trim().isEmpty()) {
			this.setErrorMessage("Field \"" + nodeGroup.getText() + "\" cannot be empty.");
			return false;
		}
		if (fields[1].trim().isEmpty()) {
			this.setErrorMessage("Field \"" + nodeName.getText() + "\" cannot be empty.");
			return false;
		}
		if (fields[0].contains(" - ")) {
			this.setErrorMessage("Field \"" + nodeGroup.getText() + "\" cannot contain \" - \".");
			return false;
		}
		if (fields[1].contains(" - ")) {
			this.setErrorMessage("Field \"" + nodeName.getText() + "\" cannot contain \" - \".");
			return false;
		}
		return true;
	}
	
	@Override
	public TagEditStageResult show(String... args) {
		nodeGroupEdit.requestFocus();
		nodeApply.setSelected(false);
		
		if (args.length == 2) {
			setTitle("Edit Tag");
			group = args[0];
			name = args[1];
			nodeGroupEdit.setText(group);
			nodeNameEdit.setText(name);
		} else {
			setTitle("Create a New Tag");
			group = "";
			name = "";
			nodeGroupEdit.setText(group);
			nodeNameEdit.setText(name);
		}
		
		nodeNameEdit.selectAll();
		nodeNameEdit.requestFocus();
		
		this.showAndWait();
		
		return new TagEditStageResult(group, name, nodeApply.isSelected());
	}
}
