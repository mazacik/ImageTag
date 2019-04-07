package user_interface.factory.node;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import system.CommonUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.enums.ColorType;

public class ColorModeSwitch extends HBox {
    private int nodeWidth = 70;
    private Label nodeDefault;
    private Label nodeDark;

    public ColorModeSwitch() {
        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        nodeDefault = NodeFactory.getLabel("Default", colorData);
        nodeDefault.setPrefWidth(nodeWidth);
        nodeDark = NodeFactory.getLabel("Dark", colorData);
        nodeDark.setPrefWidth(nodeWidth);

        nodeDefault.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (CommonUtil.isNightMode()) {
                    CommonUtil.setNightMode(false);
                    nodeDefault.setBorder(NodeFactory.getBorder(1, 1, 1, 1));
                    nodeDark.setBorder(null);
                }
            }
        });
        nodeDark.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!CommonUtil.isNightMode()) {
                    CommonUtil.setNightMode(true);
                    nodeDefault.setBorder(null);
                    nodeDark.setBorder(NodeFactory.getBorder(1, 1, 1, 1));
                }
            }
        });

        if (!CommonUtil.isNightMode()) {
            nodeDefault.setBorder(NodeFactory.getBorder(1, 1, 1, 1));
        } else {
            nodeDark.setBorder(NodeFactory.getBorder(1, 1, 1, 1));
        }

        this.getChildren().add(nodeDefault);
        this.getChildren().add(nodeDark);
        this.setAlignment(Pos.CENTER);
        this.setMaxWidth(2 * nodeWidth);
    }
}
