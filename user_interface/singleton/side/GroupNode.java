package user_interface.singleton.side;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.ColorUtil;

import java.util.ArrayList;


public class GroupNode extends HBox implements InstanceRepo {
    private final ArrayList<Label> nameNodes = new ArrayList<>();
    private Label labelArrow;
    private Label labelText;

    public GroupNode(String text, VBox owner) {
        labelArrow = NodeFactory.getLabel("+ ");
        labelText = NodeFactory.getLabel(text);

        labelArrow.setPadding(new Insets(0, 5, 0, 15));
        labelText.setPadding(new Insets(0, 15, 0, 5));

        HBox.setHgrow(labelText, Priority.ALWAYS);

        this.getChildren().addAll(labelArrow, labelText);
        labelArrow.setOnMouseClicked(event -> {
            event.consume();
            switch (event.getButton()) {
                case PRIMARY:
                    if (owner == tagListViewL) {
                        if (!tagListViewL.getExpandedGroupsList().contains(labelText.getText())) {
                            tagListViewL.getExpandedGroupsList().add(labelText.getText());
                            ObservableList<Node> nodes = tagListViewL.getTagListBox().getChildren();
                            int index = nodes.indexOf(this) + 1;
                            nodes.addAll(index, nameNodes);
                            CommonUtil.updateNodeProperties(tagListViewL.getTagListBox());
                            labelArrow.setText("− ");
                        } else {
                            tagListViewL.getExpandedGroupsList().remove(labelText.getText());
                            ObservableList<Node> nodes = tagListViewL.getTagListBox().getChildren();
                            nodes.removeAll(nameNodes);
                            labelArrow.setText("+ ");
                        }
                    } else if (owner == tagListViewR) {
                        if (!tagListViewR.getExpandedGroupsList().contains(labelText.getText())) {
                            tagListViewR.getExpandedGroupsList().add(labelText.getText());
                            ObservableList<Node> nodes = tagListViewR.getTagListBox().getChildren();
                            int index = nodes.indexOf(this) + 1;
                            nodes.addAll(index, nameNodes);
                            CommonUtil.updateNodeProperties(tagListViewR.getTagListBox());
                            labelArrow.setText("− ");
                        } else {
                            tagListViewR.getExpandedGroupsList().remove(labelText.getText());
                            ObservableList<Node> nodes = tagListViewR.getTagListBox().getChildren();
                            nodes.removeAll(nameNodes);
                            labelArrow.setText("+ ");
                        }
                    }
                    this.setBackground(ColorUtil.getBackgroundAlt());
                    break;
                case SECONDARY:
                    break;
                default:
                    break;
            }
        });
        this.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    if (owner == tagListViewL) {
                        tagListViewL.changeNodeState(this, null);
                        reload.doReload();
                        CommonUtil.updateNodeProperties(tagListViewL.getTagListBox());
                    }
                    break;
                case SECONDARY:
                    infoObjectRCM.show(this, event);
                    break;
                default:
                    break;
            }
        });
    }

    public ArrayList<Label> getNameNodes() {
        return nameNodes;
    }
    public String getText() {
        return labelText.getText();
    }

    public void setArrowExpanded(boolean value) {
        if (value) {
            labelArrow.setText("− ");
        } else {
            labelArrow.setText("+ ");
        }
    }
    public void setTextFill(Color textFill) {
        labelArrow.setTextFill(textFill);
        labelText.setTextFill(textFill);
    }
    public void setFont(Font font) {
        labelArrow.setFont(font);
        labelText.setFont(font);
    }
}
