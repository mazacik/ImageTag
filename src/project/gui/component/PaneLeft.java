package project.gui.component;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import project.database.TagDatabase;
import project.database.part.ColoredText;
import project.gui.ChangeEventControl;
import project.gui.ChangeEventEnum;
import project.gui.ChangeEventListener;

import java.util.ArrayList;

public class PaneLeft extends BorderPane implements ChangeEventListener {
    /* components */
    private final TreeView<ColoredText> treeView = new TreeView(new TreeItem());

    /* constructors */
    public PaneLeft() {
        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);

        setCellFactory();
        treeView.setShowRoot(false);
        setCenter(treeView);

        ChangeEventControl.subscribe(this, (ChangeEventEnum[]) null);
    }

    /* public methods */
    public void refreshComponent() {
        ObservableList<TreeItem<ColoredText>> treeViewItems = treeView.getRoot().getChildren();
        ArrayList<String> categoryNames = TagDatabase.getCategories();

        treeViewItems.clear();

        for (String categoryName : categoryNames) {
            TreeItem categoryTreeItem;
            if (TagDatabase.isCategoryWhitelisted(categoryName)) {
                categoryTreeItem = new TreeItem(new ColoredText(categoryName, Color.GREEN));
            } else if (TagDatabase.isCategoryBlacklisted(categoryName)) {
                categoryTreeItem = new TreeItem(new ColoredText(categoryName, Color.RED));
            } else {
                categoryTreeItem = new TreeItem(new ColoredText(categoryName, Color.BLACK));
            }

            for (String tagName : TagDatabase.getItemsInCategory(categoryName)) {
                if (TagDatabase.isItemWhitelisted(categoryName, tagName)) {
                    categoryTreeItem.getChildren().add(new TreeItem(new ColoredText(tagName, Color.GREEN)));
                } else if (TagDatabase.isItemBlacklisted(categoryName, tagName)) {
                    categoryTreeItem.getChildren().add(new TreeItem(new ColoredText(tagName, Color.RED)));
                } else {
                    categoryTreeItem.getChildren().add(new TreeItem(new ColoredText(tagName, Color.BLACK)));
                }
            }
            treeViewItems.add(categoryTreeItem);
        }
    }

    public void refreshTreeview() {
        treeView.refresh();
    }

    /* private methods */
    private void setCellFactory() {
        treeView.setCellFactory(treeView -> new TreeCell<>() {
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

                ColoredText.setOnMouseClick(this);
                ColoredText.setContextMenu(this);

                addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                    if (e.getClickCount() % 2 == 0 && e.getButton().equals(MouseButton.PRIMARY)) {
                        e.consume();
                    }
                });
            }
        });
    }
}
