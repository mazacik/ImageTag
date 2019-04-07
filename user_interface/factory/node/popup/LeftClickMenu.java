package user_interface.factory.node.popup;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import system.CommonUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.enums.ColorType;

import java.util.ArrayList;

public class LeftClickMenu extends Popup {
    private static final ArrayList<LeftClickMenu> instanceList = new ArrayList<>();
    private Label root;
    private VBox vBox = NodeFactory.getVBox(ColorType.DEF);

    private LeftClickMenu(Label root, Direction direction, Label... labels) {
        instanceList.add(this);
        this.root = root;
        this.root.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                show(direction);
            }
        });
        this.root.setOnMouseMoved(event -> {
            if (!this.isShowing()) {
                for (LeftClickMenu leftClickMenu : instanceList) {
                    if (leftClickMenu.isShowing()) {
                        leftClickMenu.hide();
                        show(direction);
                        break;
                    }
                }
            }
        });

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
    private void show(Direction direction) {
        this.show(CommonUtil.mainStage, 0, 0);

        Bounds rootBounds = root.localToScreen(root.getBoundsInLocal());
        Border border = root.getBorder();
        double x = 0;
        double y = 0;
        switch (direction) {
            case LEFT:
                x = rootBounds.getMinX();
                y = rootBounds.getMinY();
                if (border != null) {
                    x -= this.getWidth();
                    x -= border.getInsets().getLeft();
                    y -= border.getInsets().getBottom();
                }
                break;
            case RIGHT:
                x = rootBounds.getMaxX();
                y = rootBounds.getMinY();
                if (border != null) {
                    x += border.getInsets().getRight();
                    y -= border.getInsets().getBottom();
                }
                break;
            case BELOW:
                x = rootBounds.getMinX();
                y = rootBounds.getMaxY();
                if (border != null) {
                    y -= border.getInsets().getBottom();
                }
                break;
        }

        this.setAnchorX(x);
        this.setAnchorY(y);
    }

    public static void install(Label root, Direction direction, Label... labels) {
        new LeftClickMenu(root, direction, labels);
    }

    public ObservableList<Node> getChildren() {
        return vBox.getChildren();
    }
    public static ArrayList<LeftClickMenu> getInstanceList() {
        return instanceList;
    }
}
