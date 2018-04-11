package scrape.sources.posts;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.List;

/**
 * This class is an extension of {@code SimpleListProperty} to display {@link Post}s in real-PostTime.
 */
public class PostList extends SimpleListProperty<Post> {

    /**
     * The constructor of this {@code PostList}.
     * Sets the underlying list of the {@code SimpleListProperty} to an
     * observable ArrayList.
     */
    public PostList() {
        super(FXCollections.observableArrayList());
    }

    /**
     * The constructor of this {@code PostList}.
     * Sets the parameter list as the underlying list.
     *
     * @param list list with data
     */
    public PostList(List<Post> list) {
        super(FXCollections.observableArrayList(list));
    }

    /**
     * Binds the list of this {@code PostList} to the parameter.
     *
     * @param listObjectProperty listProperty to bindByOwn the list of this to
     */
    public void bindToList(ObjectProperty<ObservableList<Post>> listObjectProperty) {
        listObjectProperty.bind(this);
    }


    public boolean add(Post element) {
        boolean added = false;
        if (!this.contains(element)) {
            added = super.add(element);
        }
        return added;
    }

    public boolean addAll(Collection<? extends Post> c) {
        this.removeAll(c);
        return super.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends Post> c) {
        boolean addedAll = false;
        if (!this.containsAll(c)) {
            addedAll = super.addAll(index, c);
        }
        return addedAll;
    }

    public void add(int index, Post element) {
        if (!this.contains(element)) {
            super.add(index, element);
        }
    }

}
