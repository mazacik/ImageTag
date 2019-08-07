package application.gui.nodes.popup;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class ClickMenuRight extends ClickMenuBase {
    public void show(Node anchor, double screenX, double screenY) {
        super.show(anchor, screenX, screenY);
    }
    public void show(Node anchor, MouseEvent event) {
        super.show(anchor, event.getScreenX(), event.getScreenY());
    }
}
