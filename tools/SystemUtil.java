package tools;

import com.sun.jna.platform.FileUtils;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.File;
import java.io.IOException;

public abstract class SystemUtil {
	public static void deleteFile(String fullPath) {
		FileUtils fileUtils = FileUtils.getInstance();
		if (fileUtils.hasTrash()) {
			try {
				fileUtils.moveToTrash(new File[]{new File(fullPath)});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static final Clipboard clipboard = Clipboard.getSystemClipboard();
	private static final ClipboardContent content = new ClipboardContent();
	public static void setClipboardContent(String string) {
		content.putString(string);
		clipboard.setContent(content);
	}
}
