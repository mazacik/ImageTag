package project.frontend.components;

import project.frontend.shared.Frontend;

public class NamePaneFront extends LeftPaneFront {
    public NamePaneFront() {
        swapLeftPaneButton.setText("Mode: Names");
        swapLeftPaneButton.setStyle("-fx-focus-color: transparent;");
        swapLeftPaneButton.setOnMouseClicked(event -> Frontend.getMainBorderPane().setLeft(Frontend.getTagPane()));
    }
}
