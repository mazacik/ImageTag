package application.gui.stage;

import application.gui.decorator.Decorator;
import application.gui.decorator.SizeUtil;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.NodeUtil;
import application.gui.nodes.custom.TitleBar;
import application.gui.nodes.simple.EditNode;
import application.gui.nodes.simple.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@SuppressWarnings("FieldCanBeLocal")
public class EditorGroup extends Stage implements StageBase {
	private final EditNode nodeGroupEdit;
	private final TextNode nodeGroup;
	private final TextNode nodeOK;
	private final TextNode nodeCancel;
	
	private String result = "";
	private TitleBar titleBar;
	
	EditorGroup() {
		titleBar = new TitleBar("Edit Group");
		
		nodeGroup = new TextNode("Group", ColorType.DEF, ColorType.DEF);
		nodeGroup.setPrefWidth(80);
		
		nodeGroupEdit = new EditNode();
		nodeGroupEdit.setPrefWidth(200);
		nodeGroupEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		nodeGroupEdit.setFont(Decorator.getFont());
		Decorator.manage(nodeGroupEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		
		nodeOK = new TextNode("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		nodeOK.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				String newGroup = nodeGroupEdit.getText();
				if (this.isValid(newGroup)) {
					result = newGroup;
					this.close();
				} else {
					StageUtil.showStageError("Cannot contain the following character sequence: \" - \"");
				}
			}
		});
		nodeCancel = new TextNode("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		nodeCancel.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				this.close();
			}
		});
		
		double padding = SizeUtil.getGlobalSpacing();
		HBox hBoxGroup = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeGroup, nodeGroupEdit);
		hBoxGroup.setPadding(new Insets(padding, padding, 0, 0));
		hBoxGroup.setSpacing(padding);
		
		HBox hBoxBottom = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeCancel, nodeOK);
		
		VBox vBoxHelper = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
		vBoxHelper.setSpacing(padding);
		vBoxHelper.setAlignment(Pos.CENTER);
		vBoxHelper.setPadding(new Insets(padding, padding, 0, 0));
		vBoxHelper.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		vBoxHelper.getChildren().addAll(titleBar, hBoxGroup, hBoxBottom);
		vBoxHelper.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				String newGroup = nodeGroupEdit.getText();
				if (this.isValid(newGroup)) {
					result = newGroup;
					this.close();
				} else {
					StageUtil.showStageError("Cannot contain the following character sequence: \" - \"");
				}
			} else if (event.getCode() == KeyCode.ESCAPE) {
				this.close();
			}
		});
		
		this.setScene(new Scene(vBoxHelper));
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.initStyle(StageStyle.UNDECORATED);
	}
	
	@Override
	public String _show(String... args) {
		result = args[0];
		nodeGroupEdit.setText(result);
		nodeGroupEdit.requestFocus();
		this.showAndWait();
		return result;
	}
	
	private boolean isValid(String group) {
		if (group.isEmpty()) return false;
		return !group.contains(" - ");
	}
}
