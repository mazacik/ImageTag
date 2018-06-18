package project.gui.stage.generic;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class NumberInputWindow extends TextInputDialog {
    /* constructors */
    public NumberInputWindow(String title, String contentText) {
        setTitle(title);
        setHeaderText(null);
        setGraphic(null);
        setContentText(contentText);

        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("") && !isNumber(newValue)) {
                getEditor().setText(oldValue);
            }
        });
    }

    /* public methods */
    public Integer getResultValue() {
        Optional<String> resultValue = showAndWait();
        if (!resultValue.isPresent()) return 0;
        String resultString = resultValue.get();
        if (!isNumber(resultString)) return 0;
        return Integer.valueOf(resultString);
    }

    /* utility methods */
    public static boolean isNumber(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c == '.' || c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
