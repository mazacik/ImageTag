package ui.stage;

import base.CustomList;
import base.entity.Entity;
import control.Select;
import control.filter.Filter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import misc.FileUtil;
import misc.Settings;
import ui.override.Scene;

public class CollageStage extends Stage {
	private GridPane gridPane = new GridPane();
	
	public CollageStage() {
		doWork();
		
		this.setScene(new Scene(gridPane));
		this.show();
	}
	
	private void doWork() {
		Image originImage = new Image("file:" + FileUtil.getFileEntity(Select.getTarget()));
		Image scaledImage = getScaledImage("file:" + FileUtil.getFileEntity(Select.getTarget()), originImage.getWidth(), originImage.getHeight(), 1800, 900);
		
		int miniW = (int) scaledImage.getWidth() / Settings.getCollageSize();
		int miniH = (int) scaledImage.getHeight() / Settings.getCollageSize();
		
		CustomList<Mini> minis = getMinis(miniW, miniH);
		
		for (int y = 0; y < Settings.getCollageSize(); y++) {
			for (int x = 0; x < Settings.getCollageSize(); x++) {
				Color averageColor = getAverageColor(scaledImage, x * miniW, y * miniH, miniW, miniH);
				
				gridPane.add(new ImageView(getBestMini(averageColor, minis).image), x, y);
			}
		}
	}
	
	private Image getScaledImage(String url, double originWidth, double originHeight, double maxWidth, double maxHeight) {
		double resultWidth;
		double resultHeight;
		
		if (originWidth < maxWidth && originHeight < maxHeight) {
			// image is smaller than canvas or upscaling is off
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
	
	private Mini getBestMini(Color averageColor, CustomList<Mini> minis) {
		double avgR = averageColor.getRed();
		double avgG = averageColor.getGreen();
		double avgB = averageColor.getBlue();
		
		Mini bestMini = null;
		double bestDiff = Double.MAX_VALUE;
		
		for (Mini mini : minis) {
			double _avgR = mini.averageColor.getRed();
			double _avgG = mini.averageColor.getGreen();
			double _avgB = mini.averageColor.getBlue();
			
			double _difR = Math.abs(avgR - _avgR);
			double _difG = Math.abs(avgG - _avgG);
			double _difB = Math.abs(avgB - _avgB);
			
			double diff = _difR + _difG + _difB;
			
			if (bestDiff > diff) {
				bestMini = mini;
				bestDiff = diff;
			}
		}
		
		return bestMini;
	}
	
	private CustomList<Mini> getMinis(double reqW, double reqH) {
		CustomList<Mini> minis = new CustomList<>();
		
		for (Entity entity : Filter.getEntities()) {
			Image image = new Image("file:" + FileUtil.getFileCache(entity), reqW, reqH, false, false);
			Color averageColor = getAverageColor(image, 0, 0, image.getWidth(), image.getHeight());
			minis.add(new Mini(image, averageColor));
		}
		
		return minis;
	}
	
	public static Color getAverageColor(Image image, int x0, int y0, double w, double h) {
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
	
	private static class Mini {
		private Image image;
		private Color averageColor;
		
		public Mini(Image image, Color averageColor) {
			this.image = image;
			this.averageColor = averageColor;
		}
	}
}
