package user_interface.factory.buttons;

import com.sun.jna.platform.FileUtils;
import control.filter.FilterManager;
import control.reload.Reload;
import database.object.DataObject;
import database.object.TagObject;
import javafx.scene.input.MouseButton;
import org.apache.commons.text.WordUtils;
import system.ClipboardUtil;
import system.Instances;
import user_interface.factory.base.TextNode;
import user_interface.factory.menu.ClickMenuBase;
import user_interface.factory.stage.GroupEditStage;
import user_interface.factory.stage.OkCancelStage;
import user_interface.factory.util.ColorData;
import user_interface.factory.util.enums.ColorType;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ButtonFactory implements Instances {
    private final ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

    private ButtonFactory() {
        if (FactoryLoader.instance != null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " already instantiated");
        }
    }
    public static ButtonFactory getInstance() {
        return FactoryLoader.instance;
    }
    public TextNode get(ButtonTemplates buttonTemplate) {
        switch (buttonTemplate) {
            case OBJ_SIMILAR:
                return objSimilar();
            case OBJ_OPEN:
                return objOpen();
            case OBJ_EDIT:
                return objEdit();
            case OBJ_COPY_NAME:
                return objCopyName();
            case OBJ_COPY_PATH:
                return objCopyPath();
            case OBJ_DELETE:
                return objDelete();
            case SEL_DELETE:
                return selDelete();
            case TAG_EDIT:
                return tagEdit();
            case TAG_REMOVE:
                return tagRemove();
        }

        return objSimilar();
    }
    private TextNode objSimilar() {
        TextNode textNode = new TextNode("Show Similar", colorData);
        textNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                FilterManager.showSimilar(target.getCurrentTarget());
                reload.doReload();
                ClickMenuBase.hideAll();
            }
        });
        return textNode;
    }
    private TextNode objOpen() {
        TextNode textNode = new TextNode("Open", colorData);
        textNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                String fullPath = target.getCurrentTarget().getSourcePath();
                try {
                    Desktop.getDesktop().open(new File(fullPath));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    ClickMenuBase.hideAll();
                }
            }
        });
        return textNode;
    }
    private TextNode objEdit() {
        TextNode textNode = new TextNode("Edit", colorData);
        textNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                String fullPath = target.getCurrentTarget().getSourcePath();
                try {
                    //todo cross platform support
                    Runtime.getRuntime().exec("C:\\WINDOWS\\system32\\mspaint.exe " + fullPath);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    ClickMenuBase.hideAll();
                }
            }
        });
        return textNode;
    }
    private TextNode objCopyName() {
        TextNode textNode = new TextNode("Copy Name", colorData);
        textNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                ClipboardUtil.setClipboardContent(target.getCurrentTarget().getName());
                ClickMenuBase.hideAll();
            }
        });
        return textNode;
    }
    private TextNode objCopyPath() {
        TextNode textNode = new TextNode("Copy Path", colorData);
        textNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                ClipboardUtil.setClipboardContent(target.getCurrentTarget().getSourcePath());
                ClickMenuBase.hideAll();
            }
        });
        return textNode;
    }
    private TextNode objDelete() {
        TextNode textNode = new TextNode("Delete", colorData);
        textNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.deleteCurrentTarget();
                ClickMenuBase.hideAll();
            }
        });
        return textNode;
    }
    private TextNode selDelete() {
        TextNode textNode = new TextNode("Delete", colorData);
        textNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.deleteSelection();
                ClickMenuBase.hideAll();
            }
        });
        return textNode;
    }
    private TextNode tagEdit() {
        TextNode textNode = new TextNode("Edit", colorData);
        textNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!clickMenuInfo.getName().isEmpty()) {
                    mainInfoList.edit(mainInfoList.getTagObject(clickMenuInfo.getGroup(), clickMenuInfo.getName()));
                } else {
                    String oldGroup = clickMenuInfo.getGroup();
                    String newGroup = WordUtils.capitalize(new GroupEditStage(oldGroup).getResult().toLowerCase());
                    if (newGroup.isEmpty()) return;

                    mainInfoList.forEach(tagObject -> {
                        if (tagObject.getGroup().equals(oldGroup)) {
                            tagObject.setGroup(newGroup);
                        }
                    });

                    ArrayList<String> expandedGroupsL = tagListViewL.getExpandedGroupsList();
                    if (expandedGroupsL.contains(oldGroup)) {
                        expandedGroupsL.remove(oldGroup);
                        expandedGroupsL.add(newGroup);
                    }

                    ArrayList<String> expandedGroupsR = tagListViewR.getExpandedGroupsList();
                    if (expandedGroupsR.contains(oldGroup)) {
                        expandedGroupsR.remove(oldGroup);
                        expandedGroupsR.add(newGroup);
                    }

                    reload.notifyChangeIn(Reload.Control.INFO);
                }
                reload.doReload();
                ClickMenuBase.hideAll();
            }
        });
        return textNode;
    }
    private TextNode tagRemove() {
        TextNode textNode = new TextNode("Remove", colorData);
        textNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                TagObject tagObject = mainInfoList.getTagObject(clickMenuInfo.getGroup(), clickMenuInfo.getName());
                mainDataList.forEach(dataObject -> dataObject.getTagList().remove(tagObject));
                mainInfoList.remove(tagObject);
                reload.doReload();
                ClickMenuBase.hideAll();
            }
        });
        return textNode;
    }
    private void deleteDataObject(DataObject dataObject) {
        if (filter.contains(dataObject)) {
            String sourcePath = target.getCurrentTarget().getSourcePath();
            String cachePath = target.getCurrentTarget().getCachePath();

            FileUtils fo = FileUtils.getInstance();
            if (fo.hasTrash()) {
                try {
                    fo.moveToTrash(new File[]{new File(sourcePath), new File(cachePath)});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            tileView.getTilePane().getChildren().remove(dataObject.getBaseTile());
            select.remove(dataObject);
            filter.remove(dataObject);
            mainDataList.remove(dataObject);
        }
    }
    private void deleteSelection() {
        if (select.isEmpty()) {
            logger.debug(this, "deleteSelection() - empty selection");
            return;
        }

        ArrayList<DataObject> dataObjectsToDelete = new ArrayList<>();
        select.forEach(dataObject -> {
            if (dataObject.getMergeID() != 0 && !tileView.getExpandedGroups().contains(dataObject.getMergeID())) {
                dataObjectsToDelete.addAll(dataObject.getMergeGroup());
            } else {
                dataObjectsToDelete.add(dataObject);
            }
        });

        OkCancelStage okCancelStage = new OkCancelStage("Delete " + dataObjectsToDelete.size() + " file(s)?");
        if (okCancelStage.getResult()) {
            target.storePosition();
            dataObjectsToDelete.forEach(this::deleteDataObject);
            target.restorePosition();

            reload.notifyChangeIn(Reload.Control.FILTER, Reload.Control.TARGET);
            reload.doReload();
        }
    }
    private void deleteCurrentTarget() {
        DataObject currentTarget = target.getCurrentTarget();
        if (currentTarget.getMergeID() != 0) {
            if (!tileView.getExpandedGroups().contains(currentTarget.getMergeID())) {
                OkCancelStage okCancelStage = new OkCancelStage("Delete " + currentTarget.getMergeGroup().size() + " file(s)?");
                if (okCancelStage.getResult()) {
                    currentTarget.getMergeGroup().forEach(this::deleteDataObject);
                    reload.doReload();
                }
            }
        } else {
            String sourcePath = target.getCurrentTarget().getSourcePath();
            OkCancelStage okCancelStage = new OkCancelStage("Delete file: " + sourcePath + "?");
            if (okCancelStage.getResult()) {
                target.storePosition();
                this.deleteDataObject(currentTarget);
                target.restorePosition();

                reload.notifyChangeIn(Reload.Control.FILTER, Reload.Control.TARGET);
                reload.doReload();
            }
        }
    }
    private static class FactoryLoader {
        private static final ButtonFactory instance = new ButtonFactory();
    }
}
