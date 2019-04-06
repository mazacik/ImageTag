package user_interface.event;

import javafx.scene.input.MouseButton;
import system.InstanceRepo;
import user_interface.factory.node.popup.LeftClickMenu;

public class InfoListViewREvent implements InstanceRepo {
    public InfoListViewREvent() {
        onMouseClick();

        onAction_menuSelectAll();
        onAction_menuClearSelection();
        onAction_menuMergeSelection();
    }

    private void onMouseClick() {
        infoListViewR.setOnMouseClicked(event -> infoListViewR.requestFocus());
    }

    private void onAction_menuSelectAll() {
        infoListViewR.getNodeSelectAll().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                select.addAll(filter);
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuClearSelection() {
        infoListViewR.getNodeSelectNone().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                select.clear();
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuMergeSelection() {
        infoListViewR.getNodeSelectMerge().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                select.merge();
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }

    private void hideLeftClickMenus() {
        LeftClickMenu.getInstanceList().forEach(leftClickMenu -> {
            if (leftClickMenu.isShowing()) {
                leftClickMenu.hide();
            }
        });
    }
}
