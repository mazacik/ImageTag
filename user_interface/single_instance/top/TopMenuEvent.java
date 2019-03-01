package user_interface.single_instance.top;

import control.filter.FilterTemplate;
import javafx.stage.WindowEvent;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.node_factory.template.generic.WindowNumberInput;

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
        topMenu.getNodeSave().setOnMouseClicked(event ->
                mainListData.writeToDisk());
    }
    private void onAction_menuExit() {
        topMenu.getNodeExit().setOnMouseClicked(event -> topMenu.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST)));
    }

    private void onAction_menuSelectAll() {
        topMenu.getNodeSelectAll().setOnMouseClicked(event -> {
            select.addAll(filter);
            reload.doReload();
        });
    }
    private void onAction_menuClearSelection() {
        topMenu.getNodeSelectNone().setOnMouseClicked(event -> {
            select.clear();
            reload.doReload();
        });
    }
    private void onAction_menuMergeSelection() {
        topMenu.getNodeSelectMerge().setOnMouseClicked(event -> {
            select.merge();
            reload.doReload();
        });
    }

    private void onAction_menuCustom() {
        topMenu.getNodeCustom().setOnMouseClicked(event -> {
            int maxTags = new WindowNumberInput("Maximum number of tags:").getResult();
            if (maxTags == -1) return;
            FilterTemplate.setMaxTagsValue(maxTags);

            filter.setFilter(FilterTemplate.SHOW_MAX_X_TAGS);
            reload.doReload();
        });
    }
    private void onAction_menuReset() {
        topMenu.getNodeReset().setOnMouseClicked(event -> {
            filter.setFilter(FilterTemplate.SHOW_EVERYTHING);
            reload.doReload();
        });
    }

    /* todo implement "userSettings"
    private void onAction_menuModeWhitelistAll() {
        topMenu.getMenuModeWhitelistAll().setOnAction(event -> {
            topMenu.getMenuModeWhitelistAll().setSelected(true);
            topMenu.getMenuModeWhitelistAny().setSelected(false);
            filter.setWhitelistMode(Filter.FilterMode.All);
            filter.apply();
        });
    }
    private void onAction_menuModeWhitelistAny() {
        topMenu.getMenuModeWhitelistAny().setOnAction(event -> {
            topMenu.getMenuModeWhitelistAll().setSelected(false);
            topMenu.getMenuModeWhitelistAny().setSelected(true);
            filter.setWhitelistMode(Filter.FilterMode.Any);
            filter.apply();
        });
    }
    private void onAction_menuModeBlacklistAll() {
        topMenu.getMenuModeBlacklistAll().setOnAction(event -> {
            topMenu.getMenuModeBlacklistAll().setSelected(true);
            topMenu.getMenuModeBlacklistAny().setSelected(false);
            filter.setBlacklistMode(Filter.FilterMode.All);
            filter.apply();
        });
    }
    private void onAction_menuModeBlacklistAny() {
        topMenu.getMenuModeBlacklistAny().setOnAction(event -> {
            topMenu.getMenuModeBlacklistAll().setSelected(false);
            topMenu.getMenuModeBlacklistAny().setSelected(true);
            filter.setBlacklistMode(Filter.FilterMode.Any);
            filter.apply();
        });
    }
    */

    private void onAction_menuRandom() {
        topMenu.getNodeRandom().setOnMouseClicked(event -> {
            select.setRandom();
            reload.doReload();
        });
    }
    private void onAction_menuFullView() {
        topMenu.getNodeFullview().setOnMouseClicked(event -> CommonUtil.swapDisplayMode());
    }
}
