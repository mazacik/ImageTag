package application.gui.nodes.popup;

import application.gui.nodes.buttons.ButtonTemplates;
import application.gui.nodes.simple.SeparatorNode;
import application.gui.nodes.simple.TextNode;
import application.misc.enums.Direction;

public class ClickMenuData extends ClickMenuRight {
	public ClickMenuData() {
		TextNode nodeShowSimilar = ButtonTemplates.FILTER_SIMILAR.get();
		TextNode nodeOpen = ButtonTemplates.OBJECT_OPEN.get();
		TextNode nodeEdit = ButtonTemplates.OBJECT_EDIT.get();
		TextNode nodeCopyName = ButtonTemplates.OBJECT_COPY_NAME.get();
		TextNode nodeCopyPath = ButtonTemplates.OBJECT_COPY_PATH.get();
		TextNode nodeDeleteTarget = ButtonTemplates.OBJECT_DELETE.get();
		TextNode nodeMergeSelection = ButtonTemplates.SELECTION_MERGE.get();
		TextNode nodeDeleteSelection = ButtonTemplates.SELECTION_DELETE.get();
		
		TextNode nodeObject = new TextNode("Object", true, true, false, true);
		ClickMenuLeft.install(this, nodeObject, Direction.RIGHT,
				nodeShowSimilar,
				new SeparatorNode(),
				nodeOpen,
				nodeEdit,
				new SeparatorNode(),
				nodeCopyName,
				nodeCopyPath,
				nodeDeleteTarget
		);
		
		TextNode nodeSelection = new TextNode("Selection", true, true, false, true);
		ClickMenuLeft.install(this, nodeSelection, Direction.RIGHT,
				nodeMergeSelection,
				nodeDeleteSelection
		);
		
		this.getChildren().addAll(nodeObject, nodeSelection);
		
		instanceList.add(this);
	}
}
