package project.gui.component;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import project.Main;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.database.element.DataElement;
import project.database.loader.Serialization;
import project.gui.ChangeEventControl;
import project.gui.ChangeEventEnum;
import project.gui.ChangeEventListener;

public class PaneTop extends BorderPane implements ChangeEventListener {
    /* components */
    private final MenuBar infoLabelMenuBar = new MenuBar();
    private final Menu infoLabelMenu = new Menu();

    private final Menu menuFile = new Menu("File");
    private final MenuItem menuSave = new MenuItem("Save");
    private final MenuItem menuExit = new MenuItem("Exit");

    private final Menu menuSelection = new Menu("Selection");
    private final MenuItem menuSelectAll = new MenuItem("Select All");
    private final MenuItem menuClearSelection = new MenuItem("Clear Selection");

    private final Menu menuFilter = new Menu("Filter");
    private final MenuItem menuUntaggedOnly = new MenuItem("Untagged Only");
    private final MenuItem menuLessThanXTags = new MenuItem("Less Than X Tags");
    private final MenuItem menuReset = new MenuItem("Reset");

    /* constructors */
    public PaneTop() {
        initializeComponents();
        initializeProperties();
    }

    /* initialize */
    private void initializeComponents() {
        menuFile.getItems().addAll(menuSave, new SeparatorMenuItem(), menuExit);
        menuSelection.getItems().addAll(menuSelectAll, menuClearSelection);
        menuFilter.getItems().addAll(menuUntaggedOnly, menuLessThanXTags, new SeparatorMenuItem(), menuReset);

        infoLabelMenuBar.getMenus().add(infoLabelMenu);

        setOnAction();
    }
    private void initializeProperties() {
        setCenter(new MenuBar(menuFile, menuSelection, menuFilter));
        setRight(infoLabelMenuBar);

        ChangeEventControl.subscribe(this, ChangeEventEnum.FOCUS);
    }

    /* public */
    public void refreshComponent() {
        DataElement currentFocusedItem = FocusControl.getCurrentFocus();
        if (currentFocusedItem != null) {
            infoLabelMenu.setText(currentFocusedItem.getName());
        }
    }

    /* event */
    private void setOnAction() {
        menuSave.setOnAction(event -> Serialization.writeToDisk());
        menuExit.setOnAction(event -> fireEvent(new WindowEvent(Main.getStage(), WindowEvent.WINDOW_CLOSE_REQUEST)));

        menuSelectAll.setOnAction(event -> SelectionControl.addDataElement(FilterControl.getValidDataElements()));
        menuClearSelection.setOnAction(event -> SelectionControl.clearDataElements());

        menuUntaggedOnly.setOnAction(event -> FilterControl.customFilterUntaggedOnly());
        menuLessThanXTags.setOnAction(event -> FilterControl.customFilterLessThanXTags());
        menuReset.setOnAction(event -> FilterControl.customFilterResetFiltering());
    }
}
