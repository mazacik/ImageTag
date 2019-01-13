package userinterface.node;

import javafx.scene.control.Button;
import utils.CommonUtil;


public class CustomButton extends Button {
    public CustomButton() {
        this("");
    }
    public CustomButton(String value) {
        super(value);
        this.setBackground(CommonUtil.getButtonBackgroundDefault());
        this.setOnMouseEntered(event -> this.setBackground(CommonUtil.getButtonBackgroundHover()));
        this.setOnMouseExited(event -> this.setBackground(CommonUtil.getButtonBackgroundDefault()));
    }
}
