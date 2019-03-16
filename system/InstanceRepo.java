package system;

import control.filter.Filter;
import control.logger.Logger;
import control.reload.Reload;
import control.select.Select;
import control.target.Target;
import database.list.DataObjectListMain;
import database.list.InfoObjectList;
import database.list.InfoObjectListMain;
import settings.CoreSettings;
import settings.UserSettings;
import user_interface.MainStage;
import user_interface.singleton.center.FullView;
import user_interface.singleton.center.TileView;
import user_interface.singleton.side.InfoListViewL;
import user_interface.singleton.side.InfoListViewR;
import user_interface.singleton.top.TopMenu;

public interface InstanceRepo {
    Logger logger = Logger.getInstance();
    UserSettings userSettings = UserSettings.getInstance();
    CoreSettings coreSettings = CoreSettings.getInstance();

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

    DataObjectListMain mainDataList = new DataObjectListMain();
    InfoObjectListMain mainInfoList = new InfoObjectListMain();

    InfoObjectList infoListWhite = new InfoObjectList();
    InfoObjectList infoListBlack = new InfoObjectList();
}
