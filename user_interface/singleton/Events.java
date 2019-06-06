package user_interface.singleton;

import user_interface.scene.MainStageEvent;
import user_interface.singleton.center.MediaViewEvent;
import user_interface.singleton.center.TileViewEvent;
import user_interface.singleton.side.TagListViewLEvent;
import user_interface.singleton.side.TagListViewREvent;
import user_interface.singleton.top.TopMenuEvent;

public class Events {
    public static void init() {
        new MainStageEvent();
        new TopMenuEvent();
        new TagListViewLEvent();
        new TagListViewREvent();
        new TileViewEvent();
        new MediaViewEvent();
    }
}
