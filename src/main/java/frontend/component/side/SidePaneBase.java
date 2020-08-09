package frontend.component.side;

import backend.BaseList;
import frontend.node.ListBox;
import frontend.node.override.VBox;
import main.Main;

public abstract class SidePaneBase extends VBox {
	public static final double WIDTH = 242; //35 + 1 + 206
	
	protected final BaseList<TagNode> tagNodes;
	protected final ListBox listBox;
	
	protected SidePaneBase() {
		tagNodes = new BaseList<>();
		listBox = new ListBox();
		this.setMinWidth(WIDTH);
		this.setMaxWidth(WIDTH);
	}
	
	public boolean reload() {
		tagNodes.clear();
		Main.TAGLIST.forEach(tag -> tagNodes.add(new TagNode(tag)));
		listBox.setNodes(tagNodes);
		
		return true;
	}
}
