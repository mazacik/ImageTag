package user_interface.factory.node.popup;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import system.CommonUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.enums.ColorType;

public class RightClickMenu extends Popup {
    private VBox vBox = NodeFactory.getVBox(ColorType.DEF);

    public RightClickMenu(Label... labels) {
        vBox.getChildren().addAll(labels);
        vBox.setPadding(new Insets(CommonUtil.getPadding()));
        vBox.setBorder(NodeFactory.getBorder(1, 1, 1, 1));

        this.setOnShown(event -> {
            double width = 0;
            for (Label label : labels) {
                if (width < label.getWidth()) width = label.getWidth();
            }
            for (Label label : labels) {
                label.setPrefWidth(width);
            }
        });
        this.getContent().setAll(vBox);
        this.setAutoHide(true);
        this.setHideOnEscape(true);
    }

    public ObservableList<Node> getChildren() {
        return vBox.getChildren();
    }
}
