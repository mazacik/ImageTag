package ui.stage;

import base.CustomList;
import base.entity.Entity;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.Root;
import misc.FileUtil;
import misc.Settings;
import ui.override.GridPane;
import ui.override.Scene;

public class CollageStage extends Stage {
	public CollageStage() {
		new Thread(() -> {
			Image originImage = new Image("file:" + FileUtil.getFileEntity(Root.SELECT.getTarget()));
			Image scaledImage = getSmallerImage("file:" + FileUtil.getFileEntity(Root.SELECT.getTarget()), originImage.getWidth(), originImage.getHeight(), 1800, 900);
			
			int miniW = (int) scaledImage.getWidth() / Settings.COLLAGE_SIZE.getInteger();
			int miniH = (int) scaledImage.getHeight() / Settings.COLLAGE_SIZE.getInteger();
			
			Root.PSC.MAIN_STAGE.showLoadingBar(this, Root.FILTER.size());
			
			CustomList<CollagePiece> database = new CustomList<>();
			for (Entity entity : Root.FILTER) {
				Image image = new Image("file:" + FileUtil.getFileCache(entity), miniW, miniH, false, false);
				Color averageColor = getAverageColor(image, 0, 0, image.getWidth(), image.getHeight());
				database.addImpl(new CollagePiece(image, averageColor));
				
				Root.PSC.MAIN_STAGE.advanceLoadingBar(this);
			}
			
			Root.PSC.MAIN_STAGE.hideLoadingBar(this);
			
			GridPane gridPane = new GridPane();
			for (int y = 0; y < Settings.COLLAGE_SIZE.getInteger(); y++) {
				for (int x = 0; x < Settings.COLLAGE_SIZE.getInteger(); x++) {
					Color averageColor = getAverageColor(scaledImage, x * miniW, y * miniH, miniW, miniH);
					ImageView imageView = new ImageView(getBestPiece(averageColor, database).image);
					int finalX = x;
					int finalY = y;
					Platform.runLater(() -> gridPane.add(imageView, finalX, finalY));
				}
			}
			
			Platform.runLater(() -> {
				this.setScene(new Scene(gridPane));
				this.show();
			});
		}).start();
	}
	
	private CollagePiece getBestPiece(Color averageColor, CustomList<CollagePiece> collagePieces) {
		double avgR = averageColor.getRed();
		double avgG = averageColor.getGreen();
		double avgB = averageColor.getBlue();
		
		CollagePiece bestPiece = null;
		double bestDiff = Double.MAX_VALUE;
		
		for (CollagePiece collagePiece : collagePieces) {
			double _avgR = collagePiece.averageColor.getRed();
			double _avgG = collagePiece.averageColor.getGreen();
			double _avgB = collagePiece.averageColor.getBlue();
			
			double _difR = Math.abs(avgR - _avgR);
			double _difG = Math.abs(avgG - _avgG);
			double _difB = Math.abs(avgB - _avgB);
			
			double diff = _difR + _difG + _difB;
			
			if (bestDiff > diff) {
				bestPiece = collagePiece;
				bestDiff = diff;
			}
		}
		
		return bestPiece;
	}
	private Image getSmallerImage(String url, double originWidth, double originHeight, double maxWidth, double maxHeight) {
		double resultWidth;
		double resultHeight;
		
		if (originWidth < maxWidth && originHeight < maxHeight) {
			// image is smaller than requested
			resultWidth = originWidth;
			resultHeight = originHeight;
		} else {
			// scale image to fit width
			resultWidth = maxWidth;
			resultHeight = originHeight * maxWidth / originWidth;
			
			// if scaled image is too tall, scale to fit height instead
			if (resultHeight > maxHeight) {
				resultHeight = maxHeight;
				resultWidth = originWidth * maxHeight / originHeight;
			}
		}
		
		return new Image(url, resultWidth, resultHeight, false, false);
	}
	private Color getAverageColor(Image image, int x0, int y0, double w, double h) {
		double sumr = 0, sumg = 0, sumb = 0;
		
		for (int y = y0; y + 2 < y0 + h; y++) {
			for (int x = x0; x + 2 < x0 + w; x++) {
				Color pixel = image.getPixelReader().getColor(x, y);
				sumr += pixel.getRed();
				sumg += pixel.getGreen();
				sumb += pixel.getBlue();
			}
		}
		
		double num = w * h;
		
		double r = sumr / num;
		double g = sumg / num;
		double b = sumb / num;
		
		if (r > 1) r = 1;
		if (g > 1) g = 1;
		if (b > 1) b = 1;
		
		return new Color(r, g, b, 1);
	}
	
	private static class CollagePiece {
		private Image image;
		private Color averageColor;
		
		public CollagePiece(Image image, Color averageColor) {
			this.image = image;
			this.averageColor = averageColor;
		}
	}
}
