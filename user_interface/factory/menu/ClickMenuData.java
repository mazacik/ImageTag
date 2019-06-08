package user_interface.factory.menu;

import utils.enums.Direction;
import user_interface.factory.base.Separator;
import user_interface.factory.base.TextNode;
import user_interface.factory.buttons.ButtonFactory;
import user_interface.factory.buttons.ButtonTemplates;
import user_interface.factory.ColorData;
import user_interface.utils.enums.ColorType;
import user_interface.utils.StyleUtil;

public class ClickMenuData extends ClickMenuRight {
    public ClickMenuData() {
        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        ButtonFactory buttonFactory = ButtonFactory.getInstance();
        TextNode nodeShowSimilar = buttonFactory.get(ButtonTemplates.OBJ_SIMILAR);
        TextNode nodeOpen = buttonFactory.get(ButtonTemplates.OBJ_OPEN);
        TextNode nodeEdit = buttonFactory.get(ButtonTemplates.OBJ_EDIT);
        TextNode nodeCopyName = buttonFactory.get(ButtonTemplates.OBJ_COPY_NAME);
        TextNode nodeCopyPath = buttonFactory.get(ButtonTemplates.OBJ_COPY_PATH);
        TextNode nodeDeleteTarget = buttonFactory.get(ButtonTemplates.OBJ_DELETE);
        TextNode nodeDeleteSelection = buttonFactory.get(ButtonTemplates.SEL_DELETE);

        TextNode nodeObject = new TextNode("Object >", colorData);
        ClickMenuLeft.install(this, nodeObject, Direction.RIGHT,
                nodeShowSimilar,
                new Separator(),
                nodeOpen,
                nodeEdit,
                new Separator(),
                nodeCopyName,
                nodeCopyPath,
                nodeDeleteTarget
        );

        TextNode nodeSelection = new TextNode("Selection >", colorData);
        ClickMenuLeft.install(this, nodeSelection, Direction.RIGHT,
                nodeDeleteSelection
        );

        this.getChildren().addAll(nodeObject, nodeSelection);

        StyleUtil.applyStyle(this.getChildren());

        instanceList.add(this);
    }
}