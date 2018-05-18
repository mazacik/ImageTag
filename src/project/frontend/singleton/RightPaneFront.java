package project.frontend.singleton;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import project.backend.common.Filter;
import project.frontend.component.TagManager;

public class RightPaneFront extends BorderPane {
    private static final RightPaneFront instance = new RightPaneFront();

    private final ListView<String> listView = new ListView<>();
    private final TextField addTextField = new TextField();
    private final Button addButton = new Button("+");


    private RightPaneFront() {
        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);

        addButton.setStyle("-fx-focus-color: transparent;");
        addButton.setMinWidth(25);

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        HBox addPane = new HBox();
        addPane.setAlignment(Pos.CENTER);
        addPane.setSpacing(5);
        addPane.getChildren().addAll(addTextField, addButton);

        setCenter(listView);
        setBottom(addPane);
    }

    public ListView<String> getListView() {
        return listView;
    }

    public TextField getAddTextField() {
        return addTextField;
    }

    public Button getAddButton() {
        return addButton;
    }

    public static RightPaneFront getInstance() {
        return instance;
    }
}
