package project.backend.common;

import javafx.scene.control.TextInputDialog;
import org.apache.commons.text.WordUtils;
import project.backend.Backend;
import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.frontend.shared.Frontend;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

//todo: check, rework?
public abstract class Filter {
    private static ArrayList<DatabaseItem> databaseItems = Database.getDatabaseItems();
    private static ArrayList<DatabaseItem> databaseItemsFiltered = Database.getDatabaseItemsFiltered();
    private static ArrayList<DatabaseItem> databaseItemsSelected = Database.getDatabaseItemsSelected();

    private static ArrayList<String> databaseTagsWhitelist = Database.getDatabaseTagsWhitelist();
    private static ArrayList<String> databaseTagsBlacklist = Database.getDatabaseTagsBlacklist();

    public static void addTag(String tag) {
        String finalFormat = WordUtils.capitalizeFully(tag);
        if (!finalFormat.isEmpty()) {
            if (!Database.getDatabaseTags().contains(finalFormat)) {
                Database.getDatabaseTags().add(finalFormat);
                Database.getDatabaseTags().sort(Comparator.naturalOrder());
                Database.getDatabaseTagsWhitelist().add(finalFormat);
                Database.getDatabaseTagsWhitelist().sort(Comparator.naturalOrder());
                Backend.getLeftPane().reloadContent();
            }
            for (DatabaseItem databaseItem : Database.getDatabaseItemsSelected())
                if (!databaseItem.getTags().contains(finalFormat)) databaseItem.getTags().add(finalFormat);
            Backend.getRightPane().reloadContent();
        }
    }

    public static void renameTag(String oldTagName) {
        TextInputDialog renamePrompt = new TextInputDialog();
        renamePrompt.setTitle("Rename tag");
        renamePrompt.setHeaderText(null);
        renamePrompt.setGraphic(null);
        renamePrompt.setContentText("New name:");
        String newTagName = oldTagName;
        Optional<String> result = renamePrompt.showAndWait();
        if (result.isPresent()) newTagName = result.get();
        if (!newTagName.isEmpty()) {
            if (Database.getDatabaseTags().contains(oldTagName)) {
                Database.getDatabaseTags().set(Database.getDatabaseTags().indexOf(oldTagName), newTagName);
                Backend.getLeftPane().reloadContent();
            }
            for (DatabaseItem databaseItem : Database.getDatabaseItems())
                if (!databaseItem.getTags().contains(oldTagName))
                    databaseItem.getTags().set(databaseItem.getTags().indexOf(oldTagName), newTagName);
        }
    }

    public static void removeTag() {
        if (!Frontend.getRightPane().getListView().getSelectionModel().getSelectedIndices().isEmpty()) {
            String tag = Frontend.getRightPane().getListView().getSelectionModel().getSelectedItem();
            for (DatabaseItem databaseItem : Database.getDatabaseItemsSelected())
                databaseItem.getTags().remove(tag);
            boolean tagExists = false;
            for (DatabaseItem databaseItem : Database.getDatabaseItems())
                if (databaseItem.getTags().contains(tag)) {
                    tagExists = true;
                    break;
                }
            if (!tagExists) {
                Database.getDatabaseTags().remove(tag);
                Database.getDatabaseTagsWhitelist().remove(tag);
                Database.getDatabaseTagsBlacklist().remove(tag);
            }
            Backend.getLeftPane().reloadContent();
            Backend.getRightPane().reloadContent();
        }
    }

    public static void filterByTags() {
        databaseItemsFiltered.clear();
        if (databaseTagsWhitelist.isEmpty() && databaseTagsBlacklist.isEmpty())
            databaseItemsFiltered.addAll(databaseItems);
        else
            for (DatabaseItem databaseItem : databaseItems)
                if (databaseItem.getTags().containsAll(databaseTagsWhitelist)) {
                    databaseItemsFiltered.add(databaseItem);
                    for (String tag : databaseTagsBlacklist)
                        if (databaseItem.getTags().contains(tag)) {
                            databaseItemsFiltered.remove(databaseItem);
                            break;
                        }
                }
    }

    public static ArrayList<String> getSelectedItemsSharedTags() {
        if (databaseItemsSelected.isEmpty()) return new ArrayList<>();
        ArrayList<String> sharedTags = new ArrayList<>();
        ArrayList<String> firstItemTags = databaseItemsSelected.get(0).getTags();
        for (String tag : firstItemTags)
            for (DatabaseItem databaseItem : databaseItemsSelected) {
                if (databaseItem.getTags().contains(tag)) {
                    if (databaseItem.equals(databaseItemsSelected.get(databaseItemsSelected.size() - 1))) sharedTags.add(tag);
                    continue;
                }
                break;
            }
        return sharedTags;
    }
}
