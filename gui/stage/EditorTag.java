package application.gui.stage;

import application.database.object.TagObject;
import application.gui.decorator.Decorator;
import application.gui.decorator.SizeUtil;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.NodeUtil;
import application.gui.nodes.custom.TitleBar;
import application.gui.nodes.simple.CheckBoxNode;
import application.gui.nodes.simple.EditNode;
import application.gui.nodes.simple.TextNode;
import application.main.Instances;
import application.misc.enums.Direction;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

@SuppressWarnings("FieldCanBeLocal")
public class EditorTag extends Stage implements StageBase {
	private final EditNode nodeGroupEdit;
	private final EditNode nodeNameEdit;
	private final TextNode nodeGroup;
	private final TextNode nodeName;
	private final TextNode nodeOK;
	private final TextNode nodeCancel;
	private final CheckBoxNode nodeApplyToSelection;
	
	private TagObject tagObject = null;
	private TitleBar titleBar;
	
	EditorTag() {
		titleBar = new TitleBar("");
		
		nodeGroup = new TextNode("Group", ColorType.DEF, ColorType.DEF);
		nodeGroup.setPrefWidth(80);
		nodeGroupEdit = new EditNode();
		nodeGroupEdit.setPrefWidth(200);
		nodeName = new TextNode("Name", ColorType.DEF, ColorType.DEF);
		nodeName.setPrefWidth(80);
		nodeNameEdit = new EditNode();
		nodeNameEdit.setPrefWidth(200);
		
		nodeGroupEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		nodeNameEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		
		nodeGroupEdit.setFont(Decorator.getFont());
		nodeNameEdit.setFont(Decorator.getFont());
		
		Decorator.manage(nodeGroupEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		Decorator.manage(nodeNameEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		
		nodeOK = new TextNode("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		nodeOK.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				String group = nodeGroupEdit.getText();
				String name = nodeNameEdit.getText();
				if (!group.isEmpty() && !name.isEmpty()) {
					tagObject = new TagObject(group, name);
				}
				this.close();
			}
		});
		nodeCancel = new TextNode("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		nodeCancel.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				this.close();
			}
		});
		
		HBox hBoxGroup = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeGroup, nodeGroupEdit);
		HBox hBoxName = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeName, nodeNameEdit);
		
		nodeApplyToSelection = new CheckBoxNode("Apply to selection?", Direction.RIGHT);
		HBox hBoxNodeApplyToSelection = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeApplyToSelection);
		hBoxNodeApplyToSelection.setAlignment(Pos.CENTER);
		
		HBox hBoxBottom = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeCancel, nodeOK);
		hBoxBottom.setAlignment(Pos.CENTER);
		
		double padding = SizeUtil.getGlobalSpacing();
		VBox vBoxHelper = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
		vBoxHelper.setSpacing(padding);
		vBoxHelper.setAlignment(Pos.CENTER);
		vBoxHelper.setPadding(new Insets(padding, padding, 0, 0));
		vBoxHelper.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		vBoxHelper.getChildren().addAll(titleBar, hBoxGroup, hBoxName, hBoxNodeApplyToSelection, hBoxBottom);
		vBoxHelper.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				String group = nodeGroupEdit.getText();
				String name = nodeNameEdit.getText();
				if (!group.isEmpty() && !name.isEmpty()) {
					tagObject = new TagObject(group, name);
				}
				close();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				close();
			}
		});
		
		this.setScene(new Scene(vBoxHelper));
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.initStyle(StageStyle.UNDECORATED);
	}
	
	@Override
	public Pair<TagObject, Boolean> _show(String... args) {
		nodeGroupEdit.requestFocus();
		nodeApplyToSelection.setSelected(false);
		
		if (args.length == 2) {
			titleBar.setTitle("Edit Tag");
			tagObject = Instances.getTagListMain().getTagObject(args[0], args[1]);
			nodeGroupEdit.setText(tagObject.getGroup());
			nodeNameEdit.setText(tagObject.getName());
		} else {
			titleBar.setTitle("Create Tag");
			tagObject = null;
			nodeGroupEdit.setText("");
			nodeNameEdit.setText("");
		}
		
		this.showAndWait();
		
		return new Pair<>(tagObject, nodeApplyToSelection.isSelected());
	}
}
