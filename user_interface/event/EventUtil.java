package user_interface.event;

public class EventUtil {
    public static void init() {
        new MainStageEvent();
        new TopMenuEvent();
        new TagListViewLEvent();
        new TagListViewREvent();
        new TileViewEvent();
        new FullViewEvent();
    }
}
