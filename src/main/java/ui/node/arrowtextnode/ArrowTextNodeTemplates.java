package ui.node.arrowtextnode;

import enums.Direction;
import ui.custom.HoverMenu;
import ui.node.SeparatorNode;
import ui.node.textnode.TextNodeTemplates;

public enum ArrowTextNodeTemplates {
	FILE {
		public ArrowTextNode get() {
			ArrowTextNode arrowTextNode = new ArrowTextNode("File", true, true, false, true);
			setupNode(arrowTextNode);
			HoverMenu.install(arrowTextNode, Direction.RIGHT
					, TextNodeTemplates.FILE_OPEN.get()
					, TextNodeTemplates.FILE_EDIT.get()
					, TextNodeTemplates.FILE_BROWSE.get()
					, TextNodeTemplates.FILE_DELETE.get()
					, new SeparatorNode()
					, FILE_TAGS.get()
					, new SeparatorNode()
					, TextNodeTemplates.FILE_COPYFILENAME.get()
					, TextNodeTemplates.FILE_COPYFILEPATH.get()
					, new SeparatorNode()
					, TextNodeTemplates.FILE_GOOGLE_RIS.get()
					, TextNodeTemplates.FILE_DETAILS.get()
			);
			return arrowTextNode;
		}
	},
	FILE_TAGS {
		public ArrowTextNode get() {
			ArrowTextNode arrowTextNode = new ArrowTextNode("Tags", true, true, false, true);
			setupNode(arrowTextNode);
			HoverMenu.install(arrowTextNode, Direction.RIGHT
					, TextNodeTemplates.FILE_TAGS_COPY.get()
					, TextNodeTemplates.FILE_TAGS_PASTE.get()
					, TextNodeTemplates.FILE_TAGS_CLEAR.get()
			);
			return arrowTextNode;
		}
	},
	
	SELECTION {
		public ArrowTextNode get() {
			ArrowTextNode arrowTextNode = new ArrowTextNode("Selection", true, true, false, true);
			setupNode(arrowTextNode);
			HoverMenu.install(arrowTextNode, Direction.RIGHT
					, SELECTION_TAGS.get()
					, new SeparatorNode()
					, TextNodeTemplates.SELECTION_DELETE.get()
					, COLLECTION.get()
			);
			return arrowTextNode;
		}
	},
	SELECTION_TAGS {
		public ArrowTextNode get() {
			ArrowTextNode arrowTextNode = new ArrowTextNode("Tags", true, true, false, true);
			setupNode(arrowTextNode);
			HoverMenu.install(arrowTextNode, Direction.RIGHT
					, TextNodeTemplates.SELECTION_TAGS_COPY.get()
					, TextNodeTemplates.SELECTION_TAGS_PASTE.get()
					, TextNodeTemplates.SELECTION_TAGS_CLEAR.get()
			);
			return arrowTextNode;
		}
	},
	
	COLLECTION {
		public ArrowTextNode get() {
			ArrowTextNode arrowTextNode = new ArrowTextNode("Collection", true, true, false, true);
			setupNode(arrowTextNode);
			HoverMenu.install(arrowTextNode, Direction.RIGHT
					, TextNodeTemplates.COLLECTION_CREATE.get()
					, TextNodeTemplates.COLLECTION_DISCARD.get()
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
}
