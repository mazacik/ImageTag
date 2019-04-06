package user_interface.singleton.side;

import control.reload.Reload;
import database.object.DataObject;
import database.object.InfoObject;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
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

public class InfoListViewR extends VBox implements BaseNode, InstanceRepo {
    private final Label nodeText = NodeFactory.getLabel("", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
    private final VBox infoObjectVBox = NodeFactory.getVBox(ColorType.DEF);
    private final ScrollPane infoObjectScrollPane = new ScrollPane();
    private final ArrayList<String> expandedGroupsList = new ArrayList<>();

    private final TextField textField = new TextField();
    private String actualText = "";

    private final Label nodeSelectAll;
    private final Label nodeSelectNone;
    private final Label nodeSelectMerge;

    public InfoListViewR() {
        nodeText.prefWidthProperty().bind(this.widthProperty());
        nodeText.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        nodeText.setFont(CommonUtil.getFont());

        textField.setFont(CommonUtil.getFont());
        textField.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (select.size() == 0) {
                return;
            }
            if (event.getCode().isLetterKey()) {
                actualText += event.getText().toLowerCase();
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                actualText = actualText.substring(0, actualText.length() - 1);
            }
        });
        textField.setOnKeyTyped(event -> {
            if (select.size() == 0) {
                actualText = "";
                textField.clear();
                return;
            }
            if (actualText.isEmpty()) {
                textField.clear();
                return;
            }
            if (actualText.length() < 3) {
                textField.setText(actualText);
                textField.positionCaret(textField.getLength());
            }
            if (actualText.length() >= 3) {
                ArrayList<InfoObject> matches = new ArrayList();
                for (InfoObject infoObject : mainInfoList) {
                    if (infoObject.getGroupAndName().toLowerCase().contains(actualText.toLowerCase())) {
                        matches.add(infoObject);
                    }
                }

                InfoObject bestMatch = null;
                //tag's name starts with actualtext
                for (InfoObject infoObject : matches) {
                    if (infoObject.getName().toLowerCase().startsWith(actualText)) {
                        bestMatch = infoObject;
                        break;
                    }

                }
                if (bestMatch == null) {
                    //tag's name contains actualtext
                    for (InfoObject infoObject : matches) {
                        if (infoObject.getName().toLowerCase().contains(actualText)) {
                            bestMatch = infoObject;
                            break;
                        }

                    }
                }
                if (bestMatch == null) {
                    //tag (group and name) contains actualtext
                    for (InfoObject infoObject : matches) {
                        if (infoObject.getGroupAndName().toLowerCase().contains(actualText)) {
                            bestMatch = infoObject;
                            break;
                        }

                    }
                }

                if (bestMatch != null) {
                    String groupAndName = bestMatch.getGroupAndName();
                    textField.setText(groupAndName);
                    int caretPos = groupAndName.toLowerCase().lastIndexOf(actualText.toLowerCase()) + actualText.length();
                    textField.positionCaret(caretPos);
                }

                if (matches.isEmpty()) {
                    textField.setText(actualText);
                    textField.positionCaret(textField.getLength());
                }
            }
        });
        textField.setOnAction(event -> {
            InfoObject infoObject = mainInfoList.getInfoObject(textField.getText());
            if (infoObject != null) {
                this.addTagObjectToSelection(infoObject);
                textField.clear();
                actualText = "";
                reload.notifyChangeIn(Reload.Control.INFO);
                reload.doReload();
            }
        });
        textField.setPromptText("Start typing..");
        NodeFactory.addNodeToManager(textField, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        nodeSelectAll = NodeFactory.getLabel("Select All", colorData);
        nodeSelectNone = NodeFactory.getLabel("Select None", colorData);
        nodeSelectMerge = NodeFactory.getLabel("Merge Selection", colorData);
        new LeftClickMenu(nodeText, Direction.LEFT, nodeSelectAll, nodeSelectNone, nodeSelectMerge);

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
        this.getChildren().addAll(nodeText, textField, infoObjectScrollPane);
    }

    public void changeNodeState(GroupNode groupNode, Label nameNode) {
        InfoObject infoObject = mainInfoList.getInfoObject(groupNode.getText(), nameNode.getText());
        if (nameNode.getTextFill().equals(ColorUtil.getTextColorPos()) || nameNode.getTextFill().equals(ColorUtil.getTextColorInt())) {
            nameNode.setTextFill(ColorUtil.getTextColorDef());
            this.removeTagObjectFromSelection(infoObject);
        } else {
            nameNode.setTextFill(ColorUtil.getTextColorPos());
            this.addTagObjectToSelection(infoObject);
        }

        reload.notifyChangeIn(Reload.Control.INFO);
        reload.doReload();
    }
    public void addTagObjectToSelection(InfoObject infoObject) {
        if (select.size() < 1) {
            DataObject currentTargetedItem = target.getCurrentTarget();
            if (currentTargetedItem != null) {
                currentTargetedItem.getInfoObjectList().add(infoObject);
            }
        } else {
            select.addTagObject(infoObject);
        }
    }
    public void removeTagObjectFromSelection(InfoObject infoObject) {
        if (select.size() < 1) {
            DataObject currentTargetedItem = target.getCurrentTarget();
            if (currentTargetedItem != null) {
                currentTargetedItem.getInfoObjectList().remove(infoObject);
            }
        } else {
            select.removeTagObject(infoObject);
        }
    }

