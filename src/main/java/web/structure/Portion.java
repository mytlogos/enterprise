package web.structure;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 */
public class Portion extends Node {
    private ObjectProperty<Section> section = new SimpleObjectProperty<>();

    public Section getSection() {
        return section.get();
    }

    void setSection(Section section) {
        this.section.set(section);
    }

    public ObjectProperty<Section> sectionProperty() {
        return section;
    }

    public TableOfContent getTableOfContent() {
        return section.get() == null ? null : section.get().getTableOfContent();
    }
}
