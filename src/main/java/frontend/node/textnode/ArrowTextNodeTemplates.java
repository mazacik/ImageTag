package frontend.node.textnode;

import backend.misc.Direction;
import frontend.node.menu.HoverMenu;
import main.Main;

public enum ArrowTextNodeTemplates {
	FILE_TAGS {
		@Override public ArrowTextNode get() {
			ArrowTextNode arrowTextNode = new ArrowTextNode("Tags", true, true, false, true, this);
			setupNode(arrowTextNode);
			HoverMenu.install(arrowTextNode, Direction.RIGHT
					, TextNodeTemplates.FILE_TAGS_COPY.get()
					, TextNodeTemplates.FILE_TAGS_PASTE.get()
					, TextNodeTemplates.FILE_TAGS_CLEAR.get()
			);
			return arrowTextNode;
		}
		@Override public boolean resolveVisible() {
			return Main.SELECT.size() <= 1;
		}
	},
	SELECTION_TAGS {
		@Override public ArrowTextNode get() {
			ArrowTextNode arrowTextNode = new ArrowTextNode("Tags", true, true, false, true, this);
			setupNode(arrowTextNode);
			HoverMenu.install(arrowTextNode, Direction.RIGHT
					, TextNodeTemplates.SELECTION_TAGS_COPY.get()
					, TextNodeTemplates.SELECTION_TAGS_PASTE.get()
					, TextNodeTemplates.SELECTION_TAGS_MERGE.get()
					, TextNodeTemplates.SELECTION_TAGS_CLEAR.get()
			);
			return arrowTextNode;
		}
		@Override public boolean resolveVisible() {
			return Main.SELECT.size() > 1;
		}
	},
	
	GROUP {
		@Override public ArrowTextNode get() {
			ArrowTextNode arrowTextNode = new ArrowTextNode("Group", true, true, false, true, this);
			setupNode(arrowTextNode);
			HoverMenu.install(arrowTextNode, Direction.RIGHT
					, TextNodeTemplates.GROUP_MERGE_TAGS.get()
					, TextNodeTemplates.GROUP_CREATE.get()
					, TextNodeTemplates.GROUP_DISCARD.get()
			);
			return arrowTextNode;
		}
		@Override public boolean resolveVisible() {
			return Main.SELECT.size() > 1;
		}
	},
	
	TOOLS {
		@Override public ArrowTextNode get() {
			ArrowTextNode arrowTextNode = new ArrowTextNode("Tools", true, true, false, true, this);
			setupNode(arrowTextNode);
			HoverMenu.install(arrowTextNode, Direction.RIGHT
					, TextNodeTemplates.SELECTION_DESELECT_LARGEST.get()
					, TextNodeTemplates.FILE_GOOGLE_RIS.get()
					, TextNodeTemplates.FILE_DETAILS.get()
			);
			return arrowTextNode;
		}
	},
	;
	
	void setupNode(ArrowTextNode arrowTextNode) {
		arrowTextNode.setMaxWidth(Double.MAX_VALUE);
	}
	
	public ArrowTextNode get() {
		return null;
	}
	public boolean resolveVisible() {
		return true;
	}
}
