package user_interface.factory.stage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.CommonUtil;
import user_interface.factory.NodeUtil;
import user_interface.factory.base.TextNode;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.utils.SizeUtil;

public class OkCancelStage extends Stage {
    private boolean result = false;

    private TextNode labelContent;
    private TextNode buttonPositive;
    private TextNode buttonNegative;

    public OkCancelStage(String content) {
        buttonPositive = new TextNode("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        buttonPositive.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                result = true;
                this.close();
            }
        });
        buttonNegative = new TextNode("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        buttonNegative.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                result = false;
                this.close();
            }
        });

        double padding = SizeUtil.getGlobalSpacing();
        labelContent = new TextNode("", ColorType.DEF, ColorType.DEF);
        labelContent.setText(content);
        labelContent.setFont(CommonUtil.getFont());
        labelContent.setPadding(new Insets(0, 1.5 * padding, 0, 1.5 * padding));

        HBox hBox = NodeUtil.getHBox(ColorType.DEF, buttonPositive, buttonNegative);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(SizeUtil.getGlobalSpacing());

        VBox vBox = NodeUtil.getVBox(ColorType.DEF);
        Scene scene = new Scene(vBox);
        vBox.getChildren().add(labelContent);
        vBox.getChildren().add(hBox);
        vBox.setBackground(ColorUtil.getBackgroundDef());
        vBox.setBorder(NodeUtil.getBorder(1, 1, 1, 1));

        this.setOnShown(event -> {
            this.centerOnScreen();
            CommonUtil.updateNodeProperties(this.getScene());
        });
        this.initStyle(StageStyle.UNDECORATED);
        this.setScene(scene);
        this.setAlwaysOnTop(true);
        this.centerOnScreen();
    }

    public boolean getResult() {
        this.showAndWait();
        return result;
    }
}
