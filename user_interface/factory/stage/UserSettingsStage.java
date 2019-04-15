package user_interface.factory.stage;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import settings.SettingType;
import system.CommonUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.enums.ColorType;

import java.util.ArrayList;

public class UserSettingsStage extends Stage {
    public UserSettingsStage() {
        int spacing = CommonUtil.getPadding();
        VBox vBox = NodeFactory.getVBox(ColorType.DEF);
        vBox.setPadding(new Insets(spacing));
        vBox.setSpacing(spacing);
        ArrayList<Label> labels = new ArrayList<>();

        CommonUtil.settings.getSettingsList().forEach(setting -> {
            if (setting.getSettingType() == SettingType.USER) {
                Label label = NodeFactory.getLabel(setting.getSettingsEnum().getValue(), ColorType.DEF, ColorType.DEF);
                labels.add(label);
                TextField textField = new TextField(String.valueOf(setting.getValue()));
                textField.setFont(CommonUtil.getFont());
                textField.setBorder(NodeFactory.getBorder(1, 1, 1, 1));
                NodeFactory.addNodeToManager(textField, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
                HBox hBox = NodeFactory.getHBox(ColorType.DEF, label, textField);
                vBox.getChildren().add(hBox);
            }
        });

        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        Label lblCancel = NodeFactory.getLabel("Cancel", colorData);
        Label lblOK = NodeFactory.getLabel("OK", colorData);
        lblOK.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {

            }
        });
        HBox hBox = NodeFactory.getHBox(ColorType.DEF, lblCancel, lblOK);

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);
        borderPane.setCenter(vBox);
        borderPane.setBottom(hBox);

        this.initStyle(StageStyle.UNDECORATED);
        this.setScene(scene);
        this.setOnShown(event -> {
            int labelWidth = 0;
            for (Label label : labels) {
                if (labelWidth < label.getWidth()) {
                    labelWidth = (int) label.getWidth();
                }
            }
            for (Label label : labels) {
                label.setPrefWidth(labelWidth);
            }
            this.centerOnScreen();
        });
        this.show();
    }
}
