package project.frontend.common;

import javafx.scene.control.Alert;

public abstract class AlertWindow {


    public static void showErrorAlertSimple(String errorMessage) {
        showErrorAlertSimple("Error", errorMessage);
    }

    public static void showErrorAlertSimple(String title, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}
