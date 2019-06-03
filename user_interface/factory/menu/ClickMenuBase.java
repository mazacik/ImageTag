package user_interface.factory.menu;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;
import user_interface.factory.NodeUtil;
import user_interface.factory.util.enums.ColorType;

import java.util.ArrayList;

public class ClickMenuBase extends Popup {
    protected static final ArrayList<ClickMenuBase> instanceList = new ArrayList<>();

    private VBox vBox = NodeUtil.getVBox(ColorType.DEF);

    public ClickMenuBase(Region... children) {
        vBox.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
        vBox.getChildren().setAll(children);

        this.addEventFilter(WindowEvent.WINDOW_SHOWN, event -> NodeUtil.equalizeWidth(vBox.getChildren()));
        this.getContent().setAll(vBox);
        this.setAutoHide(true);
        this.setHideOnEscape(true);
    }
    public static void hideAll() {
        instanceList.forEach(leftClickMenu -> {
            if (leftClickMenu.isShowing()) {
                leftClickMenu.hide();
            }
        });
    }
    public ObservableList<Node> getChildren() {
        return vBox.getChildren();
    }
}
