package user_interface.factory.stage;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import user_interface.factory.base.TextNode;
import user_interface.factory.node.TitleBar;
import user_interface.utils.ColorUtil;
import user_interface.utils.NodeUtil;
import user_interface.utils.SizeUtil;
import user_interface.utils.StyleUtil;
import user_interface.utils.enums.ColorType;

public class ErrorStage extends Stage {
    public ErrorStage(String content) {
        TextNode buttonPositive = new TextNode("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        buttonPositive.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.close();
            }
        });

        double padding = SizeUtil.getGlobalSpacing();
        TextNode labelContent = new TextNode("", ColorType.DEF, ColorType.DEF);
        labelContent.setText(content);
        labelContent.setFont(StyleUtil.getFont());
        labelContent.setPadding(new Insets(0, 1.5 * padding, 0, 1.5 * padding));

        VBox vBox = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
        Scene scene = new Scene(vBox);
        vBox.getChildren().add(new TitleBar(scene, "Error"));
        vBox.getChildren().add(labelContent);
        vBox.getChildren().add(buttonPositive);
        vBox.setBackground(ColorUtil.getBackgroundDef());
        vBox.setBorder(NodeUtil.getBorder(1, 1, 1, 1));

        this.setOnShown(event -> this.centerOnScreen());
        this.initStyle(StageStyle.UNDECORATED);
        this.setScene(scene);
        this.setAlwaysOnTop(true);
        this.centerOnScreen();
        this.showAndWait();
    }
}
