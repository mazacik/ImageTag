package application.gui.nodes.popup;

import application.gui.decorator.enums.ColorType;
import application.gui.nodes.ColorData;
import application.gui.nodes.buttons.ButtonFactory;
import application.gui.nodes.buttons.ButtonTemplates;
import application.gui.nodes.simple.SeparatorNode;
import application.gui.nodes.simple.TextNode;
import application.misc.enums.Direction;

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
		TextNode nodeMergeSelection = buttonFactory.get(ButtonTemplates.SEL_MERGE);
        TextNode nodeDeleteSelection = buttonFactory.get(ButtonTemplates.SEL_DELETE);

        TextNode nodeObject = new TextNode("Object >", colorData);
        ClickMenuLeft.install(this, nodeObject, Direction.RIGHT,
                nodeShowSimilar,
				new SeparatorNode(),
                nodeOpen,
                nodeEdit,
				new SeparatorNode(),
                nodeCopyName,
                nodeCopyPath,
                nodeDeleteTarget
        );

        TextNode nodeSelection = new TextNode("Selection >", colorData);
        ClickMenuLeft.install(this, nodeSelection, Direction.RIGHT,
				nodeMergeSelection,
                nodeDeleteSelection
        );

        this.getChildren().addAll(nodeObject, nodeSelection);

        instanceList.add(this);
    }
}
