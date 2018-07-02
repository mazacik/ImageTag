package project.userinput.gui;

import project.gui.component.PreviewPane;

public abstract class UserInputPreviewPane {
    public static void initialize() {
        setOnMouseClicked_canvas();
    }

    private static void setOnMouseClicked_canvas() {
        PreviewPane.getCanvas().setOnMouseClicked(event -> PreviewPane.getInstance().requestFocus());
    }
}
