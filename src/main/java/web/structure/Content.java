package web.structure;

import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 *
 */
public class Content {
    private StringProperty title = new SimpleStringProperty();
    private IntegerProperty index = new SimpleIntegerProperty();
    private IntegerProperty bonusIndex = new SimpleIntegerProperty();
    private ObjectProperty<LocalDateTime> published = new SimpleObjectProperty<>();
    private ObjectProperty<ContentType> contentType = new SimpleObjectProperty<>();
    private StringProperty content = new SimpleStringProperty();

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public int getIndex() {
        return index.get();
    }

    public IntegerProperty indexProperty() {
        return index;
    }

    public int getBonusIndex() {
        return bonusIndex.get();
    }

    public IntegerProperty bonusIndexProperty() {
        return bonusIndex;
    }

    public LocalDateTime getPublished() {
        return published.get();
    }

    public ObjectProperty<LocalDateTime> publishedProperty() {
        return published;
    }

    public ContentType getContentType() {
        return contentType.get();
    }

    public ObjectProperty<ContentType> contentTypeProperty() {
        return contentType;
    }

    public String getContent() {
        return content.get();
    }

    public StringProperty contentProperty() {
        return content;
    }

    public enum ContentType {
        TEXT,
        IMAGE,
        VIDEO
    }

}
