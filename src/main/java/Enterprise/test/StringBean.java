package Enterprise.test;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * Created by Dominik on 08.07.2017.
 * Part of OgameBot.
 * Class for testing purposes.
 */
public class StringBean implements Serializable {
    private String name;
    private transient StringProperty stringName = new SimpleStringProperty();

    public StringBean(String name) {
        stringName.setValue(name);
    }

    public StringBean() {
        stringName.set("");
    }

    public String getStringName() {
        return stringName.get();
    }

    public StringProperty stringNameProperty() {
        return stringName;
    }

    @Override
    public String toString() {
        return stringName.toString();
    }
}
