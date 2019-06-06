package user_interface.factory.base;

import javafx.scene.layout.Region;
import user_interface.utils.NodeUtil;

public class Separator extends Region {
    public Separator() {
        super();
        this.setBorder(NodeUtil.getBorder(1, 1, 0, 0));
    }
}
