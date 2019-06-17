package user_interface.factory.stage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import user_interface.factory.ColorData;
import user_interface.factory.base.TextNode;
import user_interface.utils.NodeUtil;
import user_interface.utils.SizeUtil;
import user_interface.utils.enums.ColorType;

public class NumberInputStage extends Stage {
    private int result = -1;

    public NumberInputStage(String content) {
        TextNode nodeContent = new TextNode(content, ColorType.DEF, ColorType.DEF);
        double padding = SizeUtil.getGlobalSpacing();
        nodeContent.setPadding(new Insets(0, 1.5 * padding, 0, 1.5 * padding));

        TextField nodeTextField = new TextField();
        nodeTextField.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
        nodeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("") && !isNumberPositive(newValue)) {
                nodeTextField.setText(oldValue);
            }
        });
        nodeTextField.setOnAction(event -> {
            result = Integer.valueOf(nodeTextField.getText());
            this.close();
        });
        NodeUtil.addToManager(nodeTextField, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        TextNode buttonPositive = new TextNode("OK", colorData);
        TextNode buttonNegative = new TextNode("Cancel", colorData);
        buttonPositive.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                String text = nodeTextField.getText();
                if (!text.isEmpty()) {
                    result = Integer.valueOf(text);
                    this.close();
                }
            }
        });
        buttonNegative.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.close();
            }
        });

        HBox hBox = new HBox(buttonPositive, buttonNegative);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(padding);

        VBox vBoxMain = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
        vBoxMain.setAlignment(Pos.CENTER);
        vBoxMain.setSpacing(padding);
        vBoxMain.setBorder(NodeUtil.getBorder(1, 1, 1, 1));

        Scene scene = new Scene(vBoxMain);
        vBoxMain.getChildren().add(nodeContent);
        vBoxMain.getChildren().add(nodeTextField);
        vBoxMain.getChildren().add(hBox);
        NodeUtil.addToManager(vBoxMain, ColorType.DEF);

        this.setOnShown(event -> this.centerOnScreen());
        this.initStyle(StageStyle.UNDECORATED);
        this.setScene(scene);
        this.getScene().widthProperty().addListener(event -> nodeTextField.setMaxWidth(this.getScene().getWidth() - 4 * padding));
        this.setAlwaysOnTop(true);
    }

    public int getResult() {
        this.showAndWait();
        return result;
    }

    public static boolean isNumber(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
    public static boolean isNumberPositive(String str) {
        if (!isNumber(str)) return false;
        return !(str.charAt(0) == '-');
    }
}
