package user_interface.single_instance.top;

import javafx.scene.control.Label;
import javafx.scene.layout.*;
import system.InstanceRepo;
import user_interface.node_factory.NodeColorData;
import user_interface.node_factory.NodeFactory;
import user_interface.node_factory.template.generic.RCMenu;
import user_interface.node_factory.utils.ColorType;
import user_interface.node_factory.utils.ColorUtil;
import user_interface.single_instance.BaseNode;

public class TopMenu extends BorderPane implements BaseNode, InstanceRepo {
    Label nodeSave;
    Label nodeExit;

    Label nodeCustom;
    Label nodeReset;

    Label nodeSelectAll;
    Label nodeSelectNone;

    Label nodeRandom;
    Label nodeFullview;

    Label infoNode;

    public TopMenu() {
        NodeColorData nodeColorData = new NodeColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        Label cbFile = NodeFactory.getLabel("File", nodeColorData);
        nodeSave = NodeFactory.getLabel("Save", nodeColorData);
        nodeExit = NodeFactory.getLabel("Exit", nodeColorData);
        new RCMenu(cbFile, nodeSave, nodeExit);
        Label cbFilter = NodeFactory.getLabel("Filter", nodeColorData);
        nodeReset = NodeFactory.getLabel("Reset", nodeColorData);
        nodeCustom = NodeFactory.getLabel("Custom", nodeColorData);
        new RCMenu(cbFilter, nodeCustom, nodeReset);
        Label cbSelection = NodeFactory.getLabel("Selection", nodeColorData);
        nodeSelectAll = NodeFactory.getLabel("Select All", nodeColorData);
        nodeSelectNone = NodeFactory.getLabel("Select None", nodeColorData);
        new RCMenu(cbSelection, nodeSelectAll, nodeSelectNone);
        HBox hBoxMain = NodeFactory.getHBox(ColorType.DEF);
        hBoxMain.getChildren().addAll(cbFile, cbFilter, cbSelection);
        nodeRandom = NodeFactory.getLabel("Random", nodeColorData);
        nodeFullview = NodeFactory.getLabel("FullView", nodeColorData);
        HBox hBoxTools = NodeFactory.getHBox(ColorType.DEF, nodeRandom, nodeFullview);
        hBoxTools.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 1))));
        hBoxMain.getChildren().add(hBoxTools);

        this.setCenter(hBoxMain);
        infoNode = NodeFactory.getLabel("Info", ColorType.DEF, ColorType.DEF);
        this.setRight(infoNode);

        NodeFactory.addNodeToBackgroundManager(hBoxMain, ColorType.DEF);
        NodeFactory.addNodeToBackgroundManager(hBoxTools, ColorType.DEF);
        NodeFactory.addNodeToBackgroundManager(this, ColorType.DEF);
    }

    public void reload() {
        String text = "Item(s) selected: " + select.size();
        infoNode.setText(text);
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
    public Label getInfoNode() {
        return infoNode;
    }
}
