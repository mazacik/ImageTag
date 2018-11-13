package project.gui.event.toppane;

import javafx.stage.WindowEvent;
import project.control.filter.Filter;
import project.control.filter.FilterTemplate;
import project.database.loader.Serialization;
import project.gui.component.GUINode;
import project.gui.custom.generic.NumberInputWindow;
import project.utils.MainUtil;

public class TopPaneEvent implements MainUtil {
    public TopPaneEvent() {
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
        onAction_menuRefresh();
        onAction_menuReset();
    }

    private void onAction_menuSave() {
        topPane.getMenuSave().setOnAction(event -> Serialization.writeToDisk());
    }
    private void onAction_menuExit() {
        topPane.getMenuExit().setOnAction(event -> topPane.fireEvent(new WindowEvent(customStage, WindowEvent.WINDOW_CLOSE_REQUEST)));
    }

    private void onAction_menuSelectAll() {
        topPane.getMenuSelectAll().setOnAction(event -> {
            selection.addAll(filter);
            reload.queue(GUINode.GALLERYPANE, GUINode.RIGHTPANE);
            reload.doReload();
        });
    }
    private void onAction_menuClearSelection() {
        topPane.getMenuClearSelection().setOnAction(event -> {
            selection.clear();
            reload.queue(GUINode.GALLERYPANE, GUINode.RIGHTPANE);
            reload.doReload();
        });
    }

    private void onAction_menuUntaggedOnly() {
        topPane.getMenuUntaggedOnly().setOnAction(event -> {
            filter.setFilter(FilterTemplate.SHOW_UNTAGGED);
            reload.queue(GUINode.GALLERYPANE, GUINode.RIGHTPANE);
            reload.doReload();
        });
    }
    private void onAction_menuMaxXTags() {
        topPane.getMenuMaxXTags().setOnAction(event -> {
            int maxTags = new NumberInputWindow("FilterTemplate Settings", "Maximum number of tags:").getResultValue();
            FilterTemplate.setMaxTagsValue(maxTags);

            filter.setFilter(FilterTemplate.SHOW_MAX_X_TAGS);
            reload.queue(GUINode.GALLERYPANE, GUINode.RIGHTPANE);
            reload.doReload();
        });
    }
    private void onAction_menuModeWhitelistAll() {
        topPane.getMenuModeWhitelistAll().setOnAction(event -> {
            topPane.getMenuModeWhitelistAll().setSelected(true);
            topPane.getMenuModeWhitelistAny().setSelected(false);
            filter.setWhitelistMode(Filter.FilterMode.All);
            filter.apply();
        });
    }
    private void onAction_menuModeWhitelistAny() {
        topPane.getMenuModeWhitelistAny().setOnAction(event -> {
            topPane.getMenuModeWhitelistAll().setSelected(false);
            topPane.getMenuModeWhitelistAny().setSelected(true);
            filter.setWhitelistMode(Filter.FilterMode.Any);
            filter.apply();
        });
    }
    private void onAction_menuModeBlacklistAll() {
        topPane.getMenuModeBlacklistAll().setOnAction(event -> {
            topPane.getMenuModeBlacklistAll().setSelected(true);
            topPane.getMenuModeBlacklistAny().setSelected(false);
            filter.setBlacklistMode(Filter.FilterMode.All);
            filter.apply();
        });
    }
    private void onAction_menuModeBlacklistAny() {
        topPane.getMenuModeBlacklistAny().setOnAction(event -> {
            topPane.getMenuModeBlacklistAll().setSelected(false);
            topPane.getMenuModeBlacklistAny().setSelected(true);
            filter.setBlacklistMode(Filter.FilterMode.Any);
            filter.apply();
        });
    }
    private void onAction_menuRefresh() {
        topPane.getMenuRefresh().setOnAction(event -> {
            reload.queue(GUINode.GALLERYPANE, GUINode.RIGHTPANE);
            reload.doReload();
        });
    }
    private void onAction_menuReset() {
        topPane.getMenuReset().setOnAction(event -> {
            filter.setFilter(FilterTemplate.SHOW_EVERYTHING);
            reload.queue(GUINode.GALLERYPANE, GUINode.RIGHTPANE);
            reload.doReload();
        });
    }
}
