package project.backend.components;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import project.backend.shared.Backend;
import project.backend.shared.Database;
import project.backend.shared.DatabaseItem;
import project.frontend.shared.ColoredText;
import project.frontend.shared.Frontend;
import project.frontend.shared.LeftPaneDisplayMode;

import java.util.List;

public class LeftPaneBack {
    private final List<String> whitelist = Database.getTagsWhitelist();
    private final List<String> blacklist = Database.getTagsBlacklist();

    private ListView<ColoredText> listView = Frontend.getLeftPane().getListView();

    public LeftPaneBack() {
        Frontend.getLeftPane().getListView().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setCellFactory(listView1 -> new ListCell<>() {
            @Override
            protected void updateItem(ColoredText coloredText, boolean empty) {
                super.updateItem(coloredText, empty);
                if (coloredText == null) {
                    setText(null);
                    setTextFill(null);
                } else {
                    setText(coloredText.getText());
                    setTextFill(coloredText.getColor());
                }
            }
        });

        ContextMenu listContextMenu = new ContextMenu();
        MenuItem menuRename = new MenuItem("Rename");
        menuRename.setOnAction(event -> Backend.renameFile(Database.getLastSelectedItem()));
        listContextMenu.getItems().add(menuRename);
        listView.setContextMenu(listContextMenu);
        listView.setOnContextMenuRequested(event -> listContextMenu.show(listView, event.getScreenX(), event.getScreenY()));

        listView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (Frontend.getLeftPane().getDisplayMode() == LeftPaneDisplayMode.NAMES && listView.getSelectionModel().getSelectedItem() != null) {
                    DatabaseItem selectedItem = listView.getSelectionModel().getSelectedItem().getOwner();
                    Database.setLastSelectedItem(selectedItem);
                    if (Database.getSelectedItems().contains(selectedItem))
                        Database.removeIndexFromSelection(selectedItem);
                    else
                        Database.addToSelection(selectedItem);
                } else if (Frontend.getLeftPane().getDisplayMode() == LeftPaneDisplayMode.TAGS && listView.getSelectionModel().getSelectedItem() != null) {
                    String tag = listView.getSelectionModel().getSelectedItem().getText();
                    if (whitelist.contains(tag)) {
                        whitelist.remove(tag);
                        blacklist.add(tag);
                        listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(tag, Color.RED));
                    } else if (blacklist.contains(tag)) {
                        blacklist.remove(tag);
                        listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(tag, Color.BLACK));
                    } else {
                        whitelist.add(tag);
                        listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(tag, Color.GREEN));
                    }
                    Database.filterByTags();
                    Backend.getGalleryPane().refreshContent();
                }
            }
        });

        Frontend.getLeftPane().getLeftButton().setOnMouseClicked(event -> {
            Frontend.getLeftPane().setDisplayMode(LeftPaneDisplayMode.NAMES);
            refreshContent();
        });

        Frontend.getLeftPane().getRightButton().setOnMouseClicked(event -> {
            Frontend.getLeftPane().setDisplayMode(LeftPaneDisplayMode.TAGS);
            refreshContent();
        });
    }

    public void refreshContent() {
        listView.getItems().clear();
        if (Frontend.getLeftPane().getDisplayMode() == LeftPaneDisplayMode.TAGS)
            for (String tag : Database.getTagDatabase())
                listView.getItems().add(new ColoredText(tag, Color.BLACK));
        else
            for (DatabaseItem databaseItem : Database.getFilteredItems())
                listView.getItems().add(databaseItem.getColoredText());
    }

}
