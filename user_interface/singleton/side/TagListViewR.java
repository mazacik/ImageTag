package user_interface.singleton.side;

import control.reload.Reload;
import database.object.DataObject;
import database.object.TagObject;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
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
import user_interface.singleton.BaseNode;

import java.util.ArrayList;
import java.util.Comparator;

public class TagListViewR extends VBox implements BaseNode, InstanceRepo {
    private final Label nodeTitle;
    private final TextField tfSearch;
    private String actualText = "";

    private final Label nodeSelectAll;
    private final Label nodeSelectNone;
    private final Label nodeSelectMerge;

    private final VBox tagListBox;
    private final Label nodeEmptySelection;
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
        nodeEmptySelection = NodeFactory.getLabel("Selection is empty", ColorType.DEF, ColorType.DEF);
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
        //this.setMinWidth(SceneUtil.getSidePanelMinWidth());
        this.getChildren().addAll(nodeTitle, tfSearch, tagListScrollPane);
    }

    public void reload() {
        ObservableList<Node> tagListNodes = tagListBox.getChildren();
        tagListNodes.clear();

        if (select.size() == 0) {
            nodeTitle.setText("Selection: 0");
            tagListNodes.add(nodeEmptySelection);
            return;
        }

        int hiddenTiles = 0;
        for (DataObject dataObject : select) {
            if (!filter.contains(dataObject)) {
                hiddenTiles++;
            }
        }

        String text = "Selection: " + select.size();
        if (hiddenTiles > 0) {
            text += ", " + hiddenTiles + " tiles hidden";
        }
        nodeTitle.setText(text);

        ArrayList<String> groupsInter = select.getIntersectingTags().getGroups();
        ArrayList<String> groupsShare = select.getSharedTags().getGroups();
        ArrayList<String> groupsAll = mainInfoList.getGroups();

        Color textColorDefault = ColorUtil.getTextColorDef();
        Color textColorPositive = ColorUtil.getTextColorPos();
        Color textColorIntersect = ColorUtil.getTextColorInt();

        for (String groupInter : groupsInter) {
            groupsShare.remove(groupInter);
            groupsAll.remove(groupInter);
        }
        for (String groupShare : groupsShare) {
            groupsAll.remove(groupShare);
        }
        for (String group : mainInfoList.getGroups()) {
            //todo this needs a rework... someday
            if (groupsInter.contains(group)) {
                GroupNode groupNode = NodeFactory.getGroupNode(this, group, textColorPositive);
                ArrayList<String> namesInter = select.getIntersectingTags().getNames(group);
                ArrayList<String> namesShare = select.getSharedTags().getNames(group);
                ArrayList<String> namesAll = mainInfoList.getNames(group);

                for (String nameInter : namesInter) {
                    namesShare.remove(nameInter);
                    namesAll.remove(nameInter);

                    Label nameNode = NodeFactory.getLabel(nameInter, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                    nameNode.setTextFill(textColorPositive);
                    nameNode.prefWidthProperty().bind(tagListBox.widthProperty());
                    nameNode.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            changeNodeState(groupNode, nameNode);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            infoObjectRCM.setTagObject(mainInfoList.getTagObject(groupNode.getText(), nameNode.getText()));
                            infoObjectRCM.show(nameNode, event);
                        }
                    });
                    nameNode.setPadding(new Insets(0, 0, 0, 50));
                    nameNode.setAlignment(Pos.CENTER_LEFT);
                    groupNode.getNameNodes().add(nameNode);
                }
                for (String nameShare : namesShare) {
                    namesAll.remove(nameShare);

                    Label nameNode = NodeFactory.getLabel(nameShare, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                    nameNode.setTextFill(textColorIntersect);
                    nameNode.prefWidthProperty().bind(tagListBox.widthProperty());
                    nameNode.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            changeNodeState(groupNode, nameNode);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            infoObjectRCM.setTagObject(mainInfoList.getTagObject(groupNode.getText(), nameNode.getText()));
                            infoObjectRCM.show(nameNode, event);
                        }
                    });
                    nameNode.setPadding(new Insets(0, 0, 0, 50));
                    nameNode.setAlignment(Pos.CENTER_LEFT);
                    groupNode.getNameNodes().add(nameNode);
                }
                for (String nameAll : namesAll) {
                    Label nameNode = NodeFactory.getLabel(nameAll, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                    nameNode.setTextFill(textColorDefault);
                    nameNode.prefWidthProperty().bind(tagListBox.widthProperty());
                    nameNode.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            changeNodeState(groupNode, nameNode);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            infoObjectRCM.setTagObject(mainInfoList.getTagObject(groupNode.getText(), nameNode.getText()));
                            infoObjectRCM.show(nameNode, event);
                        }
                    });
                    nameNode.setPadding(new Insets(0, 0, 0, 50));
                    nameNode.setAlignment(Pos.CENTER_LEFT);
                    groupNode.getNameNodes().add(nameNode);
                }

                groupNode.getNameNodes().sort(Comparator.comparing(Labeled::getText));

                tagListNodes.add(groupNode);
                if (expandedGroupsList.contains(group)) {
                    groupNode.setArrowExpanded(true);
                    tagListNodes.addAll(groupNode.getNameNodes());
                }
            } else if (groupsShare.contains(group)) {
                GroupNode groupNode = NodeFactory.getGroupNode(this, group, textColorIntersect);
                ArrayList<String> namesShare = select.getSharedTags().getNames(group);
                ArrayList<String> namesAll = mainInfoList.getNames(group);

                for (String nameShare : namesShare) {
                    namesAll.remove(nameShare);

                    Label nameNode = NodeFactory.getLabel(nameShare, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                    nameNode.setTextFill(textColorIntersect);
                    nameNode.prefWidthProperty().bind(tagListBox.widthProperty());
                    nameNode.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            changeNodeState(groupNode, nameNode);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            infoObjectRCM.setTagObject(mainInfoList.getTagObject(groupNode.getText(), nameNode.getText()));
                            infoObjectRCM.show(nameNode, event);
                        }
                    });
                    nameNode.setPadding(new Insets(0, 0, 0, 50));
                    nameNode.setAlignment(Pos.CENTER_LEFT);
                    groupNode.getNameNodes().add(nameNode);
                }
                for (String nameAll : namesAll) {
                    Label nameNode = NodeFactory.getLabel(nameAll, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                    nameNode.setTextFill(textColorDefault);
                    nameNode.prefWidthProperty().bind(tagListBox.widthProperty());
                    nameNode.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            changeNodeState(groupNode, nameNode);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            infoObjectRCM.setTagObject(mainInfoList.getTagObject(groupNode.getText(), nameNode.getText()));
                            infoObjectRCM.show(nameNode, event);
                        }
                    });
                    nameNode.setPadding(new Insets(0, 0, 0, 50));
                    nameNode.setAlignment(Pos.CENTER_LEFT);
                    groupNode.getNameNodes().add(nameNode);
                }

                tagListNodes.add(groupNode);
                if (expandedGroupsList.contains(group)) {
                    groupNode.setArrowExpanded(true);
                    tagListNodes.addAll(groupNode.getNameNodes());
                }
            } else {
                GroupNode groupNode = NodeFactory.getGroupNode(this, group, textColorDefault);
                ArrayList<String> namesAll = mainInfoList.getNames(group);

                for (String nameAll : namesAll) {
                    Label nameNode = NodeFactory.getLabel(nameAll, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                    nameNode.setTextFill(textColorDefault);
                    nameNode.prefWidthProperty().bind(tagListBox.widthProperty());
                    nameNode.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            changeNodeState(groupNode, nameNode);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            infoObjectRCM.setTagObject(mainInfoList.getTagObject(groupNode.getText(), nameNode.getText()));
                            infoObjectRCM.show(nameNode, event);
                        }
                    });
                    nameNode.setPadding(new Insets(0, 0, 0, 50));
                    nameNode.setAlignment(Pos.CENTER_LEFT);
                    groupNode.getNameNodes().add(nameNode);
                }

                tagListNodes.add(groupNode);
                if (expandedGroupsList.contains(group)) {
                    groupNode.setArrowExpanded(true);
                    tagListNodes.addAll(groupNode.getNameNodes());
                }
            }
        }

        CommonUtil.updateNodeProperties(tagListBox);
    }
    public void onShown() {
        tagListScrollPane.lookup(".scroll-bar").setStyle("-fx-background-color: transparent;");
        tagListScrollPane.lookup(".increment-button").setStyle("-fx-background-color: transparent;");
        tagListScrollPane.lookup(".decrement-button").setStyle("-fx-background-color: transparent;");
        tagListScrollPane.lookup(".thumb").setStyle("-fx-background-color: gray; -fx-background-insets: 0 4 0 4;");
    }

    public void changeNodeState(GroupNode groupNode, Label nameNode) {
        TagObject tagObject = mainInfoList.getTagObject(groupNode.getText(), nameNode.getText());
        if (nameNode.getTextFill().equals(ColorUtil.getTextColorPos()) || nameNode.getTextFill().equals(ColorUtil.getTextColorInt())) {
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
