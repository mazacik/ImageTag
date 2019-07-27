package userinterface.stage;

import database.object.TagObject;
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
import main.InstanceManager;
import userinterface.nodes.NodeUtil;
import userinterface.nodes.base.CheckBoxNode;
import userinterface.nodes.base.EditNode;
import userinterface.nodes.base.TextNode;
import userinterface.nodes.node.TitleBar;
import userinterface.style.SizeUtil;
import userinterface.style.StyleUtil;
import userinterface.style.enums.ColorType;
import utils.enums.Direction;

@SuppressWarnings("FieldCanBeLocal")
public class EditorTag extends Stage implements StageBase {
	private final EditNode nodeGroupEdit = new EditNode();
	private final EditNode nodeNameEdit = new EditNode();
	private final TextNode nodeGroup = new TextNode("Group", ColorType.DEF, ColorType.DEF);
	private final TextNode nodeName = new TextNode("Name", ColorType.DEF, ColorType.DEF);
	private final TextNode nodeOK = new TextNode("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
	private final TextNode nodeCancel = new TextNode("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
	private final CheckBoxNode nodeAddToSelection = new CheckBoxNode("Apply to selection?", Direction.RIGHT);
	
	private TagObject tagObject = null;
	private TitleBar titleBar;
	
	EditorTag() {
		titleBar = new TitleBar("");
		
		nodeGroup.setPrefWidth(80);
		nodeGroupEdit.setPrefWidth(200);
		nodeName.setPrefWidth(80);
		nodeNameEdit.setPrefWidth(200);
		
		nodeGroupEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		nodeNameEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		
		nodeGroupEdit.setFont(StyleUtil.getFont());
		nodeNameEdit.setFont(StyleUtil.getFont());
		
		StyleUtil.addToManager(nodeGroupEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		StyleUtil.addToManager(nodeNameEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		
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
		nodeCancel.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				this.close();
			}
		});
		
		HBox hBoxGroup = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeGroup, nodeGroupEdit);
		HBox hBoxName = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeName, nodeNameEdit);
		
		HBox hBoxNodeApplyToSelection = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeAddToSelection);
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
		
		StyleUtil.applyStyle(nodeGroupEdit);
		StyleUtil.applyStyle(nodeNameEdit);
	}
	
	@Override
	public Pair<TagObject, Boolean> _show(String... args) {
		nodeGroupEdit.requestFocus();
		nodeAddToSelection.setSelected(false);
		
		if (args.length == 2) {
			titleBar.setTitle("Edit Tag");
			tagObject = InstanceManager.getTagListMain().getTagObject(args[0], args[1]);
			nodeGroupEdit.setText(tagObject.getGroup());
			nodeNameEdit.setText(tagObject.getName());
		} else {
			titleBar.setTitle("Create Tag");
			tagObject = null;
			nodeGroupEdit.setText("");
			nodeNameEdit.setText("");
		}
		
		this.showAndWait();
		
		return new Pair<>(tagObject, nodeAddToSelection.isSelected());
	}
}
