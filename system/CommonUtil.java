package system;

import database.list.DataObjectList;
import database.loader.LoaderUtil;
import database.object.DataObject;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import settings.SettingsEnum;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.IntroWindowCell;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.scene.MainScene;
import user_interface.singleton.center.BaseTile;
import user_interface.singleton.side.GroupNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public abstract class CommonUtil implements InstanceRepo {
    private static int nightMode = settings.intValueOf(SettingsEnum.COLORMODE);

    private static Font font = new Font(settings.intValueOf(SettingsEnum.FONTSIZE));
    public static boolean isNightMode() {
        return nightMode == 1;
    }
    public static void setNightMode(boolean value) {
        if (!value) {
            nightMode = 0;
        } else {
            nightMode = 1;
        }
        settings.setValueOf(SettingsEnum.COLORMODE, nightMode);
        updateNodeProperties();
    }
    private static Background getBackgroundDef(ColorData colorData) {
        if (colorData.getBackgroundDef() == ColorType.DEF) {
            return ColorUtil.getBackgroundDef();
        } else if (colorData.getBackgroundDef() == ColorType.ALT) {
            return ColorUtil.getBackgroundAlt();
        } else {
            return null;
        }
    }
    private static Background getBackgroundAlt(ColorData colorData) {
        if (colorData.getBackgroundAlt() == ColorType.DEF) {
            return ColorUtil.getBackgroundDef();
        } else if (colorData.getBackgroundAlt() == ColorType.ALT) {
            return ColorUtil.getBackgroundAlt();
        } else {
            return null;
        }
    }
    private static Color getTextColorDef(ColorData colorData) {
        if (colorData.getTextFillDef() == ColorType.DEF) {
            return ColorUtil.getTextColorDef();
        } else if (colorData.getTextFillDef() == ColorType.ALT) {
            return ColorUtil.getTextColorAlt();
        } else {
            return null;
        }
    }
    private static Color getTextColorHov(ColorData colorData) {
        if (colorData.getTextFillHov() == ColorType.DEF) {
            return ColorUtil.getTextColorDef();
        } else if (colorData.getTextFillHov() == ColorType.ALT) {
            return ColorUtil.getTextColorAlt();
        } else {
            return null;
        }
    }

    public static void updateNodeProperties(ArrayList<ColorData> colorDataList) {
        for (ColorData colorData : colorDataList) {
            colorData.getNode().setBackground(getBackgroundDef(colorData));
            if (colorData.getNode() instanceof Label) {
                Label label = ((Label) colorData.getNode());
                if (colorData.getTextFillDef() != ColorType.NULL) {
                    label.setTextFill(getTextColorDef(colorData));
                }
                if (colorData.getBackgroundAlt() != ColorType.NULL && colorData.getTextFillHov() != ColorType.NULL) {
                    label.setOnMouseExited(event -> {
                        label.setBackground(getBackgroundDef(colorData));
                        label.setTextFill(getTextColorDef(colorData));
                    });
                    label.setOnMouseEntered(event -> {
                        label.setBackground(getBackgroundAlt(colorData));
                        label.setTextFill(getTextColorHov(colorData));
                    });
                } else if (colorData.getBackgroundAlt() != ColorType.NULL) {
                    label.setOnMouseExited(event -> label.setBackground(getBackgroundDef(colorData)));
                    label.setOnMouseEntered(event -> label.setBackground(getBackgroundAlt(colorData)));
                } else if (colorData.getTextFillHov() != ColorType.NULL) {
                    label.setOnMouseExited(event -> label.setTextFill(getTextColorDef(colorData)));
                    label.setOnMouseEntered(event -> label.setTextFill(getTextColorHov(colorData)));
                }
            } else if (colorData.getNode() instanceof GroupNode) {
                if (colorData.getBackgroundAlt() != ColorType.NULL) {
                    colorData.getNode().setOnMouseExited(event -> colorData.getNode().setBackground(getBackgroundDef(colorData)));
                    colorData.getNode().setOnMouseEntered(event -> colorData.getNode().setBackground(getBackgroundAlt(colorData)));
                }
            } else if (colorData.getNode() instanceof IntroWindowCell) {
                IntroWindowCell introWindowCell = ((IntroWindowCell) colorData.getNode());
                introWindowCell.setBackground(getBackgroundDef(colorData));
                introWindowCell.setOnMouseEntered(event -> {
                    introWindowCell.setBackground(getBackgroundAlt(colorData));
                    introWindowCell.setCursor(Cursor.HAND);
                    introWindowCell.getNodeRemove().setVisible(true);
                });
                introWindowCell.setOnMouseExited(event -> {
                    introWindowCell.setBackground(getBackgroundDef(colorData));
                    introWindowCell.setCursor(Cursor.DEFAULT);
                    introWindowCell.getNodeRemove().setVisible(false);
                });
            } else if (colorData.getNode() instanceof TextField) {
                TextField textField = ((TextField) colorData.getNode());
                Color color = getTextColorDef(colorData);
                String colorString = String.format("#%02X%02X%02X",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255));
                textField.setStyle("-fx-text-fill: " + colorString + ";");
            }
        }
    }
    public static void updateNodeProperties(Scene scene) {
        ArrayList<ColorData> colorDataList = new ArrayList<>();
        for (ColorData colorData : NodeFactory.getNodeList()) {
            if (colorData.getNode().getScene() != null && colorData.getNode().getScene().equals(scene)) {
                colorDataList.add(colorData);
            }
        }
        updateNodeProperties(colorDataList);
    }
    public static void updateNodeProperties(VBox vBox) {
        ArrayList<ColorData> colorDataList = new ArrayList<>();
        for (ColorData colorData : NodeFactory.getNodeList()) {
            if (colorData.getNode().getParent() != null && colorData.getNode().getParent().equals(vBox)) {
                colorDataList.add(colorData);
            }
        }
        updateNodeProperties(colorDataList);
    }
    public static void updateNodeProperties() {
        updateNodeProperties(NodeFactory.getNodeList());
    }

    public static Font getFont() {
        return font;
    }
    public static int getPadding() {
        return settings.intValueOf(SettingsEnum.GLOBAL_PADDING);
    }

    public static Image textToImage(String text) {
        Label label = new Label(text);
        label.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        label.setTextFill(Color.RED);
        label.setWrapText(true);
        label.setFont(CommonUtil.getFont());
        label.setPadding(new Insets(0, 0, 0, 0));

        Text theText = new Text(label.getText());
        theText.setFont(label.getFont());
        int width = (int) theText.getBoundsInLocal().getWidth();
        int height = (int) theText.getBoundsInLocal().getHeight();

        WritableImage img = new WritableImage(width, height);
        Scene scene = new Scene(new Group(label));
        scene.setFill(Color.TRANSPARENT);
        scene.snapshot(img);
        return img;
    }

    public static ArrayList<File> getValidFiles(String directory) {
        return new ArrayList<>(Arrays.asList(new File(directory).listFiles((dir, name) -> {
            String _name = name.toLowerCase();
            return _name.endsWith(".jpg") || _name.endsWith(".jpeg") || _name.endsWith(".png");
        })));
    }
    public static void importFiles() {
        //todo finish this
        String pathSource = settings.getCurrentDirectory();
        ArrayList<String> importDirectories = settings.getImportDirList();

        DataObjectList newDataObjects = new DataObjectList();
        importDirectories.forEach(dir -> {
            for (File file : CommonUtil.getValidFiles(dir)) {
                try {
                    //Files.copy(Paths.get(file.getAbsolutePath()), Paths.get((pathSource) + file.getName()));
                    Files.move(Paths.get(file.getAbsolutePath()), Paths.get((pathSource) + file.getName()));
                    newDataObjects.add(new DataObject(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        LoaderUtil.readImageCache(newDataObjects);
        mainDataList.addAll(newDataObjects);
        mainDataList.sort();
        filter.apply();
        reload.doReload();
    }

    public static void swapDisplayMode() {
        MainScene.swapViewMode();
    }
    public static boolean isFullView() {
        return MainScene.isFullView();
    }
    public static DataObject getRandomDataObject() {
        ArrayList<BaseTile> tiles = tileView.getVisibleTiles();
        if (tiles.size() >= 1) {
            int index = new Random().nextInt(tiles.size());
            DataObject chosenDataObject = tiles.get(index).getParentDataObject();

            if (chosenDataObject.getMergeID() == 0) {
                return chosenDataObject;
            } else {
                ArrayList<DataObject> dataObjectsSameMergeID = new ArrayList<>();
                for (DataObject dataObjectFromFilter : filter) {
                    if (dataObjectFromFilter.getMergeID() == chosenDataObject.getMergeID()) {
                        dataObjectsSameMergeID.add(dataObjectFromFilter);
                    }
                }
                return dataObjectsSameMergeID.get(new Random().nextInt(dataObjectsSameMergeID.size()));
            }
        } else {
            return null;
        }
    }
    public static int getHash() {
        return new Random().nextInt();
    }
}
