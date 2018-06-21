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
        PreviewPane previewPane = PreviewPane.getInstance();
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> {
            previewPane.setCanvasSize(previewPane.getWidth(), previewPane.getHeight());
            PreviewPaneBack.getInstance().reloadContent();
        };
        previewPane.widthProperty().addListener(previewPaneSizeListener);
        previewPane.heightProperty().addListener(previewPaneSizeListener);
    }
}
