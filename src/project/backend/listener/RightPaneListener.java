package project.backend.listener;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import project.backend.common.Filter;
import project.frontend.component.TagManager;
import project.frontend.singleton.RightPaneFront;

public class RightPaneListener {
    private static final RightPaneListener instace = new RightPaneListener();

    RightPaneFront rightPaneFront = RightPaneFront.getInstance();


    private RightPaneListener() {
        setKeyListener();
        setAddButtonListener();
        setContextMenu(rightPaneFront.getListView());
    }

    private void setKeyListener() {
        rightPaneFront.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                addTag();
            else if (event.getCode() == KeyCode.DELETE)
                Filter.removeTagSelectedItems();
        });
    }

    private void setAddButtonListener() {
        rightPaneFront.getAddButton().setOnAction(event -> addTag());
    }

    private void setContextMenu(ListView<String> source) {
        ContextMenu listContextMenu = new ContextMenu();
        MenuItem menuRemoveTag = new MenuItem("Remove");
        menuRemoveTag.setOnAction(event -> Filter.removeTagSelectedItems());
        listContextMenu.getItems().add(menuRemoveTag);
        source.setContextMenu(listContextMenu);
    }

    private void addTag() {
        if (rightPaneFront.getAddTextField().getText().isEmpty())
            new TagManager();
        else {
            Filter.addTagSelectedItems(rightPaneFront.getAddTextField().getText());
            rightPaneFront.getAddTextField().clear();
        }
    }

    public static RightPaneListener getInstace() {
        return instace;
    }
}
