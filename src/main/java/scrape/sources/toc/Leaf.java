package scrape.sources.toc;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 */
public abstract class Leaf extends CreationContent {
    private StringProperty localUri = new SimpleStringProperty();
    private StringProperty internetUri = new SimpleStringProperty();
    private DoubleProperty partialIndex = new SimpleDoubleProperty();


    protected Leaf(String title, String type, boolean isExtra, double index, double partIndex) {
        super(title, type, isExtra, index);
        partialIndex.set(partIndex);
    }

    public String getLocalUri() {
        return localUri.get();
    }

    public StringProperty localUriProperty() {
        return localUri;
    }

    public String getInternetUri() {
        return internetUri.get();
    }

    public StringProperty internetUriProperty() {
        return internetUri;
    }

    @Override
    public String toString() {
        return super.toString() + ", partIndex=" + partialIndex.get() + ", localUri=" + getLocalUri() + ", internetUri=" + getInternetUri();
    }

}
