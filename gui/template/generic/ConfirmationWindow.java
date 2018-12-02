package gui.template.generic;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ConfirmationWindow {
    private Alert alert;

    public ConfirmationWindow(String title, String header, String context) {
        alert = new Alert(AlertType.CONFIRMATION, null, ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        alert.setGraphic(null);
    }
    public ConfirmationWindow() {
        this("Title", "HeaderText", "ContentText");
    }

    public boolean getResult() {
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    public void setTitle(String title) {
        alert.setTitle(title);
    }
    public void setHeaderText(String header) {
        alert.setTitle(header);
    }
    public void setContentText(String context) {
        alert.setTitle(context);
    }
}
