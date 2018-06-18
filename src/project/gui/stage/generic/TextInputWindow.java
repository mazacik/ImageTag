package project.gui.stage.generic;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class TextInputWindow extends TextInputDialog {
    /* constructors */
    public TextInputWindow(String title, String contentText) {
        setTitle(title);
        setHeaderText(null);
        setGraphic(null);
        setContentText(contentText);
    }

    /* public methods */
    public String getResultValue() {
        Optional<String> resultValue = showAndWait();
        return resultValue.orElse(null);
    }
}
