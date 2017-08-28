package Enterprise.test;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.Serializable;

/**
 * Created by Dominik on 08.07.2017.
 * Class for testing purposes.
 */
public class IntegerBean implements Serializable {
    private int intNum;
    private transient IntegerProperty integer = new SimpleIntegerProperty();

    public IntegerBean() {
        integer.set(0);
    }
    public IntegerBean(int number) {
        integer.set(number);
    }

    public int getInteger() {
        return integer.get();
    }

    public IntegerProperty integerProperty() {
        return integer;
    }

    @Override
    public String toString() {
        return integer.toString();
    }

    public void increment() {
        int number = integer.get() + 1;
        integer.set(number);
    }
}
