package user_interface.factory.stage;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.CommonUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.enums.ColorType;

public class GroupEditStage extends Stage {
    private final TextField nodeGroupEdit = new TextField();
    private final Label nodeGroup = NodeFactory.getLabel("Group", ColorType.DEF, ColorType.DEF);
    private final Label nodeOK = NodeFactory.getLabel("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
    private final Label nodeCancel = NodeFactory.getLabel("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

    private String result;

    public GroupEditStage(String group) {
        BorderPane borderPane = new BorderPane();

        result = group;

        nodeGroup.setPrefWidth(60);
        nodeGroupEdit.setText(group);
        nodeGroupEdit.setPrefWidth(200);
        nodeGroupEdit.setBorder(NodeFactory.getBorder(1, 1, 1, 1));
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

        NodeFactory.addNodeToManager(nodeGroupEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
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

        HBox hBoxGroup = NodeFactory.getHBox(ColorType.DEF, nodeGroup, nodeGroupEdit);
        double padding = CommonUtil.getPadding();
        hBoxGroup.setPadding(new Insets(padding, padding, 0, 0));
        hBoxGroup.setSpacing(padding);

        HBox hBoxBottom = NodeFactory.getHBox(ColorType.DEF, nodeCancel, nodeOK);

        Scene scene = new Scene(borderPane);
        this.setTitle("Edit Group");
        borderPane.setCenter(hBoxGroup);
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

    private boolean isValid(String group) {
        if (group.isEmpty()) return false;
        return !group.contains(" - ");
    }

    public String getResult() {
        return result;
    }
}
