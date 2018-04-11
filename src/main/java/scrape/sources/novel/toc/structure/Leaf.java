package scrape.sources.novel.toc.structure;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 */
public abstract class Leaf extends CreationContent {
    private final StringProperty localUri = new SimpleStringProperty();
    private final StringProperty internetUri = new SimpleStringProperty();
    private final DoubleProperty partialIndex = new SimpleDoubleProperty();


    protected Leaf(String title, String type, boolean isExtra, double index, double partIndex) {
        super(title, type, isExtra, index);
        partialIndex.set(partIndex);
    }

    public StringProperty localUriProperty() {
        return localUri;
    }

    public StringProperty internetUriProperty() {
        return internetUri;
    }

    @Override
    public String toString() {
        return super.toString() + ", partIndex=" + partialIndex.get() + ", localUri=" + getLocalUri() + ", internetUri=" + getInternetUri();
    }

    public String getLocalUri() {
        return localUri.get();
    }

    public String getInternetUri() {
        return internetUri.get();
    }

}
