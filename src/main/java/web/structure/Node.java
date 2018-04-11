package web.structure;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 */
public class Node {
    private IntegerProperty extraIndex = new SimpleIntegerProperty();
    private IntegerProperty index = new SimpleIntegerProperty();
    private StringProperty title = new SimpleStringProperty();
    private StringProperty link = new SimpleStringProperty();
    private IntegerProperty length = new SimpleIntegerProperty();

    public int getExtraIndex() {
        return extraIndex.get();
    }

    public IntegerProperty extraIndexProperty() {
        return extraIndex;
    }

    public int getIndex() {
        return index.get();
    }

    public IntegerProperty indexProperty() {
        return index;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getLink() {
        return link.get();
    }

    public StringProperty linkProperty() {
        return link;
    }

    public int getLength() {
        return length.get();
    }

    public IntegerProperty lengthProperty() {
        return length;
    }
}
