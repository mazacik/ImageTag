package userinterface.node.topmenu;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import settings.SettingsNamespace;
import userinterface.node.BaseNode;
import utils.MainUtil;

public class TopMenu extends BorderPane implements MainUtil, BaseNode {
    private final Menu menuFile = new Menu("File");
    private final MenuItem menuSave = new MenuItem("Save");
    private final MenuItem menuExit = new MenuItem("Exit");
    private final Menu menuModeWhitelist = new Menu("Whitelist Mode");
    private final Menu menuModeBlacklist = new Menu("Blacklist Mode");

    private final Menu menuSelection = new Menu("Select");
    private final MenuItem menuSelectAll = new MenuItem("Select All");
    private final MenuItem menuClearSelection = new MenuItem("Clear Select");
    private final ClickableMenu menuRandom = new ClickableMenu("Random");
    private final ClickableMenu menuFullView = new ClickableMenu("FullView");

    private final Menu menuFilter = new Menu("Filter");
    private final CheckMenuItem menuUntaggedOnly = new CheckMenuItem("Untagged");
    private final CheckMenuItem menuMaxXTags = new CheckMenuItem("Max X Tags");
    public TopMenu() {
        this.setPadding(new Insets(settings.valueOf(SettingsNamespace.GLOBAL_SPACING)));

        menuFile.getItems().addAll(menuSave, new SeparatorMenuItem(), menuExit);
        menuSelection.getItems().addAll(menuSelectAll, menuClearSelection);

        menuModeWhitelist.getItems().addAll(menuModeWhitelistAll, menuModeWhitelistAny);
        menuModeBlacklist.getItems().addAll(menuModeBlacklistAll, menuModeBlacklistAny);
        menuModeWhitelistAll.setSelected(true);
        menuModeBlacklistAny.setSelected(true);
        menuFilter.getItems().addAll(menuUntaggedOnly, menuMaxXTags, new SeparatorMenuItem(), menuModeWhitelist, menuModeBlacklist, new SeparatorMenuItem(), menuReset);

        MenuBar menuBarL = new MenuBar(menuFile, menuSelection, menuFilter);
        MenuBar centerBar = new MenuBar(menuRandom, menuFullView);
        MenuBar menuBarR = new MenuBar(infoLabelMenu);

        menuBarL.setPrefWidth(200);
        menuBarR.setPrefWidth(200);
        menuBarR.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        menuBarL.minHeightProperty().bind(centerBar.heightProperty());
        menuBarR.minHeightProperty().bind(centerBar.heightProperty());

        this.setLeft(menuBarL);
        this.setCenter(centerBar);
        this.setRight(menuBarR);
    }
    public MenuItem getMenuSave() {
        return menuSave;
    }
    public MenuItem getMenuExit() {
        return menuExit;
    }
    private final CheckMenuItem menuModeWhitelistAll = new CheckMenuItem("All");
    private final CheckMenuItem menuModeWhitelistAny = new CheckMenuItem("Any");
    public MenuItem getMenuSelectAll() {
        return menuSelectAll;
    }
    public MenuItem getMenuClearSelection() {
        return menuClearSelection;
    }
    public CheckMenuItem getMenuUntaggedOnly() {
        return menuUntaggedOnly;
    }
    private final CheckMenuItem menuModeBlacklistAll = new CheckMenuItem("All");
    private final CheckMenuItem menuModeBlacklistAny = new CheckMenuItem("Any");
    public CheckMenuItem getMenuMaxXTags() {
        return menuMaxXTags;
    }
    public CheckMenuItem getMenuModeWhitelistAll() {
        return menuModeWhitelistAll;
    }
    private final MenuItem menuReset = new MenuItem("Reset");
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
    public ClickableMenu getMenuRandom() {
        return menuRandom;
    }

    private final Menu infoLabelMenu = new Menu();
    public ClickableMenu getMenuFullView() {
        return menuFullView;
    }

    public void reload() {
        String text = select.size() + " items selected";
        infoLabelMenu.setText(text);
    }
}
