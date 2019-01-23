package userinterface.node.topmenu;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import userinterface.node.BaseNode;
import utils.CommonUtil;
import utils.InstanceRepo;

public class TopMenu extends BorderPane implements BaseNode, InstanceRepo {
    Insets nodePadding = new Insets(5, 10, 5, 10);

    Label nodeSave = createNode("Save");
    Label nodeExit = createNode("Exit");

    Label nodeSelectAll = createNode("Select All");
    Label nodeSelectNone = createNode("Select None");

    Label nodeCustom = createNode("Custom");
    Label nodeReset = createNode("Reset");

    Label nodeRandom = createNode("Random");
    Label nodeFullview = createNode("FullView");

    Label nodeInfo = createNode("Info");

    public TopMenu() {
        HBox hBox = new HBox();
        ObservableList hboxChildren = hBox.getChildren();
        hboxChildren.add(createRoot("File", nodeSave, nodeExit));
        hboxChildren.add(createRoot("Select", nodeSelectAll, nodeSelectNone));
        hboxChildren.add(createRoot("Filter", nodeCustom, nodeReset));

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

    private Label createNode(String text) {
        Label node = new Label(text);
        node.setFont(CommonUtil.getFont());
        node.setTextFill(Color.LIGHTGRAY);
        node.setOnMouseEntered(event -> node.setBackground(CommonUtil.getButtonBackgroundHover()));
        node.setOnMouseExited(event -> node.setBackground(CommonUtil.getButtonBackgroundDefault()));
        node.setPadding(nodePadding);
        return node;
    }
    private Label createRoot(String text, Label... children) {
        CustomCM customCM = new CustomCM(children);

        Label root = new Label(text);
        root.setFont(CommonUtil.getFont());
        root.setTextFill(Color.LIGHTGRAY);
        root.setOnMouseEntered(event -> root.setBackground(CommonUtil.getButtonBackgroundHover()));
        root.setOnMouseExited(event -> root.setBackground(CommonUtil.getButtonBackgroundDefault()));
        root.setOnMouseClicked(event -> {
            Bounds boundsInScene = root.localToScene(root.getBoundsInLocal());
            customCM.show(root, boundsInScene.getMinX(), boundsInScene.getMaxY() + root.getHeight() - root.getPadding().getTop());
            setLabelGroupWidth(children);
        });
        root.setPadding(nodePadding);
        return root;
    }

    private void setLabelGroupWidth(Label... labels) {
        double width = 0;
        for (Label label : labels) {
            if (width < label.getWidth()) width = label.getWidth();
        }
        for (Label label : labels) {
            label.setPrefWidth(width);
        }
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
