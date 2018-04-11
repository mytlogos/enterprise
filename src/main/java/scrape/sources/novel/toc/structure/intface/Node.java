package scrape.sources.novel.toc.structure.intface;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

/**
 *
 */
public interface Node {
    List<? extends Node> getChildren();

    String getTitle();

    StringProperty titleProperty();

    int getLength();

    IntegerProperty lengthProperty();

    Node getParent();

    void setParent(Node node);

    String getType();

    void removeParent();

    boolean isRoot();

    boolean isLeaf();

    boolean isBranch();

    Node getRoot();
}
