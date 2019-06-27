package userinterface.nodes.base;

import javafx.scene.layout.Region;
import userinterface.nodes.NodeUtil;

public class Separator extends Region {
    public Separator() {
        super();
        this.setBorder(NodeUtil.getBorder(1, 1, 0, 0));
    }
}
