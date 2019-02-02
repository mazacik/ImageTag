package userinterface.node.topmenu;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import userinterface.node.BaseNode;
import utils.CommonUtil;
import utils.InstanceRepo;

public class TopMenu extends BorderPane implements BaseNode, InstanceRepo {
    Label nodeSave = CommonUtil.createNode("Save");
    Label nodeExit = CommonUtil.createNode("Exit");

    Label nodeSelectAll = CommonUtil.createNode("Select All");
    Label nodeSelectNone = CommonUtil.createNode("Select None");

    Label nodeCustom = CommonUtil.createNode("Custom");
    Label nodeReset = CommonUtil.createNode("Reset");

    Label nodeRandom = CommonUtil.createNode("Random");
    Label nodeFullview = CommonUtil.createNode("FullView");

    Label nodeInfo = CommonUtil.createNode("Info");

    public TopMenu() {
        HBox hBox = new HBox();
        ObservableList hboxChildren = hBox.getChildren();
        hboxChildren.add(CommonUtil.createRoot("File", nodeSave, nodeExit));
        hboxChildren.add(CommonUtil.createRoot("Select", nodeSelectAll, nodeSelectNone));
        hboxChildren.add(CommonUtil.createRoot("Filter", nodeCustom, nodeReset));

        HBox tools = new HBox(nodeRandom, nodeFullview);
        tools.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 1))));
        hboxChildren.add(tools);

        this.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

        this.setBackground(CommonUtil.getBackgroundDefault());
        this.setCenter(hBox);
        this.setRight(nodeInfo);
    }

    public void reload() {
        String text = "Item(s) selected: " + select.size();
        nodeInfo.setText(text);
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
    public Label getNodeInfo() {
        return nodeInfo;
    }
}
