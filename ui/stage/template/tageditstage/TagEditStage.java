package ui.stage.template.tageditstage;

import base.tag.TagList;
import enums.Direction;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.NodeUtil;
import ui.component.simple.*;
import ui.stage.base.StageBase;

public class TagEditStage extends StageBase {
	private final EditNode editGroup;
	private final EditNode editName;
	
	private final CheckboxNode nodeApplyToSelect;
	
	private String group;
	private String name;
	
	public TagEditStage() {
		super("", true, true, true);
		
		TextNode nodeGroup = new TextNode("Group", false, false, false, false);
		nodeGroup.setPrefWidth(80);
		editGroup = new EditNode();
		editGroup.setPrefWidth(200);
		TextNode nodeName = new TextNode("Name", false, false, false, false);
		nodeName.setPrefWidth(80);
		editName = new EditNode();
		editName.setPrefWidth(200);
		
		editGroup.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		editName.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		
		TextNode nodeOK = new TextNode("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::returnValue);
		
		TextNode nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		HBox hBoxGroup = new HBox(nodeGroup, editGroup);
		hBoxGroup.setAlignment(Pos.CENTER);
		HBox hBoxName = new HBox(nodeName, editName);
		hBoxName.setAlignment(Pos.CENTER);
		
		nodeApplyToSelect = new CheckboxNode("Apply to selection?", Direction.RIGHT);
		
		VBox vBoxMain = new VBox(hBoxGroup, hBoxName, nodeApplyToSelect);
		vBoxMain.setSpacing(5);
		vBoxMain.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.returnValue();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				this.close();
			}
		});
		
		setRoot(vBoxMain);
		setButtons(nodeOK, nodeCancel);
	}
	
	private void returnValue() {
		if (this.checkEditNodes()) {
			this.group = editGroup.getText();
			this.name = editName.getText();
			this.close();
		}
	}
	private boolean checkEditNodes() {
		String group = editGroup.getText().trim();
		String name = editName.getText().trim();
		
		if (group.isEmpty()) {
			this.setErrorMessage("Field \"Group\" cannot be empty.");
			return false;
		}
		if (name.isEmpty()) {
			this.setErrorMessage("Field \"Name\" cannot be empty.");
			return false;
		}
		
		if (TagList.getMain().getTag(group, name) != null) {
			this.setErrorMessage("Tag \"" + group + " - " + name + "\" already exists.");
			return false;
		}
		
		return true;
	}
	
	@Override
	public TagEditStageResult show(String... args) {
		if (args.length == 2) {
			setTitle("Edit Tag");
			group = args[0];
			name = args[1];
		} else {
			setTitle("Create a New Tag");
			group = "";
			name = "";
		}
		
		editGroup.setText(group);
		editName.setText(name);
		
		if (group.isEmpty()) {
			editGroup.requestFocus();
		} else {
			editName.selectAll();
			editName.requestFocus();
		}
		
		nodeApplyToSelect.setSelected(false);
		
		this.showAndWait();
		
		return new TagEditStageResult(group, name, nodeApplyToSelect.isSelected());
	}
}
