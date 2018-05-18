package project.frontend.common;

    import javafx.scene.control.TextInputDialog;
    import project.backend.common.Utility;

    import java.util.Optional;

public class NumberInputWindow extends TextInputDialog {
    public NumberInputWindow(String title, String contentText) {
        setTitle(title);
        setHeaderText(null);
        setGraphic(null);
        setContentText(contentText);

        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("") && !Utility.isNumber(newValue)) {
                getEditor().setText(oldValue);
            }
        });
    }

    public Integer getResultValue() {
        Optional<String> resultValue = showAndWait();
        if (!resultValue.isPresent()) return 0;
        String resultString = resultValue.get();
        if (!Utility.isNumber(resultString)) return 0;
        return Integer.valueOf(resultString);
    }
}
