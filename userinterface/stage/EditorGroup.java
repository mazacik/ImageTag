package userinterface.stage;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import userinterface.nodes.NodeUtil;
import userinterface.nodes.base.TextNode;
import userinterface.style.SizeUtil;
import userinterface.style.StyleUtil;
import userinterface.style.enums.ColorType;

@SuppressWarnings("FieldCanBeLocal")
public class EditorGroup extends Stage implements StageBase {
    private final TextField nodeGroupEdit;
    private final TextNode nodeGroup;
    private final TextNode nodeOK;
    private final TextNode nodeCancel;

    private String result = "";

    EditorGroup() {
        double padding = SizeUtil.getGlobalSpacing();

        nodeGroup = new TextNode("Group", ColorType.DEF, ColorType.DEF);
        nodeGroup.setPrefWidth(60);
        nodeGroupEdit = new TextField();
        nodeGroupEdit.setPrefWidth(200);
        nodeGroupEdit.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
        nodeGroupEdit.setFont(StyleUtil.getFont());
        nodeGroupEdit.requestFocus();
		StyleUtil.addToManager(nodeGroupEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        nodeOK = new TextNode("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        nodeOK.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                String newGroup = nodeGroupEdit.getText();
                if (this.isValid(newGroup)) {
                    result = newGroup;
                    this.close();
                } else {
					StageUtil.showStageError("tag group cannot contain the following character sequence: \" - \"");
                }
            }
        });
        nodeCancel = new TextNode("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        nodeCancel.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.close();
            }
        });

        HBox hBoxGroup = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeGroup, nodeGroupEdit);
        hBoxGroup.setPadding(new Insets(padding, padding, 0, 0));
        hBoxGroup.setSpacing(padding);

        HBox hBoxBottom = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, nodeCancel, nodeOK);

        BorderPane borderPane = new BorderPane();
        borderPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String newGroup = nodeGroupEdit.getText();
                if (this.isValid(newGroup)) {
                    result = newGroup;
                    this.close();
                } else {
					StageUtil.showStageError("tag group cannot contain the following character sequence: \" - \"");
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                this.close();
            }
        });
        borderPane.setCenter(hBoxGroup);
        borderPane.setBottom(hBoxBottom);
        borderPane.setBorder(NodeUtil.getBorder(1, 1, 1, 1));

        Scene scene = new Scene(borderPane);

        this.setTitle("Edit Tag Group");
        this.initStyle(StageStyle.UNDECORATED);
        this.setAlwaysOnTop(true);
        this.setScene(scene);
        this.setResizable(false);
        this.setOnShown(event -> this.centerOnScreen());
    }

    @Override
    public String _show(String... args) {
        result = args[0];
        nodeGroupEdit.setText(result);
        this.showAndWait();
        return result;
    }

    private boolean isValid(String group) {
        if (group.isEmpty()) return false;
        return !group.contains(" - ");
    }
}
