package utils;

import control.filter.Filter;
import control.logger.Logger;
import control.reload.Reload;
import control.select.Select;
import control.target.Target;
import database.list.BaseListInfo;
import database.list.MainListData;
import database.list.MainListInfo;
import settings.Settings;
import userinterface.MainStage;
import userinterface.node.center.FullView;
import userinterface.node.center.TileView;
import userinterface.node.side.InfoListViewL;
import userinterface.node.side.InfoListViewR;
import userinterface.node.topmenu.TopMenu;

public interface InstanceRepo {
    Logger logger = Logger.getInstance();
    Settings settings = Settings.getInstance();

    TopMenu topMenu = new TopMenu();
    TileView tileView = new TileView();
    FullView fullView = new FullView();
    InfoListViewL infoListViewL = new InfoListViewL();
    InfoListViewR infoListViewR = new InfoListViewR();
    MainStage mainStage = new MainStage();

    Filter filter = new Filter();
    Target target = new Target();
    Select select = new Select();
    Reload reload = new Reload();

    MainListData mainListData = new MainListData();
    MainListInfo mainListInfo = new MainListInfo();

    BaseListInfo infoListWhite = new BaseListInfo();
    BaseListInfo infoListBlack = new BaseListInfo();
}
