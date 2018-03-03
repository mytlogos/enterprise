package enterprise.test;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * Created on 08.07.2017.
 * Part of OgameBot.
 * Class for testing purposes.
 */
class StringBean implements Serializable {
    private final transient StringProperty stringName = new SimpleStringProperty();
    private String name;

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
