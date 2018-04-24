package project.frontend.components;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import project.backend.Backend;

public class RightPaneFront extends BorderPane {
    private final TextField addTextField = new TextField();
    private final ListView<String> listView = new ListView<>();

    public RightPaneFront() {
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) Backend.getRightPaneBack().addTag();
            else if (event.getCode() == KeyCode.DELETE) Backend.getRightPaneBack().removeTag();
        });
        ContextMenu listContextMenu = new ContextMenu();
        MenuItem menuRemoveTag = new MenuItem("Remove");
        menuRemoveTag.setOnAction(event -> Backend.getRightPaneBack().removeTag());
        listContextMenu.getItems().add(menuRemoveTag);
        listView.setContextMenu(listContextMenu);
        listView.setOnContextMenuRequested(event -> listContextMenu.show(listView, event.getScreenX(), event.getScreenY()));
        setPrefWidth(200);
        Button addButton = new Button("+");
        addButton.setStyle("-fx-focus-color: transparent;");
        addButton.setOnAction(event -> Backend.getRightPaneBack().addTag());
        HBox addPane = new HBox();
        addPane.setAlignment(Pos.CENTER);
        addPane.setSpacing(10);
        addPane.getChildren().addAll(addTextField, addButton);
        setCenter(listView);
        setBottom(addPane);
    }

    public TextField getAddTextField() {
        return addTextField;
    }

    public ListView<String> getListView() {
        return listView;
    }
}
