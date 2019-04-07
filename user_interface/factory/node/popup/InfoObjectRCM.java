package user_interface.factory.node.popup;

import control.reload.Reload;
import database.object.TagObject;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.commons.text.WordUtils;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.stage.GroupEditStage;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.side.GroupNode;

import java.util.ArrayList;

public class InfoObjectRCM extends RightClickMenu implements InstanceRepo {
    private TagObject tagObject = null;

    public InfoObjectRCM() {
        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        Label nodeEdit = NodeFactory.getLabel("Edit", colorData);
        Label nodeRemove = NodeFactory.getLabel("Remove", colorData);

        nodeEdit.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (this.getOwnerNode() instanceof GroupNode) {
                    GroupNode groupNode = (GroupNode) this.getOwnerNode();
                    String oldGroup = groupNode.getText();
                    String newGroup = WordUtils.capitalize(new GroupEditStage(oldGroup).getResult().toLowerCase());
                    if (newGroup.isEmpty()) return; //todo show error in the editor instead of returning

                    mainInfoList.forEach(tagObject -> {
                        if (tagObject.getGroup().equals(oldGroup)) {
                            tagObject.setGroup(newGroup);
                        }
                    });

                    ArrayList<String> expandedGroupsL = tagListViewL.getExpandedGroupsList();
                    if (expandedGroupsL.contains(oldGroup)) {
                        expandedGroupsL.remove(oldGroup);
                        expandedGroupsL.add(newGroup);
                    }

                    ArrayList<String> expandedGroupsR = tagListViewR.getExpandedGroupsList();
                    if (expandedGroupsR.contains(oldGroup)) {
                        expandedGroupsR.remove(oldGroup);
                        expandedGroupsR.add(newGroup);
                    }

                    reload.notifyChangeIn(Reload.Control.INFO);
                } else {
                    mainInfoList.edit(tagObject);
                }
                reload.doReload();
            }
        });
        nodeRemove.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mainDataList.forEach(dataObject -> dataObject.getInfoObjectList().remove(tagObject));
                mainInfoList.remove(tagObject);
                reload.doReload();
            }
        });

        this.getChildren().addAll(nodeEdit, nodeRemove);
        this.setOnShown(event -> {
            double width = 0;
            for (Node node : this.getChildren()) {
                if (width < ((Label) node).getWidth()) width = ((Label) node).getWidth();
            }
            for (Node node : this.getChildren()) {
                ((Label) node).setPrefWidth(width);
            }
        });
    }

    public void show(Node anchor, double screenX, double screenY) {
        super.show(anchor, screenX, screenY);
    }
    public void show(Node anchor, MouseEvent event) {
        super.show(anchor, event.getScreenX(), event.getScreenY());
    }

    public void setTagObject(TagObject tagObject) {
        this.tagObject = tagObject;
    }
}
