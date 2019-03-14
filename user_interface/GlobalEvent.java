package user_interface;

import system.CommonUtil;
import system.InstanceRepo;
import user_interface.single_instance.center.BaseTileEvent;

public class GlobalEvent implements InstanceRepo {
    public GlobalEvent() {
        onKeyPressed();
    }

    private void onKeyPressed() {
        mainStage.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    topMenu.requestFocus();
                    break;
                case Q:
                    select.swapState(target.getCurrentTarget());
                    reload.doReload();
                    break;
                case E:
                    BaseTileEvent.onGroupButtonClick(target.getCurrentTarget());
                    reload.doReload();
                    break;
                case R:
                    select.setRandom();
                    reload.doReload();
                    break;
                case F:
                    CommonUtil.swapDisplayMode();
                    break;
                case W:
                case A:
                case S:
                case D:
                    target.move(event.getCode());
                    reload.doReload();
                    break;
                default:
                    break;
            }
        });
    }
}
