package userinterface.template.generic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import settings.SettingsNamespace;
import utils.CommonUtil;

public class WindowOkCancel extends Stage {
    private boolean result = false;

    private Label labelContent = new Label();
    private Label buttonPositive = CommonUtil.createNode("OK");
    private Label buttonNegative = CommonUtil.createNode("Cancel");

    public WindowOkCancel(String content) {
        buttonPositive.setOnMouseClicked(event -> {
            result = true;
            this.close();
        });
        buttonNegative.setOnMouseClicked(event -> {
            result = false;
            this.close();
        });

        labelContent.setText(content);
        labelContent.setFont(CommonUtil.getFont());
        labelContent.setTextFill(CommonUtil.getTextColorDefault());
        int padding = CommonUtil.coreSettings.valueOf(SettingsNamespace.GLOBAL_PADDING);
        labelContent.setPadding(new Insets(0, 1.5 * padding, 0, 1.5 * padding));

        HBox hBox = new HBox(buttonPositive, buttonNegative);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(CommonUtil.coreSettings.valueOf(SettingsNamespace.GLOBAL_PADDING));

        VBox vBox = new VBox();
        vBox.getChildren().add(new TitleBar(this));
        vBox.getChildren().add(labelContent);
        vBox.getChildren().add(hBox);
        vBox.setBackground(CommonUtil.getBackgroundDefault());
        vBox.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));

        this.initStyle(StageStyle.UNDECORATED);

        this.setScene(new Scene(vBox));
        this.setAlwaysOnTop(true);
        this.centerOnScreen();
    }

    public boolean getResult() {
        this.showAndWait();
        return result;
    }
}
