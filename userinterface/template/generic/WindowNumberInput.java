package userinterface.template.generic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import settings.SettingsNamespace;
import utils.CommonUtil;

public class WindowNumberInput extends Stage {
    private int result = -1;

    private Label labelContent = new Label();
    private TextField textField = new TextField();
    private Label buttonPositive = CommonUtil.createNode("OK");
    private Label buttonNegative = CommonUtil.createNode("Cancel");

    public WindowNumberInput(String content) {
        buttonPositive.setOnMouseClicked(event -> {
            result = Integer.valueOf(textField.getText());
            this.close();
        });
        buttonNegative.setOnMouseClicked(event -> this.close());

        labelContent.setText(content);
        labelContent.setFont(CommonUtil.getFont());
        labelContent.setTextFill(CommonUtil.getTextColorDefault());

        HBox hBox = new HBox(buttonPositive, buttonNegative);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(CommonUtil.coreSettings.valueOf(SettingsNamespace.GLOBAL_PADDING));

        VBox vBox = new VBox();
        vBox.getChildren().add(labelContent);
        vBox.getChildren().add(textField);
        vBox.getChildren().add(hBox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(CommonUtil.coreSettings.valueOf(SettingsNamespace.GLOBAL_PADDING));
        vBox.setPadding(new Insets(2 * CommonUtil.coreSettings.valueOf(SettingsNamespace.GLOBAL_PADDING)));
        vBox.setBackground(CommonUtil.getBackgroundDefault());
        vBox.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("") && !isNumberPositive(newValue)) {
                textField.setText(oldValue);
            }
        });
        textField.setBackground(CommonUtil.getBackgroundAlternative());
        if (CommonUtil.isNightMode()) {
            textField.setStyle("-fx-border-color: gray; -fx-font-size: 14; -fx-text-fill: lightgray;");
        } else {
            textField.setStyle("-fx-border-color: black; -fx-font-size: 14;");
        }

        textField.setOnAction(event -> {
            result = Integer.valueOf(textField.getText());
            this.close();
        });

        this.initStyle(StageStyle.UNDECORATED);
        this.setScene(new Scene(vBox));
        this.setAlwaysOnTop(true);
        this.centerOnScreen();
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
