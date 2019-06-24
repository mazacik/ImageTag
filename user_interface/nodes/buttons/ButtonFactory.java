package user_interface.nodes.buttons;

import com.sun.jna.platform.FileUtils;
import control.Reload;
import database.object.DataObject;
import database.object.TagObject;
import javafx.scene.input.MouseButton;
import lifecycle.InstanceManager;
import org.apache.commons.text.WordUtils;
import user_interface.nodes.ColorData;
import user_interface.nodes.base.TextNode;
import user_interface.nodes.menu.ClickMenuBase;
import user_interface.stage.StageUtil;
import user_interface.style.enums.ColorType;
import utils.ClipboardUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ButtonFactory {
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
                InstanceManager.getFilter().showSimilar(InstanceManager.getTarget().getCurrentTarget());
                InstanceManager.getReload().doReload();
                ClickMenuBase.hideAll();
            }
        });
        return textNode;
    }
    private TextNode objOpen() {
        TextNode textNode = new TextNode("Open", colorData);
        textNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                String fullPath = InstanceManager.getTarget().getCurrentTarget().getPath();
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
                String fullPath = InstanceManager.getTarget().getCurrentTarget().getPath();
                try {
                    Runtime.getRuntime().exec("mspaint.exe " + fullPath);
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
                ClipboardUtil.setClipboardContent(InstanceManager.getTarget().getCurrentTarget().getName());
                ClickMenuBase.hideAll();
            }
        });
        return textNode;
    }
    private TextNode objCopyPath() {
        TextNode textNode = new TextNode("Copy Path", colorData);
        textNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                ClipboardUtil.setClipboardContent(InstanceManager.getTarget().getCurrentTarget().getName());
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
                if (!InstanceManager.getClickMenuInfo().getName().isEmpty()) {
					String group = InstanceManager.getClickMenuInfo().getGroup();
					String oldName = InstanceManager.getClickMenuInfo().getName();
					TagObject tagObject = InstanceManager.getTagListMain().getTagObject(group, oldName);
					InstanceManager.getTagListMain().edit(tagObject);
					if (!oldName.equals(tagObject.getName())) {
						InstanceManager.getFilterPane().updateNameNode(group, oldName, tagObject.getName());
						InstanceManager.getSelectPane().updateNameNode(group, oldName, tagObject.getName());
					}
                } else {
                    String oldGroup = InstanceManager.getClickMenuInfo().getGroup();
					String newGroup = WordUtils.capitalize(StageUtil.showStageEditorGroup(oldGroup).toLowerCase());
                    if (newGroup.isEmpty()) return;

                    InstanceManager.getTagListMain().forEach(tagObject -> {
                        if (tagObject.getGroup().equals(oldGroup)) {
                            tagObject.setGroup(newGroup);
                        }
                    });
					InstanceManager.getFilterPane().refresh();
					InstanceManager.getSelectPane().refresh();
                    InstanceManager.getReload().flag(Reload.Control.INFO);
                }
                ClickMenuBase.hideAll();
				InstanceManager.getReload().doReload();
            }
        });
        return textNode;
    }
    private TextNode tagRemove() {
        TextNode textNode = new TextNode("Remove", colorData);
        textNode.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                TagObject tagObject = InstanceManager.getTagListMain().getTagObject(InstanceManager.getClickMenuInfo().getGroup(), InstanceManager.getClickMenuInfo().getName());
                InstanceManager.getObjectListMain().forEach(dataObject -> dataObject.getTagList().remove(tagObject));
                InstanceManager.getTagListMain().remove(tagObject);
				InstanceManager.getFilterPane().removeNameNode(tagObject.getGroup(), tagObject.getName());
				InstanceManager.getSelectPane().removeNameNode(tagObject.getGroup(), tagObject.getName());
                InstanceManager.getReload().doReload();
                ClickMenuBase.hideAll();
            }
        });
        return textNode;
    }
    private void deleteDataObject(DataObject dataObject) {
        if (InstanceManager.getFilter().contains(dataObject)) {
            DataObject currentTarget = InstanceManager.getTarget().getCurrentTarget();
            String sourcePath = currentTarget.getPath();
            String cachePath = currentTarget.getCacheFile();
	
			FileUtils fileUtils = FileUtils.getInstance();
			if (fileUtils.hasTrash()) {
                try {
					fileUtils.moveToTrash(new File[]{new File(sourcePath), new File(cachePath)});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            InstanceManager.getGalleryPane().getTilePane().getChildren().remove(dataObject.getBaseTile());
            InstanceManager.getSelect().remove(dataObject);
            InstanceManager.getFilter().remove(dataObject);
            InstanceManager.getObjectListMain().remove(dataObject);
        }
    }
    private void deleteSelection() {
        if (InstanceManager.getSelect().isEmpty()) {
            InstanceManager.getLogger().debug("deleteSelection() - empty selection");
            return;
        }

        ArrayList<DataObject> dataObjectsToDelete = new ArrayList<>();
        InstanceManager.getSelect().forEach(dataObject -> {
            if (dataObject.getMergeID() != 0 && !InstanceManager.getGalleryPane().getExpandedGroups().contains(dataObject.getMergeID())) {
                dataObjectsToDelete.addAll(dataObject.getMergeGroup());
            } else {
                dataObjectsToDelete.add(dataObject);
            }
        });
	
		if (StageUtil.showStageOkCancel("Delete " + dataObjectsToDelete.size() + " file(s)?")) {
            InstanceManager.getTarget().storePosition();
            dataObjectsToDelete.forEach(this::deleteDataObject);
            InstanceManager.getTarget().restorePosition();

            InstanceManager.getReload().flag(Reload.Control.FILTER, Reload.Control.TARGET);
            InstanceManager.getReload().doReload();
        }
    }
    private void deleteCurrentTarget() {
        DataObject currentTarget = InstanceManager.getTarget().getCurrentTarget();
        if (currentTarget.getMergeID() != 0) {
            if (!InstanceManager.getGalleryPane().getExpandedGroups().contains(currentTarget.getMergeID())) {
				if (StageUtil.showStageOkCancel("Delete " + currentTarget.getMergeGroup().size() + " file(s)?")) {
                    currentTarget.getMergeGroup().forEach(this::deleteDataObject);
                    InstanceManager.getReload().doReload();
                }
            }
        } else {
            String sourcePath = InstanceManager.getTarget().getCurrentTarget().getPath();
			if (StageUtil.showStageOkCancel("Delete file: " + sourcePath + "?")) {
                InstanceManager.getTarget().storePosition();
                this.deleteDataObject(currentTarget);
                InstanceManager.getTarget().restorePosition();

                InstanceManager.getReload().flag(Reload.Control.FILTER, Reload.Control.TARGET);
                InstanceManager.getReload().doReload();
            }
        }
    }
    private static class FactoryLoader {
        private static final ButtonFactory instance = new ButtonFactory();
    }
}
