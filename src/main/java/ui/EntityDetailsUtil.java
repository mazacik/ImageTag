package ui;

import javafx.scene.image.Image;
import main.Root;
import misc.FileUtil;
import ui.stage.SimpleMessageStage;

public abstract class EntityDetailsUtil {
	public static void show() {
		Image entityImage = new Image("file:" + FileUtil.getFileEntity(Root.SELECT.getTarget()));
		int width = (int) entityImage.getWidth();
		int height = (int) entityImage.getHeight();
		
		new SimpleMessageStage("Details", width + "x" + height).show();
	}
}
