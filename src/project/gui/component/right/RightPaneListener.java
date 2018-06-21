package project.gui.component.right;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import project.backend.Filter;
import project.database.TagDatabase;
import project.gui.component.top.TopPane;

public class RightPaneListener {
    /* lazy singleton */
    private static RightPaneListener instance;
    public static RightPaneListener getInstance() {
        if (instance == null) instance = new RightPaneListener();
        return instance;
    }

    /* imports */
    private final RightPane rightPane = RightPane.getInstance();

    /* constructors */
    private RightPaneListener() {
        setOnKeyPress();
        setAddButtonOnAction();
        setContextMenu(rightPane.getListView());
    }

    /* private methods */
    private void addTag() {
        Object categoryComboBoxValue = rightPane.getCbCategory().getValue();
        Object nameComboBoxValue = rightPane.getCbName().getValue();
        String category = "";
        String name = "";

        if (categoryComboBoxValue != null)
            category = categoryComboBoxValue.toString();
        if (nameComboBoxValue != null)
            name = nameComboBoxValue.toString();

        if (!category.isEmpty() && !name.isEmpty()) {
            Filter.addTagToSelectedItems(TagDatabase.getTagItem(category, name));
        }
    }

    /* event methods */
    private void setOnKeyPress() {
        rightPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                addTag();
            else if (event.getCode() == KeyCode.DELETE)
                Filter.removeSelectedTagsFromItemSelection();
            else if (event.getCode() == KeyCode.ESCAPE)
                TopPane.getInstance().requestFocus();
        });
    }

    private void setAddButtonOnAction() {
        rightPane.getBtnAdd().setOnAction(event -> addTag());
    }

    private void setContextMenu(ListView<String> source) {
        ContextMenu listContextMenu = new ContextMenu();
        MenuItem menuRemoveTag = new MenuItem("Remove");
        menuRemoveTag.setOnAction(event -> Filter.removeSelectedTagsFromItemSelection());
        listContextMenu.getItems().add(menuRemoveTag);
        source.setContextMenu(listContextMenu);
    }
}
