package project.gui.event.handler.previewpane;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import project.control.FocusControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
import project.gui.component.previewpane.PreviewPane;

public abstract class EventHandlerPreviewPaneCanvas {
    private static final Region previewPane = PreviewPane.getInstance();
    private static final Canvas canvas = PreviewPane.getCanvas();
    private static final ContextMenu contextMenu = PreviewPane.getContextMenu();

    public static void onLeftClick() {
        previewPane.requestFocus();
        contextMenu.hide();
    }
    public static void onRightClick(MouseEvent event) {
        SelectionControl.addDataElement(FocusControl.getCurrentFocus());
        contextMenu.show(previewPane, event.getScreenX(), event.getScreenY());
    }

    public static void onResize() {
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) ->
                ReloadControl.requestComponentReload(true, PreviewPane.class);
        canvas.widthProperty().addListener(previewPaneSizeListener);
        canvas.heightProperty().addListener(previewPaneSizeListener);
    }
}
