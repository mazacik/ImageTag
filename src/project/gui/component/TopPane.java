package project.gui.component;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.WindowEvent;
import project.Main;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.control.change.ChangeEventControl;
import project.control.change.ChangeEventEnum;
import project.database.element.DataElement;
import project.database.loader.Serialization;

public abstract class TopPane extends BorderPane {
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

    private static final CheckMenuItem menuUntaggedOnly = new CheckMenuItem("Untagged Only");
    private static final CheckMenuItem menuLessThanXTags = new CheckMenuItem("Less Than X Tags");
    private static final MenuItem menuRefresh = new MenuItem("Refresh");
    private static final MenuItem menuReset = new MenuItem("Reset");

    /* initialize */
    public static void initialize() {
        initializeComponents();
        initializeProperties();
    }
    private static void initializeComponents() {
        menuFile.getItems().addAll(menuSave, new SeparatorMenuItem(), menuExit);
        menuSelection.getItems().addAll(menuSelectAll, menuClearSelection);
        menuFilter.getItems().addAll(menuUntaggedOnly, menuLessThanXTags, new SeparatorMenuItem(), menuRefresh, menuReset);

        infoLabelMenuBar.getMenus().add(infoLabelMenu);

        setOnAction();
    }
    private static void initializeProperties() {
        _this.setCenter(new MenuBar(menuFile, menuSelection, menuFilter));
        _this.setRight(infoLabelMenuBar);

        ChangeEventControl.subscribe(TopPane.class, ChangeEventEnum.FOCUS);
    }

    /* public */
    public static void refreshComponent() {
        DataElement currentFocusedItem = FocusControl.getCurrentFocus();
        if (currentFocusedItem != null) {
            infoLabelMenu.setText(currentFocusedItem.getName());
        }
    }

    /* event */
    private static void setOnAction() {
        menuSave.setOnAction(event -> Serialization.writeToDisk());
        menuExit.setOnAction(event -> _this.fireEvent(new WindowEvent(Main.getStage(), WindowEvent.WINDOW_CLOSE_REQUEST)));

        menuSelectAll.setOnAction(event -> SelectionControl.addDataElement(FilterControl.getValidDataElements()));
        menuClearSelection.setOnAction(event -> SelectionControl.clearDataElements());

        menuUntaggedOnly.setOnAction(event -> {
            if (!FilterControl.isCustomFilterUntaggedOnly()) {
                FilterControl.setCustomFilterUntaggedOnly(true);
            } else {
                FilterControl.setCustomFilterUntaggedOnly(false);
            }
            menuLessThanXTags.setSelected(false);
            FilterControl.setCustomFilterLessThanXTags(false);
            FilterControl.revalidateDataElements();
        });
        menuLessThanXTags.setOnAction(event -> {
            if (!FilterControl.isCustomFilterLessThanXTags()) {
                FilterControl.setCustomFilterLessThanXTags(true);
            } else {
                FilterControl.setCustomFilterLessThanXTags(false);
            }
            menuUntaggedOnly.setSelected(false);
            FilterControl.setCustomFilterUntaggedOnly(false);
            FilterControl.customFilterLessThanXTags();
        });
        menuRefresh.setOnAction(event -> FilterControl.revalidateDataElements());
        menuReset.setOnAction(event -> {
            menuUntaggedOnly.setSelected(false);
            menuLessThanXTags.setSelected(false);
            FilterControl.customFilterResetFiltering();
        });
    }

    /* get */
    public static Region getInstance() {
        return _this;
    }
}
