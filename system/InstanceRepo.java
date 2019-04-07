package system;

import control.filter.Filter;
import control.logger.Logger;
import control.reload.Reload;
import control.select.Select;
import control.target.Target;
import database.list.DataObjectListMain;
import database.list.InfoObjectList;
import database.list.InfoObjectListMain;
import javafx.stage.Stage;
import settings.Settings;
import user_interface.factory.node.popup.DataObjectRCM;
import user_interface.factory.node.popup.InfoObjectRCM;
import user_interface.singleton.center.FullView;
import user_interface.singleton.center.TileView;
import user_interface.singleton.side.TagListViewL;
import user_interface.singleton.side.TagListViewR;
import user_interface.singleton.top.TopMenu;

public interface InstanceRepo {
    Logger logger = Logger.getInstance();
    Settings settings = Settings.getInstance();

    TopMenu topMenu = new TopMenu();
    TileView tileView = new TileView();
    FullView fullView = new FullView();
    TagListViewL tagListViewL = new TagListViewL();
    TagListViewR tagListViewR = new TagListViewR();
    Stage mainStage = new Stage();

    DataObjectRCM dataObjectRCM = new DataObjectRCM();
    InfoObjectRCM infoObjectRCM = new InfoObjectRCM();

    Filter filter = new Filter();
    Target target = new Target();
    Select select = new Select();
    Reload reload = new Reload();

    DataObjectListMain mainDataList = new DataObjectListMain();
    InfoObjectListMain mainInfoList = new InfoObjectListMain();

    InfoObjectList infoListWhite = new InfoObjectList();
    InfoObjectList infoListBlack = new InfoObjectList();
}
