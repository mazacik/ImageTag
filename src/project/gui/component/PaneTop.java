package project.gui.component;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import project.common.Settings;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.database.DataElementDatabase;
import project.database.TagElementDatabase;
import project.database.element.DataElement;
import project.database.loader.Serialization;
import project.gui.ChangeEventControl;
import project.gui.ChangeEventEnum;
import project.gui.ChangeEventListener;
import project.gui.stage.generic.NumberInputWindow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

//todo holy shit this needs to be reworked
public class PaneTop extends BorderPane implements ChangeEventListener {
    /* components */
    private final MenuBar infoLabelMenuBar = new MenuBar();
    private final Menu infoLabelMenu = new Menu();

    private final Menu menuFile = new Menu("File");
    private final MenuItem menuSave = new MenuItem("Save");
    private final MenuItem menuExit = new MenuItem("Exit");

    private final Menu menuSelection = new Menu("SelectionControl");
    private final MenuItem menuSelectAll = new MenuItem("Select All");
    private final MenuItem menuClearSelection = new MenuItem("Clear SelectionControl");

    private final Menu menuFilter = new Menu("Filter");
    private final MenuItem menuUntaggedOnly = new MenuItem("Untagged");
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
        setInfoLabelContextMenu();
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
        menuExit.setOnAction(event -> fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST)));

        menuSelectAll.setOnAction(event -> SelectionControl.addDataElement(FilterControl.getValidDataElements()));
        menuClearSelection.setOnAction(event -> SelectionControl.clearDataElements());

        menuUntaggedOnly.setOnAction(event -> {
            FilterControl.getTagElementWhitelist().clear();
            FilterControl.getTagElementBlacklist().clear();
            FilterControl.getTagElementBlacklist().addAll(TagElementDatabase.getTagElements());
            FilterControl.refreshValidDataElements();
        });
        menuLessThanXTags.setOnAction(event -> {
            int maxTags = new NumberInputWindow("Filter Settings", "Maximum number of tags:").getResultValue();
            if (maxTags == 0) return;
            FilterControl.getTagElementWhitelist().clear();
            FilterControl.getTagElementBlacklist().clear();
            FilterControl.getValidDataElements().clear();
            for (DataElement dataElement : DataElementDatabase.getDataElements())
                if (dataElement.getTagElements().size() <= maxTags)
                    FilterControl.getValidDataElements().add(dataElement);
            ChangeEventControl.requestReloadGlobal();
        });
        menuReset.setOnAction(event -> {
            FilterControl.getTagElementWhitelist().clear();
            FilterControl.getTagElementBlacklist().clear();
            FilterControl.refreshValidDataElements();
            ChangeEventControl.requestReloadGlobal();
        });
    }
    private void setInfoLabelContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuDelete = new MenuItem("Delete");
        menuDelete.setOnAction(event -> {
            String fileName = infoLabelMenu.getText();
            try {
                Files.delete(Paths.get(Settings.getMainDirectoryPath() + "\\" + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (DataElement dataElement : DataElementDatabase.getDataElements()) {
                if (dataElement.getName().equals(fileName)) {
                    int index = FilterControl.getValidDataElements().indexOf(dataElement);
                    DataElementDatabase.getDataElements().remove(dataElement);
                    FilterControl.getValidDataElements().remove(dataElement);
                    SelectionControl.getDataElements().remove(dataElement);
                    if (FilterControl.getValidDataElements().get(index) == null)
                        index--;
                    FocusControl.setFocus(FilterControl.getValidDataElements().get(index));

                    break;
                }
            }
        });

        MenuItem menuCopy = new MenuItem("Copy");
        menuCopy.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(infoLabelMenu.getText());
            clipboard.setContent(content);
        });

        contextMenu.getItems().addAll(menuCopy, menuDelete);
        infoLabelMenuBar.setContextMenu(contextMenu);
    }
}
