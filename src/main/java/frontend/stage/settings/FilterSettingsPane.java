package frontend.stage.settings;

import backend.control.filter.FilterOption;
import backend.control.reload.InvokeHelper;
import backend.control.reload.Notifier;
import backend.control.reload.Reload;
import backend.list.BaseList;
import backend.misc.FileUtil;
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
	private final BaseList<Pair<FilterOption, CheckBox>> checkBoxes = new BaseList<>();
	private final BaseList<Pair<FilterOption, EditNode>> editNodes = new BaseList<>();
	
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
		CheckBox nodeImages = new CheckBox("Enable", FilterOption.ENABLE_IMG.getBooleanValue());
		checkBoxes.add(new Pair<>(FilterOption.ENABLE_IMG, nodeImages));
		
		VBox boxSettings = new VBox();
		boxSettings.setPadding(new Insets(0, 5, 5, 5));
		boxSettings.setSpacing(3);
		boxSettings.getChildren().add(nodeImages);
		
		HBox boxExtensions = new HBox();
		boxExtensions.setSpacing(15);
		boxSettings.getChildren().add(boxExtensions);
		
		for (String ext : FileUtil.EXT_IMG) {
			FilterOption filterOption = FilterOption.valueOf("ENABLE_IMG_" + ext.toUpperCase());
			CheckBox nodeExt = new CheckBox(ext, filterOption.getBooleanValue());
			checkBoxes.add(new Pair<>(filterOption, nodeExt));
			nodeExt.disableProperty().bind(nodeImages.selectedProperty().not());
			boxExtensions.getChildren().add(nodeExt);
		}
		
		TitlePane titlePane = new TitlePane("Images", boxSettings);
		titlePane.setPadding(new Insets(3, 5, 0, 5));
		
		return titlePane;
	}
	private TitlePane getPaneVideos() {
		CheckBox nodeVideos = new CheckBox("Enable", FilterOption.ENABLE_VID.getBooleanValue());
		checkBoxes.add(new Pair<>(FilterOption.ENABLE_VID, nodeVideos));
		
		VBox boxSettings = new VBox();
		boxSettings.setPadding(new Insets(0, 5, 5, 5));
		boxSettings.setSpacing(3);
		boxSettings.getChildren().add(nodeVideos);
		
		HBox boxExtensions = new HBox();
		boxExtensions.setSpacing(15);
		boxSettings.getChildren().add(boxExtensions);
		
		for (String ext : FileUtil.EXT_VID) {
			FilterOption filterOption = FilterOption.valueOf("ENABLE_VID_" + ext.toUpperCase());
			CheckBox nodeExt = new CheckBox(ext, filterOption.getBooleanValue());
			checkBoxes.add(new Pair<>(filterOption, nodeExt));
			nodeExt.disableProperty().bind(nodeVideos.selectedProperty().not());
			boxExtensions.getChildren().add(nodeExt);
		}
		for (String ext : FileUtil.EXT_GIF) {
			FilterOption filterOption = FilterOption.valueOf("ENABLE_VID_" + ext.toUpperCase());
			CheckBox nodeExt = new CheckBox(ext, filterOption.getBooleanValue());
			checkBoxes.add(new Pair<>(filterOption, nodeExt));
			nodeExt.disableProperty().bind(nodeVideos.selectedProperty().not());
			boxExtensions.getChildren().add(nodeExt);
		}
		
		TitlePane titlePane = new TitlePane("Videos", boxSettings);
		titlePane.setPadding(new Insets(3, 5, 0, 5));
		
		return titlePane;
	}
	private TitlePane getPaneTagCount() {
		EditNode nodeTagLimitMin = new EditNode(String.valueOf(FilterOption.TAG_COUNT_MIN.getIntValue()), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(FilterOption.TAG_COUNT_MIN, nodeTagLimitMin));
		EditNode nodeTagLimitMax = new EditNode(String.valueOf(FilterOption.TAG_COUNT_MAX.getIntValue()), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(FilterOption.TAG_COUNT_MAX, nodeTagLimitMax));
		
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
		EditNode nodeGroupSizeMin = new EditNode(String.valueOf(FilterOption.GROUP_SIZE_MIN.getIntValue()), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(FilterOption.GROUP_SIZE_MIN, nodeGroupSizeMin));
		EditNode nodeGroupSizeMax = new EditNode(String.valueOf(FilterOption.GROUP_SIZE_MAX.getIntValue()), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(FilterOption.GROUP_SIZE_MAX, nodeGroupSizeMax));
		
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
		EditNode nodeLengthLimitMin = new EditNode(String.valueOf(FilterOption.MEDIA_LENGTH_MIN.getIntValue()), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(FilterOption.MEDIA_LENGTH_MIN, nodeLengthLimitMin));
		EditNode nodeLengthLimitMax = new EditNode(String.valueOf(FilterOption.MEDIA_LENGTH_MAX.getIntValue()), EditNode.EditNodeType.NUMERIC_POSITIVE);
		editNodes.add(new Pair<>(FilterOption.MEDIA_LENGTH_MAX, nodeLengthLimitMax));
		
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
	private TitlePane getPaneOther() {
		CheckBox nodeLastImport = new CheckBox("Only Last Import", FilterOption.LAST_IMPORT_ONLY.getBooleanValue());
		checkBoxes.add(new Pair<>(FilterOption.LAST_IMPORT_ONLY, nodeLastImport));
		
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
