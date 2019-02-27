package user_interface;

import system.CommonUtil;
import system.InstanceRepo;

public class GlobalEvent implements InstanceRepo {
    public GlobalEvent() {
        onKeyPressed();
    }

    private void onKeyPressed() {
        mainStage.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    mainStage.requestFocus();
                    break;
                case Q:
                    select.swapState(target.getCurrentTarget());
                    break;
                case F2:
                    select.setRandom();
                    reload.doReload();
                    break;
                case F3:
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
