package project.gui.component.toppane;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import project.control.FocusControl;
import project.database.element.DataObject;
import project.gui.event.listener.toppane.UserInputTopPane;

public abstract class TopPane {
    /* components */
    private static final BorderPane _this = new BorderPane();

    private static final MenuBar infoLabelMenuBar = new MenuBar();
    private static final Menu infoLabelMenu = new Menu();

    private static final Menu menuFile = new Menu("File");
    private static final MenuItem menuSave = new MenuItem("Save");
    private static final MenuItem menuExit = new MenuItem("Exit");

    private static final Menu menuSelection = new Menu("Selection");
    private static final MenuItem menuSelectAll = new MenuItem("Select All");
    private static final MenuItem menuClearSelection = new MenuItem("Clear Selection");

    private static final Menu menuFilter = new Menu("Filter");
    private static final CheckMenuItem menuUntaggedOnly = new CheckMenuItem("Untagged");
    private static final CheckMenuItem menuMaxXTags = new CheckMenuItem("Max X Tags");
    private static final MenuItem menuRefresh = new MenuItem("Refresh");
    private static final MenuItem menuReset = new MenuItem("Reset");

    /* initialize */
    public static void initialize() {
        initializeComponents();
        initializeInstance();
        UserInputTopPane.initialize();
    }
    private static void initializeComponents() {
        menuFile.getItems().addAll(menuSave, new SeparatorMenuItem(), menuExit);
        menuSelection.getItems().addAll(menuSelectAll, menuClearSelection);
        menuFilter.getItems().addAll(menuUntaggedOnly, menuMaxXTags, new SeparatorMenuItem(), menuRefresh, menuReset);

        infoLabelMenuBar.getMenus().add(infoLabelMenu);
    }
    private static void initializeInstance() {
        _this.setCenter(new MenuBar(menuFile, menuSelection, menuFilter));
        _this.setRight(infoLabelMenuBar);
    }

    /* public */
    public static void reload() {
        DataObject currentFocusedItem = FocusControl.getCurrentFocus();
        if (currentFocusedItem != null) {
            infoLabelMenu.setText(currentFocusedItem.getName());
        }
    }

    /* get */
    public static MenuItem getMenuSave() {
        return menuSave;
    }
    public static MenuItem getMenuExit() {
        return menuExit;
    }

    public static MenuItem getMenuSelectAll() {
        return menuSelectAll;
    }
    public static MenuItem getMenuClearSelection() {
        return menuClearSelection;
    }

    public static CheckMenuItem getMenuUntaggedOnly() {
        return menuUntaggedOnly;
    }
    public static CheckMenuItem getMenuMaxXTags() {
        return menuMaxXTags;
    }
    public static MenuItem getMenuRefresh() {
        return menuRefresh;
    }
    public static MenuItem getMenuReset() {
        return menuReset;
    }
    public static Region getInstance() {
        return _this;
    }
}
