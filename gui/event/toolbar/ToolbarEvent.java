package gui.event.toolbar;

import control.filter.Filter;
import control.filter.FilterTemplate;
import gui.template.generic.NumberInputWindow;
import javafx.stage.WindowEvent;
import utils.MainUtil;

public class ToolbarEvent implements MainUtil {
    public ToolbarEvent() {
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
    }

    private void onAction_menuSave() {
        toolbar.getMenuSave().setOnAction(event -> MAIN_LIST_DATA.writeToDisk());
    }
    private void onAction_menuExit() {
        toolbar.getMenuExit().setOnAction(event -> toolbar.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST)));
    }

    private void onAction_menuSelectAll() {
        toolbar.getMenuSelectAll().setOnAction(event -> {
            select.addAll(filter);
            reload.doReload();
        });
    }
    private void onAction_menuClearSelection() {
        toolbar.getMenuClearSelection().setOnAction(event -> {
            select.clear();
            reload.doReload();
        });
    }

    private void onAction_menuUntaggedOnly() {
        toolbar.getMenuUntaggedOnly().setOnAction(event -> {
            filter.setFilter(FilterTemplate.SHOW_UNTAGGED);
            reload.doReload();
        });
    }
    private void onAction_menuMaxXTags() {
        toolbar.getMenuMaxXTags().setOnAction(event -> {
            int maxTags = new NumberInputWindow("FilterTemplate Settings", "Maximum number of tags:").getResultValue();
            FilterTemplate.setMaxTagsValue(maxTags);

            filter.setFilter(FilterTemplate.SHOW_MAX_X_TAGS);
            reload.doReload();
        });
    }
    private void onAction_menuModeWhitelistAll() {
        toolbar.getMenuModeWhitelistAll().setOnAction(event -> {
            toolbar.getMenuModeWhitelistAll().setSelected(true);
            toolbar.getMenuModeWhitelistAny().setSelected(false);
            filter.setWhitelistMode(Filter.FilterMode.All);
            filter.apply();
        });
    }
    private void onAction_menuModeWhitelistAny() {
        toolbar.getMenuModeWhitelistAny().setOnAction(event -> {
            toolbar.getMenuModeWhitelistAll().setSelected(false);
            toolbar.getMenuModeWhitelistAny().setSelected(true);
            filter.setWhitelistMode(Filter.FilterMode.Any);
            filter.apply();
        });
    }
    private void onAction_menuModeBlacklistAll() {
        toolbar.getMenuModeBlacklistAll().setOnAction(event -> {
            toolbar.getMenuModeBlacklistAll().setSelected(true);
            toolbar.getMenuModeBlacklistAny().setSelected(false);
            filter.setBlacklistMode(Filter.FilterMode.All);
            filter.apply();
        });
    }
    private void onAction_menuModeBlacklistAny() {
        toolbar.getMenuModeBlacklistAny().setOnAction(event -> {
            toolbar.getMenuModeBlacklistAll().setSelected(false);
            toolbar.getMenuModeBlacklistAny().setSelected(true);
            filter.setBlacklistMode(Filter.FilterMode.Any);
            filter.apply();
        });
    }
    private void onAction_menuReset() {
        toolbar.getMenuReset().setOnAction(event -> {
            filter.setFilter(FilterTemplate.SHOW_EVERYTHING);
            reload.doReload();
        });
    }
}
