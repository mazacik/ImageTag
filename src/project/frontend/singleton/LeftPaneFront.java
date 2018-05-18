package project.frontend.singleton;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import project.backend.listener.ListenerTemplate;
import project.frontend.component.ColoredText;

public class LeftPaneFront extends BorderPane {
    private static final LeftPaneFront instance = new LeftPaneFront();

    private final ListView<ColoredText> listView = new ListView<>();


    private LeftPaneFront() {
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
                ListenerTemplate.setColoredTextMouseClick(this, coloredText);
                ListenerTemplate.setColoredTextContextMenu(this, coloredText);
            }
        });
    }

    public ListView<ColoredText> getListView() {
        return listView;
    }

    public static LeftPaneFront getInstance() {
        return instance;
    }
}
