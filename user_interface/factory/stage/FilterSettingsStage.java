package user_interface.factory.stage;

import control.Filter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lifecycle.InstanceManager;
import user_interface.factory.ColorData;
import user_interface.factory.base.CheckBoxNode;
import user_interface.factory.base.EditNode;
import user_interface.factory.base.EditNodeType;
import user_interface.factory.base.TextNode;
import user_interface.factory.node.TitleBar;
import user_interface.utils.NodeUtil;
import user_interface.utils.SizeUtil;
import user_interface.utils.enums.ColorType;

public class FilterSettingsStage extends Stage {
    CheckBoxNode cbImages = new CheckBoxNode("Images");
    CheckBoxNode cbGifs = new CheckBoxNode("Gifs");
    CheckBoxNode cbVideos = new CheckBoxNode("Videos");
    CheckBoxNode cbSession = new CheckBoxNode("Session");
    CheckBoxNode cbLimit = new CheckBoxNode("Limit");
    EditNode tfLimit = new EditNode("", EditNodeType.NUMERIC_POSITIVE);

    public FilterSettingsStage() {
        double spacing = SizeUtil.getGlobalSpacing();
        VBox vBox = NodeUtil.getVBox(ColorType.DEF);
        vBox.setPadding(new Insets(spacing));
        vBox.setSpacing(spacing);

        tfLimit.setPadding(new Insets(0, 1, -1, 1));
        tfLimit.setPrefWidth(60);

        vBox.getChildren().addAll(
                cbImages,
                cbGifs,
                cbVideos,
                cbSession,
                cbLimit,
                tfLimit
        );

        tfLimit.setVisible(false);
        cbLimit.getSelectedProperty().addListener((observable, oldValue, newValue) -> tfLimit.setVisible(newValue));

        double dinsets = 5 * SizeUtil.getGlobalSpacing();
        Insets insets = new Insets(dinsets, dinsets, 0, dinsets);
        vBox.setPadding(insets);

        ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        TextNode lblOK = new TextNode("OK", colorData);
        lblOK.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Filter filter = InstanceManager.getFilter();
                filter.setShowImages(cbImages.isSelected());
                filter.setShowGifs(cbGifs.isSelected());
                filter.setShowVideos(cbVideos.isSelected());
                filter.setSessionOnly(cbSession.isSelected());
                filter.setEnableLimit(cbLimit.isSelected());
                filter.setLimit(Integer.valueOf(tfLimit.getText()));
                filter.refresh();
                InstanceManager.getReload().doReload();
                this.hide();
            }
        });
        TextNode lblCancel = new TextNode("Cancel", colorData);
        lblCancel.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.hide();
            }
        });
        HBox hBoxOkCancel = NodeUtil.getHBox(ColorType.DEF, lblOK, lblCancel);
        hBoxOkCancel.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);
        borderPane.setTop(new TitleBar(scene, "Filter Settings"));
        borderPane.setCenter(vBox);
        borderPane.setBottom(hBoxOkCancel);
        borderPane.setBorder(NodeUtil.getBorder(1, 1, 1, 1));

        this.initStyle(StageStyle.UNDECORATED);
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.setScene(scene);
    }

    public void _show() {
        Filter filter = InstanceManager.getFilter();
        cbImages.setSelected(filter.isShowImages());
        cbGifs.setSelected(filter.isShowGifs());
        cbVideos.setSelected(filter.isShowVideos());
        cbSession.setSelected(filter.isSessionOnly());
        cbLimit.setSelected(filter.isEnableLimit());
        tfLimit.setText(String.valueOf(filter.getLimit()));

        this.show();
    }
}
