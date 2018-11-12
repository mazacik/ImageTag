package project.gui.event.global;

import project.database.object.DataObject;
import project.utils.MainUtil;

public class GlobalEvent implements MainUtil {
    public GlobalEvent() {
        onKeyPressed();
    }

    private void onKeyPressed() {
        customStage.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Q:
                    selection.swapState(focus.getCurrentFocus());
                    break;
                case R:
                    DataObject dataObject = filter.getRandomObject();
                    selection.set(dataObject);
                    focus.set(dataObject);
                    break;
                case F12:
                    swapDisplayMode();
                    break;
                case W:
                case A:
                case S:
                case D:
                    focus.moveFocusByKeyCode(event.getCode());
                    break;
                default:
                    break;
            }
            reload.doReload();
        });
    }
}
