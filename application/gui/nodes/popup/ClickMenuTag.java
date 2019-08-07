package application.gui.nodes.popup;

import application.gui.nodes.buttons.ButtonFactory;
import application.gui.nodes.buttons.ButtonTemplates;
import application.gui.nodes.simple.TextNode;

public class ClickMenuTag extends ClickMenuRight {
    private String group;
    private String name;
	
	public ClickMenuTag() {
        ButtonFactory buttonFactory = ButtonFactory.getInstance();
        TextNode nodeEdit = buttonFactory.get(ButtonTemplates.TAG_EDIT);
        TextNode nodeRemove = buttonFactory.get(ButtonTemplates.TAG_REMOVE);

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
