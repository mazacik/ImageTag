package application.gui.panes.top;

import application.gui.decorator.SizeUtil;
import application.gui.nodes.buttons.ButtonTemplates;
import application.gui.nodes.custom.TitleBar;
import application.gui.nodes.popup.ClickMenuLeft;
import application.gui.nodes.simple.SeparatorNode;
import application.gui.nodes.simple.TextNode;
import application.gui.panes.NodeBase;
import application.main.Instances;
import application.misc.enums.Direction;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class ToolbarPane extends TitleBar implements NodeBase {
	private final TextNode nodeTarget;
	
	private boolean needsReload;
	
	public ToolbarPane() {
		super("");
		
		needsReload = false;
		
		TextNode nodeFile = new TextNode("File", true, true, false, true);
		TextNode nodeSave = ButtonTemplates.APPLICATION_SAVE.get();
		TextNode nodeImport = ButtonTemplates.APPLICATION_IMPORT.get();
		TextNode nodeExit = ButtonTemplates.APPLICATION_EXIT.get();
		ClickMenuLeft.install(nodeFile, Direction.DOWN, nodeSave, nodeImport, new SeparatorNode(), nodeExit);
		
		TextNode nodeRandom = ButtonTemplates.FILTER_RANDOM.get();
		HBox hBoxTools = new HBox(nodeRandom);
		hBoxTools.setAlignment(Pos.CENTER);
		
		TextNode nodeShowSimilar = ButtonTemplates.FILTER_SIMILAR.get();
		TextNode nodeOpen = ButtonTemplates.OBJECT_OPEN.get();
		TextNode nodeEdit = ButtonTemplates.OBJECT_EDIT.get();
		TextNode nodeCopyName = ButtonTemplates.OBJECT_COPY_NAME.get();
		TextNode nodeCopyPath = ButtonTemplates.OBJECT_COPY_PATH.get();
		TextNode nodeUnmergeGroup = ButtonTemplates.GROUP_UNMERGE.get();
		TextNode nodeDeleteTarget = ButtonTemplates.OBJECT_DELETE.get();
		nodeTarget = new TextNode("", true, true, false, true);
		ClickMenuLeft.install(nodeTarget, Direction.DOWN,
				nodeShowSimilar,
				nodeOpen,
				nodeEdit,
				nodeCopyName,
				nodeCopyPath,
				nodeUnmergeGroup,
				nodeDeleteTarget
		);
		
		HBox hBox = new HBox(nodeFile, hBoxTools);
		hBox.setAlignment(Pos.CENTER);
		
		this.setPrefHeight(SizeUtil.getPrefHeightTopMenu());
		this.setLeft(hBox);
		this.setCenter(nodeTarget);
	}
	
	public boolean reload() {
		nodeTarget.setText(Instances.getTarget().getCurrentTarget().getName());
		return true;
	}
	
	@Override
	public boolean getNeedsReload() {
		return needsReload;
	}
	@Override
	public void setNeedsReload(boolean needsReload) {
		this.needsReload = needsReload;
	}
}
