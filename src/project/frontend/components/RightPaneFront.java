package project.frontend.components;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import project.backend.common.Filter;

public class RightPaneFront extends BorderPane {
    private final TextField addTextField = new TextField();
    private final ListView<String> listView = new ListView<>();

    public RightPaneFront() {
        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) addTag();
            else if (event.getCode() == KeyCode.DELETE) Filter.removeTag();
        });
        ContextMenu listContextMenu = new ContextMenu();
        MenuItem menuRemoveTag = new MenuItem("Remove");
        menuRemoveTag.setOnAction(event -> Filter.removeTag());
        listContextMenu.getItems().add(menuRemoveTag);
        listView.setContextMenu(listContextMenu);
        listView.setOnContextMenuRequested(event -> listContextMenu.show(listView, event.getScreenX(), event.getScreenY()));
        Button addButton = new Button("+");
        addButton.setStyle("-fx-focus-color: transparent;");
        addButton.setMinWidth(25);
        addButton.setOnAction(event -> addTag());
        HBox addPane = new HBox();
        addPane.setAlignment(Pos.CENTER);
        addPane.setSpacing(5);
        addPane.getChildren().addAll(addTextField, addButton);
        setCenter(listView);
        setBottom(addPane);
    }

    private void addTag() {
        if (addTextField.getText().isEmpty())
            new TagManager();
        else {
            Filter.addTag(addTextField.getText());
            addTextField.clear();
        }
    }

    public ListView<String> getListView() {
        return listView;
    }
}
