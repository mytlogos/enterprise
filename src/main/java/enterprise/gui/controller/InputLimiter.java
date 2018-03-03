package enterprise.gui.controller;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * Utility Interface for providing several default methods restraining user input.
 */
public interface InputLimiter {

    /**
     * Consumes every KeyEvent which is no number.
     * Replaces every new input, which contains characters
     * which are not numbers and text length greater than 9
     * with the old text.
     *
     * @param text {@link TextField} to restrain the user input on
     */
    default void inputLimitToInt(TextField text) {
        filterNumbers(text);
        text.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*") | newValue.length() > 9) {
                text.setText(oldValue);
            }
        });
    }

    /**
     * Consumes every {@link KeyEvent#KEY_TYPED} which is not
     * a number.
     *
     * @param text {@link TextField} to restrain the user input on
     */
    default void filterNumbers(TextField text) {
        text.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!"0123456789".contains(keyEvent.getCharacter())) {
                keyEvent.consume();
            }
        });
    }

    /**
     * Consumes every {@link KeyEvent#KEY_TYPED} which is no number.
     * Replaces every new input, which contains characters
     * which are not numbers and text length greater than the
     * provided {@code mxLength} with the old text.
     *
     * @param text {@link TextField} to restrain the user input on
     */
    default void inputLimitToInt(TextField text, int mxLength) {
        filterNumbers(text);

        text.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*") | newValue.length() > mxLength) {
                text.setText(oldValue);
            }
        });
    }

    /**
     * Consumes every {@link KeyEvent#KEY_TYPED} which is no number.
     * Replaces every new input, which contains characters
     * which are not numbers, text length greater than the
     * provided {@code mxLength} and number value greater than
     * the specified {@code maxNumber} with the old text.
     *
     * @param text {@link TextField} to restrain the user input on
     */
    default void inputLimitToInt(TextField text, int mxLength, int maxNumber) {
        filterNumbers(text);
        text.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*") | newValue.length() > mxLength) {
                text.setText(oldValue);
            } else if (!newValue.isEmpty()) {
                int newVal = Integer.parseInt(newValue);
                if (newVal > maxNumber) {
                    text.setText(oldValue);
                }
            }
        });
    }

    /**
     * Replaces every new input, which text length is greater than the
     * provided {@code mxLength} with the old text.
     *
     * @param textField {@link TextField} to restrain the user input on
     */
    default void limitToLength(TextField textField, int mxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > mxLength) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Replaces every new input, which text length is greater than the
     * provided {@code mxLength} with the old text.
     *
     * @param area {@link TextArea} to restrain the user input on
     */
    default void limitToLength(TextArea area, int mxLength) {
        area.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > mxLength) {
                area.setText(oldValue);
            }
        });
    }
}
