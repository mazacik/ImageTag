package user_interface.singleton.side;

import javafx.scene.layout.VBox;
import user_interface.factory.base.TextNode;

public interface SidePane {
    void changeNodeState(TagNode tagNode, TextNode nameNode);
    VBox getTagNodes();

    void expandAll();
    void collapseAll();
}
