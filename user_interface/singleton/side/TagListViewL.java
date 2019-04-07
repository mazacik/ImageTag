package user_interface.singleton.side;

import database.object.TagObject;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
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

public class TagListViewL extends VBox implements BaseNode, InstanceRepo {
    private final Label nodeTitle;
    private final VBox tagListBox;
    private final ScrollPane tagListScrollPane;
    private final ArrayList<String> expandedGroupsList;

    private final Label nodeLimit;
    private final Label nodeReset;

    public TagListViewL() {
        ColorData colorDataSimple = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        nodeTitle = NodeFactory.getLabel("", colorDataSimple);
        nodeTitle.setBorder(NodeFactory.getBorder(0, 0, 1, 0));
        nodeTitle.prefWidthProperty().bind(this.widthProperty());

        nodeReset = NodeFactory.getLabel("Reset", colorDataSimple);
        nodeLimit = NodeFactory.getLabel("Limit", colorDataSimple);
        LeftClickMenu.install(nodeTitle, Direction.RIGHT, nodeLimit, nodeReset);
        Tooltip.install(nodeLimit, NodeFactory.getTooltip("Only shows images with no tags.\nCtrl + Click to specify the upper limit."));

        Label btnNew = NodeFactory.getLabel("Create a new tag", colorDataSimple);
        btnNew.setBorder(NodeFactory.getBorder(0, 0, 1, 0));
        btnNew.prefWidthProperty().bind(this.widthProperty());
        btnNew.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mainInfoList.add(new InfoObjectEditStage().getResult());
                mainInfoList.sort();
                reload.doReload();
            }
        });

        tagListBox = NodeFactory.getVBox(ColorType.DEF);
        tagListScrollPane = new ScrollPane();
        tagListScrollPane.setContent(tagListBox);
        tagListScrollPane.setFitToWidth(true);
        tagListScrollPane.setFitToHeight(true);
        tagListScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tagListScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        NodeFactory.addNodeToManager(tagListScrollPane, ColorType.DEF);

        expandedGroupsList = new ArrayList<>();

        this.setPrefWidth(CommonUtil.getUsableScreenWidth());
        this.setMinWidth(CommonUtil.getUsableScreenWidth() / 10);
        this.getChildren().addAll(nodeTitle, btnNew, tagListScrollPane);
    }

    public void reload() {
        nodeTitle.setText("Filter: " + filter.size());

        ObservableList<Node> tagListNodes = tagListBox.getChildren();
        tagListNodes.clear();

        Color textColorDefault = ColorUtil.getTextColorDef();
        Color textColorPositive = ColorUtil.getTextColorPos();
        Color textColorNegative = ColorUtil.getTextColorNeg();

        //todo this also probably needs a rework... someday2
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
            groupNode.prefWidthProperty().bind(tagListBox.widthProperty());

            for (String tagName : mainInfoList.getNames(groupName)) {
                Label nameNode = NodeFactory.getLabel(tagName, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);

                if (filter.isTagObjectWhitelisted(groupName, tagName)) {
                    nameNode.setTextFill(textColorPositive);
                } else if (filter.isTagObjectBlacklisted(groupName, tagName)) {
                    nameNode.setTextFill(textColorNegative);
                } else {
                    nameNode.setTextFill(textColorDefault);
                }
                nameNode.prefWidthProperty().bind(tagListBox.widthProperty());
                nameNode.setAlignment(Pos.CENTER_LEFT);
                nameNode.setPadding(new Insets(0, 0, 0, 50));
                nameNode.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        changeNodeState(groupNode, nameNode);
                        reload.doReload();
                        CommonUtil.updateNodeProperties(this.tagListBox);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        infoObjectRCM.setTagObject(mainInfoList.getTagObject(groupNode.getText(), nameNode.getText()));
                        infoObjectRCM.show(nameNode, event);
                    }
                });
                groupNode.getNameNodes().add(nameNode);
                groupNode.setPadding(new Insets(0));
            }
            tagListNodes.add(groupNode);
            if (expandedGroupsList.contains(groupNode.getText())) {
                groupNode.setArrowExpanded(true);
                tagListNodes.addAll(groupNode.getNameNodes());
            }
        }
        CommonUtil.updateNodeProperties(tagListBox);
    }
    public void onShown() {
        tagListScrollPane.lookup(".scroll-bar").setStyle("-fx-background-color: transparent;");
        tagListScrollPane.lookup(".increment-button").setStyle("-fx-background-color: transparent;");
        tagListScrollPane.lookup(".decrement-button").setStyle("-fx-background-color: transparent;");
        tagListScrollPane.lookup(".thumb").setStyle("-fx-background-color: gray; -fx-background-insets: 0 4 0 4;");
        CommonUtil.updateNodeProperties(tagListBox);
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
            TagObject tagObject = mainInfoList.getTagObject(groupNode.getText(), nameNode.getText());
            if (filter.isTagObjectWhitelisted(tagObject)) {
                filter.blacklistTagObject(tagObject);
                if (filter.isGroupBlacklisted(tagObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorNeg());
                } else if (!filter.isGroupWhitelisted(tagObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorDef());
                }
                nameNode.setTextFill(ColorUtil.getTextColorNeg());
            } else if (filter.isTagObjectBlacklisted(tagObject)) {
                filter.unlistTagObject(tagObject);
                if (!filter.isGroupWhitelisted(tagObject.getGroup()) && !filter.isGroupBlacklisted(tagObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorDef());
                }
                nameNode.setTextFill(ColorUtil.getTextColorDef());
            } else {
                filter.whitelistTagObject(tagObject);
                if (filter.isGroupWhitelisted(tagObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorPos());
                }
                nameNode.setTextFill(ColorUtil.getTextColorPos());
            }
        }
        filter.apply();
    }

    public VBox getTagListBox() {
        return tagListBox;
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
