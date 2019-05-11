package user_interface.singleton.top;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.popup.Direction;
import user_interface.factory.node.popup.LeftClickMenu;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.BaseNode;

public class TopMenu extends BorderPane implements BaseNode, InstanceRepo {
    Label nodeSave;
    Label nodeImport;
    Label nodeSettings;
    Label nodeExit;

    Label nodeInpaint;

    Label nodeRandom;
    Label nodeFullview;

    public TopMenu() {
        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        Label nodeFile = NodeFactory.getLabel("File", colorData);
        nodeSave = NodeFactory.getLabel("Save", colorData);
        nodeImport = NodeFactory.getLabel("Import", colorData);
        nodeSettings = NodeFactory.getLabel("Settings", colorData);
        nodeExit = NodeFactory.getLabel("Exit", colorData);
        LeftClickMenu.install(nodeFile, Direction.DOWN, nodeSave, nodeImport, nodeSettings, NodeFactory.getSeparator(), nodeExit);

        Label nodeTools = NodeFactory.getLabel("Tools", colorData);
        nodeInpaint = NodeFactory.getLabel("Inpaint", colorData);
        LeftClickMenu.install(nodeTools, Direction.DOWN, nodeInpaint);

        nodeRandom = NodeFactory.getLabel("Random", colorData);
        nodeFullview = NodeFactory.getLabel("MediaView", colorData);
        HBox hBoxTools = NodeFactory.getHBox(ColorType.DEF, nodeRandom, nodeFullview);
        hBoxTools.setBorder(NodeFactory.getBorder(0, 1, 0, 1));
        NodeFactory.addNodeToManager(hBoxTools, ColorType.DEF);

        HBox hBoxMain = NodeFactory.getHBox(ColorType.DEF);
        hBoxMain.getChildren().addAll(nodeFile, nodeTools);
        hBoxMain.getChildren().add(hBoxTools);
        NodeFactory.addNodeToManager(hBoxMain, ColorType.DEF);

        this.setBorder(NodeFactory.getBorder(0, 0, 1, 0));
        this.setPrefHeight(30);
        this.setLeft(hBoxMain);
        NodeFactory.addNodeToManager(this, ColorType.DEF);
    }

    public void reload() {

    }

    public Label getNodeSave() {
        return nodeSave;
    }
    public Label getNodeImport() {
        return nodeImport;
    }
    public Label getNodeSettings() {
        return nodeSettings;
    }
    public Label getNodeExit() {
        return nodeExit;
    }

    public Label getNodeInpaint() {
        return nodeInpaint;
    }
    public Label getNodeRandom() {
        return nodeRandom;
    }
    public Label getNodeFullview() {
        return nodeFullview;
    }
}