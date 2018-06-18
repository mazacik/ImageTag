package project.gui.component.right;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class RightPaneFront extends BorderPane {
    /* lazy singleton */
    private static RightPaneFront instance;
    public static RightPaneFront getInstance() {
        if (instance == null) instance = new RightPaneFront();
        return instance;
    }

    /* variables */
    private final ListView<String> listView = new ListView<>();
    private final TextField addTextField = new TextField();
    private final Button addButton = new Button("+");

    /* constructor */
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

    /* getter */
    public ListView<String> getListView() {
        return listView;
    }
    public TextField getAddTextField() {
        return addTextField;
    }
    public Button getAddButton() {
        return addButton;
    }
}
