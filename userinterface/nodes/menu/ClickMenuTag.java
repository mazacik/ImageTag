package userinterface.nodes.menu;

import userinterface.nodes.base.TextNode;
import userinterface.nodes.buttons.ButtonFactory;
import userinterface.nodes.buttons.ButtonTemplates;

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
