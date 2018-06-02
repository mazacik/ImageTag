package project.component.right;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import project.common.Filter;
import project.component.top.TopPaneFront;
import project.customdialog.TagManager;

public class RightPaneListener {
    /* lazy singleton */
    private static RightPaneListener instance;
    public static RightPaneListener getInstance() {
        if (instance == null) instance = new RightPaneListener();
        return instance;
    }

    /* imports */
    private final RightPaneFront rightPaneFront = RightPaneFront.getInstance();

    /* constructors */
    private RightPaneListener() {
        setOnKeyPress();
        setAddButtonOnAction();
        setContextMenu(rightPaneFront.getListView());
    }

    /* private methods */
    private void addTag() {
        if (rightPaneFront.getAddTextField().getText().isEmpty())
            new TagManager();
        else {
            Filter.addTagToSelectedItems(rightPaneFront.getAddTextField().getText());
            rightPaneFront.getAddTextField().clear();
        }
    }

    /* event methods */
    private void setOnKeyPress() {
        rightPaneFront.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                addTag();
            else if (event.getCode() == KeyCode.DELETE)
                Filter.removeTagFromSelectedItems();
            else if (event.getCode() == KeyCode.ESCAPE)
                TopPaneFront.getInstance().requestFocus();
        });
    }

    private void setAddButtonOnAction() {
        rightPaneFront.getAddButton().setOnAction(event -> addTag());
    }

    private void setContextMenu(ListView<String> source) {
        ContextMenu listContextMenu = new ContextMenu();
        MenuItem menuRemoveTag = new MenuItem("Remove");
        menuRemoveTag.setOnAction(event -> Filter.removeTagFromSelectedItems());
        listContextMenu.getItems().add(menuRemoveTag);
        source.setContextMenu(listContextMenu);
    }
}
