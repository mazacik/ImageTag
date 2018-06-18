package project.custom.stage.generic;

import javafx.scene.control.Alert;

public class AlertWindow extends Alert {
    /* constructors */
    public AlertWindow(String errorMessage) {
        this("Error", errorMessage);
    }

    public AlertWindow(String title, String errorMessage) {
        super(AlertType.INFORMATION);
        setTitle(title);
        setHeaderText(null);
        setGraphic(null);
        setContentText(errorMessage);
        show();
    }
}
