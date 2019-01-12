package userinterface.node.topmenu;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import utils.MainUtil;

import java.util.List;

public class CustomMenu extends Label {
    public CustomMenu(List<MenuItem> nodeList) {
        this.setText("test");
        this.setOnMouseClicked(event -> {
            ContextMenu cm = new ContextMenu();
            cm.getItems().addAll(nodeList);
            cm.show(MainUtil.topMenu.getMenuSelection(), Side.BOTTOM, 0, 0);
        });
    }
}
