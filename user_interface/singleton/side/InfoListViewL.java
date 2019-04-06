package user_interface.singleton.side;

import database.object.InfoObject;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.popup.Direction;
import user_interface.factory.node.popup.LeftClickMenu;
import user_interface.factory.stage.InfoObjectEditStage;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.BaseNode;

import java.util.ArrayList;

public class InfoListViewL extends VBox implements BaseNode, InstanceRepo {
    private final Label nodeText = NodeFactory.getLabel("", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
    private final VBox infoObjectVBox = NodeFactory.getVBox(ColorType.DEF);
    private final ScrollPane infoObjectScrollPane = new ScrollPane();
    private final ArrayList<String> expandedGroupsList = new ArrayList<>();

    private final Label nodeLimit;
    private final Label nodeReset;

    public InfoListViewL() {
        nodeText.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        nodeText.setFont(CommonUtil.getFont());
        nodeText.prefWidthProperty().bind(this.widthProperty());

        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        nodeReset = NodeFactory.getLabel("Reset", colorData);
        nodeLimit = NodeFactory.getLabel("Limit", colorData);
        new LeftClickMenu(nodeText, Direction.RIGHT, nodeLimit, nodeReset);
        Tooltip.install(nodeLimit, NodeFactory.getTooltip("Only shows images with no tags.\nCtrl + Click to specify the upper limit."));

        Label btnNew = NodeFactory.getLabel("New", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.NULL);
        btnNew.setPrefWidth(999);
        btnNew.setFont(CommonUtil.getFont());
        btnNew.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        btnNew.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mainInfoList.add(new InfoObjectEditStage().getResult());
                reload.doReload();
            }
        });

        infoObjectScrollPane.setContent(infoObjectVBox);
        infoObjectScrollPane.setFitToWidth(true);
        infoObjectScrollPane.setFitToHeight(true);
        infoObjectScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        infoObjectScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(infoObjectScrollPane, Priority.ALWAYS);
        NodeFactory.addNodeToManager(infoObjectScrollPane, ColorType.DEF);

        HBox.setHgrow(this, Priority.ALWAYS);
        this.setPrefWidth(999);
        this.setMinWidth(200);
        this.getChildren().addAll(nodeText, btnNew, infoObjectScrollPane);
    }

    public void changeNodeState(GroupNode groupNode, Label nameNode) {
        if (nameNode == null) {
            String groupName = groupNode.getText();
            Color textColor;
            if (filter.isGroupWhitelisted(groupName)) {
                filter.blacklistGroup(groupName);
                textColor = ColorUtil.getTextColorNeg();
            } else if (filter.isGroupBlacklisted(groupName)) {
                filter.unlistGroup(groupName);
                textColor = ColorUtil.getTextColorDef();
            } else {
                filter.whitelistGroup(groupName);
                textColor = ColorUtil.getTextColorPos();
            }
            groupNode.setTextFill(textColor);
            groupNode.getNameNodes().forEach(node -> node.setTextFill(textColor));
        } else {
            InfoObject infoObject = mainInfoList.getInfoObject(groupNode.getText(), nameNode.getText());
            if (filter.isTagObjectWhitelisted(infoObject)) {
                filter.blacklistTagObject(infoObject);
                if (filter.isGroupBlacklisted(infoObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorNeg());
                } else if (!filter.isGroupWhitelisted(infoObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorDef());
                }
                nameNode.setTextFill(ColorUtil.getTextColorNeg());
            } else if (filter.isTagObjectBlacklisted(infoObject)) {
                filter.unlistTagObject(infoObject);
                if (!filter.isGroupWhitelisted(infoObject.getGroup()) && !filter.isGroupBlacklisted(infoObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorDef());
                }
                nameNode.setTextFill(ColorUtil.getTextColorDef());
            } else {
                filter.whitelistTagObject(infoObject);
                if (filter.isGroupWhitelisted(infoObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorPos());
                }
                nameNode.setTextFill(ColorUtil.getTextColorPos());
            }
        }
        filter.apply();
    }

    public void reload() {
        nodeText.setText("Filter: " + filter.size());

        ObservableList<Node> nodes = infoObjectVBox.getChildren();
        nodes.clear();

        Color textColorDefault = ColorUtil.getTextColorDef();
        Color textColorPositive = ColorUtil.getTextColorPos();
        Color textColorNegative = ColorUtil.getTextColorNeg();

        ArrayList<String> groupNames = mainInfoList.getGroups();
        for (String groupName : groupNames) {
            GroupNode groupNode;
            if (filter.isGroupWhitelisted(groupName)) {
                groupNode = NodeFactory.getGroupNode(this, groupName, textColorPositive);
            } else if (filter.isGroupBlacklisted(groupName)) {
                groupNode = NodeFactory.getGroupNode(this, groupName, textColorNegative);
            } else {
                groupNode = NodeFactory.getGroupNode(this, groupName, textColorDefault);
            }
            groupNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());

            for (String tagName : mainInfoList.getNames(groupName)) {
                Label nameNode = NodeFactory.getLabel(tagName, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);

                if (filter.isTagObjectWhitelisted(groupName, tagName)) {
                    nameNode.setTextFill(textColorPositive);
                } else if (filter.isTagObjectBlacklisted(groupName, tagName)) {
                    nameNode.setTextFill(textColorNegative);
                } else {
                    nameNode.setTextFill(textColorDefault);
                }
                nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                nameNode.setAlignment(Pos.CENTER_LEFT);
                nameNode.setPadding(new Insets(0, 0, 0, 50));
                nameNode.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        changeNodeState(groupNode, nameNode);
                        reload.doReload();
                        CommonUtil.updateNodeProperties(this.infoObjectVBox);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        infoObjectRCM.setInfoObject(mainInfoList.getInfoObject(groupNode.getText(), nameNode.getText()));
                        infoObjectRCM.show(nameNode, event);
                    }
                });
                groupNode.getNameNodes().add(nameNode);
                groupNode.setPadding(new Insets(0));
            }
            nodes.add(groupNode);
            if (expandedGroupsList.contains(groupNode.getText())) {
                groupNode.setArrowExpanded(true);
                nodes.addAll(groupNode.getNameNodes());
            }
        }
        CommonUtil.updateNodeProperties(infoObjectVBox);
    }
    public void onShown() {
        infoObjectScrollPane.lookup(".scroll-bar").setStyle("-fx-background-color: transparent;");
        infoObjectScrollPane.lookup(".increment-button").setStyle("-fx-background-color: transparent;");
        infoObjectScrollPane.lookup(".decrement-button").setStyle("-fx-background-color: transparent;");
        infoObjectScrollPane.lookup(".thumb").setStyle("-fx-background-color: gray; -fx-background-insets: 0 4 0 4;");
        CommonUtil.updateNodeProperties(infoObjectVBox);
    }

    public VBox getInfoObjectVBox() {
        return infoObjectVBox;
    }
    public ArrayList<String> getExpandedGroupsList() {
        return expandedGroupsList;
    }

    public Label getNodeLimit() {
        return nodeLimit;
    }
    public Label getNodeReset() {
        return nodeReset;
    }
}
