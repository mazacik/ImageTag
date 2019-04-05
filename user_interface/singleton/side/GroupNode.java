package user_interface.singleton.side;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;

import java.util.ArrayList;


public class GroupNode extends HBox implements InstanceRepo {
    private final ArrayList<Label> nameNodes = new ArrayList<>();
    private Label labelArrow;
    private Label labelText;

    public GroupNode(String text) {
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
                    if (this.getParent().getParent() instanceof InfoListViewL) {
                        if (!infoListViewL.getExpandedGroupsList().contains(labelText.getText())) {
                            infoListViewL.getExpandedGroupsList().add(labelText.getText());
                            ObservableList<Node> nodes = infoListViewL.getInfoObjectVBox().getChildren();
                            int index = nodes.indexOf(this) + 1;
                            nodes.addAll(index, nameNodes);
                            CommonUtil.updateNodeProperties(infoListViewL.getInfoObjectVBox());
                            labelArrow.setText("− ");
                        } else {
                            infoListViewL.getExpandedGroupsList().remove(labelText.getText());
                            ObservableList<Node> nodes = infoListViewL.getInfoObjectVBox().getChildren();
                            nodes.removeAll(nameNodes);
                            labelArrow.setText("+ ");
                        }
                    } else if (this.getParent().getParent() instanceof InfoListViewR) {
                        if (!infoListViewR.getExpandedGroupsList().contains(labelText.getText())) {
                            infoListViewR.getExpandedGroupsList().add(labelText.getText());
                            ObservableList<Node> nodes = infoListViewR.getInfoObjectVBox().getChildren();
                            int index = nodes.indexOf(this) + 1;
                            nodes.addAll(index, nameNodes);
                            CommonUtil.updateNodeProperties(infoListViewR.getInfoObjectVBox());
                            labelArrow.setText("− ");
                        } else {
                            infoListViewR.getExpandedGroupsList().remove(labelText.getText());
                            ObservableList<Node> nodes = infoListViewR.getInfoObjectVBox().getChildren();
                            nodes.removeAll(nameNodes);
                            labelArrow.setText("+ ");
                        }
                    }
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
                    if (this.getParent().getParent() instanceof InfoListViewL) {
                        infoListViewL.changeNodeState(this, null);
                        reload.doReload();
                        CommonUtil.updateNodeProperties(infoListViewL.getInfoObjectVBox());
                    }
                    break;
                case SECONDARY:
                    if (this.getParent().getParent() instanceof InfoListViewL) {
                        infoObjectRCM.show(this, event);
                    }
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
    public Paint getTextFill() {
        return labelText.getTextFill();
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
