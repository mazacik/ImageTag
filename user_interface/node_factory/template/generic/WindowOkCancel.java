package user_interface.node_factory.template.generic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import settings.SettingsEnum;
import system.CommonUtil;
import user_interface.node_factory.NodeFactory;
import user_interface.node_factory.utils.ColorType;
import user_interface.node_factory.utils.ColorUtil;

public class WindowOkCancel extends Stage {
    private boolean result = false;

    private Label labelContent;
    private Label buttonPositive;
    private Label buttonNegative;

    public WindowOkCancel(String content) {
        buttonPositive = NodeFactory.getLabel("OK", ColorType.DEF, ColorType.DEF);
        buttonPositive.setOnMouseClicked(event -> {
            result = true;
            this.close();
        });
        buttonNegative = NodeFactory.getLabel("Cancel", ColorType.DEF, ColorType.DEF);
        buttonNegative.setOnMouseClicked(event -> {
            result = false;
            this.close();
        });

        labelContent = NodeFactory.getLabel("", ColorType.DEF, ColorType.DEF);
        labelContent.setText(content);
        labelContent.setFont(CommonUtil.getFont());
        int padding = CommonUtil.coreSettings.valueOf(SettingsEnum.GLOBAL_PADDING);
        labelContent.setPadding(new Insets(0, 1.5 * padding, 0, 1.5 * padding));

        HBox hBox = NodeFactory.getHBox(ColorType.DEF, buttonPositive, buttonNegative);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(CommonUtil.coreSettings.valueOf(SettingsEnum.GLOBAL_PADDING));

        VBox vBox = NodeFactory.getVBox(ColorType.DEF);
        vBox.getChildren().add(new TitleBar(this));
        vBox.getChildren().add(labelContent);
        vBox.getChildren().add(hBox);
        vBox.setBackground(ColorUtil.getBackgroundDef());
        vBox.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));

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
