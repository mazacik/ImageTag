package project.gui.component.preview;

import javafx.beans.value.ChangeListener;

public class PreviewPaneListener {
    /* lazy singleton */
    private static PreviewPaneListener instance;
    public static PreviewPaneListener getInstance() {
        if (instance == null) instance = new PreviewPaneListener();
        return instance;
    }

    /* constructors */
    private PreviewPaneListener() {
        setSizePropertyListener();
    }

    /* event methods */
    private void setSizePropertyListener() {
        PreviewPaneFront previewPaneFront = PreviewPaneFront.getInstance();
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> {
            previewPaneFront.setCanvasSize(previewPaneFront.getWidth(), previewPaneFront.getHeight());
            PreviewPaneBack.getInstance().reloadContent();
        };
        previewPaneFront.widthProperty().addListener(previewPaneSizeListener);
        previewPaneFront.heightProperty().addListener(previewPaneSizeListener);
    }
}
