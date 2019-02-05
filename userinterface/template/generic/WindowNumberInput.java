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

    public WindowNumberInput(String content) {
        VBox vBoxMain = new VBox();

        vBoxMain.getChildren().add(new TitleBar(this));

        Label labelContent = new Label();
        labelContent.setText(content);
        labelContent.setFont(CommonUtil.getFont());
        labelContent.setTextFill(CommonUtil.getTextColorDefault());
        int padding = CommonUtil.coreSettings.valueOf(SettingsNamespace.GLOBAL_PADDING);
        labelContent.setPadding(new Insets(0, 1.5 * padding, 0, 1.5 * padding));
        vBoxMain.getChildren().add(labelContent);

        TextField textField = new TextField();
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("") && !isNumberPositive(newValue)) {
                textField.setText(oldValue);
            }
        });
        textField.setBackground(CommonUtil.getBackgroundAlternative());
        textField.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));
        textField.setOnAction(event -> {
            result = Integer.valueOf(textField.getText());
            this.close();
        });
        if (CommonUtil.isNightMode()) {
            textField.setStyle("-fx-border-color: gray; -fx-font-size: 14; -fx-text-fill: lightgray;");
        } else {
            textField.setStyle("-fx-border-color: black; -fx-font-size: 14;");
        }
        vBoxMain.getChildren().add(textField);

        Label buttonPositive = CommonUtil.createNode("OK");
        Label buttonNegative = CommonUtil.createNode("Cancel");
        buttonPositive.setOnMouseClicked(event -> {
            String text = textField.getText();
            if (!text.isEmpty()) {
                result = Integer.valueOf(text);
                this.close();
            }
        });
        buttonNegative.setOnMouseClicked(event -> this.close());

        HBox hBox = new HBox(buttonPositive, buttonNegative);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(CommonUtil.coreSettings.valueOf(SettingsNamespace.GLOBAL_PADDING));
        vBoxMain.getChildren().add(hBox);

        vBoxMain.setAlignment(Pos.CENTER);
        vBoxMain.setSpacing(CommonUtil.coreSettings.valueOf(SettingsNamespace.GLOBAL_PADDING));
        vBoxMain.setBackground(CommonUtil.getBackgroundDefault());
        vBoxMain.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));

        this.initStyle(StageStyle.UNDECORATED);
        this.setScene(new Scene(vBoxMain));
        this.getScene().widthProperty().addListener(event -> textField.setMaxWidth(this.getScene().getWidth() - 4 * padding));
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
