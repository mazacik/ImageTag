package utils;

import control.filter.Filter;
import control.target.Target;
import control.logger.Logger;
import database.list.InfoListMain;
import control.reload.Reload;
import control.select.Select;
import database.list.DataListMain;
import database.list.InfoList;
import gui.MainStage;
import gui.singleton.center.TileView;
import gui.singleton.side.InfoListL;
import gui.singleton.center.FullView;
import gui.singleton.side.InfoListR;
import gui.singleton.toolbar.Toolbar;

public interface MainUtil {
    /* controls */
    Filter filter = new Filter();
    Target target = new Target();
    Select select = new Select();
    Reload reload = new Reload();
    Logger logger = new Logger();

    /* arraylists */
    DataListMain dataListMain = new DataListMain();
    InfoListMain infoListMain = new InfoListMain();

    InfoList infoListWhite = new InfoList();
    InfoList infoListBlack = new InfoList();

    /* gui nodes */
    Toolbar toolbar = new Toolbar();
    TileView tileView = new TileView();
    FullView fullView = new FullView();
    InfoListL infoListL = new InfoListL();
    InfoListR infoListR = new InfoListR();
    MainStage mainStage = new MainStage();

    default void swapDisplayMode() {
        mainStage.swapDisplayMode();
    }
    default boolean isFullView() {
        return mainStage.isFullView();
    }
}
