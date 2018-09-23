package project.gui.event.toppane;

import javafx.stage.WindowEvent;
import project.MainUtils;
import project.control.Filter;
import project.control.FilterControl;
import project.database.loader.Serialization;
import project.gui.component.GUINode;
import project.gui.custom.generic.NumberInputWindow;

public abstract class TopPaneEvent implements MainUtils {
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
            selectionControl.addDataObject(filterControl.getCollection());
            reloadControl.reload(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
    private static void onAction_menuClearSelection() {
        topPane.getMenuClearSelection().setOnAction(event -> {
            selectionControl.clearDataObjects();
            reloadControl.reload(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }

    private static void onAction_menuUntaggedOnly() {
        topPane.getMenuUntaggedOnly().setOnAction(event -> {
            filterControl.setFilter(Filter.SHOW_UNTAGGED);
            reloadControl.reload(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
    private static void onAction_menuMaxXTags() {
        topPane.getMenuMaxXTags().setOnAction(event -> {
            int maxTags = new NumberInputWindow("Filter Settings", "Maximum number of tags:").getResultValue();
            Filter.setMaxTagsValue(maxTags);

            filterControl.setFilter(Filter.SHOW_MAX_X_TAGS);
            reloadControl.reload(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
    private static void onAction_menuModeWhitelistAll() {
        topPane.getMenuModeWhitelistAll().setOnAction(event -> {
            topPane.getMenuModeWhitelistAll().setSelected(true);
            topPane.getMenuModeWhitelistAny().setSelected(false);
            filterControl.setWhitelistMode(FilterControl.FilterMode.All);
            filterControl.applyFilter();
        });
    }
    private static void onAction_menuModeWhitelistAny() {
        topPane.getMenuModeWhitelistAny().setOnAction(event -> {
            topPane.getMenuModeWhitelistAll().setSelected(false);
            topPane.getMenuModeWhitelistAny().setSelected(true);
            filterControl.setWhitelistMode(FilterControl.FilterMode.Any);
            filterControl.applyFilter();
        });
    }
    private static void onAction_menuModeBlacklistAll() {
        topPane.getMenuModeBlacklistAll().setOnAction(event -> {
            topPane.getMenuModeBlacklistAll().setSelected(true);
            topPane.getMenuModeBlacklistAny().setSelected(false);
            filterControl.setBlacklistMode(FilterControl.FilterMode.All);
            filterControl.applyFilter();
        });
    }
    private static void onAction_menuModeBlacklistAny() {
        topPane.getMenuModeBlacklistAny().setOnAction(event -> {
            topPane.getMenuModeBlacklistAll().setSelected(false);
            topPane.getMenuModeBlacklistAny().setSelected(true);
            filterControl.setBlacklistMode(FilterControl.FilterMode.Any);
            filterControl.applyFilter();
        });
    }
    private static void onAction_menuRefresh() {
        topPane.getMenuRefresh().setOnAction(event -> {
            reloadControl.reload(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
    private static void onAction_menuReset() {
        topPane.getMenuReset().setOnAction(event -> {
            filterControl.setFilter(Filter.SHOW_EVERYTHING);
            reloadControl.reload(true, GUINode.GALLERYPANE, GUINode.RIGHTPANE);
        });
    }
}
