package ui.stage;

import base.tag.TagList;
import enums.Direction;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.decorator.Decorator;
import ui.node.NodeCheckbox;
import ui.node.NodeEdit;
import ui.node.NodeText;
import ui.override.HBox;
import ui.override.VBox;

public class StageEditTag extends StageBase {
	private static final NodeEdit editGroup;
	private static final NodeEdit editName;
	private static final NodeCheckbox nodeApplyToSelect;
	
	private static final VBox boxContent;
	private static final NodeText nodeOK;
	private static final NodeText nodeCancel;
	
	private static String group;
	private static String name;
	
	static {
		NodeText nodeGroup = new NodeText("Group", false, false, false, false);
		nodeGroup.setPrefWidth(80);
		editGroup = new NodeEdit();
		editGroup.setPrefWidth(200);
		
		NodeText nodeName = new NodeText("Name", false, false, false, false);
		nodeName.setPrefWidth(80);
		editName = new NodeEdit();
		editName.setPrefWidth(200);
		
		editGroup.setBorder(Decorator.getBorder(1, 1, 1, 1));
		editName.setBorder(Decorator.getBorder(1, 1, 1, 1));
		
		HBox hBoxGroup = new HBox(nodeGroup, editGroup);
		hBoxGroup.setAlignment(Pos.CENTER);
		
		HBox hBoxName = new HBox(nodeName, editName);
		hBoxName.setAlignment(Pos.CENTER);
		
		nodeApplyToSelect = new NodeCheckbox("Apply to selection?", Direction.RIGHT);
		
		boxContent = new VBox(hBoxGroup, hBoxName, nodeApplyToSelect);
		boxContent.setSpacing(5);
		boxContent.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				returnValue();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				getInstance().close();
			}
		});
		
		nodeOK = new NodeText("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, StageEditTag::returnValue);
		
		nodeCancel = new NodeText("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, getInstance()::close);
	}
	
	private static void returnValue() {
		if (checkEditNodes()) {
			group = editGroup.getText();
			name = editName.getText();
			getInstance().close();
		}
	}
	private static boolean checkEditNodes() {
		String group = editGroup.getText().trim();
		String name = editName.getText().trim();
		
		if (group.isEmpty()) {
			getInstance().setErrorMessage("Field \"Group\" cannot be empty.");
			return false;
		}
		if (name.isEmpty()) {
			getInstance().setErrorMessage("Field \"Name\" cannot be empty.");
			return false;
		}
		
		if (TagList.getMain().getTag(group, name) != null) {
			getInstance().setErrorMessage("Tag \"" + group + " - " + name + "\" already exists.");
			return false;
		}
		
		return true;
	}
	
	public static Result show(String... args) {
		if (args.length == 2) {
			getInstance().setTitle("Edit Tag");
			group = args[0];
			name = args[1];
		} else {
			getInstance().setTitle("Create a New Tag");
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
		
		getInstance().showAndWait();
		
		if (group.isEmpty() || name.isEmpty()) {
			return null;
		} else {
			return new Result(group, name, nodeApplyToSelect.isSelected());
		}
	}
	
	private StageEditTag() {
		super("", true, true, true);
		setRoot(boxContent);
		setButtons(nodeOK, nodeCancel);
	}
	private static class Loader {
		private static final StageEditTag INSTANCE = new StageEditTag();
	}
	public static StageEditTag getInstance() {
		return Loader.INSTANCE;
	}
	
	public static class Result {
		private String group;
		private String name;
		private boolean addToSelect;
		
		public Result(String group, String name, boolean addToSelect) {
			this.group = group;
			this.name = name;
			this.addToSelect = addToSelect;
		}
		
		public String getGroup() {
			return group;
		}
		public String getName() {
			return name;
		}
		public boolean isAddToSelect() {
			return addToSelect;
		}
	}
}
