package user_interface.factory.stage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.CommonUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.enums.ColorType;

public class NumberInputStage extends Stage {
    private int result = -1;

    public NumberInputStage(String content) {
        Label nodeContent = NodeFactory.getLabel(content, ColorType.DEF, ColorType.DEF);
        int padding = CommonUtil.getPadding();
        nodeContent.setPadding(new Insets(0, 1.5 * padding, 0, 1.5 * padding));

        TextField nodeTextField = new TextField();
        nodeTextField.setBorder(NodeFactory.getBorder(1, 1, 1, 1));
        nodeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("") && !isNumberPositive(newValue)) {
                nodeTextField.setText(oldValue);
            }
        });
        nodeTextField.setOnAction(event -> {
            result = Integer.valueOf(nodeTextField.getText());
            this.close();
        });
        NodeFactory.addNodeToManager(nodeTextField, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        Label buttonPositive = NodeFactory.getLabel("OK", colorData);
        Label buttonNegative = NodeFactory.getLabel("Cancel", colorData);
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

        VBox vBoxMain = NodeFactory.getVBox(ColorType.DEF);
        vBoxMain.setAlignment(Pos.CENTER);
        vBoxMain.setSpacing(padding);
        vBoxMain.setBorder(NodeFactory.getBorder(1, 1, 1, 1));

        Scene scene = new Scene(vBoxMain);
        vBoxMain.getChildren().add(nodeContent);
        vBoxMain.getChildren().add(nodeTextField);
        vBoxMain.getChildren().add(hBox);
        NodeFactory.addNodeToManager(vBoxMain, ColorType.DEF);

        this.setOnShown(event -> {
            this.centerOnScreen();
            CommonUtil.updateNodeProperties(this.getScene());
        });
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
