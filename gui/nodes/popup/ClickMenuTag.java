package application.gui.nodes.popup;

import application.gui.nodes.buttons.ButtonTemplates;
import application.gui.nodes.simple.TextNode;

public class ClickMenuTag extends ClickMenuRight {
	private String group;
	private String name;
	
	public ClickMenuTag() {
		TextNode nodeEdit = ButtonTemplates.TAG_EDIT.get();
		TextNode nodeRemove = ButtonTemplates.TAG_REMOVE.get();
		
		this.getChildren().addAll(nodeEdit, nodeRemove);
	}
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
