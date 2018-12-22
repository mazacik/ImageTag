package gui.template.specific;

import database.object.InfoObject;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import utils.MainUtil;

public class InfoContextMenu extends ContextMenu implements MainUtil {
    private final MenuItem menuEdit = new MenuItem("Edit Tag");
    private final MenuItem menuRemove = new MenuItem("Remove Tag");

    private InfoObject infoObject = null;

    public InfoContextMenu() {
        getItems().addAll(menuEdit, menuRemove);
        menuEdit.setOnAction(event -> mainListInfo.edit(infoObject));
        menuRemove.setOnAction(event -> mainListInfo.remove(infoObject));
    }

    public void setInfoObject(InfoObject infoObject) {
        this.infoObject = infoObject;
    }
}
