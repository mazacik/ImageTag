package project.gui;

import java.util.ArrayList;

public interface GUIComponent {
    void refresh();
    void addToSubscribers(GUIComponent subscriber);
    ArrayList<GUIComponent> getChangeListeners();
}
