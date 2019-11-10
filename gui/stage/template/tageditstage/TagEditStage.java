package gui.stage.template.tageditstage;

import gui.component.simple.CheckboxNode;
import gui.component.simple.EditNode;
import gui.component.simple.TextNode;
import gui.stage.base.StageBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
		btnOK.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				//todo field validity checks
				group = edtGroup.getText();
				name = edtName.getText();
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
		HBox hBoxHelper1 = new HBox(cbApply);
		hBoxHelper1.setAlignment(Pos.CENTER);
		
		HBox hBoxBottom = new HBox(btnCancel, btnOK);
		hBoxBottom.setAlignment(Pos.CENTER);
		
		VBox vBoxMain = new VBox(hBoxGroup, hBoxName, hBoxHelper1, hBoxBottom);
		vBoxMain.setSpacing(5);
		vBoxMain.setPadding(new Insets(5));
		vBoxMain.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				group = edtGroup.getText();
				name = edtName.getText();
				close();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				close();
			}
		});
		setRoot(vBoxMain);
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
