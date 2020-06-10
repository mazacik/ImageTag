package backend.cache;

import backend.list.entity.Entity;
import backend.list.entity.EntityList;
import backend.misc.FileUtil;
import backend.misc.Settings;
import backend.override.Thread;
import frontend.UserInterface;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.Main;

import java.io.File;

public abstract class CacheLoader {
	private static final Image placeholder = new WritableImage(Settings.GALLERY_TILE_SIZE.getInteger(), Settings.GALLERY_TILE_SIZE.getInteger()) {{
		Label label = new Label("Placeholder");
		label.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
		label.setWrapText(true);
		label.setFont(new Font(26));
		label.setAlignment(Pos.CENTER);
		
		int size = Settings.GALLERY_TILE_SIZE.getInteger();
		label.setMinWidth(size);
		label.setMinHeight(size);
		label.setMaxWidth(size);
		label.setMaxHeight(size);
		
		Scene scene = new Scene(new Group(label));
		scene.setFill(Color.GRAY);
		scene.snapshot(this);
	}};
	
	public static void startCacheThread(EntityList entityList) {
		startCacheThread(entityList, false);
	}
	public static void startCacheThread(EntityList entityList, boolean recreate) {
		if (Main.DEBUG_USE_CACHE) {
			new Thread(() -> {
				UserInterface.getStage().getMainScene().showLoadingBar(Thread.currentThread(), entityList.size());
				
				for (Entity entity : new EntityList(entityList)) {
					if (Thread.currentThread().isInterrupted()) {
						return;
					}
					
					entity.getTile().setImage(CacheLoader.get(entity, recreate));
					UserInterface.getStage().getMainScene().advanceLoadingBar(Thread.currentThread());
				}
				
				UserInterface.getStage().getMainScene().hideLoadingBar(Thread.currentThread());
			}).start();
		}
	}
	
	public static void reset() {
		Main.DB_ENTITY.forEach(entity -> entity.getTile().setImage(null));
		CacheLoader.startCacheThread(Main.DB_ENTITY, true);
	}
	
	public static Image get(Entity entity) {
		return get(entity, false);
	}
	public static Image get(Entity entity, boolean forceCreate) {
		File cacheFile = new File(FileUtil.getFileCache(entity));
		Image image;
		
		if (forceCreate || !cacheFile.exists()) {
			image = CacheCreator.create(entity);
		} else {
			image = new Image("file:" + cacheFile.getAbsolutePath());
		}
		
		return (image == null) ? placeholder : image;
	}
}
