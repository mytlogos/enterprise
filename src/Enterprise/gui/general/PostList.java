package Enterprise.gui.general;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import scrape.sources.Post;

import java.util.Collection;

/**
 * This class is an extension {@code SimpleListProperty} to display {@link Post}s in real-Time.
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

    @Override
    public boolean add(Post element) {
        boolean added = false;
        if (!this.contains(element)) {
            added = super.add(element);
        }
        return added;
    }


    @Override
    public void add(int index, Post element) {
        if (!this.contains(element)) {
            super.add(index, element);
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends Post> c) {
        boolean addedAll = false;
        if (!this.containsAll(c)) {
            addedAll = super.addAll(index, c);
        }
        return addedAll;
    }

    @Override
    public boolean addAll(Collection<? extends Post> c) {
        removeAll(c);
        return super.addAll(c);
    }
}
