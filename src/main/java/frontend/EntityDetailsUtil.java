package frontend;

import backend.misc.FileUtil;
import frontend.stage.SimpleMessageStage;
import javafx.scene.image.Image;
import main.Main;

public abstract class EntityDetailsUtil {
	public static void show() {
		Image entityImage = new Image("file:" + FileUtil.getFileEntity(Main.SELECT.getTarget()));
		int width = (int) entityImage.getWidth();
		int height = (int) entityImage.getHeight();
		
		new SimpleMessageStage("Details", width + "x" + height).show();
	}
}
