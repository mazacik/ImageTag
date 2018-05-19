package project.component.left;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import project.component.left.part.ColoredText;

public class LeftPaneFront extends BorderPane {
    /* lazy singleton */
    private static LeftPaneFront instance;
    public static LeftPaneFront getInstance() {
        if (instance == null) instance = new LeftPaneFront();
        return instance;
    }

    /* variables */
    private final ListView<ColoredText> listView = new ListView<>();

    /* constructors */
    private LeftPaneFront() {
        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);

        setCellFactory();
        setCenter(listView);
    }

    /* private methods */
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
                ColoredText.setOnMouseClick(this, coloredText);
                ColoredText.setOnContextMenuRequest(this, coloredText);
            }
        });
    }

    /* getters */
    public ListView<ColoredText> getListView() {
        return listView;
    }
}
