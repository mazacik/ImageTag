package gui.event.global;

import database.object.DataObject;
import utils.MainUtil;

public class GlobalEvent implements MainUtil {
    public GlobalEvent() {
        onKeyPressed();
    }

    private void onKeyPressed() {
        mainStage.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Q:
                    select.swapState(target.getCurrentFocus());
                    break;
                case R:
                    DataObject dataObject = filter.getRandomObject();
                    select.set(dataObject);
                    target.set(dataObject);
                    break;
                case F12:
                    swapDisplayMode();
                    break;
                case W:
                case A:
                case S:
                case D:
                    target.move(event.getCode());
                    break;
                default:
                    break;
            }
            reload.doReload();
        });
    }
}
