package user_interface.singleton.side;

import control.reload.Reload;
import database.object.DataObject;
import database.object.TagObject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.popup.Direction;
import user_interface.factory.node.popup.LeftClickMenu;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.scene.SceneUtil;
import user_interface.singleton.BaseNode;

import java.util.ArrayList;

public class TagListViewR extends VBox implements BaseNode, InstanceRepo {
    private final Label nodeTitle;
    private final TextField tfSearch;
    private String actualText = "";

    private final Label nodeSelectAll;
    private final Label nodeSelectNone;
    private final Label nodeSelectMerge;

    private final VBox tagListBox;
    private final ScrollPane tagListScrollPane;
    private final ArrayList<String> expandedGroupsList;

    public TagListViewR() {
        ColorData colorDataSimple = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        nodeTitle = NodeFactory.getLabel("", colorDataSimple);
        nodeTitle.setBorder(NodeFactory.getBorder(0, 0, 1, 0));
        nodeTitle.prefWidthProperty().bind(this.widthProperty());

        tfSearch = new TextField();
        tfSearch.setFont(CommonUtil.getFont());
        tfSearch.setBorder(NodeFactory.getBorder(0, 0, 1, 0));
        tfSearch.setPromptText("Start typing..");
        NodeFactory.addNodeToManager(tfSearch, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        nodeSelectAll = NodeFactory.getLabel("Select All", colorDataSimple);
        nodeSelectNone = NodeFactory.getLabel("Select None", colorDataSimple);
        nodeSelectMerge = NodeFactory.getLabel("Merge Selection", colorDataSimple);
        LeftClickMenu.install(nodeTitle, Direction.LEFT, nodeSelectAll, nodeSelectNone, NodeFactory.getSeparator(), nodeSelectMerge);

        tagListBox = NodeFactory.getVBox(ColorType.DEF);
        tagListScrollPane = new ScrollPane();
        tagListScrollPane.setContent(tagListBox);
        tagListScrollPane.setFitToWidth(true);
        tagListScrollPane.setFitToHeight(true);
        tagListScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tagListScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        NodeFactory.addNodeToManager(tagListScrollPane, ColorType.DEF);

        expandedGroupsList = new ArrayList<>();

        this.setPrefWidth(SceneUtil.getUsableScreenWidth());
        this.setMinWidth(SceneUtil.getSidePanelMinWidth());
        this.getChildren().addAll(nodeTitle, tfSearch, tagListScrollPane);
    }

    public void reload() {
        tagListBox.getChildren().clear();

        if (select.size() == 0) {
            if (target.getCurrentTarget() != null) {
                nodeTitle.setText("Selection: " + target.getCurrentTarget().getName());
            }
        } else {
            int hiddenTilesCount = 0;
            for (DataObject dataObject : select) {
                if (!filter.contains(dataObject)) {
                    hiddenTilesCount++;
                }
            }

            String text = "Selection: " + select.size() + " file(s)";
            if (hiddenTilesCount > 0) {
                text += ", " + hiddenTilesCount + " filtered out";
            }
            nodeTitle.setText(text);
        }
        this.actuallyReload();
        CommonUtil.updateNodeProperties(tagListBox);
    }
    private void actuallyReload() {
        Color textColorDefault = ColorUtil.getTextColorDef();
        Color textColorPositive = ColorUtil.getTextColorPos();
        Color textColorShare = ColorUtil.getTextColorShr();

        ArrayList<String> groupsInter;
        ArrayList<String> groupsShare;
        if (select.size() == 0) {
            if (target.getCurrentTarget() != null) {
                groupsInter = target.getCurrentTarget().getInfoObjectList().getGroups();
                groupsShare = new ArrayList<>();
            } else {
                return;
            }
        } else {
            groupsInter = select.getIntersectingTags().getGroups();
            groupsShare = select.getSharedTags().getGroups();
        }

        for (String group : mainInfoList.getGroups()) {
            GroupNode groupNode;
            if (groupsInter.contains(group)) {
                groupNode = NodeFactory.getGroupNode(this, group, textColorPositive);
            } else if (groupsShare.contains(group)) {
                groupNode = NodeFactory.getGroupNode(this, group, textColorShare);
            } else {
                groupNode = NodeFactory.getGroupNode(this, group, textColorDefault);
            }

            ArrayList<String> namesInter;
            ArrayList<String> namesShare;
            if (select.size() == 0) {
                namesInter = target.getCurrentTarget().getInfoObjectList().getNames(group);
                namesShare = new ArrayList<>();
            } else {
                namesInter = select.getIntersectingTags().getNames(group);
                namesShare = select.getSharedTags().getNames(group);
            }

            for (String name : mainInfoList.getNames(group)) {
                if (namesInter.contains(name)) {
                    groupNode.getNameNodes().add(this.createNameNode(name, textColorPositive, groupNode));
                } else if (namesShare.contains(name)) {
                    groupNode.getNameNodes().add(this.createNameNode(name, textColorShare, groupNode));
                } else {
                    groupNode.getNameNodes().add(this.createNameNode(name, textColorDefault, groupNode));
                }
            }

            tagListBox.getChildren().add(groupNode);

            if (expandedGroupsList.contains(group)) {
                groupNode.setArrowExpanded(true);
                tagListBox.getChildren().addAll(groupNode.getNameNodes());
            }
        }
    }
    private Label createNameNode(String name, Color textColor, GroupNode groupNode) {
        Label nameNode = NodeFactory.getLabel(name, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);

        nameNode.setTextFill(textColor);
        nameNode.setAlignment(Pos.CENTER_LEFT);
        nameNode.prefWidthProperty().bind(tagListBox.widthProperty());
        nameNode.setPadding(new Insets(0, 0, 0, 50));
        nameNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                changeNodeState(groupNode, nameNode);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                infoObjectRCM.setTagObject(mainInfoList.getTagObject(groupNode.getText(), nameNode.getText()));
                infoObjectRCM.show(nameNode, event);
            }
        });

        return nameNode;
    }

    public void onShown() {
        tagListScrollPane.lookup(".scroll-bar").setStyle("-fx-background-color: transparent;");
        tagListScrollPane.lookup(".increment-button").setStyle("-fx-background-color: transparent;");
        tagListScrollPane.lookup(".decrement-button").setStyle("-fx-background-color: transparent;");
        tagListScrollPane.lookup(".thumb").setStyle("-fx-background-color: gray; -fx-background-insets: 0 4 0 4;");
    }

    public void changeNodeState(GroupNode groupNode, Label nameNode) {
        TagObject tagObject = mainInfoList.getTagObject(groupNode.getText(), nameNode.getText());
        if (nameNode.getTextFill().equals(ColorUtil.getTextColorPos()) || nameNode.getTextFill().equals(ColorUtil.getTextColorShr())) {
            nameNode.setTextFill(ColorUtil.getTextColorDef());
            this.removeTagObjectFromSelection(tagObject);
        } else {
            nameNode.setTextFill(ColorUtil.getTextColorPos());
            this.addTagObjectToSelection(tagObject);
        }

        reload.notifyChangeIn(Reload.Control.INFO);
        reload.doReload();
    }
    public void addTagObjectToSelection(TagObject tagObject) {
        if (select.size() < 1) {
            DataObject currentTargetedItem = target.getCurrentTarget();
            if (currentTargetedItem != null) {
                currentTargetedItem.getInfoObjectList().add(tagObject);
            }
        } else {
            select.addTagObject(tagObject);
        }
    }
    public void removeTagObjectFromSelection(TagObject tagObject) {
        if (select.size() < 1) {
            DataObject currentTargetedItem = target.getCurrentTarget();
            if (currentTargetedItem != null) {
                currentTargetedItem.getInfoObjectList().remove(tagObject);
            }
        } else {
            select.removeTagObject(tagObject);
        }
    }

    public TextField getTfSearch() {
        return tfSearch;
    }
    public String getActualText() {
        return actualText;
    }
    public void setActualText(String actualText) {
        this.actualText = actualText;
    }
    public ArrayList<String> getExpandedGroupsList() {
        return expandedGroupsList;
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
    public VBox getTagListBox() {
        return tagListBox;
    }
}
