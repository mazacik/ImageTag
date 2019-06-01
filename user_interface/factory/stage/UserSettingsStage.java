package user_interface.factory.stage;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lifecycle.InstanceManager;
import system.CommonUtil;
import user_interface.factory.NodeUtil;
import user_interface.factory.base.TextNode;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.utils.SizeUtil;

import java.util.ArrayList;

public class UserSettingsStage extends Stage {
    public UserSettingsStage() {
        //todo finish this
        double spacing = SizeUtil.getGlobalSpacing();
        VBox vBox = NodeUtil.getVBox(ColorType.DEF);
        vBox.setPadding(new Insets(spacing));
        vBox.setSpacing(spacing);
        ArrayList<TextNode> labels = new ArrayList<>();

        InstanceManager.getSettings().getSettingsList().forEach(setting -> {
            TextNode label = new TextNode(setting.getSettingsEnum().toString(), ColorType.DEF, ColorType.DEF);
            labels.add(label);
            TextField textField = new TextField(String.valueOf(setting.getValue()));
            textField.setFont(CommonUtil.getFont());
            textField.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
            NodeUtil.addToManager(textField, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
            HBox hBox = NodeUtil.getHBox(ColorType.DEF, label, textField);
            vBox.getChildren().add(hBox);
        });

        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        TextNode lblCancel = new TextNode("Cancel", colorData);
        TextNode lblOK = new TextNode("OK", colorData);
        lblOK.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {

            }
        });
        lblCancel.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.close();
            }
        });
        HBox hBox = NodeUtil.getHBox(ColorType.DEF, lblCancel, lblOK);

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);
        borderPane.setCenter(vBox);
        borderPane.setBottom(hBox);

        this.setScene(scene);
        this.setOnShown(event -> {
            int labelWidth = 0;
            for (TextNode label : labels) {
                if (labelWidth < label.getWidth()) {
                    labelWidth = (int) label.getWidth();
                }
            }
            for (TextNode label : labels) {
                label.setPrefWidth(labelWidth);
            }
            this.centerOnScreen();
        });
    }
}
