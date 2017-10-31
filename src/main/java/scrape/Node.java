package scrape;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 */
public class Node {
    private StringProperty title = new SimpleStringProperty();
    private IntegerProperty length = new SimpleIntegerProperty();

    private List<Node> nodes = new ArrayList<>();

    public Node(String title, int length) {
        this.title.set(title);
        this.length.set(length);
    }

    public Node(String title) {
        this.title.set(title);
    }


    public void addChild(Node node) {
        Objects.requireNonNull(node);
        nodes.add(node);
    }

    public void addChildren(List<Node> nodes) {
        nodes.forEach(Objects::requireNonNull);
        this.nodes.addAll(nodes);
    }

    public void removeChildren(List<Node> nodes) {
        nodes.forEach(Objects::requireNonNull);
        this.nodes.removeAll(nodes);
    }

    public void removeChild(Node node) {
        Objects.requireNonNull(node);
        nodes.remove(node);
    }

    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public int getLength() {
        return length.get();
    }

    public IntegerProperty lengthProperty() {
        if (length.get() == 0 && !nodes.isEmpty()) {
            length.set(nodes.size());
        }
        return length;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(title.get()).append(" ").append(lengthProperty().get());

        nodes.forEach(node -> builder.append("\n").append(node.toString()));
        return builder.toString();
    }
}

