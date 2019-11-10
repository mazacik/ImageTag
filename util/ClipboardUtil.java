package application.util;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public abstract class ClipboardUtil {
    private static final Clipboard clipboard = Clipboard.getSystemClipboard();
    private static final ClipboardContent content = new ClipboardContent();

    public static void setClipboardContent(String string) {
        content.putString(string);
        clipboard.setContent(content);
    }
}
