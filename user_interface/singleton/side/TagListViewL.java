package user_interface.singleton.side;

import control.filter.FilterManager;
import database.object.TagObject;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import system.CommonUtil;
import system.Direction;
import system.Instances;
import user_interface.factory.NodeUtil;
import user_interface.factory.base.TextNode;
import user_interface.factory.menu.ClickMenuLeft;
import user_interface.factory.stage.InfoObjectEditStage;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.BaseNode;
import user_interface.singleton.utils.SizeUtil;

import java.util.ArrayList;

public class TagListViewL extends VBox implements BaseNode, Instances {
    private final TextNode nodeTitle;
    private final VBox tagListBox;
    private final ScrollPane tagListScrollPane;
    private final ArrayList<String> expandedGroupsList;

    private final TextNode nodeSettings;
    private final TextNode nodeReset;

    public TagListViewL() {
        ColorData colorDataSimple = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        nodeTitle = new TextNode("", colorDataSimple);
        nodeTitle.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
        nodeTitle.prefWidthProperty().bind(this.widthProperty());

        nodeSettings = new TextNode("Settings", colorDataSimple);
        nodeReset = new TextNode("Reset", colorDataSimple);
        ClickMenuLeft.install(nodeTitle, Direction.RIGHT, nodeSettings, nodeReset);

        TextNode btnNew = new TextNode("Create a new tag", colorDataSimple);
        btnNew.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
        btnNew.prefWidthProperty().bind(this.widthProperty());
        btnNew.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mainInfoList.add(new InfoObjectEditStage().getResult());
                mainInfoList.sort();
                reload.doReload();
            }
        });

        tagListBox = NodeUtil.getVBox(ColorType.DEF);
        tagListScrollPane = new ScrollPane();
        tagListScrollPane.setContent(tagListBox);
        tagListScrollPane.setFitToWidth(true);
        tagListScrollPane.setFitToHeight(true);
        tagListScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tagListScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        NodeUtil.addToManager(tagListScrollPane, ColorType.DEF);

        expandedGroupsList = new ArrayList<>();

        this.setPrefWidth(SizeUtil.getUsableScreenWidth());
        this.setMinWidth(SizeUtil.getMinWidthSideLists());
        this.getChildren().addAll(nodeTitle, btnNew, tagListScrollPane);
    }

    public boolean reload() {
        nodeTitle.setText("Filter: " + filter.size() + " matches");

        ObservableList<Node> tagListNodes = tagListBox.getChildren();
        tagListNodes.clear();

        Color textColorDefault = ColorUtil.getTextColorDef();
        Color textColorPositive = ColorUtil.getTextColorPos();
        Color textColorNegative = ColorUtil.getTextColorNeg();

        ArrayList<String> groupNames = mainInfoList.getGroups();
        for (String groupName : groupNames) {
            GroupNode groupNode;
            if (filter.isWhitelisted(groupName)) {
                groupNode = NodeUtil.getGroupNode(this, groupName, textColorPositive);
            } else if (filter.isBlacklisted(groupName)) {
                groupNode = NodeUtil.getGroupNode(this, groupName, textColorNegative);
            } else {
                groupNode = NodeUtil.getGroupNode(this, groupName, textColorDefault);
            }
            groupNode.prefWidthProperty().bind(tagListBox.widthProperty());

            for (String tagName : mainInfoList.getNames(groupName)) {
                TextNode nameNode = new TextNode(tagName, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);

                if (filter.isWhitelisted(groupName, tagName)) {
                    nameNode.setTextFill(textColorPositive);
                } else if (filter.isBlacklisted(groupName, tagName)) {
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
                        clickMenuInfo.setGroup(groupNode.getText());
                        clickMenuInfo.setName(nameNode.getText());
                        clickMenuInfo.show(nameNode, event);
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
        return true;
    }

    public void changeNodeState(GroupNode groupNode, TextNode nameNode) {
        if (nameNode == null) {
            String groupName = groupNode.getText();
            Color textColor;
            if (filter.isWhitelisted(groupName)) {
                filter.blacklist(groupName);
                textColor = ColorUtil.getTextColorNeg();
            } else if (filter.isBlacklisted(groupName)) {
                filter.unlist(groupName);
                textColor = ColorUtil.getTextColorDef();
            } else {
                filter.whitelist(groupName);
                textColor = ColorUtil.getTextColorPos();
            }
            groupNode.setTextFill(textColor);
            groupNode.getNameNodes().forEach(node -> node.setTextFill(textColor));
        } else {
            TagObject tagObject = mainInfoList.getTagObject(groupNode.getText(), nameNode.getText());
            if (filter.isWhitelisted(tagObject)) {
                filter.blacklist(tagObject);
                if (filter.isBlacklisted(tagObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorNeg());
                } else if (!filter.isWhitelisted(tagObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorDef());
                }
                nameNode.setTextFill(ColorUtil.getTextColorNeg());
            } else if (filter.isBlacklisted(tagObject)) {
                filter.unlist(tagObject);
                if (!filter.isWhitelisted(tagObject.getGroup()) && !filter.isBlacklisted(tagObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorDef());
                }
                nameNode.setTextFill(ColorUtil.getTextColorDef());
            } else {
                filter.whitelist(tagObject);
                if (filter.isWhitelisted(tagObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorPos());
                }
                nameNode.setTextFill(ColorUtil.getTextColorPos());
            }
        }
        FilterManager.refresh();
    }

    public ScrollPane getTagListScrollPane() {
        return tagListScrollPane;
    }
    public VBox getTagListBox() {
        return tagListBox;
    }
    public ArrayList<String> getExpandedGroupsList() {
        return expandedGroupsList;
    }

    public TextNode getNodeSettings() {
        return nodeSettings;
    }
    public TextNode getNodeReset() {
        return nodeReset;
    }
}
