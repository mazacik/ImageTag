package user_interface.factory.node.popup;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import system.CommonUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;

import java.util.ArrayList;

public class LeftClickMenu extends Popup {
    private static final ArrayList<LeftClickMenu> instanceList = new ArrayList<>();
    private Label root;
    private VBox vBox = NodeFactory.getVBox(ColorType.DEF);

    public LeftClickMenu(Label root, Label... labels) {
        instanceList.add(this);
        this.root = root;
        this.root.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Bounds rootBounds = root.getBoundsInParent();
                this.show(CommonUtil.mainStage, rootBounds.getMinX(), rootBounds.getMaxY());
            }
        });
        this.root.setOnMouseMoved(event -> {
            if (!this.isShowing()) {
                for (LeftClickMenu leftClickMenu : instanceList) {
                    if (leftClickMenu.isShowing()) {
                        leftClickMenu.hide();
                        Bounds rootBounds = root.getBoundsInParent();
                        this.show(CommonUtil.mainStage, rootBounds.getMinX(), rootBounds.getMaxY());
                        break;
                    }
                }
            }
        });

        vBox.getChildren().addAll(labels);
        vBox.setPadding(new Insets(CommonUtil.getPadding()));
        vBox.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));

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

    public Label getRoot() {
        return root;
    }
    public ObservableList<Node> getChildren() {
        return vBox.getChildren();
    }
}
