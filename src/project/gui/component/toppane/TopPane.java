package project.gui.component.toppane;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import project.MainUtils;
import project.database.object.DataObject;

public class TopPane extends BorderPane implements MainUtils {
    private final MenuBar infoLabelMenuBar = new MenuBar();
    private final Menu infoLabelMenu = new Menu();

    private final Menu menuFile = new Menu("File");
    private final MenuItem menuSave = new MenuItem("Save");
    private final MenuItem menuExit = new MenuItem("Exit");

    private final Menu menuSelection = new Menu("Selection");
    private final MenuItem menuSelectAll = new MenuItem("Select All");
    private final MenuItem menuClearSelection = new MenuItem("Clear Selection");

    private final Menu menuFilter = new Menu("Filter");
    private final CheckMenuItem menuUntaggedOnly = new CheckMenuItem("Untagged");
    private final CheckMenuItem menuMaxXTags = new CheckMenuItem("Max X Tags");
    private final Menu menuMode = new Menu("Mode");
    private final Menu menuModeWhitelist = new Menu("Whitelist");
    private final CheckMenuItem menuModeWhitelistAll = new CheckMenuItem("All");
    private final CheckMenuItem menuModeWhitelistAny = new CheckMenuItem("Any");
    private final Menu menuModeBlacklist = new Menu("Blacklist");
    private final CheckMenuItem menuModeBlacklistAll = new CheckMenuItem("All");
    private final CheckMenuItem menuModeBlacklistAny = new CheckMenuItem("Any");
    private final MenuItem menuRefresh = new MenuItem("Refresh");
    private final MenuItem menuReset = new MenuItem("Reset");

    public TopPane() {
        menuFile.getItems().addAll(menuSave, new SeparatorMenuItem(), menuExit);
        menuSelection.getItems().addAll(menuSelectAll, menuClearSelection);

        menuMode.getItems().addAll(menuModeWhitelist, menuModeBlacklist);
        menuModeWhitelist.getItems().addAll(menuModeWhitelistAll, menuModeWhitelistAny);
        menuModeBlacklist.getItems().addAll(menuModeBlacklistAll, menuModeBlacklistAny);
        menuModeWhitelistAll.setSelected(true);
        menuModeBlacklistAny.setSelected(true);
        menuFilter.getItems().addAll(menuUntaggedOnly, menuMaxXTags, new SeparatorMenuItem(), menuMode, menuRefresh, menuReset);

        infoLabelMenuBar.getMenus().add(infoLabelMenu);

        this.setCenter(new MenuBar(menuFile, menuSelection, menuFilter));
        this.setRight(infoLabelMenuBar);
    }

    public void reload() {
        DataObject currentFocusedItem = focusControl.getCurrentFocus();
        if (currentFocusedItem != null) {
            infoLabelMenu.setText(currentFocusedItem.getName());
        }
    }

    public MenuItem getMenuSave() {
        return menuSave;
    }
    public MenuItem getMenuExit() {
        return menuExit;
    }

    public MenuItem getMenuSelectAll() {
        return menuSelectAll;
    }
    public MenuItem getMenuClearSelection() {
        return menuClearSelection;
    }

    public CheckMenuItem getMenuUntaggedOnly() {
        return menuUntaggedOnly;
    }
    public CheckMenuItem getMenuMaxXTags() {
        return menuMaxXTags;
    }
    public CheckMenuItem getMenuModeWhitelistAll() {
        return menuModeWhitelistAll;
    }
    public CheckMenuItem getMenuModeWhitelistAny() {
        return menuModeWhitelistAny;
    }
    public CheckMenuItem getMenuModeBlacklistAll() {
        return menuModeBlacklistAll;
    }
    public CheckMenuItem getMenuModeBlacklistAny() {
        return menuModeBlacklistAny;
    }
    public MenuItem getMenuRefresh() {
        return menuRefresh;
    }
    public MenuItem getMenuReset() {
        return menuReset;
    }
}
