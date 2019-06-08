package user_interface.factory.node;

import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import user_interface.factory.ColorData;
import user_interface.factory.base.TextNode;
import user_interface.utils.ColorUtil;
import user_interface.utils.NodeUtil;
import user_interface.utils.enums.ColorType;

public class ColorModeSwitch extends HBox {
    private int nodeWidth = 70;
    private TextNode nodeDefault;
    private TextNode nodeDark;

    public ColorModeSwitch() {
        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        nodeDefault = new TextNode("Default", colorData);
        nodeDefault.setPrefWidth(nodeWidth);
        nodeDark = new TextNode("Dark", colorData);
        nodeDark.setPrefWidth(nodeWidth);

        nodeDefault.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (ColorUtil.isNightMode()) {
                    ColorUtil.setNightMode(false);
                    nodeDefault.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
                    nodeDark.setBorder(null);
                }
            }
        });
        nodeDark.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!ColorUtil.isNightMode()) {
                    ColorUtil.setNightMode(true);
                    nodeDefault.setBorder(null);
                    nodeDark.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
                }
            }
        });

        if (!ColorUtil.isNightMode()) {
            nodeDefault.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
        } else {
            nodeDark.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
        }

        this.getChildren().add(nodeDefault);
        this.getChildren().add(nodeDark);
        this.setAlignment(Pos.CENTER);
        this.setMaxWidth(2 * nodeWidth);
    }
}
