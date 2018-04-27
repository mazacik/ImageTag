package project.frontend.components;

import project.frontend.shared.Frontend;

public class TagPaneFront extends LeftPaneFront {
    public TagPaneFront() {
        swapLeftPaneButton.setText("Mode: Tags");
        swapLeftPaneButton.setStyle("-fx-focus-color: transparent;");
        swapLeftPaneButton.setOnMouseClicked(event -> Frontend.getMainBorderPane().setLeft(Frontend.getNamePane()));
    }
}
