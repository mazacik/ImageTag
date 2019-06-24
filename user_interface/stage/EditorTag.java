package user_interface.stage;

import database.object.TagObject;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lifecycle.InstanceManager;
import user_interface.nodes.NodeUtil;
import user_interface.nodes.base.CheckBoxNode;
import user_interface.nodes.base.TextNode;
import user_interface.nodes.node.TitleBar;
import user_interface.style.SizeUtil;
import user_interface.style.StyleUtil;
import user_interface.style.enums.ColorType;

@SuppressWarnings("FieldCanBeLocal")
public class EditorTag extends Stage implements StageBase {
    private final TextField nodeGroupEdit = new TextField();
    private final TextField nodeNameEdit = new TextField();
    private final TextNode nodeGroup = new TextNode("Group", ColorType.DEF, ColorType.DEF);
    private final TextNode nodeName = new TextNode("Name", ColorType.DEF, ColorType.DEF);
    private final TextNode nodeOK = new TextNode("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
    private final TextNode nodeCancel = new TextNode("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
	private final CheckBoxNode nodeAddToSelection = new CheckBoxNode("Apply to selection?"); //todo implement me

    private TagObject tagObject = null;
	private TitleBar titleBar;
	
	EditorTag() {
        nodeGroup.setPrefWidth(60);
        nodeGroupEdit.setPrefWidth(200);
        nodeName.setPrefWidth(60);
        nodeNameEdit.setPrefWidth(200);

        nodeGroupEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
        nodeNameEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));

        nodeGroupEdit.setFont(StyleUtil.getFont());
        nodeNameEdit.setFont(StyleUtil.getFont());

        NodeUtil.addToManager(nodeGroupEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        NodeUtil.addToManager(nodeNameEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);

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
        VBox vBoxHelper = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF, hBoxGroup, hBoxName, nodeAddToSelection);
        double padding = SizeUtil.getGlobalSpacing();
        vBoxHelper.setPadding(new Insets(padding, padding, 0, 0));
        vBoxHelper.setSpacing(padding);

        HBox hBoxBottom = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeCancel, nodeOK);
		
		BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);
		titleBar = new TitleBar(this.getScene(), "");
		
		borderPane.setTop(titleBar);
        borderPane.setCenter(vBoxHelper);
        borderPane.setBottom(hBoxBottom);
		borderPane.setOnKeyPressed(event -> {
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
		borderPane.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		
		this.setScene(scene);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.initStyle(StageStyle.UNDECORATED);
	}
	
	@Override
	public TagObject _show(String... args) {
		nodeGroupEdit.requestFocus();
		
		if (args.length == 2) {
			titleBar.setTitle("Edit Tag");
			nodeGroupEdit.setText(tagObject.getGroup());
			nodeNameEdit.setText(tagObject.getName());
			tagObject = InstanceManager.getTagListMain().getTagObject(args[0], args[1]);
		} else {
			titleBar.setTitle("Create Tag");
			nodeGroupEdit.setText("");
			nodeNameEdit.setText("");
			tagObject = null;
		}
		
		this.showAndWait();
		
		return tagObject;
    }
}
