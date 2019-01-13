package userinterface.node.topmenu;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import userinterface.BackgroundEnum;
import userinterface.node.BaseNode;
import utils.CommonUtil;

public class TopMenu2 extends BorderPane implements BaseNode {
    Insets nodePadding = new Insets(5, 10, 5, 10);

    Label nodeSave = createNode("Save");
    Label nodeExit = createNode("Exit");

    Label labelSelectAll = createNode("Select All");
    Label labelClearSelect = createNode("Clear");

    Label labelUntagged = createNode("Untagged");
    Label labelMaxX = createNode("MaxX");

    Label labelRandom = createNode("Random");
    Label labelFullView = createNode("FullView");

    public TopMenu2() {
        HBox hBox = new HBox(5);
        ObservableList hboxChildren = hBox.getChildren();
        hboxChildren.add(createRoot("File", nodeSave, nodeExit));
        hboxChildren.add(createRoot("Select", labelSelectAll, labelClearSelect));
        hboxChildren.add(createRoot("Filter", labelUntagged, labelMaxX));
        hboxChildren.add(new HBox(labelRandom, labelFullView));

        this.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

        this.setBackground(BackgroundEnum.NIGHT_1.getValue());
        this.setCenter(hBox);
    }

    public void reload() {
        //String text = select.size() + " items selected";
        //infoLabelMenu.setText(text);
    }

    private Label createNode(String text) {
        Label node = new Label(text);
        node.setFont(CommonUtil.getFont());
        node.setTextFill(Color.LIGHTGRAY);
        node.setOnMouseEntered(event -> node.setBackground(BackgroundEnum.NIGHT_3.getValue()));
        node.setOnMouseExited(event -> node.setBackground(BackgroundEnum.NIGHT_1.getValue()));
        node.setPadding(nodePadding);
        return node;
    }
    private Label createRoot(String text, Label... children) {
        CustomCM customCM = new CustomCM(children);

        Label root = new Label(text);
        root.setFont(CommonUtil.getFont());
        root.setTextFill(Color.LIGHTGRAY);
        root.setOnMouseEntered(event -> root.setBackground(BackgroundEnum.NIGHT_3.getValue()));
        root.setOnMouseExited(event -> root.setBackground(BackgroundEnum.NIGHT_1.getValue()));
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
            System.out.println(label.getPrefWidth());
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
    public Label getLabelSelectAll() {
        return labelSelectAll;
    }
    public Label getLabelClearSelect() {
        return labelClearSelect;
    }
    public Label getLabelUntagged() {
        return labelUntagged;
    }
    public Label getLabelMaxX() {
        return labelMaxX;
    }
    public Label getLabelRandom() {
        return labelRandom;
    }
    public Label getLabelFullView() {
        return labelFullView;
    }
}
