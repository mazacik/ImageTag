package project.gui.component.left;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import project.database.part.ColoredText;

public class LeftPaneFront extends BorderPane {
    /* lazy singleton */
    private static LeftPaneFront instance;
    public static LeftPaneFront getInstance() {
        if (instance == null) instance = new LeftPaneFront();
        return instance;
    }

    /* components */
    private final TreeView<ColoredText> treeView = new TreeView(new TreeItem());
    //private final ListView<ColoredText> listView = new ListView<>();

    /* constructors */
    private LeftPaneFront() {
        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);

        setCellFactory();
        setCenter(treeView);
    }

    /* builder methods */
    private void setCellFactory() {
        treeView.setCellFactory(treeView -> new TreeCell<>() {
            @Override
            protected void updateItem(ColoredText coloredText, boolean empty) {
                super.updateItem(coloredText, empty);
                if (coloredText == null) {
                    setText(null);
                    setTextFill(null);
                } else {
                    setText(coloredText.getTagItem().getCategoryAndName());
                    setTextFill(coloredText.getColor());
                }
                ColoredText.setOnMouseClick(this, coloredText);
                ColoredText.setOnContextMenuRequest(this, coloredText);
            }
        });
    }

    /* getters */
    public TreeView<ColoredText> getTreeView() {
        return treeView;
    }
}
