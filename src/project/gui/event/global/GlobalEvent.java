package project.gui.event.global;

import project.MainUtil;

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
                    selection.set(filter.getRandomObject());
                    galleryPane.adjustViewportToCurrentFocus();
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
