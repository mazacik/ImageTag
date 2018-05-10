package project.frontend.components;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import project.frontend.shared.ColoredText;

public class LeftPaneFront extends BorderPane {
    private final ListView<ColoredText> listView = new ListView<>();

    public LeftPaneFront() {
        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);
        setCellFactory();
        setCenter(listView);
    }

    private void setCellFactory() {
        listView.setCellFactory(lv -> new ListCell<>() {
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
            }
        });
    }

    public ListView<ColoredText> getListView() {
        return listView;
    }
}
