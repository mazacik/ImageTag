package user_interface.factory.stage;

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
import system.CommonUtil;
import user_interface.factory.NodeUtil;
import user_interface.factory.base.TextNode;
import user_interface.factory.node.TitleBar;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.utils.SizeUtil;

public class InfoObjectEditStage extends Stage {
    private final TextField nodeGroupEdit = new TextField();
    private final TextField nodeNameEdit = new TextField();
    private final TextNode nodeGroup = new TextNode("Group", ColorType.DEF, ColorType.DEF);
    private final TextNode nodeName = new TextNode("Name", ColorType.DEF, ColorType.DEF);
    private final TextNode nodeOK = new TextNode("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
    private final TextNode nodeCancel = new TextNode("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

    private TagObject tagObject = null;

    public InfoObjectEditStage(TagObject tagObject) {
        BorderPane borderPane = new BorderPane();

        nodeGroup.setPrefWidth(60);
        nodeGroupEdit.setPrefWidth(200);
        nodeName.setPrefWidth(60);
        nodeNameEdit.setPrefWidth(200);

        nodeGroupEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
        nodeNameEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));

        nodeGroupEdit.setFont(CommonUtil.getFont());
        nodeNameEdit.setFont(CommonUtil.getFont());

        borderPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                getValue();
                close();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                close();
            }
        });

        NodeUtil.addToManager(nodeGroupEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        NodeUtil.addToManager(nodeNameEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        nodeGroupEdit.requestFocus();


        nodeOK.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.getValue();
                this.close();
            }
        });
        nodeCancel.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.close();
            }
        });

        HBox hBoxGroup = NodeUtil.getHBox(ColorType.DEF, nodeGroup, nodeGroupEdit);
        HBox hBoxName = NodeUtil.getHBox(ColorType.DEF, nodeName, nodeNameEdit);
        VBox vBoxHelper = NodeUtil.getVBox(ColorType.DEF, hBoxGroup, hBoxName);
        double padding = SizeUtil.getGlobalSpacing();
        vBoxHelper.setPadding(new Insets(padding, padding, 0, 0));
        vBoxHelper.setSpacing(padding);

        HBox hBoxBottom = NodeUtil.getHBox(ColorType.DEF, nodeCancel, nodeOK);

        Scene scene = new Scene(borderPane);
        if (tagObject != null) {
            borderPane.setTop(new TitleBar(scene, "Edit Tag"));
            nodeGroupEdit.setText(tagObject.getGroup());
            nodeNameEdit.setText(tagObject.getName());
        } else {
            borderPane.setTop(new TitleBar(scene, "Create a new Tag"));
        }

        borderPane.setCenter(vBoxHelper);
        borderPane.setBottom(hBoxBottom);

        borderPane.setBorder(NodeUtil.getBorder(1, 1, 1, 1));

        this.initStyle(StageStyle.UNDECORATED);
        setAlwaysOnTop(true);
        setScene(scene);
        setResizable(false);

        CommonUtil.updateNodeProperties(this.getScene());

        showAndWait();
    }
    public InfoObjectEditStage() {
        this(null);
    }

    public TagObject getResult() {
        return tagObject;
    }
    private void getValue() {
        String group = nodeGroupEdit.getText();
        String name = nodeNameEdit.getText();
        if (!group.isEmpty() && !name.isEmpty()) {
            tagObject = new TagObject(group, name);
        }
    }
}
