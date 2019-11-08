package application.frontend.stage.template;

import application.backend.base.tag.Tag;
import application.backend.util.enums.Direction;
import application.frontend.component.NodeUtil;
import application.frontend.component.simple.CheckboxNode;
import application.frontend.component.simple.EditNode;
import application.frontend.component.simple.TextNode;
import application.frontend.stage.base.StageBase;
import application.main.InstanceCollector;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

@SuppressWarnings("FieldCanBeLocal")
public class TagEditStage extends StageBase implements InstanceCollector {
	private final EditNode edtGroup;
	private final EditNode edtName;
	private final TextNode lblGroup;
	private final TextNode lblName;
	private final TextNode btnOK;
	private final TextNode btnCancel;
	private final CheckboxNode cbApply;
	
	private Tag result = null;
	
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
				String group = edtGroup.getText();
				String name = edtName.getText();
				if (!group.isEmpty() && !name.isEmpty()) {
					result = new Tag(group, name);
				}
				this.close();
			}
		});
		btnCancel = new TextNode("Cancel", true, true, false, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
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
				String group = edtGroup.getText();
				String name = edtName.getText();
				if (!group.isEmpty() && !name.isEmpty()) {
					result = new Tag(group, name);
				}
				close();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				close();
			}
		});
		setRoot(vBoxMain);
	}
	
	@Override
	public Pair<Tag, Boolean> show(String... args) {
		edtGroup.requestFocus();
		cbApply.setSelected(false);
		
		if (args.length == 2) {
			setTitle("Edit Tag");
			result = tagListMain.getTag(args[0], args[1]);
			edtGroup.setText(result.getGroup());
			edtName.setText(result.getName());
		} else {
			setTitle("Create a New Tag");
			result = null;
			edtGroup.setText("");
			edtName.setText("");
		}
		
		showAndWait();
		
		return new Pair<>(result, cbApply.isSelected());
	}
}
