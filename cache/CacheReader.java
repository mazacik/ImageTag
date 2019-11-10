package application.cache;

import application.baseobject.entity.Entity;
import application.gui.decorator.SizeUtil;
import application.tools.FileUtil;
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

import java.io.File;

public abstract class CacheReader {
	private static Image placeholder = null;
	private static final boolean ALWAYS_USE_PLACEHOLDER = false;
	
	public static Image get(Entity entity) {
		File cacheFile = new File(FileUtil.getCacheFilePath(entity));
		Image image;
		
		if (cacheFile.exists()) {
			image = new Image("file:" + cacheFile.getAbsolutePath(), true);
		} else {
			image = CacheWriter.write(entity);
		}
		
		if (image == null || ALWAYS_USE_PLACEHOLDER) {
			if (placeholder == null) {
				placeholder = CacheReader.createPlaceholder();
			}
			
			return placeholder;
		}
		
		return image;
	}
	
	private static Image createPlaceholder() {
		Label label = new Label("Placeholder");
		label.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
		label.setWrapText(true);
		label.setFont(new Font(25));
		label.setAlignment(Pos.CENTER);
		
		int size = (int) SizeUtil.getGalleryTileSize();
		label.setMinWidth(size);
		label.setMinHeight(size);
		label.setMaxWidth(size);
		label.setMaxHeight(size);
		
		WritableImage img = new WritableImage(size, size);
		Scene scene = new Scene(new Group(label));
		scene.setFill(Color.GRAY);
		scene.snapshot(img);
		
		return img;
	}
}
