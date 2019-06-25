package user_interface.main.side;

import javafx.scene.layout.VBox;
import user_interface.nodes.base.TextNode;

public interface SidePane {
	void changeNodeState(TagNode tagNode, TextNode nameNode);
	VBox getTagNodesBox();
	
	void refresh();
	
	void expandAll();
	void collapseAll();
}
