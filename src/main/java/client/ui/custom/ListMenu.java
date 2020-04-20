package client.ui.custom;

import client.ui.custom.arrowtextnode.ArrowTextNode;
import client.ui.custom.arrowtextnode.ArrowTextNodeTemplates;
import client.ui.custom.textnode.TextNode;
import client.ui.custom.textnode.TextNodeTemplates;
import client.ui.override.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import server.base.CustomList;

public abstract class ListMenu extends Popup {
	protected static final CustomList<ListMenu> instances = new CustomList<>();
	
	protected CustomList<Region> children;
	protected VBox vBox;
	
	protected void resolveChildren() {
		CustomList<Region> resultList = new CustomList<>();
		
		for (Region child : children) {
			if (child instanceof TextNode) {
				TextNode textNode = (TextNode) child;
				for (TextNodeTemplates template : TextNodeTemplates.values()) {
					if (textNode.getTemplate() == template) {
						if (template.resolve()) {
							resultList.add(textNode);
						}
						break;
					}
				}
			} else if (child instanceof ArrowTextNode) {
				ArrowTextNode arrowTextNode = (ArrowTextNode) child;
				for (ArrowTextNodeTemplates template : ArrowTextNodeTemplates.values()) {
					if (arrowTextNode.getTemplate() == template) {
						if (template.resolve()) {
							resultList.add(arrowTextNode);
						}
						break;
					}
				}
			} else {
				resultList.add(child);
			}
		}
		
		vBox.getChildren().setAll(resultList);
	}
	
	public static void hideMenus() {
		instances.forEach(PopupWindow::hide);
	}
	public static void hideMenus(HoverMenu except) {
		instances.forEach(listMenu -> {
			if (listMenu instanceof HoverMenu) {
				if (listMenu != except) {
					listMenu.hide();
				}
			}
		});
	}
	
	public enum Preset {
		ENTITY(TextNodeTemplates.FILE_OPEN.get(),
		       TextNodeTemplates.FILE_EDIT.get(),
		       TextNodeTemplates.FILE_BROWSE.get(),
		       TextNodeTemplates.FILE_DELETE.get(),
		       TextNodeTemplates.SELECTION_DELETE.get(),
		       new SeparatorNode(),
		       ArrowTextNodeTemplates.FILE_TAGS.get(),
		       ArrowTextNodeTemplates.SELECTION_TAGS.get(),
		       TextNodeTemplates.COLLECTION_CREATE.get(),
		       TextNodeTemplates.COLLECTION_DISCARD.get(),
		       new SeparatorNode(),
		       TextNodeTemplates.FILE_COPYFILENAME.get(),
		       TextNodeTemplates.FILE_COPYFILEPATH.get(),
		       new SeparatorNode(),
		       ArrowTextNodeTemplates.TOOLS.get()
		),
		TAG(TextNodeTemplates.TAG_CREATE_CHILD.get(),
		    TextNodeTemplates.TAG_EDIT.get(),
		    TextNodeTemplates.TAG_REMOVE.get()
		),
		;
		
		Region[] regions;
		Preset(Region... regions) {
			this.regions = regions;
		}
	}
}
