package ui.node.textnodewitharrow;

import enums.Direction;
import ui.custom.ClickMenu;
import ui.custom.HoverMenu;
import ui.node.SeparatorNode;
import ui.node.textnode.Templates_TextNode;

public enum Templates_TextNodeWithArrow {
	FILE {
		public TextNodeWithArrow get() {
			TextNodeWithArrow textNode = new TextNodeWithArrow("File", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			HoverMenu.install(textNode, Direction.RIGHT
					, Templates_TextNode.FILE_OPEN.get()
					, Templates_TextNode.FILE_EDIT.get()
					, Templates_TextNode.FILE_BROWSE.get()
					, Templates_TextNode.FILE_DELETE.get()
					, new SeparatorNode()
					, FILE_TAGS.get()
					, new SeparatorNode()
					, Templates_TextNode.FILE_COPYFILENAME.get()
					, Templates_TextNode.FILE_COPYFILEPATH.get()
					, new SeparatorNode()
					, Templates_TextNode.FILE_GOOGLE_RIS.get()
					, Templates_TextNode.FILE_DETAILS.get()
			);
			return textNode;
		}
	},
	FILE_TAGS {
		public TextNodeWithArrow get() {
			TextNodeWithArrow textNode = new TextNodeWithArrow("Tags", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			HoverMenu.install(textNode, Direction.RIGHT
					, Templates_TextNode.FILE_TAGS_COPY.get()
					, Templates_TextNode.FILE_TAGS_PASTE.get()
					, Templates_TextNode.FILE_TAGS_CLEAR.get()
			);
			return textNode;
		}
	},
	
	SELECTION {
		public TextNodeWithArrow get() {
			TextNodeWithArrow textNode = new TextNodeWithArrow("Selection", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			HoverMenu.install(textNode, Direction.RIGHT
					, SELECTION_TAGS.get()
					, new SeparatorNode()
					, Templates_TextNode.SELECTION_DELETE.get()
			                  //TODO NodeTemplates.COLLECTION_CREATE.get() and NodeTemplates.COLLECTION_DISCARD.get()
			);
			return textNode;
		}
	},
	SELECTION_TAGS {
		public TextNodeWithArrow get() {
			TextNodeWithArrow textNode = new TextNodeWithArrow("Tags", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			HoverMenu.install(textNode, Direction.RIGHT
					, Templates_TextNode.SELECTION_TAG_COPY.get()
					, Templates_TextNode.SELECTION_TAG_PASTE.get()
					, Templates_TextNode.SELECTION_TAG_CLEAR.get()
			);
			return textNode;
		}
	},
	;
	
	public TextNodeWithArrow get() {
		return null;
	}
	
	public static void hideMenus() {
		ClickMenu.hideMenus();
		HoverMenu.hideMenus();
	}
}
