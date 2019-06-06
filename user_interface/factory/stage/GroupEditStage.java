package user_interface.factory.stage;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.CommonUtil;
import user_interface.utils.NodeUtil;
import user_interface.factory.base.TextNode;
import user_interface.utils.enums.ColorType;
import user_interface.utils.SizeUtil;
import user_interface.utils.StyleUtil;

public class GroupEditStage extends Stage {
    private final TextField nodeGroupEdit = new TextField();
    private final TextNode nodeGroup = new TextNode("Group", ColorType.DEF, ColorType.DEF);
    private final TextNode nodeOK = new TextNode("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
    private final TextNode nodeCancel = new TextNode("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

    private String result;

    public GroupEditStage(String group) {
        BorderPane borderPane = new BorderPane();

        result = group;

        nodeGroup.setPrefWidth(60);
        nodeGroupEdit.setText(group);
        nodeGroupEdit.setPrefWidth(200);
        nodeGroupEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
        nodeGroupEdit.setFont(CommonUtil.getFont());

        borderPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String newGroup = nodeGroupEdit.getText();
                if (this.isValid(newGroup)) {
                    result = newGroup;
                    this.close();
                } else {
                    //todo show a warning
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                this.close();
            }
        });

        NodeUtil.addToManager(nodeGroupEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        nodeGroupEdit.requestFocus();

        nodeOK.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                String newGroup = nodeGroupEdit.getText();
                if (this.isValid(newGroup)) {
                    result = newGroup;
                    this.close();
                } else {
                    //todo show a warning
                }
            }
        });
        nodeCancel.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.close();
            }
        });

        HBox hBoxGroup = NodeUtil.getHBox(ColorType.DEF, nodeGroup, nodeGroupEdit);
        double padding = SizeUtil.getGlobalSpacing();
        hBoxGroup.setPadding(new Insets(padding, padding, 0, 0));
        hBoxGroup.setSpacing(padding);

        HBox hBoxBottom = NodeUtil.getHBox(ColorType.DEF, nodeCancel, nodeOK);

        Scene scene = new Scene(borderPane);
        this.setTitle("Edit Group");
        borderPane.setCenter(hBoxGroup);
        borderPane.setBottom(hBoxBottom);

        borderPane.setBorder(NodeUtil.getBorder(1, 1, 1, 1));

        this.initStyle(StageStyle.UNDECORATED);
        setAlwaysOnTop(true);
        setScene(scene);
        setResizable(false);
        this.setOnShown(event -> {
            this.centerOnScreen();
            StyleUtil.applyStyle(this.getScene());
        });

        showAndWait();
    }

    private boolean isValid(String group) {
        if (group.isEmpty()) return false;
        return !group.contains(" - ");
    }

    public String getResult() {
        return result;
    }
}
