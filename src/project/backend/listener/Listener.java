package project.backend.listener;

import javafx.beans.value.ChangeListener;
import project.backend.singleton.PreviewPaneBack;
import project.frontend.singleton.PreviewPaneFront;

public class Listener {
    private static final Listener instance = new Listener();


    private Listener() {
        setPreviewPaneOnResizeListener();
    }

    private void setPreviewPaneOnResizeListener() {
        PreviewPaneFront previewPaneFront = PreviewPaneFront.getInstance();
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> {
            previewPaneFront.setCanvasSize(previewPaneFront.getWidth(), previewPaneFront.getHeight());
            PreviewPaneBack.getInstance().draw();
        };
        previewPaneFront.widthProperty().addListener(previewPaneSizeListener);
        previewPaneFront.heightProperty().addListener(previewPaneSizeListener);
    }

    public static Listener getInstance() {
        return instance;
    }
}
