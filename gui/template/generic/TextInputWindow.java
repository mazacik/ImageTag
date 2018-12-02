package gui.template.generic;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class TextInputWindow extends TextInputDialog {
    public TextInputWindow(String title, String contentText) {
        setTitle(title);
        setHeaderText(null);
        setGraphic(null);
        setContentText(contentText);
    }

    public String getResultValue() {
        Optional<String> resultValue = showAndWait();
        return resultValue.orElse("");
    }
}
