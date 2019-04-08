package user_interface.event;

import javafx.beans.value.ChangeListener;
import system.InstanceRepo;

public class FullViewEvent implements InstanceRepo {
    public FullViewEvent() {
        onMouseClick();
        onResize();
    }

    private void onMouseClick() {
        fullView.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    fullView.requestFocus();
                    dataObjectRCM.hide();
                    break;
                case SECONDARY:
                    dataObjectRCM.show(fullView, event.getScreenX(), event.getScreenY());
                    break;
                default:
                    break;
            }
        });
    }
    private void onResize() {
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> fullView.reload();
        fullView.widthProperty().addListener(previewPaneSizeListener);
        fullView.heightProperty().addListener(previewPaneSizeListener);
    }
}
