package gui.event.global;

import database.object.DataObject;
import utils.MainUtil;

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
                    focus.move(event.getCode());
                    break;
                default:
                    break;
            }
            reload.doReload();
        });
    }
}
