package user_interface.node_factory.template.generic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import settings.SettingsNamespace;
import system.CommonUtil;
import user_interface.node_factory.NodeColorData;
import user_interface.node_factory.NodeFactory;
import user_interface.node_factory.utils.ColorType;
import user_interface.node_factory.utils.ColorUtil;

public class WindowOkCancel extends Stage {
    private boolean result = false;

    private Label labelContent;
    private Label buttonPositive;
    private Label buttonNegative;

    public WindowOkCancel(String content) {
        NodeColorData nodeColorData = new NodeColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        buttonPositive = NodeFactory.getLabel("OK", nodeColorData);
        buttonPositive.setOnMouseClicked(event -> {
            result = true;
            this.close();
        });
        buttonNegative = NodeFactory.getLabel("Cancel", nodeColorData);
        buttonNegative.setOnMouseClicked(event -> {
            result = false;
            this.close();
        });

        labelContent = NodeFactory.getLabel("", nodeColorData);
        labelContent.setText(content);
        labelContent.setFont(CommonUtil.getFont());
        int padding = CommonUtil.coreSettings.valueOf(SettingsNamespace.GLOBAL_PADDING);
        labelContent.setPadding(new Insets(0, 1.5 * padding, 0, 1.5 * padding));

        HBox hBox = new HBox(buttonPositive, buttonNegative);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(CommonUtil.coreSettings.valueOf(SettingsNamespace.GLOBAL_PADDING));

        VBox vBox = new VBox();
        vBox.getChildren().add(new TitleBar(this));
        vBox.getChildren().add(labelContent);
        vBox.getChildren().add(hBox);
        vBox.setBackground(ColorUtil.getBackgroundDef());
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
