package application.util.enums;

import com.sun.jna.platform.FileUtils;

import java.io.File;
import java.io.IOException;

public class SystemUtil {
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
}
