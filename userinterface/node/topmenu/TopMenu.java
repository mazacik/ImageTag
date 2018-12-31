package userinterface.node.topmenu;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import settings.SettingsNamespace;
import userinterface.node.BaseNode;
import utils.MainUtil;

public class TopMenu extends BorderPane implements MainUtil, BaseNode {
    private final Menu menuFile = new Menu("File");
    private final MenuItem menuSave = new MenuItem("Save");
    private final MenuItem menuExit = new MenuItem("Exit");

    private final Menu menuSelection = new Menu("Select");
    private final MenuItem menuSelectAll = new MenuItem("Select All");
    private final MenuItem menuClearSelection = new MenuItem("Clear Select");

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
    private final MenuItem menuReset = new MenuItem("Reset");

    private final Button btnRandom = new Button("Random");
    private final Button btnFullView = new Button("FullView");

    private final Menu infoLabelMenu = new Menu();

    public TopMenu() {
        //todo refactor and fix height
        this.setPadding(new Insets(settings.valueOf(SettingsNamespace.GLOBAL_SPACING)));

        menuFile.getItems().addAll(menuSave, new SeparatorMenuItem(), menuExit);
        menuSelection.getItems().addAll(menuSelectAll, menuClearSelection);

        menuMode.getItems().addAll(menuModeWhitelist, menuModeBlacklist);
        menuModeWhitelist.getItems().addAll(menuModeWhitelistAll, menuModeWhitelistAny);
        menuModeBlacklist.getItems().addAll(menuModeBlacklistAll, menuModeBlacklistAny);
        menuModeWhitelistAll.setSelected(true);
        menuModeBlacklistAny.setSelected(true);
        menuFilter.getItems().addAll(menuUntaggedOnly, menuMaxXTags, new SeparatorMenuItem(), menuMode, menuReset);

        MenuBar menuBarL = new MenuBar(menuFile, menuSelection, menuFilter);
        ToolBar centerBar = new ToolBar(btnRandom, btnFullView);
        MenuBar menuBarR = new MenuBar(infoLabelMenu);

        menuBarL.setPrefWidth(200);
        menuBarR.setPrefWidth(200);

        menuBarL.minHeightProperty().bind(centerBar.heightProperty());
        menuBarR.minHeightProperty().bind(centerBar.heightProperty());

        this.setLeft(menuBarL);
        this.setCenter(centerBar);
        this.setRight(menuBarR);
    }

    public void reload() {
        String text = select.size() + " items selected";
        infoLabelMenu.setText(text);
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
    public MenuItem getMenuReset() {
        return menuReset;
    }

    public Button getBtnRandom() {
        return btnRandom;
    }
    public Button getBtnFullView() {
        return btnFullView;
    }
}
