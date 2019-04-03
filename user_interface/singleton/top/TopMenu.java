package user_interface.singleton.top;

import javafx.scene.control.Label;
import javafx.scene.layout.*;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.popup.LeftClickMenu;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.BaseNode;

public class TopMenu extends BorderPane implements BaseNode, InstanceRepo {
    Label nodeSave;
    Label nodeExit;

    Label nodeCustom;
    Label nodeReset;

    Label nodeSelectAll;
    Label nodeSelectNone;
    Label nodeSelectMerge;

    Label nodeRandom;
    Label nodeFullview;

    public TopMenu() {
        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        Label cbFile = NodeFactory.getLabel("File", colorData);
        nodeSave = NodeFactory.getLabel("Save", colorData);
        nodeExit = NodeFactory.getLabel("Exit", colorData);
        new LeftClickMenu(cbFile, nodeSave, nodeExit);

        Label cbFilter = NodeFactory.getLabel("Filter", colorData);
        nodeReset = NodeFactory.getLabel("Reset", colorData);
        nodeCustom = NodeFactory.getLabel("Custom", colorData);
        new LeftClickMenu(cbFilter, nodeCustom, nodeReset);

        Label cbSelection = NodeFactory.getLabel("Selection", colorData);
        nodeSelectAll = NodeFactory.getLabel("Select All", colorData);
        nodeSelectNone = NodeFactory.getLabel("Select None", colorData);
        nodeSelectMerge = NodeFactory.getLabel("Merge Selection", colorData);
        new LeftClickMenu(cbSelection, nodeSelectAll, nodeSelectNone, nodeSelectMerge);

        nodeRandom = NodeFactory.getLabel("Random", colorData);
        nodeFullview = NodeFactory.getLabel("FullView", colorData);
        HBox hBoxTools = NodeFactory.getHBox(ColorType.DEF, nodeRandom, nodeFullview);
        hBoxTools.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 1))));
        NodeFactory.addNodeToBackgroundManager(hBoxTools, ColorType.DEF);

        HBox hBoxMain = NodeFactory.getHBox(ColorType.DEF);
        hBoxMain.getChildren().addAll(cbFile, cbFilter, cbSelection);
        hBoxMain.getChildren().add(hBoxTools);
        NodeFactory.addNodeToBackgroundManager(hBoxMain, ColorType.DEF);

        this.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        this.setPrefHeight(30);
        this.setLeft(hBoxMain);
        NodeFactory.addNodeToBackgroundManager(this, ColorType.DEF);
    }

    public void reload() {

    }

    public Label getNodeSave() {
        return nodeSave;
    }
    public Label getNodeExit() {
        return nodeExit;
    }
    public Label getNodeSelectAll() {
        return nodeSelectAll;
    }
    public Label getNodeSelectNone() {
        return nodeSelectNone;
    }
    public Label getNodeSelectMerge() {
        return nodeSelectMerge;
    }
    public Label getNodeCustom() {
        return nodeCustom;
    }
    public Label getNodeReset() {
        return nodeReset;
    }
    public Label getNodeRandom() {
        return nodeRandom;
    }
    public Label getNodeFullview() {
        return nodeFullview;
    }
}
