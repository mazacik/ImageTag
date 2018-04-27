package project.backend.shared;


import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import project.backend.components.*;
import project.frontend.shared.Frontend;

import java.io.File;
import java.util.Optional;

public class Backend {
    private static final TopPaneBack topPane = new TopPaneBack();
    private static final NamePaneBack namePane = new NamePaneBack();
    private static final TagPaneBack tagPane = new TagPaneBack();
    private static final RightPaneBack rightPane = new RightPaneBack();
    private static final GalleryPaneBack galleryPane = new GalleryPaneBack();
    private static final PreviewPaneBack previewPane = new PreviewPaneBack();

    static int GALLERY_ICON_SIZE = 150;
    static String DIRECTORY_PATH;

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
        Database.initilize();
        new DatabaseLoader().start();
    }

    public static void addTag() {
        String newTag = Frontend.getRightPane().getAddTextField().getText();
        Frontend.getRightPane().getAddTextField().clear();
        if (!newTag.isEmpty()) {
            if (!Database.getTagDatabase().contains(newTag)) {
                Database.getTagDatabase().add(newTag);
                Backend.getTagPane().refreshContent();
            }
            for (DatabaseItem databaseItem : Database.getSelectedItems())
                if (!databaseItem.getTags().contains(newTag))
                    databaseItem.getTags().add(newTag);
            Backend.getRightPane().refreshContent();
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
            Backend.getTagPane().refreshContent();
            Backend.getRightPane().refreshContent();
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
        if (result.isPresent())
            newTagName = result.get();
        if (!newTagName.isEmpty()) {
            if (Database.getTagDatabase().contains(oldTagName)) {
                Database.getTagDatabase().set(Database.getTagDatabase().indexOf(oldTagName), newTagName);
                Backend.getTagPane().refreshContent();
            }
            for (DatabaseItem databaseItem : Database.getItemDatabase())
                if (!databaseItem.getTags().contains(oldTagName))
                    databaseItem.getTags().set(databaseItem.getTags().indexOf(oldTagName), newTagName);
        }
    }

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
        if (result.isPresent())
            newName = result.get() + "." + databaseItem.getExtension();
        databaseItem.setSimpleName(newName);
        databaseItem.setFullPath(DIRECTORY_PATH + "/" + newName);
        databaseItem.getColoredText().setText(newName);
        new File(DIRECTORY_PATH + "/" + oldName).renameTo(new File(DIRECTORY_PATH + "/" + newName));
        new File(DIRECTORY_PATH + "/imagecache/" + oldName).renameTo(new File(DIRECTORY_PATH + "/imagecache/" + newName));
        Frontend.refreshContent();
    }

    public static void swapImageDisplayMode() {
        if (Frontend.getMainBorderPane().getCenter().equals(Frontend.getGalleryPane())) {
            Frontend.getMainBorderPane().setCenter(Frontend.getPreviewPane());
            Frontend.getPreviewPane().setCanvasSize(Frontend.getGalleryPane().getWidth(), Frontend.getGalleryPane().getHeight());
            Backend.getPreviewPane().drawPreview();
        } else {
            Frontend.getMainBorderPane().setCenter(Frontend.getGalleryPane());
        }
    }

    public static NamePaneBack getNamePane() {
        return namePane;
    }

    public static TagPaneBack getTagPane() {
        return tagPane;
    }

    public static TopPaneBack getTopPane() {
        return topPane;
    }

    public static RightPaneBack getRightPane() {
        return rightPane;
    }

    public static GalleryPaneBack getGalleryPane() {
        return galleryPane;
    }

    public static PreviewPaneBack getPreviewPane() {
        return previewPane;
    }

    private static void setDirectoryPath(String directoryPath) {
        DIRECTORY_PATH = directoryPath;
    }
}
