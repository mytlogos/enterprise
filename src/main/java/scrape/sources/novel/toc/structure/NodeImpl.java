package scrape.sources.novel.toc.structure;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scrape.sources.novel.toc.structure.intface.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 */
public abstract class NodeImpl implements Node {
    private final StringProperty title = new SimpleStringProperty();
    private final IntegerProperty length = new SimpleIntegerProperty();

    private final String type;
    private final List<Node> children = new ArrayList<>();
    private String childType = null;
    private transient Node parent;
    private transient TableOfContent root;

    NodeImpl(String title, String type) {
        this.type = type;
        this.title.set(title);
    }

    @Override
    public List<? extends Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String getTitle() {
        return title.get();
    }

    @Override
    public StringProperty titleProperty() {
        return title;
    }

    @Override
    public int getLength() {
        return length.get();
    }

    @Override
    public IntegerProperty lengthProperty() {
        return length;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public void setParent(Node node) {
        this.parent = node;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void removeParent() {
        this.parent = null;
    }

    @Override
    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public boolean isBranch() {
        return !(isLeaf() || isRoot());
    }

    @Override
    public Node getRoot() {
        return root;
    }

    void setRoot(TableOfContent root) {
        children.forEach(node -> setRoot(root, node));
        this.root = root;
    }

    @Override
    public String toString() {
        return title.get() + ", type=" + type;
    }

    protected void addChild(Node node) {
        Objects.requireNonNull(node);

        if (checkNode(node)) {
            children.add(node);
            updateSize();

            setRoot(root, node);
            node.setParent(this);
        }
    }

    protected void addChildren(List<? extends Node> nodes) {
        nodes.forEach(Objects::requireNonNull);
        nodes.forEach(node -> node.setParent(this));
        nodes.forEach(node -> setRoot(root, node));
        nodes = nodes.stream().filter(this::checkNode).collect(Collectors.toList());

        this.children.addAll(nodes);
        updateSize();
    }

    protected void removeChildren(List<Node> nodes) {
        nodes.forEach(Objects::requireNonNull);
        nodes.forEach(Node::removeParent);

        this.children.removeAll(nodes);
        updateSize();
    }

    private void updateSize() {
        length.set(children.size());
    }

    protected void removeChild(Node node) {
        Objects.requireNonNull(node);
        children.remove(node);
        node.removeParent();

        updateSize();
    }

    private void setRoot(TableOfContent root, Node node) {
        if (node instanceof NodeImpl) {
            ((NodeImpl) node).setRoot(root);
            if (root != null && !root.contains(node)) {
                root.put(node);
            }
        }
    }

    private boolean checkNode(Node node) {
        if (childType == null) {
            childType = node.getType();
            return true;
        } else {
            return childType.equals(node.getType());
        }
    }
}

