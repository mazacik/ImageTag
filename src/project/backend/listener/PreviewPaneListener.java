package project.backend.listener;

import javafx.beans.value.ChangeListener;
import project.backend.singleton.PreviewPaneBack;
import project.frontend.singleton.PreviewPaneFront;

public class PreviewPaneListener {
    private static final PreviewPaneListener instance = new PreviewPaneListener();


    private PreviewPaneListener() {
        setSizePropertyListener();
    }

    private void setSizePropertyListener() {
        PreviewPaneFront previewPaneFront = PreviewPaneFront.getInstance();
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> {
            previewPaneFront.setCanvasSize(previewPaneFront.getWidth(), previewPaneFront.getHeight());
            PreviewPaneBack.getInstance().draw();
        };
        previewPaneFront.widthProperty().addListener(previewPaneSizeListener);
        previewPaneFront.heightProperty().addListener(previewPaneSizeListener);
    }

    public static PreviewPaneListener getInstance() {
        return instance;
    }
}
