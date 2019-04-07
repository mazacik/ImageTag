package user_interface.factory.stage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.CommonUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.TitleBar;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;

public class OkCancelStage extends Stage {
    private boolean result = false;

    private Label labelContent;
    private Label buttonPositive;
    private Label buttonNegative;

    public OkCancelStage(String content) {
        buttonPositive = NodeFactory.getLabel("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        buttonPositive.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                result = true;
                this.close();
            }
        });
        buttonNegative = NodeFactory.getLabel("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        buttonNegative.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                result = false;
                this.close();
            }
        });

        int padding = CommonUtil.getPadding();
        labelContent = NodeFactory.getLabel("", ColorType.DEF, ColorType.DEF);
        labelContent.setText(content);
        labelContent.setFont(CommonUtil.getFont());
        labelContent.setPadding(new Insets(0, 1.5 * padding, 0, 1.5 * padding));

        HBox hBox = NodeFactory.getHBox(ColorType.DEF, buttonPositive, buttonNegative);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(CommonUtil.getPadding());

        VBox vBox = NodeFactory.getVBox(ColorType.DEF);
        Scene scene = new Scene(vBox);
        vBox.getChildren().add(new TitleBar(scene));
        vBox.getChildren().add(labelContent);
        vBox.getChildren().add(hBox);
        vBox.setBackground(ColorUtil.getBackgroundDef());
        vBox.setBorder(NodeFactory.getBorder(1, 1, 1, 1));

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
