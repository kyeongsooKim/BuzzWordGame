package gui;


import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;

public class ToolbarButton extends Button {
    Rectangle rect = new Rectangle(120.0D, 40.0D);

    public ToolbarButton(String name) {
        this.rect.setArcHeight(20.0D);
        this.rect.setArcWidth(20.0D);
        this.setShape(this.rect);
        this.setStyle("-fx-background-color : #888888; -fx-padding:14;");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPrefSize(130.0D, 40.0D);
        this.setText(name);
    }
}
