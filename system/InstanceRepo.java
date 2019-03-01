package system;

import control.filter.Filter;
import control.logger.Logger;
import control.reload.Reload;
import control.select.Select;
import control.target.Target;
import database.list.InfoList;
import database.list.MainInfoList;
import database.list.MainListData;
import settings.CoreSettings;
import settings.UserSettings;
import user_interface.MainStage;
import user_interface.single_instance.center.FullView;
import user_interface.single_instance.center.TileView;
import user_interface.single_instance.side.InfoListViewL;
import user_interface.single_instance.side.InfoListViewR;
import user_interface.single_instance.top.TopMenu;

public interface InstanceRepo {
    Logger logger = Logger.getInstance();
    CoreSettings coreSettings = CoreSettings.getInstance();
    UserSettings userSettings = UserSettings.getInstance();

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
    MainInfoList mainListInfo = new MainInfoList();

    InfoList infoListWhite = new InfoList();
    InfoList infoListBlack = new InfoList();
}
