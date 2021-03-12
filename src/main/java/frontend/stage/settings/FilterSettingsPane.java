package frontend.stage.settings;

import backend.BaseList;
import backend.misc.FileUtil;
import backend.reload.InvokeHelper;
import backend.reload.Notifier;
import backend.reload.Reload;
import backend.settings.Settings;
import frontend.node.CheckBox;
import frontend.node.EditNode;
import frontend.node.TitlePane;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Pair;

public class FilterSettingsPane extends HBox {
	private final BaseList<Pair<Settings, CheckBox>> checkBoxes = new BaseList<>();
	private final BaseList<Pair<Settings, EditNode>> editNodes = new BaseList<>();
	
	private EditNode editNodeString;
	public static String QUERY = "";
	
	public FilterSettingsPane() {
		VBox boxTitlePanes = new VBox();
		boxTitlePanes.getChildren().add(this.getPaneString());
		boxTitlePanes.getChildren().add(this.getPaneImages());
		boxTitlePanes.getChildren().add(this.getPaneVideos());
		boxTitlePanes.getChildren().add(this.getPaneTagCount());
		boxTitlePanes.getChildren().add(this.getPaneGroupSize());
		boxTitlePanes.getChildren().add(this.getPaneMedia());
		boxTitlePanes.getChildren().add(this.getPaneLikes());
		boxTitlePanes.getChildren().add(this.getPaneOther());
		
		this.getChildren().add(boxTitlePanes);
	}
	
