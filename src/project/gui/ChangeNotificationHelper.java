package project.gui;

import java.util.ArrayList;

public interface ChangeNotificationHelper {
    void refresh();
    ArrayList<ChangeNotificationHelper> getChangeListeners();
}
