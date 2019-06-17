package lifecycle;

import control.*;
import database.list.ObjectListMain;
import database.list.TagListMain;
import javafx.stage.Stage;
import settings.Settings;
import user_interface.factory.menu.ClickMenuData;
import user_interface.factory.menu.ClickMenuInfo;
import user_interface.scene.MainStageEvent;
import user_interface.singleton.center.GalleryPane;
import user_interface.singleton.center.MediaPane;
import user_interface.singleton.center.MediaViewEvent;
import user_interface.singleton.center.TileViewEvent;
import user_interface.singleton.side.FilterPane;
import user_interface.singleton.side.SelectPane;
import user_interface.singleton.side.TagListViewLEvent;
import user_interface.singleton.side.TagListViewREvent;
import user_interface.singleton.top.ToolbarPane;
import user_interface.singleton.top.TopMenuEvent;

public abstract class InstanceManager {
    private static Logger logger;
    private static Settings settings;

    private static Filter filter;
    private static Target target;
    private static Select select;
    private static Reload reload;

    private static TagListMain tagListMain;
    private static ObjectListMain objectListMain;

    private static Stage mainStage;
    private static MediaPane mediaPane;
    private static FilterPane filterPane;
    private static SelectPane selectPane;
    private static ToolbarPane toolbarPane;
    private static GalleryPane galleryPane;

    private static ClickMenuData clickMenuData;
    private static ClickMenuInfo clickMenuInfo;

    private static MainStageEvent mainStageEvent;
    private static TopMenuEvent topMenuEvent;
    private static TagListViewLEvent tagListViewLEvent;
    private static TagListViewREvent tagListViewREvent;
    private static TileViewEvent tileViewEvent;
    private static MediaViewEvent mediaViewEvent;


    public static void createInstances() {
        createInstancesSystem();

        createInstancesDatabase();

        createInstancesFrontend();

        createInstancesBackend();
    }

    private static void createInstancesSystem() {
        logger = new Logger();
        settings = new Settings();
    }
    private static void createInstancesDatabase() {
        objectListMain = new ObjectListMain();
        tagListMain = new TagListMain();
    }
    private static void createInstancesFrontend() {
        mainStage = new Stage();

        toolbarPane = new ToolbarPane();    /* needs Settings */
        galleryPane = new GalleryPane();    /* needs Settings */
        mediaPane = new MediaPane();        /* needs Settings, GalleryPane */
        filterPane = new FilterPane();      /* needs Settings */
        selectPane = new SelectPane();      /* needs Settings */

        clickMenuData = new ClickMenuData();
        clickMenuInfo = new ClickMenuInfo();
    }
    private static void createInstancesBackend() {
        filter = new Filter();
        target = new Target();
        select = new Select();
        reload = new Reload();  /* needs Frontend */
    }
    public static void createInstancesEvents() {
        mainStageEvent = new MainStageEvent();
        topMenuEvent = new TopMenuEvent();
        tagListViewLEvent = new TagListViewLEvent();
        tagListViewREvent = new TagListViewREvent();
        tileViewEvent = new TileViewEvent();
        mediaViewEvent = new MediaViewEvent();
    }

    public static Logger getLogger() {
        return logger;
    }
    public static Settings getSettings() {
        return settings;
    }
    public static Filter getFilter() {
        return filter;
    }
    public static Target getTarget() {
        return target;
    }
    public static Select getSelect() {
        return select;
    }
    public static Reload getReload() {
        return reload;
    }
    public static ObjectListMain getObjectListMain() {
        return objectListMain;
    }
    public static TagListMain getTagListMain() {
        return tagListMain;
    }
    public static Stage getMainStage() {
        return mainStage;
    }
    public static ToolbarPane getToolbarPane() {
        return toolbarPane;
    }
    public static GalleryPane getGalleryPane() {
        return galleryPane;
    }
    public static MediaPane getMediaPane() {
        return mediaPane;
    }
    public static FilterPane getFilterPane() {
        return filterPane;
    }
    public static SelectPane getSelectPane() {
        return selectPane;
    }
    public static ClickMenuData getClickMenuData() {
        return clickMenuData;
    }
    public static ClickMenuInfo getClickMenuInfo() {
        return clickMenuInfo;
    }

    public static MainStageEvent getMainStageEvent() {
        return mainStageEvent;
    }
    public static TopMenuEvent getTopMenuEvent() {
        return topMenuEvent;
    }
    public static TagListViewLEvent getTagListViewLEvent() {
        return tagListViewLEvent;
    }
    public static TagListViewREvent getTagListViewREvent() {
        return tagListViewREvent;
    }
    public static TileViewEvent getTileViewEvent() {
        return tileViewEvent;
    }
    public static MediaViewEvent getMediaViewEvent() {
        return mediaViewEvent;
    }
}
