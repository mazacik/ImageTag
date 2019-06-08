package user_interface.utils;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import user_interface.factory.ColorData;
import user_interface.factory.node.IntroWindowCell;
import user_interface.singleton.side.GroupNode;
import user_interface.utils.enums.ColorType;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class NodeUtil {
    private static ArrayList<ColorData> nodeList = new ArrayList<>();
    public static void equalizeWidth(Region... regions) {
        equalizeWidth(new ArrayList<>(Arrays.asList(regions)));
    }
    public static void equalizeWidth(ObservableList<Node> nodes) {
        ArrayList<Region> regions = new ArrayList<>();
        for (Node node : nodes) regions.add((Region) node);
        equalizeWidth(regions);
    }
    public static void equalizeWidth(ArrayList<Region> regions) {
        double width = 0;
        for (Region region : regions) {
            if (width < region.getWidth()) width = region.getWidth();
        }
        for (Region region : regions) {
            region.setPrefWidth(width);
        }
    }
    public static ArrayList<ColorData> getNodeList() {
        return nodeList;
    }

    public static GroupNode getGroupNode(VBox owner, String text, Color textFill) {
        GroupNode groupNode = new GroupNode(text, owner);
        groupNode.setTextFill(textFill);
        groupNode.setFont(StyleUtil.getFont());
        addToManager(groupNode, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);

        return groupNode;
    }
    public static IntroWindowCell getIntroWindowCell(String projectFile, String workingDirectory) {
        IntroWindowCell introWindowCell = new IntroWindowCell(projectFile, workingDirectory);
        addToManager(introWindowCell, ColorType.ALT, ColorType.DEF, ColorType.NULL, ColorType.NULL);
        return introWindowCell;
    }

    public static HBox getHBox(ColorType colorType, Node... children) {
        HBox hBox = new HBox(children);
        addToManager(hBox, colorType);
        return hBox;
    }
    public static VBox getVBox(ColorType colorType, Node... children) {
        VBox vBox = new VBox(children);
        addToManager(vBox, colorType);
        return vBox;
    }

    public static Tooltip getTooltip(String text) {
        Tooltip t = new Tooltip(text);
        t.setFont(StyleUtil.getFont());
        //t.setShowDelay(new Duration(500));
        return t;
    }
    public static Border getBorder(int top, int right, int bottom, int left) {
        return new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(top, right, bottom, left)));
    }
    public static Border getBorder(int border) {
        return getBorder(1, 1, 1, 1);
    }

    public static void addToManager(Region region, ColorType ct) {
        addToManager(region, ct, ct, ct, ct);
    }
    public static void addToManager(Region region, ColorType backgroundDef, ColorType backgroundAlt, ColorType textFillDef, ColorType textFillAlt) {
        nodeList.add(new ColorData(region, backgroundDef, backgroundAlt, textFillDef, textFillAlt));
        StyleUtil.applyStyle(region);
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
}
