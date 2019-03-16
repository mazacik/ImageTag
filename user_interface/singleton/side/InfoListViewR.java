package user_interface.singleton.side;

import control.reload.Reload;
import database.object.DataObject;
import database.object.InfoObject;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.BaseNode;

import java.util.ArrayList;

public class InfoListViewR extends VBox implements BaseNode, InstanceRepo {
    private final Label nodeText = NodeFactory.getLabel("Selection", ColorType.DEF, ColorType.DEF);
    private final VBox infoObjectVBox = NodeFactory.getVBox(ColorType.DEF);
    private final ArrayList<String> expandedGroupsList = new ArrayList<>();
    private final TextField textField = new TextField();
    private String actualText = "";

    public InfoListViewR() {
        nodeText.prefWidthProperty().bind(this.widthProperty());
        nodeText.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        nodeText.setFont(CommonUtil.getFont());

        textField.setFont(CommonUtil.getFont());
        textField.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                actualText = actualText.substring(0, actualText.length() - 1);
            } else {
                actualText += event.getText();
            }
        });
        textField.setOnKeyTyped(event -> {
            if (actualText.equals("")) {
                textField.setText("");
                return;
            }
            boolean match = false;
            for (InfoObject infoObject : mainInfoList) {
                if (infoObject.getGroupAndName().toLowerCase().contains(actualText.toLowerCase())) {
                    String groupAndName = infoObject.getGroupAndName();
                    textField.setText(groupAndName);
                    int caretPos = groupAndName.toLowerCase().lastIndexOf(actualText.toLowerCase()) + actualText.length();
                    textField.positionCaret(caretPos);
                    match = true;
                    break;
                }
            }
            if (!match) {
                textField.setText(actualText);
                textField.positionCaret(textField.getLength());
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
        NodeFactory.addNodeToBackgroundManager(textField, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        this.setPrefWidth(999);
        this.setMinWidth(200);
        this.getChildren().addAll(nodeText, textField, infoObjectVBox);
    }

    public void changeNodeState(GroupNode groupNode, Label nameNode) {
        InfoObject infoObject = mainInfoList.getInfoObject(groupNode.getText(), nameNode.getText());
        if (groupNode.getTextFill().equals(ColorUtil.getTextColorPos()) || groupNode.getTextFill().equals(ColorUtil.getTextColorInt())) {
            nameNode.setTextFill(ColorUtil.getTextColorDef());
            this.removeTagObjectFromSelection(infoObject);
        } else {
            nameNode.setTextFill(ColorUtil.getTextColorPos());
            this.addTagObjectToSelection(infoObject);
        }

        boolean groupPos = false;
        boolean groupNeg = false;

        String group = groupNode.getText();
        ArrayList<String> names = mainInfoList.getNames(group);
        for (String name : names) {
            InfoObject infoObjectSameGroup = mainInfoList.getInfoObject(group, name);
            for (DataObject dataObject : select) {
                if (!groupNeg && !dataObject.getInfoObjectList().contains(infoObjectSameGroup)) {
                    groupNeg = true;
                }
                if (!groupPos && dataObject.getInfoObjectList().contains(infoObjectSameGroup)) {
                    groupPos = true;
                }
            }
        }

        if (groupPos && !groupNeg) {
            groupNode.setTextFill(ColorUtil.getTextColorPos());
        } else if (!groupPos && groupNeg) {
            groupNode.setTextFill(ColorUtil.getTextColorDef());
        }
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

        if (select.size() == 0) return;

        ArrayList<String> groupsInter = select.getIntersectingTags().getGroups();
        ArrayList<String> groupsShare = select.getSharedTags().getGroups();
        ArrayList<String> groupsAll = mainInfoList.getGroups();

        Color textColorDefault = ColorUtil.getTextColorDef();
        Color textColorPositive = ColorUtil.getTextColorPos();
        Color textColorIntersect = ColorUtil.getTextColorInt();

        for (String groupInter : groupsInter) {
            groupsShare.remove(groupInter);
            groupsAll.remove(groupInter);

            GroupNode groupNode = NodeFactory.getGroupNode(groupInter, textColorPositive);
            ArrayList<String> namesInter = select.getIntersectingTags().getNames(groupInter);
            ArrayList<String> namesShare = select.getSharedTags().getNames(groupInter);
            ArrayList<String> namesAll = mainInfoList.getNames(groupInter);

            for (String nameInter : namesInter) {
                namesShare.remove(nameInter);
                namesAll.remove(nameInter);

                Label nameNode = NodeFactory.getLabel(nameInter, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                nameNode.setTextFill(textColorPositive);
                nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                nameNode.setOnMouseClicked(event -> changeNodeState(groupNode, nameNode));
                nameNode.setPadding(new Insets(0, 0, 0, 50));
                nameNode.setAlignment(Pos.CENTER_LEFT);
                groupNode.getNameNodes().add(nameNode);
            }
            for (String nameShare : namesShare) {
                namesAll.remove(nameShare);

                Label nameNode = NodeFactory.getLabel(nameShare, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                nameNode.setTextFill(textColorIntersect);
                nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                nameNode.setOnMouseClicked(event -> changeNodeState(groupNode, nameNode));
                nameNode.setPadding(new Insets(0, 0, 0, 50));
                nameNode.setAlignment(Pos.CENTER_LEFT);
                groupNode.getNameNodes().add(nameNode);
            }
            for (String nameAll : namesAll) {
                Label nameNode = NodeFactory.getLabel(nameAll, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                nameNode.setTextFill(textColorDefault);
                nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                nameNode.setOnMouseClicked(event -> changeNodeState(groupNode, nameNode));
                nameNode.setPadding(new Insets(0, 0, 0, 50));
                nameNode.setAlignment(Pos.CENTER_LEFT);
                groupNode.getNameNodes().add(nameNode);
            }

            nodes.add(groupNode);
            if (expandedGroupsList.contains(groupNode.getText())) {
                nodes.addAll(groupNode.getNameNodes());
            }
        }
        for (String groupShare : groupsShare) {
            groupsAll.remove(groupShare);
            GroupNode groupNode = NodeFactory.getGroupNode(groupShare, textColorIntersect);
            ArrayList<String> namesShare = select.getSharedTags().getNames(groupShare);
            ArrayList<String> namesAll = mainInfoList.getNames(groupShare);

            for (String nameShare : namesShare) {
                namesAll.remove(nameShare);

                Label nameNode = NodeFactory.getLabel(nameShare, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                nameNode.setTextFill(textColorIntersect);
                nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                nameNode.setOnMouseClicked(event -> changeNodeState(groupNode, nameNode));
                nameNode.setPadding(new Insets(0, 0, 0, 50));
                nameNode.setAlignment(Pos.CENTER_LEFT);
                groupNode.getNameNodes().add(nameNode);
            }
            for (String nameAll : namesAll) {
                Label nameNode = NodeFactory.getLabel(nameAll, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                nameNode.setTextFill(textColorDefault);
                nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                nameNode.setOnMouseClicked(event -> changeNodeState(groupNode, nameNode));
                nameNode.setPadding(new Insets(0, 0, 0, 50));
                nameNode.setAlignment(Pos.CENTER_LEFT);
                groupNode.getNameNodes().add(nameNode);
            }

            nodes.add(groupNode);
            if (expandedGroupsList.contains(groupNode.getText())) {
                nodes.addAll(groupNode.getNameNodes());
            }
        }
        for (String groupAll : groupsAll) {
            GroupNode groupNode = NodeFactory.getGroupNode(groupAll, textColorDefault);
            ArrayList<String> namesAll = mainInfoList.getNames(groupAll);

            for (String nameAll : namesAll) {
                Label nameNode = NodeFactory.getLabel(nameAll, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
                nameNode.setTextFill(textColorDefault);
                nameNode.prefWidthProperty().bind(infoObjectVBox.widthProperty());
                nameNode.setOnMouseClicked(event -> changeNodeState(groupNode, nameNode));
                nameNode.setPadding(new Insets(0, 0, 0, 50));
                nameNode.setAlignment(Pos.CENTER_LEFT);
                groupNode.getNameNodes().add(nameNode);
            }

            nodes.add(groupNode);
            if (expandedGroupsList.contains(groupNode.getText())) {
                nodes.addAll(groupNode.getNameNodes());
            }
        }
    }
    public void postInit() {
        infoObjectVBox.lookupAll(".scroll-bar").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
        infoObjectVBox.lookupAll(".increment-button").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
        infoObjectVBox.lookupAll(".decrement-button").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
        infoObjectVBox.lookupAll(".thumb").forEach(sb -> sb.setStyle("-fx-background-color: gray; -fx-background-insets: 0 4 0 4;"));
    }

    public VBox getInfoObjectVBox() {
        return infoObjectVBox;
    }
    public ArrayList<String> getExpandedGroupsList() {
        return expandedGroupsList;
    }
}
