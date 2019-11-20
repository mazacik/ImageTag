package gui.stage.template.tageditstage;

import gui.component.simple.*;
import gui.stage.base.StageBase;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.InstanceCollector;
import tools.NodeUtil;
import tools.enums.Direction;

@SuppressWarnings("FieldCanBeLocal")
public class TagEditStage extends StageBase implements InstanceCollector {
	private final EditNode edtGroup;
	private final EditNode edtName;
	private final TextNode lblGroup;
	private final TextNode lblName;
	private final TextNode btnOK;
	private final TextNode btnCancel;
	private final CheckboxNode cbApply;
	
	private String group;
	private String name;
	
	public TagEditStage() {
		super("", true, true, true);
		
		lblGroup = new TextNode("Group", false, false, false, false);
		lblGroup.setPrefWidth(80);
		edtGroup = new EditNode();
		edtGroup.setPrefWidth(200);
		lblName = new TextNode("Name", false, false, false, false);
		lblName.setPrefWidth(80);
		edtName = new EditNode();
		edtName.setPrefWidth(200);
		
		edtGroup.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		edtName.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		
		btnOK = new TextNode("OK", true, true, false, true);
		btnOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			String _group = edtGroup.getText();
			String _name = edtName.getText();
			if (this.checkFieldValidity(_group, _name)) {
				group = _group;
				name = _name;
				this.close();
			}
		});
		
		btnCancel = new TextNode("Cancel", true, true, false, true);
		btnCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		HBox hBoxGroup = new HBox(lblGroup, edtGroup);
		hBoxGroup.setAlignment(Pos.CENTER);
		HBox hBoxName = new HBox(lblName, edtName);
		hBoxName.setAlignment(Pos.CENTER);
		
		cbApply = new CheckboxNode("Apply to selection?", Direction.RIGHT);
		
		VBox vBoxMain = new VBox(hBoxGroup, hBoxName, cbApply);
		vBoxMain.setSpacing(5);
		vBoxMain.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				String _group = edtGroup.getText();
				String _name = edtName.getText();
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
		setButtons(btnOK, btnCancel);
	}
	
	public boolean checkFieldValidity(String... fields) {
		if (fields[0].trim().isEmpty()) {
			this.setErrorMessage("Field \"" + lblGroup.getText() + "\" cannot be empty.");
			return false;
		}
		if (fields[1].trim().isEmpty()) {
			this.setErrorMessage("Field \"" + lblName.getText() + "\" cannot be empty.");
			return false;
		}
		if (fields[0].contains(" - ")) {
			this.setErrorMessage("Field \"" + lblGroup.getText() + "\" cannot contain \" - \".");
			return false;
		}
		if (fields[1].contains(" - ")) {
			this.setErrorMessage("Field \"" + lblName.getText() + "\" cannot contain \" - \".");
			return false;
		}
		return true;
	}
	
	@Override
	public TagEditStageResult show(String... args) {
		edtGroup.requestFocus();
		cbApply.setSelected(false);
		
		if (args.length == 2) {
			setTitle("Edit Tag");
			group = args[0];
			name = args[1];
			edtGroup.setText(group);
			edtName.setText(name);
		} else {
			setTitle("Create a New Tag");
			group = "";
			name = "";
			edtGroup.setText(group);
			edtName.setText(name);
		}
		
		showAndWait();
		
		return new TagEditStageResult(group, name, cbApply.isSelected());
	}
}
