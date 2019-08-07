package application.gui.decorator;

import application.gui.decorator.enums.ColorType;
import application.gui.nodes.ColorData;
import application.gui.nodes.custom.IntroStageNode;
import application.gui.nodes.simple.CheckBoxNode;
import application.gui.nodes.simple.EditNode;
import application.gui.nodes.simple.TextNode;
import application.main.Instances;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Window;

import java.util.ArrayList;

public abstract class Decorator {
	private static Font font = new Font(Instances.getSettings().getFontSize());
	private static ArrayList<ColorData> nodeList = new ArrayList<>();
	
	public static void applyScrollbarStyle(Node node) {
		try {
			node.applyCss();
			node.lookup(".track").setStyle("-fx-background-color: transparent;" +
					" -fx-border-color: transparent;" +
					" -fx-background-radius: 0.0em;" +
					" -fx-border-radius: 0.0em;" +
					" -fx-padding: 0.0 0.0 0.0 0.0;");
			node.lookup(".scroll-bar").setStyle("-fx-background-color: transparent;" +
					" -fx-pref-width: 15;" +
					" -fx-padding: 3 2 3 3;");
			node.lookup(".increment-button").setStyle("-fx-background-color: transparent;" +
					" -fx-background-radius: 0.0em;" +
					" -fx-padding: 0.0 0.0 0.0 0.0;");
			node.lookup(".decrement-button").setStyle("-fx-background-color: transparent;" +
					" -fx-background-radius: 0.0em;" +
					" -fx-padding: 0.0 0.0 0.0 0.0;");
			node.lookup(".increment-arrow").setStyle("-fx-padding: 0.0em 0.0;");
			node.lookup(".decrement-arrow").setStyle("-fx-padding: 0.0em 0.0;");
			node.lookup(".thumb").setStyle("-fx-background-color: derive(black, 90.0%);" +
					" -fx-background-insets: 0.0, 0.0, 0.0;" +
					" -fx-background-radius: 0.0em;");
			node.lookup(".viewport").setStyle("-fx-background-color: transparent;");
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public static void applyStyle(Region region) {
		ArrayList<ColorData> colorDataList = new ArrayList<>();
		for (ColorData colorData : getNodeList()) {
			if (colorData.getRegion() == region) {
				colorDataList.add(colorData);
			}
		}
		applyStyle(colorDataList);
	}
	public static void applyStyle(ArrayList<ColorData> colorDataList) {
		for (ColorData colorData : colorDataList) {
			colorData.getRegion().setBackground(ColorUtil.getBackgroundDef(colorData));
			if (colorData.getRegion() instanceof HBox || colorData.getRegion() instanceof VBox) {
				colorData.getRegion().setOnMouseEntered(event -> colorData.getRegion().setBackground(ColorUtil.getBackgroundAlt(colorData)));
				colorData.getRegion().setOnMouseExited(event -> colorData.getRegion().setBackground(ColorUtil.getBackgroundDef(colorData)));
			} else if (colorData.getRegion() instanceof TextNode) {
				TextNode label = ((TextNode) colorData.getRegion());
				if (colorData.getTextFillDef() != ColorType.NULL) {
					label.setTextFill(ColorUtil.getTextColorDef(colorData));
				}
				if (colorData.getBackgroundAlt() != ColorType.NULL && colorData.getTextFillAlt() != ColorType.NULL) {
					label.setOnMouseExited(event -> {
						label.setBackground(ColorUtil.getBackgroundDef(colorData));
						label.setTextFill(ColorUtil.getTextColorDef(colorData));
						//label.setCursor(Cursor.DEFAULT);
					});
					label.setOnMouseEntered(event -> {
						label.setBackground(ColorUtil.getBackgroundAlt(colorData));
						label.setTextFill(ColorUtil.getTextColorAlt(colorData));
						//label.setCursor(Cursor.HAND);
					});
				} else if (colorData.getBackgroundAlt() != ColorType.NULL) {
					label.setOnMouseExited(event -> {
						label.setBackground(ColorUtil.getBackgroundDef(colorData));
						//label.setCursor(Cursor.DEFAULT);
					});
					label.setOnMouseEntered(event -> {
						label.setBackground(ColorUtil.getBackgroundAlt(colorData));
						//label.setCursor(Cursor.HAND);
					});
				} else if (colorData.getTextFillAlt() != ColorType.NULL) {
					label.setOnMouseExited(event -> {
						label.setTextFill(ColorUtil.getTextColorDef(colorData));
						//label.setCursor(Cursor.DEFAULT);
					});
					label.setOnMouseEntered(event -> {
						label.setTextFill(ColorUtil.getTextColorAlt(colorData));
						//label.setCursor(Cursor.HAND);
					});
				}
			} else if (colorData.getRegion() instanceof IntroStageNode) {
				IntroStageNode introStageNode = ((IntroStageNode) colorData.getRegion());
				introStageNode.setBackground(ColorUtil.getBackgroundDef(colorData));
				introStageNode.setOnMouseEntered(event -> {
					introStageNode.setBackground(ColorUtil.getBackgroundAlt(colorData));
					introStageNode.setCursor(Cursor.HAND);
					introStageNode.getNodeRemove().setVisible(true);
				});
				introStageNode.setOnMouseExited(event -> {
					introStageNode.setBackground(ColorUtil.getBackgroundDef(colorData));
					introStageNode.setCursor(Cursor.DEFAULT);
					introStageNode.getNodeRemove().setVisible(false);
				});
			} else if (colorData.getRegion() instanceof EditNode) {
				EditNode editNode = ((EditNode) colorData.getRegion());
				Color color = ColorUtil.getTextColorDef(colorData);
				String colorString = String.format("#%02X%02X%02X",
						(int) (color.getRed() * 255),
						(int) (color.getGreen() * 255),
						(int) (color.getBlue() * 255));
				editNode.setStyle("-fx-text-fill: " + colorString + ";");
			} else if (colorData.getRegion() instanceof CheckBoxNode) {
				CheckBoxNode checkBoxNode = ((CheckBoxNode) colorData.getRegion());
				if (colorData.getTextFillDef() != ColorType.NULL) {
					checkBoxNode.setTextFill(ColorUtil.getTextColorDef(colorData));
				}
			}
		}
	}
	public static void applyStyle() {
		applyStyle(getNodeList());
	}
	
	public static void manage(Region region, ColorType ct) {
		manage(region, ct, ct, ct, ct);
	}
	public static void manage(Region region, ColorType backgroundDef, ColorType backgroundAlt, ColorType textFillDef, ColorType textFillAlt) {
		nodeList.add(new ColorData(region, backgroundDef, backgroundAlt, textFillDef, textFillAlt));
		applyStyle(region);
	}
	
	public static void removeOrphanNodes() {
		ArrayList<ColorData> orphans = new ArrayList<>();
		for (ColorData colorData : nodeList) {
			try {
				Node node = colorData.getRegion();
				Scene scene = node.getScene();
				Window window = scene.getWindow();
				
				//Window window = colorData.getRegion().getScene().getWindow();
				if (window == null) throw new NullPointerException();
			} catch (NullPointerException e) {
				orphans.add(colorData);
			}
		}
		
		for (ColorData orphan : orphans) {
			nodeList.remove(orphan);
		}
	}
	
	public static Font getFont() {
		return font;
	}
	public static ArrayList<ColorData> getNodeList() {
		return nodeList;
	}
}
