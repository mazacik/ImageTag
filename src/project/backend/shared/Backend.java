package project.backend.shared;

import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import project.backend.components.*;
import project.frontend.shared.Frontend;

import java.io.File;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;

/**
 * Shared backend class for common instances and respective methods used across the entire project.
 */
public class Backend {
    private static final TopPaneBack topPane = new TopPaneBack();
    private static final LeftPaneBack tagPane = new LeftPaneBack();
    private static final RightPaneBack rightPane = new RightPaneBack();
    private static final GalleryPaneBack galleryPane = new GalleryPaneBack();
    private static final PreviewPaneBack previewPane = new PreviewPaneBack();

    static String DIRECTORY_PATH = "C:/abc/dnnsfw";

    /**
     * Initialization.
     */
    static void initialize() {
        if (Backend.DIRECTORY_PATH == null) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select working directory");
            directoryChooser.setInitialDirectory(new File("C:/"));
            File selectedDirectory = directoryChooser.showDialog(Frontend.getMainStage());
            if (selectedDirectory == null) {
                Platform.exit();
                return;
            }
            Backend.setDirectoryPath(selectedDirectory.getAbsolutePath());
        }

        initializeKeybindings();
        Database.initilize();
        new DatabaseLoader().start();
    }

    /**
     * Initialization of the key listeners.
     */
    private static void initializeKeybindings() {
        Frontend.getMainScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Q:
                    if (Database.getSelectedItems().contains(Database.getSelectedItem()))
                        Database.removeIndexFromSelection(Database.getSelectedItem());
                    else Database.addToSelection(Database.getSelectedItem());
                    break;
                case R:
                    Database.clearSelection();
                    Database.addToSelection(Database.getFilteredItems().get(new Random().nextInt(Database.getFilteredItems().size())));
                    break;
                case F12:
                    Backend.swapImageDisplayMode();
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * Swaps the center component of the main border pane from gallery to full-screen preview, and vice versa.
     */
    public static void swapImageDisplayMode() {
        double[] dividerPositions = Frontend.getSplitPane().getDividerPositions();
        if (Frontend.getSplitPane().getItems().contains(Frontend.getGalleryPane())) {
            Frontend.getSplitPane().getItems().set(1, Frontend.getPreviewPane());
            Frontend.getPreviewPane().setCanvasSize(Frontend.getGalleryPane().getWidth(), Frontend.getGalleryPane().getHeight());
            Backend.getPreviewPane().drawPreview();
        } else {
            Frontend.getSplitPane().getItems().set(1, Frontend.getGalleryPane());
        }
        Frontend.getSplitPane().setDividerPositions(dividerPositions);
    }

    public static PreviewPaneBack getPreviewPane() {
        return previewPane;
    }

    private static void setDirectoryPath(String directoryPath) {
        DIRECTORY_PATH = directoryPath;
    }

    /**
     * Renames the last selected file.
     */
    public static void renameFile(DatabaseItem databaseItem) {
        if (databaseItem == null) return;
        TextInputDialog renamePrompt = new TextInputDialog();
        renamePrompt.setTitle("Rename file");
        renamePrompt.setHeaderText(null);
        renamePrompt.setGraphic(null);
        renamePrompt.setContentText("New name:");
        Optional<String> result = renamePrompt.showAndWait();
        String oldName = databaseItem.getSimpleName();
        String newName = databaseItem.getSimpleName(); // nullpointer prevention
        if (result.isPresent()) newName = result.get() + "." + databaseItem.getExtension();
        databaseItem.setSimpleName(newName);
        databaseItem.setFullPath(DIRECTORY_PATH + "/" + newName);
        databaseItem.getColoredText().setText(newName);
        new File(DIRECTORY_PATH + "/" + oldName).renameTo(new File(DIRECTORY_PATH + "/" + newName));
        new File(DIRECTORY_PATH + "/imagecache/" + oldName)
                .renameTo(new File(DIRECTORY_PATH + "/imagecache/" + newName));
        Backend.reloadContent();
    }

    /**
     * Sorts all databases and reloads the entire backend of the project.
     */
    public static void reloadContent() {
        Database.getFilteredItems().sort(Comparator.comparing(DatabaseItem::getSimpleName));
        Database.getSelectedItems().sort(Comparator.comparing(DatabaseItem::getSimpleName));
        Database.getItemDatabase().sort(Comparator.comparing(DatabaseItem::getSimpleName));
        Backend.getTagPane().reloadContent();
        Backend.getGalleryPane().reloadContent();
    }

    public static LeftPaneBack getTagPane() {
        return tagPane;
    }

    public static GalleryPaneBack getGalleryPane() {
        return galleryPane;
    }

    public static void addTag() {
        String newTag = Frontend.getRightPane().getAddTextField().getText();
        Frontend.getRightPane().getAddTextField().clear();
        if (!newTag.isEmpty()) {
            if (!Database.getTagDatabase().contains(newTag)) {
                Database.getTagDatabase().add(newTag);
                Backend.getTagPane().reloadContent();
            }
            for (DatabaseItem databaseItem : Database.getSelectedItems())
                if (!databaseItem.getTags().contains(newTag)) databaseItem.getTags().add(newTag);
            Backend.getRightPane().reloadContent();
        }
    }

    public static RightPaneBack getRightPane() {
        return rightPane;
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
            if (Database.getTagDatabase().contains(oldTagName)) {
                Database.getTagDatabase().set(Database.getTagDatabase().indexOf(oldTagName), newTagName);
                Backend.getTagPane().reloadContent();
            }
            for (DatabaseItem databaseItem : Database.getItemDatabase())
                if (!databaseItem.getTags().contains(oldTagName))
                    databaseItem.getTags().set(databaseItem.getTags().indexOf(oldTagName), newTagName);
        }
    }

    public static void removeTag() {
        if (!Frontend.getRightPane().getListView().getSelectionModel().getSelectedIndices().isEmpty()) {
            String tag = Frontend.getRightPane().getListView().getSelectionModel().getSelectedItem();
            for (DatabaseItem databaseItem : Database.getSelectedItems())
                databaseItem.getTags().remove(tag);
            boolean tagExists = false;
            for (DatabaseItem databaseItem : Database.getItemDatabase())
                if (databaseItem.getTags().contains(tag)) {
                    tagExists = true;
                    break;
                }
            if (!tagExists) {
                Database.getTagDatabase().remove(tag);
                Database.getTagsWhitelist().remove(tag);
                Database.getTagsBlacklist().remove(tag);
            }
            Backend.getTagPane().reloadContent();
            Backend.getRightPane().reloadContent();
        }
    }

    public static TopPaneBack getTopPane() {
        return topPane;
    }
}
