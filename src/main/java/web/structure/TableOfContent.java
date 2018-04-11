package web.structure;

import enterprise.data.intface.Creation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;

/**
 *
 */
public class TableOfContent {
    private ObservableList<Section> sections = FXCollections.observableArrayList();
    private Creation creation;
    private Section defaultSection;

    public TableOfContent(Creation creation) {
        this.creation = creation;
    }

    public Section getDefaultSection() {
        if (defaultSection == null) {
            defaultSection = new Section();
        }
        return defaultSection;
    }

    public ObservableList<Section> getSections() {
        return FXCollections.unmodifiableObservableList(sections);
    }

    public void addSection(Section section) {
        Objects.requireNonNull(section, "section is null!");
        section.setTableOfContent(this);
        sections.add(section);
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }
}