	private TitlePane getPaneString() {
		editNodeString = new EditNode();
		
		VBox paddingHelper = new VBox(editNodeString);
		paddingHelper.setPadding(new Insets(0, 5, 5, 5));
		
		TitlePane titlePane = new TitlePane("Query", paddingHelper);
		titlePane.setPadding(new Insets(3, 5, 0, 5));
		
		return titlePane;
	}
	private TitlePane getPaneImages() {
		CheckBox nodeImages = new CheckBox("Enable", Settings.ENABLE_IMG.getBooleanValue());
		checkBoxes.add(new Pair<>(Settings.ENABLE_IMG, nodeImages));
		
		VBox boxSettings = new VBox();
		boxSettings.setPadding(new Insets(0, 5, 5, 5));
		boxSettings.setSpacing(3);
		boxSettings.getChildren().add(nodeImages);
		
		HBox boxExtensions = new HBox();
		boxExtensions.setSpacing(15);
		boxSettings.getChildren().add(boxExtensions);
		
		for (String ext : FileUtil.EXT_IMG) {
			Settings setting = Settings.valueOf("ENABLE_IMG_" + ext.toUpperCase());
			CheckBox nodeExt = new CheckBox(ext, setting.getBooleanValue());
			checkBoxes.add(new Pair<>(setting, nodeExt));
			nodeExt.disableProperty().bind(nodeImages.selectedProperty().not());
			boxExtensions.getChildren().add(nodeExt);
		}
		
		TitlePane titlePane = new TitlePane("Images", boxSettings);
		titlePane.setPadding(new Insets(3, 5, 0, 5));
		
		return titlePane;
	}
	private TitlePane getPaneVideos() {
		CheckBox nodeVideos = new CheckBox("Enable", Settings.ENABLE_VID.getBooleanValue());
		checkBoxes.add(new Pair<>(Settings.ENABLE_VID, nodeVideos));
		
		VBox boxSettings = new VBox();
		boxSettings.setPadding(new Insets(0, 5, 5, 5));
		boxSettings.setSpacing(3);
		boxSettings.getChildren().add(nodeVideos);
		
		HBox boxExtensions = new HBox();
		boxExtensions.setSpacing(15);
		boxSettings.getChildren().add(boxExtensions);
		
		for (String ext : FileUtil.EXT_VID) {
			Settings setting = Settings.valueOf("ENABLE_VID_" + ext.toUpperCase());
			CheckBox nodeExt = new CheckBox(ext, setting.getBooleanValue());
			checkBoxes.add(new Pair<>(setting, nodeExt));
			nodeExt.disableProperty().bind(nodeVideos.selectedProperty().not());
			boxExtensions.getChildren().add(nodeExt);
		}
		for (String ext : FileUtil.EXT_GIF) {
			Settings setting = Settings.valueOf("ENABLE_VID_" + ext.toUpperCase());
			CheckBox nodeExt = new CheckBox(ext, setting.getBooleanValue());
			checkBoxes.add(new Pair<>(setting, nodeExt));
			nodeExt.disableProperty().bind(nodeVideos.selectedProperty().not());
			boxExtensions.getChildren().add(nodeExt);
		}
		
		TitlePane titlePane = new TitlePane("Videos", boxSettings);
		titlePane.setPadding(new Insets(3, 5, 0, 5));
		
		return titlePane;
	}
	private TitlePane getPaneTagCount() {
		EditNode nodeTagLimitMin = new EditNode(Settings.MIN_TAG_COUNT.getStringValue(), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(Settings.MIN_TAG_COUNT, nodeTagLimitMin));
		EditNode nodeTagLimitMax = new EditNode(Settings.MAX_TAG_COUNT.getStringValue(), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(Settings.MAX_TAG_COUNT, nodeTagLimitMax));
		
		TextNode nodeTagLimitMinText = new TextNode("Min");
		TextNode nodeTagLimitMaxText = new TextNode("Max");
		
		HBox boxTagLimitMin = new HBox(nodeTagLimitMinText, nodeTagLimitMin);
		boxTagLimitMin.setSpacing(5);
		boxTagLimitMin.setAlignment(Pos.CENTER);
		HBox boxTagLimitMax = new HBox(nodeTagLimitMaxText, nodeTagLimitMax);
		boxTagLimitMax.setSpacing(5);
		boxTagLimitMax.setAlignment(Pos.CENTER);
		
		HBox boxTagLimit = new HBox(boxTagLimitMin, boxTagLimitMax);
		boxTagLimit.setSpacing(15);
		boxTagLimit.setPadding(new Insets(0, 5, 5, 7));
		
		TitlePane titlePane = new TitlePane("Tag Count", boxTagLimit);
		titlePane.setPadding(new Insets(3, 5, 0, 5));
		
		return titlePane;
	}
	private TitlePane getPaneGroupSize() {
		EditNode nodeGroupSizeMin = new EditNode(Settings.MIN_GROUP_SIZE.getStringValue(), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(Settings.MIN_GROUP_SIZE, nodeGroupSizeMin));
		EditNode nodeGroupSizeMax = new EditNode(Settings.MAX_GROUP_SIZE.getStringValue(), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(Settings.MAX_GROUP_SIZE, nodeGroupSizeMax));
		
		TextNode groupSizeMinText = new TextNode("Min");
		TextNode groupSizeMaxText = new TextNode("Max");
		
		HBox boxGroupSizeMin = new HBox(groupSizeMinText, nodeGroupSizeMin);
		boxGroupSizeMin.setSpacing(5);
		boxGroupSizeMin.setAlignment(Pos.CENTER);
		HBox boxGroupSizeMax = new HBox(groupSizeMaxText, nodeGroupSizeMax);
		boxGroupSizeMax.setSpacing(5);
		boxGroupSizeMax.setAlignment(Pos.CENTER);
		
		HBox boxGroupSize = new HBox(boxGroupSizeMin, boxGroupSizeMax);
		boxGroupSize.setSpacing(15);
		boxGroupSize.setPadding(new Insets(0, 5, 5, 7));
		
		TitlePane titlePane = new TitlePane("Group Size", boxGroupSize);
		titlePane.setPadding(new Insets(3, 5, 0, 5));
		
		return titlePane;
	}
	private TitlePane getPaneMedia() {
		EditNode nodeLengthLimitMin = new EditNode(Settings.MIN_MEDIA_LENGTH.getStringValue(), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(Settings.MIN_MEDIA_LENGTH, nodeLengthLimitMin));
		EditNode nodeLengthLimitMax = new EditNode(Settings.MAX_MEDIA_LENGTH.getStringValue(), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(Settings.MAX_MEDIA_LENGTH, nodeLengthLimitMax));
		
		TextNode nodeTagLimitMinText = new TextNode("Min");
		TextNode nodeTagLimitMaxText = new TextNode("Max");
		
		HBox boxLengthLimitMin = new HBox(nodeTagLimitMinText, nodeLengthLimitMin);
		boxLengthLimitMin.setSpacing(5);
		boxLengthLimitMin.setAlignment(Pos.CENTER);
		HBox boxLengthLimitMax = new HBox(nodeTagLimitMaxText, nodeLengthLimitMax);
		boxLengthLimitMax.setSpacing(5);
		boxLengthLimitMax.setAlignment(Pos.CENTER);
		
		HBox boxLengthLimit = new HBox(boxLengthLimitMin, boxLengthLimitMax);
		boxLengthLimit.setSpacing(15);
		boxLengthLimit.setPadding(new Insets(0, 5, 5, 7));
		
		TitlePane titlePane = new TitlePane("Media Length", boxLengthLimit);
		titlePane.setPadding(new Insets(3, 5, 0, 5));
		
		return titlePane;
	}
	private TitlePane getPaneLikes() {
		EditNode nodeLikesMin = new EditNode(Settings.MIN_LIKES.getStringValue(), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(Settings.MIN_LIKES, nodeLikesMin));
		EditNode nodeLikesMax = new EditNode(Settings.MAX_LIKES.getStringValue(), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(Settings.MAX_LIKES, nodeLikesMax));
		
		TextNode nodeTagLimitMinText = new TextNode("Min");
		TextNode nodeTagLimitMaxText = new TextNode("Max");
		
		HBox boxLikesMin = new HBox(nodeTagLimitMinText, nodeLikesMin);
		boxLikesMin.setSpacing(5);
		boxLikesMin.setAlignment(Pos.CENTER);
		HBox boxLikesMax = new HBox(nodeTagLimitMaxText, nodeLikesMax);
		boxLikesMax.setSpacing(5);
		boxLikesMax.setAlignment(Pos.CENTER);
		
		HBox boxLikes = new HBox(boxLikesMin, boxLikesMax);
		boxLikes.setSpacing(15);
		boxLikes.setPadding(new Insets(0, 5, 5, 7));
		
		TitlePane titlePane = new TitlePane("Likes", boxLikes);
		titlePane.setPadding(new Insets(3, 5, 0, 5));
		
		return titlePane;
	}
	private TitlePane getPaneOther() {
		CheckBox nodeLastImport = new CheckBox("Only Last Import", Settings.ONLY_LAST_IMPORT.getBooleanValue());
		checkBoxes.add(new Pair<>(Settings.ONLY_LAST_IMPORT, nodeLastImport));
		
		VBox boxSettings = new VBox();
		boxSettings.setPadding(new Insets(0, 5, 5, 5));
		boxSettings.setSpacing(3);
		boxSettings.getChildren().add(nodeLastImport);
		
		TitlePane titlePane = new TitlePane("Other", boxSettings);
		titlePane.setPadding(new Insets(3, 5, 0, 5));
		
		return titlePane;
	}
	
	public void apply() {
		QUERY = editNodeString.getText();
		
		checkBoxes.forEach(pair -> pair.getKey().setValue(pair.getValue().isSelected()));
		editNodes.forEach(pair -> {
			try {
				pair.getKey().setValue(Integer.parseInt(pair.getValue().getText()));
			} catch (NumberFormatException e) {
				pair.getKey().setValue(pair.getKey().getDefaultValue());
			}
		});
		
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
		Reload.request(InvokeHelper.PANE_DISPLAY_RELOAD);
		Reload.start();
	}
}
