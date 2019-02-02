package userinterface.template.generic;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class WindowTextInput extends TextInputDialog {
    public WindowTextInput(String title, String contentText) {
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
