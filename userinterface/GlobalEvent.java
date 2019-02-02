package userinterface;

import utils.CommonUtil;
import utils.InstanceRepo;

public class GlobalEvent implements InstanceRepo {
    public GlobalEvent() {
        onKeyPressed();
    }

    private void onKeyPressed() {
        mainStage.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Q:
                    select.swapState(target.getCurrentTarget());
                    break;
                case R:
                    select.setRandom();
                    reload.doReload();
                    break;
                case F12:
                    CommonUtil.swapDisplayMode();
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
        });
    }
}
