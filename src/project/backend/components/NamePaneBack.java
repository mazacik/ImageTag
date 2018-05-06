package project.backend.components;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import project.backend.shared.Backend;
import project.backend.shared.Database;
import project.backend.shared.DatabaseItem;
import project.frontend.shared.ColoredText;
import project.frontend.shared.Frontend;

/**
 * Backend of a GUI component which displays every filtered item's file name in a list.
 */
public class NamePaneBack {
    private final ContextMenu listContextMenu = new ContextMenu();
    private ListView<ColoredText> listView = Frontend.getNamePane().getListView();

    /**
     * Initialization of the component.
     */
    public NamePaneBack() {
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        buildContextMenu();
        listView.setContextMenu(listContextMenu);
        listView.setOnContextMenuRequested(
                event -> listContextMenu.show(listView, event.getScreenX(), event.getScreenY()));
        listView.setOnMouseClicked(
                event -> {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        if (listView.getSelectionModel().getSelectedItem() != null) {
                            DatabaseItem selectedItem = listView.getSelectionModel().getSelectedItem().getOwner();
                            Database.setSelectedItem(selectedItem);
                            if (Database.getSelectedItems().contains(selectedItem))
                                Database.removeIndexFromSelection(selectedItem);
                            else Database.addToSelection(selectedItem);
                        }
                    }
                });
    }

    /**
     * Initialization of the right-click context menu of the component.
     */
    private void buildContextMenu() {
        MenuItem menuRename = new MenuItem("Rename");
        menuRename.setOnAction(
                event -> Backend.renameFile(listView.getSelectionModel().getSelectedItem().getOwner()));
        listContextMenu.getItems().addAll(menuRename);
    }

    /**
     * Reloads the names of items in the filtered database.
     */
    public void reloadContent() {
        listView.getItems().clear();
        for (DatabaseItem databaseItem : Database.getFilteredItems())
            listView.getItems().add(databaseItem.getColoredText());
    }
}
