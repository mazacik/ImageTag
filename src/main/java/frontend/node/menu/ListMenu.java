package frontend.node.menu;

import backend.BaseList;
import frontend.node.SeparatorNode;
import frontend.node.override.VBox;
import frontend.node.textnode.ArrowTextNode;
import frontend.node.textnode.ArrowTextNodeTemplates;
import frontend.node.textnode.TextNode;
import frontend.node.textnode.TextNodeTemplates;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;

public abstract class ListMenu extends Popup {
	protected static final BaseList<ListMenu> instances = new BaseList<>();
	
	protected BaseList<Region> children;
	protected VBox vBox;
	
	protected void resolveChildrenVisible() {
		BaseList<Region> resultList = new BaseList<>();
		
		for (Region child : children) {
			if (child instanceof TextNode) {
				TextNode textNode = (TextNode) child;
				for (TextNodeTemplates template : TextNodeTemplates.values()) {
					if (textNode.getTemplate() == template) {
						if (template.resolveVisible()) {
							resultList.add(textNode);
						}
						break;
					}
				}
			} else if (child instanceof ArrowTextNode) {
				ArrowTextNode arrowTextNode = (ArrowTextNode) child;
				for (ArrowTextNodeTemplates template : ArrowTextNodeTemplates.values()) {
					if (arrowTextNode.getTemplate() == template) {
						if (template.resolveVisible()) {
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
	
	public static ListMenu getShowing() {
		for (ListMenu instance : instances) {
			if (instance.isShowing()) {
				return instance;
			}
		}
		return null;
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
		       ArrowTextNodeTemplates.GROUP.get(),
		       new SeparatorNode(),
		       TextNodeTemplates.FILE_COPYFILENAME.get(),
		       TextNodeTemplates.FILE_COPYFILEPATH.get(),
		       new SeparatorNode(),
		       ArrowTextNodeTemplates.TOOLS.get()
		),
		TAG(TextNodeTemplates.TAG_CREATE_CHILD.get(),
		    TextNodeTemplates.TAG_EDIT.get(),
		    TextNodeTemplates.TAG_REMOVE_LEVEL.get(),
		    TextNodeTemplates.TAG_REMOVE_CHILDREN.get()
		),
		;
		
		Region[] regions;
		Preset(Region... regions) {
			this.regions = regions;
		}
	}
}
