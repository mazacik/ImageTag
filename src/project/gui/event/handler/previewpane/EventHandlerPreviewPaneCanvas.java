package project.gui.event.handler.previewpane;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import project.control.FocusControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
import project.gui.GUIInstance;
import project.gui.component.previewpane.PreviewPane;
import project.gui.custom.generic.DataObjectContextMenu;

public abstract class EventHandlerPreviewPaneCanvas {
    private static final DataObjectContextMenu contextMenu = GUIInstance.getDataObjectContextMenu();
    private static final Region previewPane = PreviewPane.getInstance();
    private static final Canvas canvas = PreviewPane.getCanvas();

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
