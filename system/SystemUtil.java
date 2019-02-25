package system;

import java.awt.*;

public abstract class SystemUtil {
    private static Toolkit systemToolkit = Toolkit.getDefaultToolkit();

    public static int getScreenWidth() {
        return systemToolkit.getScreenSize().width;
    }
    public static int getScreenHeight() {
        return systemToolkit.getScreenSize().height;
    }
}
