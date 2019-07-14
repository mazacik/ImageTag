package database.loader;

import database.object.DataObject;
import javafx.application.Platform;
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
import userinterface.style.SizeUtil;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@SuppressWarnings("FieldCanBeLocal")
public abstract class ThumbnailReader {
	private static Image placeholder = null;
	private static boolean alwaysUsePlaceholder = false;
	
	public static Image readThumbnail(DataObject dataObject) {
		File cacheFile = new File(dataObject.getCacheFile());
		Image image;
		
		if (cacheFile.exists()) {
			image = new Image("file:" + dataObject.getCacheFile(), true);
		} else {
			image = ThumbnailCreator.createThumbnail(dataObject, cacheFile);
		}
		
		if (image == null || alwaysUsePlaceholder) {
			if (placeholder == null) {
				final FutureTask<Image> query = new FutureTask<>(ThumbnailReader::createPlaceholder);
				Platform.runLater(query);
				try {
					placeholder = query.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
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
		
		int size = (int) SizeUtil.getGalleryIconSize();
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
