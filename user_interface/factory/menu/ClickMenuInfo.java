package user_interface.factory.menu;

import user_interface.factory.base.TextNode;
import user_interface.factory.buttons.ButtonFactory;
import user_interface.factory.buttons.ButtonTemplates;

public class ClickMenuInfo extends ClickMenuRight {
    private String group;
    private String name;

    public ClickMenuInfo() {
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
