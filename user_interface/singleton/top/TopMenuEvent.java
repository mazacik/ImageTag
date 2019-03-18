package user_interface.singleton.top;

import control.filter.FilterTemplate;
import javafx.scene.input.MouseButton;
import javafx.stage.WindowEvent;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.node.popup.LeftClickMenu;
import user_interface.factory.stage.NumberInputStage;

public class TopMenuEvent implements InstanceRepo {
    public TopMenuEvent() {
        onAction_menuSave();
        onAction_menuExit();

        onAction_menuSelectAll();
        onAction_menuClearSelection();
        onAction_menuMergeSelection();

        onAction_menuCustom();
        onAction_menuReset();

        onAction_menuRandom();
        onAction_menuFullView();
    }

    private void onAction_menuSave() {
        topMenu.getNodeSave().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mainDataList.writeToDisk();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuExit() {
        topMenu.getNodeExit().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                topMenu.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST));
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuSelectAll() {
        topMenu.getNodeSelectAll().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                select.addAll(filter);
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuClearSelection() {
        topMenu.getNodeSelectNone().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                select.clear();
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuMergeSelection() {
        topMenu.getNodeSelectMerge().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                select.merge();
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }

    private void onAction_menuCustom() {
        topMenu.getNodeCustom().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                int maxTags = new NumberInputStage("Maximum number of tags:").getResult();
                if (maxTags == -1) return;
                FilterTemplate.setMaxTagsValue(maxTags);

                filter.setFilter(FilterTemplate.SHOW_MAX_X_TAGS);
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuReset() {
        topMenu.getNodeReset().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                filter.setFilter(FilterTemplate.SHOW_EVERYTHING);
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

    private void onAction_menuRandom() {
        topMenu.getNodeRandom().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                select.setRandom();
                reload.doReload();
            }
        });
    }
    private void onAction_menuFullView() {
        topMenu.getNodeFullview().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                CommonUtil.swapDisplayMode();
            }
        });
    }
}
