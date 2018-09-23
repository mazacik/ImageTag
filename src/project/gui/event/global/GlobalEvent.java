package project.gui.event.global;

import project.MainUtils;

public class GlobalEvent implements MainUtils {
    public GlobalEvent() {
        onKeyPressed();
    }

    private void onKeyPressed() {
        customStage.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Q:
                    selectionControl.swapSelectionStateOf(focusControl.getCurrentFocus());
                    break;
                case R:
                    selectionControl.setRandomValidDataObject();
                    break;
                case F12:
                    swapDisplayMode();
                    break;
                case W:
                case A:
                case S:
                case D:
                    focusControl.moveFocusByKeyCode(event.getCode());
                    break;
                default:
                    break;
            }
            reloadControl.doReload();
        });
    }
}
