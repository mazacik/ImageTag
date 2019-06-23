package user_interface.nodes.base;

import javafx.scene.layout.Region;
import user_interface.nodes.NodeUtil;

public class Separator extends Region {
    public Separator() {
        super();
        this.setBorder(NodeUtil.getBorder(1, 1, 0, 0));
    }
}