    public void reload() {
        ObservableList<Node> nodes = infoObjectVBox.getChildren();
        nodes.clear();

        if (select.size() == 0) {
            nodeText.setText("Selection: 0");
            nodes.add(NodeFactory.getLabel("Selection is empty", ColorType.DEF, ColorType.DEF));
            CommonUtil.updateNodeProperties(infoObjectVBox);
            return;
        }

        int hidden = 0;
        for (DataObject dataObject : select) {
            if (!filter.contains(dataObject)) {
                hidden++;
            }
        }

        String text = "Selection: " + select.size();
        if (hidden > 0) {
            text += ", " + hidden + " hidden";
        }
        nodeText.setText(text);

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
                    nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                    nameNode.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            changeNodeState(groupNode, nameNode);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            infoObjectRCM.setInfoObject(mainInfoList.getInfoObject(groupNode.getText(), nameNode.getText()));
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
                    nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                    nameNode.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            changeNodeState(groupNode, nameNode);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            infoObjectRCM.setInfoObject(mainInfoList.getInfoObject(groupNode.getText(), nameNode.getText()));
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
                    nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                    nameNode.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            changeNodeState(groupNode, nameNode);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            infoObjectRCM.setInfoObject(mainInfoList.getInfoObject(groupNode.getText(), nameNode.getText()));
                            infoObjectRCM.show(nameNode, event);
                        }
                    });
                    nameNode.setPadding(new Insets(0, 0, 0, 50));
                    nameNode.setAlignment(Pos.CENTER_LEFT);
                    groupNode.getNameNodes().add(nameNode);
                }

                groupNode.getNameNodes().sort(Comparator.comparing(Labeled::getText));

                nodes.add(groupNode);
                if (expandedGroupsList.contains(group)) {
                    groupNode.setArrowExpanded(true);
                    nodes.addAll(groupNode.getNameNodes());
                }
            } else if (groupsShare.contains(group)) {
                GroupNode groupNode = NodeFactory.getGroupNode(this, group, textColorIntersect);
                ArrayList<String> namesShare = select.getSharedTags().getNames(group);
                ArrayList<String> namesAll = mainInfoList.getNames(group);

                for (String nameShare : namesShare) {
                    namesAll.remove(nameShare);

                    Label nameNode = NodeFactory.getLabel(nameShare, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                    nameNode.setTextFill(textColorIntersect);
                    nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                    nameNode.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            changeNodeState(groupNode, nameNode);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            infoObjectRCM.setInfoObject(mainInfoList.getInfoObject(groupNode.getText(), nameNode.getText()));
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
                    nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                    nameNode.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            changeNodeState(groupNode, nameNode);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            infoObjectRCM.setInfoObject(mainInfoList.getInfoObject(groupNode.getText(), nameNode.getText()));
                            infoObjectRCM.show(nameNode, event);
                        }
                    });
                    nameNode.setPadding(new Insets(0, 0, 0, 50));
                    nameNode.setAlignment(Pos.CENTER_LEFT);
                    groupNode.getNameNodes().add(nameNode);
                }

                nodes.add(groupNode);
                if (expandedGroupsList.contains(group)) {
                    groupNode.setArrowExpanded(true);
                    nodes.addAll(groupNode.getNameNodes());
                }
            } else {
                GroupNode groupNode = NodeFactory.getGroupNode(this, group, textColorDefault);
                ArrayList<String> namesAll = mainInfoList.getNames(group);

                for (String nameAll : namesAll) {
                    Label nameNode = NodeFactory.getLabel(nameAll, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                    nameNode.setTextFill(textColorDefault);
                    nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                    nameNode.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            changeNodeState(groupNode, nameNode);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            infoObjectRCM.setInfoObject(mainInfoList.getInfoObject(groupNode.getText(), nameNode.getText()));
                            infoObjectRCM.show(nameNode, event);
                        }
                    });
                    nameNode.setPadding(new Insets(0, 0, 0, 50));
                    nameNode.setAlignment(Pos.CENTER_LEFT);
                    groupNode.getNameNodes().add(nameNode);
                }

                nodes.add(groupNode);
                if (expandedGroupsList.contains(group)) {
                    groupNode.setArrowExpanded(true);
                    nodes.addAll(groupNode.getNameNodes());
                }
            }
        }

        CommonUtil.updateNodeProperties(infoObjectVBox);
    }
    public void onShown() {
        infoObjectScrollPane.lookup(".scroll-bar").setStyle("-fx-background-color: transparent;");
        infoObjectScrollPane.lookup(".increment-button").setStyle("-fx-background-color: transparent;");
        infoObjectScrollPane.lookup(".decrement-button").setStyle("-fx-background-color: transparent;");
        infoObjectScrollPane.lookup(".thumb").setStyle("-fx-background-color: gray; -fx-background-insets: 0 4 0 4;");
    }

    public VBox getInfoObjectBox() {
        return infoObjectVBox;
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
}
