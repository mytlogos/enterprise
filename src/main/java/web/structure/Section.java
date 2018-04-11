package web.structure;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.Objects;

/**
 *
 */
public class Section extends Node {
    private ObservableList<Portion> portions;
    private ObjectProperty<TableOfContent> tableOfContent = new SimpleObjectProperty<>();


    public Section() {
        portions = FXCollections.observableArrayList();
        portions = portions.sorted(Comparator.comparingInt(Portion::getIndex));
    }

    public ObservableList<Portion> getPortions() {
        return FXCollections.unmodifiableObservableList(portions);
    }

    public void addPortion(Portion portion) {
        Objects.requireNonNull(portion, "portion is null!");
        portions.add(portion);
    }

    public TableOfContent getTableOfContent() {
        return tableOfContent.get();
    }

    void setTableOfContent(TableOfContent tableOfContent) {
        this.tableOfContent.set(tableOfContent);
    }

    public ObjectProperty<TableOfContent> tableOfContentProperty() {
        return tableOfContent;
    }
}
