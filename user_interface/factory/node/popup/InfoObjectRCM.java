package user_interface.factory.node.popup;

import database.object.InfoObject;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.enums.ColorType;

public class InfoObjectRCM extends RightClickMenu implements InstanceRepo {
    private InfoObject infoObject = null;

    public InfoObjectRCM() {
        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        Label nodeEdit = NodeFactory.getLabel("Edit Tag", colorData);
        Label nodeRemove = NodeFactory.getLabel("Remove Tag", colorData);

        nodeEdit.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mainInfoList.edit(infoObject);
                reload.doReload();
            }
        });
        nodeRemove.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mainInfoList.remove(infoObject);
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

    public void setInfoObject(InfoObject infoObject) {
        this.infoObject = infoObject;
    }
}
