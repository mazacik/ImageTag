package lifecycle;

import control.filter.Filter;
import control.logger.Logger;
import control.reload.Reload;
import control.select.Select;
import control.target.Target;
import database.list.DataObjectListMain;
import database.list.TagListMain;
import javafx.stage.Stage;
import settings.Settings;
import user_interface.factory.menu.ClickMenuData;
import user_interface.factory.menu.ClickMenuInfo;
import user_interface.singleton.center.MediaView;
import user_interface.singleton.center.TileView;
import user_interface.singleton.side.TagListViewL;
import user_interface.singleton.side.TagListViewR;
import user_interface.singleton.top.TopMenu;

public abstract class InstanceManager {
    private static Logger logger;
    private static Settings settings;

    private static Filter filter;
    private static Target target;
    private static Select select;
    private static Reload reload;

    private static DataObjectListMain mainDataList;
    private static TagListMain mainInfoList;

    private static Stage mainStage;
    private static TopMenu topMenu;
    private static TileView tileView;
    private static MediaView mediaView;
    private static TagListViewL tagListViewL;
    private static TagListViewR tagListViewR;

    private static ClickMenuData clickMenuData;
    private static ClickMenuInfo clickMenuInfo;

    public static void init() {
        initSystem();

        initDatabase();

        initGUI();

        reload.init();
    }

    private static void initSystem() {
        logger = new Logger();
        settings = Settings.readFromDisk();

        filter = new Filter();
        target = new Target();
        select = new Select();
        reload = new Reload();
    }
    private static void initDatabase() {
        mainDataList = new DataObjectListMain();
        mainInfoList = new TagListMain();
    }
    private static void initGUI() {
        mainStage = new Stage();
        topMenu = new TopMenu();
        tileView = new TileView();
        mediaView = new MediaView();
        tagListViewL = new TagListViewL();
        tagListViewR = new TagListViewR();

        clickMenuData = new ClickMenuData();
        clickMenuInfo = new ClickMenuInfo();
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
    public static DataObjectListMain getMainDataList() {
        return mainDataList;
    }
    public static TagListMain getMainInfoList() {
        return mainInfoList;
    }
    public static Stage getMainStage() {
        return mainStage;
    }
    public static TopMenu getTopMenu() {
        return topMenu;
    }
    public static TileView getTileView() {
        return tileView;
    }
    public static MediaView getMediaView() {
        return mediaView;
    }
    public static TagListViewL getTagListViewL() {
        return tagListViewL;
    }
    public static TagListViewR getTagListViewR() {
        return tagListViewR;
    }
    public static ClickMenuData getClickMenuData() {
        return clickMenuData;
    }
    public static ClickMenuInfo getClickMenuInfo() {
        return clickMenuInfo;
    }
}
