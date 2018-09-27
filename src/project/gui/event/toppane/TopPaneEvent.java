package project.gui.event.toppane;

import javafx.stage.WindowEvent;
import project.control.filter.Filter;
import project.control.filter.FilterData;
import project.database.loader.Serialization;
import project.gui.component.GUINode;
import project.gui.custom.generic.NumberInputWindow;
import project.utils.MainUtil;

public abstract class TopPaneEvent implements MainUtil {
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

    private static void onAction_menuSave() {
        topPane.getMenuSave().setOnAction(event -> Serialization.writeToDisk());
    }
    private static void onAction_menuExit() {
        topPane.getMenuExit().setOnAction(event -> topPane.fireEvent(new WindowEvent(customStage, WindowEvent.WINDOW_CLOSE_REQUEST)));
    }

    private static void onAction_menuSelectAll() {
        topPane.getMenuSelectAll().setOnAction(event -> {
            selection.addAll(filter);
            reload.queue(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
    private static void onAction_menuClearSelection() {
        topPane.getMenuClearSelection().setOnAction(event -> {
            selection.clear();
            reload.queue(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }

    private static void onAction_menuUntaggedOnly() {
        topPane.getMenuUntaggedOnly().setOnAction(event -> {
            filter.setFilter(FilterData.SHOW_UNTAGGED);
            reload.queue(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
    private static void onAction_menuMaxXTags() {
        topPane.getMenuMaxXTags().setOnAction(event -> {
            int maxTags = new NumberInputWindow("FilterData Settings", "Maximum number of tags:").getResultValue();
            FilterData.setMaxTagsValue(maxTags);

            filter.setFilter(FilterData.SHOW_MAX_X_TAGS);
            reload.queue(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
    private static void onAction_menuModeWhitelistAll() {
        topPane.getMenuModeWhitelistAll().setOnAction(event -> {
            topPane.getMenuModeWhitelistAll().setSelected(true);
            topPane.getMenuModeWhitelistAny().setSelected(false);
            filter.setWhitelistMode(Filter.FilterMode.All);
            filter.apply();
        });
    }
    private static void onAction_menuModeWhitelistAny() {
        topPane.getMenuModeWhitelistAny().setOnAction(event -> {
            topPane.getMenuModeWhitelistAll().setSelected(false);
            topPane.getMenuModeWhitelistAny().setSelected(true);
            filter.setWhitelistMode(Filter.FilterMode.Any);
            filter.apply();
        });
    }
    private static void onAction_menuModeBlacklistAll() {
        topPane.getMenuModeBlacklistAll().setOnAction(event -> {
            topPane.getMenuModeBlacklistAll().setSelected(true);
            topPane.getMenuModeBlacklistAny().setSelected(false);
            filter.setBlacklistMode(Filter.FilterMode.All);
            filter.apply();
        });
    }
    private static void onAction_menuModeBlacklistAny() {
        topPane.getMenuModeBlacklistAny().setOnAction(event -> {
            topPane.getMenuModeBlacklistAll().setSelected(false);
            topPane.getMenuModeBlacklistAny().setSelected(true);
            filter.setBlacklistMode(Filter.FilterMode.Any);
            filter.apply();
        });
    }
    private static void onAction_menuRefresh() {
        topPane.getMenuRefresh().setOnAction(event -> {
            reload.queue(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
    private static void onAction_menuReset() {
        topPane.getMenuReset().setOnAction(event -> {
            filter.setFilter(FilterData.SHOW_EVERYTHING);
            reload.queue(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
}
