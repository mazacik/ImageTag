package user_interface.node_factory.template;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import system.CommonUtil;
import user_interface.node_factory.NodeColorData;
import user_interface.node_factory.NodeFactory;
import user_interface.node_factory.utils.ColorType;
import user_interface.node_factory.utils.ColorUtil;

public class ColorModeSwitch extends HBox {
    private int nodeWidth = 70;
    private Label nodeDefault;
    private Label nodeDark;

    public ColorModeSwitch() {
        NodeColorData nodeColorData = new NodeColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        nodeDefault = NodeFactory.getLabel("Default", nodeColorData);
        nodeDefault.setPrefWidth(nodeWidth);
        nodeDark = NodeFactory.getLabel("Dark", nodeColorData);
        nodeDark.setPrefWidth(nodeWidth);

        nodeDefault.setOnMouseClicked(event -> {
            if (CommonUtil.isNightMode()) {
                CommonUtil.setNightMode(false);
                nodeDefault.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));
                nodeDark.setBorder(null);
            }
        });
        nodeDark.setOnMouseClicked(event -> {
            if (!CommonUtil.isNightMode()) {
                CommonUtil.setNightMode(true);
                nodeDefault.setBorder(null);
                nodeDark.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));
            }
        });

        if (!CommonUtil.isNightMode()) {
            nodeDefault.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));
        } else {
            nodeDark.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));
        }

        this.getChildren().add(nodeDefault);
        this.getChildren().add(nodeDark);
        this.setAlignment(Pos.CENTER);
        this.setMaxWidth(2 * nodeWidth);
    }
}
