package user_interface.singleton.side;

import database.object.TagObject;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lifecycle.InstanceManager;
import user_interface.factory.ColorData;
import user_interface.factory.base.TextNode;
import user_interface.factory.menu.ClickMenuLeft;
import user_interface.factory.stage.InfoObjectEditStage;
import user_interface.singleton.NodeBase;
import user_interface.utils.ColorUtil;
import user_interface.utils.NodeUtil;
import user_interface.utils.SizeUtil;
import user_interface.utils.StyleUtil;
import user_interface.utils.enums.ColorType;
import utils.enums.Direction;

import java.util.ArrayList;

public class FilterPane extends VBox implements NodeBase {
    private final TextNode nodeTitle;
    private final VBox tagListBox;
    private final ScrollPane tagListScrollPane;
    private final ArrayList<String> expandedGroupsList;

    private final TextNode nodeSettings;
    private final TextNode nodeReset;

    public FilterPane() {
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
                InstanceManager.getTagListMain().add(new InfoObjectEditStage().getResult());
                InstanceManager.getTagListMain().sort();
                InstanceManager.getReload().doReload();
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
        nodeTitle.setText("Filter: " + InstanceManager.getFilter().size() + " matches");

        ObservableList<Node> tagListNodes = tagListBox.getChildren();
        tagListNodes.clear();

        Color textColorDefault = ColorUtil.getTextColorDef();
        Color textColorPositive = ColorUtil.getTextColorPos();
        Color textColorNegative = ColorUtil.getTextColorNeg();

        ArrayList<String> groupNames = InstanceManager.getTagListMain().getGroups();
        for (String groupName : groupNames) {
            GroupNode groupNode;
            if (InstanceManager.getFilter().isWhitelisted(groupName)) {
                groupNode = NodeUtil.getGroupNode(this, groupName, textColorPositive);
            } else if (InstanceManager.getFilter().isBlacklisted(groupName)) {
                groupNode = NodeUtil.getGroupNode(this, groupName, textColorNegative);
            } else {
                groupNode = NodeUtil.getGroupNode(this, groupName, textColorDefault);
            }
            groupNode.prefWidthProperty().bind(tagListBox.widthProperty());

            for (String tagName : InstanceManager.getTagListMain().getNames(groupName)) {
                TextNode nameNode = new TextNode(tagName, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);

                if (InstanceManager.getFilter().isWhitelisted(groupName, tagName)) {
                    nameNode.setTextFill(textColorPositive);
                } else if (InstanceManager.getFilter().isBlacklisted(groupName, tagName)) {
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
                        InstanceManager.getReload().doReload();
                        StyleUtil.applyStyle(this.tagListBox);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        InstanceManager.getClickMenuInfo().setGroup(groupNode.getText());
                        InstanceManager.getClickMenuInfo().setName(nameNode.getText());
                        InstanceManager.getClickMenuInfo().show(nameNode, event);
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
        StyleUtil.applyStyle(tagListBox);
        return true;
    }

    public void changeNodeState(GroupNode groupNode, TextNode nameNode) {
        if (nameNode == null) {
            String groupName = groupNode.getText();
            Color textColor;
            if (InstanceManager.getFilter().isWhitelisted(groupName)) {
                InstanceManager.getFilter().blacklist(groupName);
                textColor = ColorUtil.getTextColorNeg();
            } else if (InstanceManager.getFilter().isBlacklisted(groupName)) {
                InstanceManager.getFilter().unlist(groupName);
                textColor = ColorUtil.getTextColorDef();
            } else {
                InstanceManager.getFilter().whitelist(groupName);
                textColor = ColorUtil.getTextColorPos();
            }
            groupNode.setTextFill(textColor);
            groupNode.getNameNodes().forEach(node -> node.setTextFill(textColor));
        } else {
            TagObject tagObject = InstanceManager.getTagListMain().getTagObject(groupNode.getText(), nameNode.getText());
            if (InstanceManager.getFilter().isWhitelisted(tagObject)) {
                InstanceManager.getFilter().blacklist(tagObject);
                if (InstanceManager.getFilter().isBlacklisted(tagObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorNeg());
                } else if (!InstanceManager.getFilter().isWhitelisted(tagObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorDef());
                }
                nameNode.setTextFill(ColorUtil.getTextColorNeg());
            } else if (InstanceManager.getFilter().isBlacklisted(tagObject)) {
                InstanceManager.getFilter().unlist(tagObject);
                if (!InstanceManager.getFilter().isWhitelisted(tagObject.getGroup()) && !InstanceManager.getFilter().isBlacklisted(tagObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorDef());
                }
                nameNode.setTextFill(ColorUtil.getTextColorDef());
            } else {
                InstanceManager.getFilter().whitelist(tagObject);
                if (InstanceManager.getFilter().isWhitelisted(tagObject.getGroup())) {
                    groupNode.setTextFill(ColorUtil.getTextColorPos());
                }
                nameNode.setTextFill(ColorUtil.getTextColorPos());
            }
        }
        InstanceManager.getFilter().refresh();
    }

    public void expand() {
        //todo finish this
        for (Node node : tagListBox.getChildren()) {
            if (node instanceof GroupNode) {
                expandedGroupsList.add(((GroupNode) node).getText());
            }
        }
        reload();
    }

    public void collapse() {
        expandedGroupsList.clear();
        reload();
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
