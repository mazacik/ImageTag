package project.frontend.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import project.frontend.shared.ColoredText;
import project.frontend.shared.LeftPaneDisplayMode;

public class LeftPaneFront extends BorderPane {
    private final Button leftButton = new Button();
    private final Button rightButton = new Button();
    private final ListView<ColoredText> listView = new ListView<>();
    private LeftPaneDisplayMode displayMode = LeftPaneDisplayMode.TAGS;

    public LeftPaneFront() {
        setPrefWidth(200);

        leftButton.setText("Names");
        leftButton.setStyle("-fx-focus-color: transparent;");
        leftButton.setPrefWidth(90);

        rightButton.setText("Tags");
        rightButton.setStyle("-fx-focus-color: transparent;");
        rightButton.setPrefWidth(90);

        HBox buttonPane = new HBox();
        buttonPane.getChildren().addAll(leftButton, rightButton);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setSpacing(3);
        setCenter(listView);
        setBottom(buttonPane);
    }

    public Button getLeftButton() {
        return leftButton;
    }

    public Button getRightButton() {
        return rightButton;
    }

    public LeftPaneDisplayMode getDisplayMode() {
        return displayMode;
    }

    public ListView<ColoredText> getListView() {
        return listView;
    }

    public void setDisplayMode(LeftPaneDisplayMode displayMode) {
        this.displayMode = displayMode;
    }
}