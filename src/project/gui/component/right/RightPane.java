package project.gui.component.right;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import project.database.TagDatabase;
import project.gui.stage.TagManager;

public class RightPane extends BorderPane {
    /* components */
    private final ListView<String> listView = new ListView<>();
    private final ComboBox cbCategory = new ComboBox();
    private final ComboBox cbName = new ComboBox();
    private final Button btnAdd = new Button("Add");
    private final Button btnManage = new Button("Manage");

    /* constructor */
    public RightPane() {
        /* design */
        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);

        /* component initialization */
        btnAdd.setStyle("-fx-focus-color: transparent;");
        btnAdd.setMinWidth(25);
        btnAdd.prefWidthProperty().bind(prefWidthProperty());
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        btnAdd.setMaxWidth(getMaxWidth());
        btnManage.setMaxWidth(getMaxWidth());
        cbCategory.setMaxWidth(getMaxWidth());
        cbName.setMaxWidth(getMaxWidth());

        btnManage.prefWidthProperty().bind(prefWidthProperty());
        btnManage.setOnAction(event -> new TagManager());

        cbCategory.prefWidthProperty().bind(prefWidthProperty());
        cbCategory.setOnShown(event -> {
            cbCategory.getItems().clear();
            cbCategory.getItems().addAll(TagDatabase.getCategories());
        });

        cbName.prefWidthProperty().bind(prefWidthProperty());
        cbName.setOnShown(event -> {
            String category = cbCategory.getValue().toString();
            cbName.getItems().clear();
            if (cbCategory.getValue() != null && !category.isEmpty()) {
                cbName.getItems().addAll(TagDatabase.getItemsInCategory(category));
            }
        });

        VBox addPane = new VBox();
        addPane.setAlignment(Pos.CENTER);
        addPane.setSpacing(2);
        addPane.getChildren().addAll(cbCategory, cbName, btnAdd, btnManage);

        /* scene initialization */
        setCenter(listView);
        setBottom(addPane);
    }

    /* getter */
    public ListView<String> getListView() {
        return listView;
    }
    public Button getBtnAdd() {
        return btnAdd;
    }
    public ComboBox getCbCategory() {
        return cbCategory;
    }
    public ComboBox getCbName() {
        return cbName;
    }
}
