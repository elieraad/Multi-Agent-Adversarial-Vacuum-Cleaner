package sample;

import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

class DigitalClock extends Parent {

    private Text digits = new Text();

    DigitalClock() {
        configureDigits();
        getChildren().add(digits);
    }

    private void configureDigits() {
        digits = new Text("");
        digits.setTextOrigin(VPos.TOP);
        digits.setLayoutY(-10);
        digits.setFill(Color.BLACK);

    }

    void refreshDigits(String sec) {
        digits.setText(sec);
    }
}

























