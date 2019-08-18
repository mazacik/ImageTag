package application.gui.nodes.popup;

import application.gui.nodes.NodeUtil;
import application.gui.stage.Stages;
import application.misc.enums.Direction;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.Window;

public class ClickMenuLeft extends ClickMenuBase {
    private ClickMenuLeft(Window owner, Region root, Direction direction, Region... children) {
        super(children);

        root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.show(owner, root, direction);
            }
        });
        root.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            if (!this.isShowing()) {
                for (ClickMenuBase clickMenu : instanceList) {
                    if (clickMenu instanceof ClickMenuLeft && clickMenu.isShowing()) {
                        clickMenu.hide();
                        show(owner, root, direction);
                        break;
                    }
                }
            }
        });

        if (owner != null) owner.setOnHidden(event -> ClickMenuLeft.hideAll());

        this.setOnShown(event -> NodeUtil.equalizeWidth(children));
        this.setAutoHide(true);
        this.setHideOnEscape(true);
        instanceList.add(this);
    }
    public static void install(Region root, Direction direction, Region... labels) {
        new ClickMenuLeft(null, root, direction, labels);
    }
    public static void install(Window window, Region root, Direction direction, Region... labels) {
        new ClickMenuLeft(window, root, direction, labels);
    }
    private void show(Window owner, Region root, Direction direction) {
        double x;
        double y;

        Border rootBorder;
        if (owner != null) {
            Node node = ((Popup) owner).getContent().get(0);
            rootBorder = ((Region) node).getBorder();
        } else {
            rootBorder = root.getBorder();
        }

        Bounds rootBounds = root.localToScreen(root.getBoundsInLocal());

        switch (direction) {
            case LEFT:
                x = rootBounds.getMinX();
                y = rootBounds.getMinY();
                if (rootBorder != null) {
                    x -= rootBorder.getInsets().getLeft();
                    if (owner == null) {
                        y -= rootBorder.getInsets().getBottom();
                    }
                    y -= rootBorder.getInsets().getTop();
                }
                this.show(Stages.getMainStage(), x, y);
                this.setAnchorX(this.getAnchorX() - this.getWidth());
                break;
            case RIGHT:
                x = rootBounds.getMaxX();
                y = rootBounds.getMinY();
                if (rootBorder != null) {
                    if (owner == null) {
                        y -= rootBorder.getInsets().getBottom();
                    }
                    y -= rootBorder.getInsets().getTop();
                }
                this.show(Stages.getMainStage(), x, y);
                break;
            case DOWN:
                x = rootBounds.getMinX();
                y = rootBounds.getMaxY();
                if (rootBorder != null) {
                    y -= rootBorder.getInsets().getBottom();
                }
                this.show(Stages.getMainStage(), x, y);
                break;
        }
    }
}