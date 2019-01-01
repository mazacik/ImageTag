package userinterface.node.topmenu;

import control.filter.Filter;
import control.filter.FilterTemplate;
import database.object.DataObject;
import javafx.stage.WindowEvent;
import userinterface.template.generic.NumberInputWindow;
import utils.MainUtil;

public class TopMenuEvent implements MainUtil {
    public TopMenuEvent() {
        onAction_menuSave();
        onAction_menuExit();

        onAction_menuSelectAll();
        onAction_menuClearSelection();

        onAction_menuUntaggedOnly();
        onAction_menuMaxXTags();
        onAction_menuModeWhitelistAll();
        onAction_menuModeWhitelistAny();
        onAction_menuModeBlacklistAll();
        onAction_menuModeBlacklistAny();
        onAction_menuReset();

        onAction_menuRandom();
        onAction_menuFullView();
    }

    private void onAction_menuSave() {
        topMenu.getMenuSave().setOnAction(event ->
                mainListData.writeToDisk());
    }
    private void onAction_menuExit() {
        topMenu.getMenuExit().setOnAction(event -> topMenu.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST)));
    }

    private void onAction_menuSelectAll() {
        topMenu.getMenuSelectAll().setOnAction(event -> {
            select.addAll(filter);
            reload.doReload();
        });
    }
    private void onAction_menuClearSelection() {
        topMenu.getMenuClearSelection().setOnAction(event -> {
            select.clear();
            reload.doReload();
        });
    }

    private void onAction_menuUntaggedOnly() {
        topMenu.getMenuUntaggedOnly().setOnAction(event -> {
            filter.setFilter(FilterTemplate.SHOW_UNTAGGED);
            reload.doReload();
        });
    }
    private void onAction_menuMaxXTags() {
        topMenu.getMenuMaxXTags().setOnAction(event -> {
            int maxTags = new NumberInputWindow("FilterTemplate Settings", "Maximum number of tags:").getResultValue();
            FilterTemplate.setMaxTagsValue(maxTags);

            filter.setFilter(FilterTemplate.SHOW_MAX_X_TAGS);
            reload.doReload();
        });
    }
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
    private void onAction_menuReset() {
        topMenu.getMenuReset().setOnAction(event -> {
            filter.setFilter(FilterTemplate.SHOW_EVERYTHING);
            reload.doReload();
        });
    }

    private void onAction_menuRandom() {
        topMenu.getMenuRandom()._setOnAction(event -> {
            //todo shared with global keybind. move
            DataObject dataObject = filter.getRandomObject();
            select.set(dataObject);
            target.set(dataObject);
            reload.doReload();
        });
    }
    private void onAction_menuFullView() {
        topMenu.getMenuFullView()._setOnAction(event -> swapDisplayMode());
    }
}
