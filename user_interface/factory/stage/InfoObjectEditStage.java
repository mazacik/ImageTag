package user_interface.factory.stage;

import database.object.TagObject;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.CommonUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.TitleBar;
import user_interface.factory.util.enums.ColorType;

public class InfoObjectEditStage extends Stage {
    private final TextField nodeGroupEdit = new TextField();
    private final TextField nodeNameEdit = new TextField();
    private final Label nodeGroup = NodeFactory.getLabel("Group", ColorType.DEF, ColorType.DEF);
    private final Label nodeName = NodeFactory.getLabel("Name", ColorType.DEF, ColorType.DEF);
    private final Label nodeOK = NodeFactory.getLabel("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
    private final Label nodeCancel = NodeFactory.getLabel("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

    private TagObject tagObject = null;

    public InfoObjectEditStage(TagObject tagObject) {
        BorderPane borderPane = new BorderPane();

        nodeGroup.setPrefWidth(60);
        nodeGroupEdit.setPrefWidth(200);
        nodeName.setPrefWidth(60);
        nodeNameEdit.setPrefWidth(200);

        nodeGroupEdit.setBorder(NodeFactory.getBorder(1, 1, 1, 1));
        nodeNameEdit.setBorder(NodeFactory.getBorder(1, 1, 1, 1));

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

        NodeFactory.addNodeToManager(nodeGroupEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        NodeFactory.addNodeToManager(nodeNameEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
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

        HBox hBoxGroup = NodeFactory.getHBox(ColorType.DEF, nodeGroup, nodeGroupEdit);
        HBox hBoxName = NodeFactory.getHBox(ColorType.DEF, nodeName, nodeNameEdit);
        VBox vBoxHelper = NodeFactory.getVBox(ColorType.DEF, hBoxGroup, hBoxName);
        double padding = CommonUtil.getPadding();
        vBoxHelper.setPadding(new Insets(padding, padding, 0, 0));
        vBoxHelper.setSpacing(padding);

        HBox hBoxBottom = NodeFactory.getHBox(ColorType.DEF, nodeCancel, nodeOK);

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

        borderPane.setBorder(NodeFactory.getBorder(1, 1, 1, 1));

        this.initStyle(StageStyle.UNDECORATED);
        setAlwaysOnTop(true);
        setScene(scene);
        setResizable(false);
        this.setOnShown(event -> {
            this.centerOnScreen();
            CommonUtil.updateNodeProperties(this.getScene());
        });

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
