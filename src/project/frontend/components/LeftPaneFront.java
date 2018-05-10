package project.frontend.components;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import project.frontend.shared.ColoredText;

public abstract class LeftPaneFront extends BorderPane {
    final Button swapLeftPaneButton = new Button();
    private final ListView<ColoredText> listView = new ListView<>();

    LeftPaneFront() {
        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);
        setCellFactory();

        swapLeftPaneButton.setPrefWidth(getPrefWidth());

        setCenter(listView);
        //setBottom(swapLeftPaneButton);
    }

    private void setCellFactory() {
        listView.setCellFactory(
                lv ->
                        new ListCell<>() {
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
