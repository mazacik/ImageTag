package utils;

import control.filter.Filter;
import control.logger.Logger;
import control.reload.Reload;
import control.select.Select;
import control.target.Target;
import database.list.BaseListInfo;
import database.list.MainListData;
import database.list.MainListInfo;
import gui.MainStage;
import gui.node.center.FullView;
import gui.node.center.TileView;
import gui.node.side.InfoListViewL;
import gui.node.side.InfoListViewR;
import gui.node.toolbar.Toolbar;
import settings.Settings;

public interface MainUtil {
    /* controls */
    Filter filter = new Filter();
    Target target = new Target();
    Select select = new Select();
    Reload reload = new Reload();
    Logger logger = new Logger();

    Settings settings = new Settings().readFromDisk();

    /* arraylists */
    MainListData mainListData = new MainListData();
    MainListInfo mainListInfo = new MainListInfo();

    BaseListInfo infoListWhite = new BaseListInfo();
    BaseListInfo infoListBlack = new BaseListInfo();

    /* gui nodes */
    Toolbar toolbar = new Toolbar();
    TileView tileView = new TileView();
    FullView fullView = new FullView();
    InfoListViewL infoListViewL = new InfoListViewL();
    InfoListViewR infoListViewR = new InfoListViewR();
    MainStage mainStage = new MainStage();

    default void swapDisplayMode() {
        mainStage.swapDisplayMode();
    }
    default boolean isFullView() {
        return mainStage.isFullView();
    }
}
