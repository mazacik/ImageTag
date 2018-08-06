package project.gui.event.listener.previewpane;

import project.gui.component.previewpane.PreviewPane;
import project.gui.event.handler.previewpane.EventHandlerPreviewPaneCanvas;

public abstract class EventListenerPreviewPaneCanvas {
    public static void initialize() {
        EventListenerPreviewPaneCanvas.onMouseClick();
        EventListenerPreviewPaneCanvas.onResize();
    }

    private static void onMouseClick() {
        PreviewPane.getCanvas().setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    EventHandlerPreviewPaneCanvas.onLeftClick();
                    break;
                case SECONDARY:
                    EventHandlerPreviewPaneCanvas.onRightClick(event);
                    break;
                default:
                    break;
            }
        });
    }

    private static void onResize() {
        EventHandlerPreviewPaneCanvas.onResize();
    }
}
