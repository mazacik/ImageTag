package user_interface.singleton.side;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lifecycle.InstanceManager;
import system.CommonUtil;
import user_interface.factory.base.TextNode;
import user_interface.factory.util.ColorUtil;

import java.util.ArrayList;


public class GroupNode extends HBox {
    private final ArrayList<TextNode> nameNodes = new ArrayList<>();
    private TextNode labelArrow;
    private TextNode labelText;

    public GroupNode(String text, VBox owner) {
        labelArrow = new TextNode("+ ");
        labelText = new TextNode(text);

        labelArrow.setPadding(new Insets(0, 5, 0, 15));
        labelText.setPadding(new Insets(0, 15, 0, 5));

        HBox.setHgrow(labelText, Priority.ALWAYS);

        this.getChildren().addAll(labelArrow, labelText);
        labelArrow.setOnMouseClicked(event -> {
            event.consume();
            switch (event.getButton()) {
                case PRIMARY:
                    if (owner == InstanceManager.getFilterPane()) {
                        if (!InstanceManager.getFilterPane().getExpandedGroupsList().contains(labelText.getText())) {
                            InstanceManager.getFilterPane().getExpandedGroupsList().add(labelText.getText());
                            ObservableList<Node> nodes = InstanceManager.getFilterPane().getTagListBox().getChildren();
                            int index = nodes.indexOf(this) + 1;
                            nodes.addAll(index, nameNodes);
                            CommonUtil.updateNodeProperties(InstanceManager.getFilterPane().getTagListBox());
                            labelArrow.setText("− ");
                        } else {
                            InstanceManager.getFilterPane().getExpandedGroupsList().remove(labelText.getText());
                            ObservableList<Node> nodes = InstanceManager.getFilterPane().getTagListBox().getChildren();
                            nodes.removeAll(nameNodes);
                            labelArrow.setText("+ ");
                        }
                    } else if (owner == InstanceManager.getSelectPane()) {
                        if (!InstanceManager.getSelectPane().getExpandedGroupsList().contains(labelText.getText())) {
                            InstanceManager.getSelectPane().getExpandedGroupsList().add(labelText.getText());
                            ObservableList<Node> nodes = InstanceManager.getSelectPane().getTagListBox().getChildren();
                            int index = nodes.indexOf(this) + 1;
                            nodes.addAll(index, nameNodes);
                            CommonUtil.updateNodeProperties(InstanceManager.getSelectPane().getTagListBox());
                            labelArrow.setText("− ");
                        } else {
                            InstanceManager.getSelectPane().getExpandedGroupsList().remove(labelText.getText());
                            ObservableList<Node> nodes = InstanceManager.getSelectPane().getTagListBox().getChildren();
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
                    if (owner == InstanceManager.getFilterPane()) {
                        InstanceManager.getFilterPane().changeNodeState(this, null);
                        InstanceManager.getReload().doReload();
                        CommonUtil.updateNodeProperties(InstanceManager.getFilterPane().getTagListBox());
                    }
                    break;
                case SECONDARY:
                    InstanceManager.getClickMenuInfo().setGroup(text);
                    InstanceManager.getClickMenuInfo().setName("");
                    InstanceManager.getClickMenuInfo().show(this, event);
                    break;
                default:
                    break;
            }
        });
    }

    public ArrayList<TextNode> getNameNodes() {
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
