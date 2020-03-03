package ui.node.textnodewitharrow;

import enums.Direction;
import ui.custom.HoverMenu;
import ui.node.SeparatorNode;
import ui.node.textnode.TextNodeTemplates;

public enum ArrowTextNodeTemplates {
	FILE {
		public ArrowTextNode get() {
			ArrowTextNode textNode = new ArrowTextNode("File", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			HoverMenu.install(textNode, Direction.RIGHT
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
			return textNode;
		}
	},
	FILE_TAGS {
		public ArrowTextNode get() {
			ArrowTextNode textNode = new ArrowTextNode("Tags", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			HoverMenu.install(textNode, Direction.RIGHT
					, TextNodeTemplates.FILE_TAGS_COPY.get()
					, TextNodeTemplates.FILE_TAGS_PASTE.get()
					, TextNodeTemplates.FILE_TAGS_CLEAR.get()
			);
			return textNode;
		}
	},
	
	SELECTION {
		public ArrowTextNode get() {
			ArrowTextNode textNode = new ArrowTextNode("Selection", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			HoverMenu.install(textNode, Direction.RIGHT
					, SELECTION_TAGS.get()
					, new SeparatorNode()
					, TextNodeTemplates.SELECTION_DELETE.get()
			                  //TODO NodeTemplates.COLLECTION_CREATE.get() and NodeTemplates.COLLECTION_DISCARD.get()
			);
			return textNode;
		}
	},
	SELECTION_TAGS {
		public ArrowTextNode get() {
			ArrowTextNode textNode = new ArrowTextNode("Tags", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			HoverMenu.install(textNode, Direction.RIGHT
					, TextNodeTemplates.SELECTION_TAG_COPY.get()
					, TextNodeTemplates.SELECTION_TAG_PASTE.get()
					, TextNodeTemplates.SELECTION_TAG_CLEAR.get()
			);
			return textNode;
		}
	},
	;
	
	public ArrowTextNode get() {
		return null;
	}
}
