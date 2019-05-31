package system;

import control.filter.Filter;
import control.logger.Logger;
import control.reload.Reload;
import control.select.Select;
import control.target.Target;
import database.list.DataObjectListMain;
import database.list.TagList;
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

public interface Instances {
    Logger logger = Logger.getInstance();
    Settings settings = Settings.getInstance();

    TopMenu topMenu = new TopMenu();
    TileView tileView = new TileView();
    MediaView mediaView = new MediaView();
    TagListViewL tagListViewL = new TagListViewL();
    TagListViewR tagListViewR = new TagListViewR();
    Stage mainStage = new Stage();

    ClickMenuData clickMenuData = new ClickMenuData();
    ClickMenuInfo clickMenuInfo = new ClickMenuInfo();

    Filter filter = new Filter();
    Target target = new Target();
    Select select = new Select();
    Reload reload = new Reload();

    DataObjectListMain mainDataList = new DataObjectListMain();
    TagListMain mainInfoList = new TagListMain();

    TagList infoListWhite = new TagList();
    TagList infoListBlack = new TagList();
}
