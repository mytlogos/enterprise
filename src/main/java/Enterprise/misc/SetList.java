package Enterprise.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * This class is an awkward way between a List and a Set.
 * This class should have the functions of an {@code ArrayList} and
 * the property of the Set, having only unique elements.
 * This class places a restraint mechanism on the several {@code add}
 * Methods of the {@code List}. Adding does only succeed if an element
 * or collection is not null and this {@code SetList} does not contain said
 * element.
 * <p>
 * If it tries to add an {@code Collection}, it will proceed to remove
 * any duplicates, following an {@link #addAll} operation.
 * </p>
 */
public class SetList<E> extends ArrayList<E> implements Set<E> {
    @Override
    public boolean add(E element) {
        boolean added = false;
        if (element == null) {
            throw new UnsupportedOperationException("Element should not be null");
        }
        if (!this.contains(element)) {
            added = super.add(element);
        }
        return added;
    }

    @Override
    public void add(int index, E element) {
        if (element == null) {
            throw new UnsupportedOperationException("Element should not be null");
        }
        if (!this.contains(element)) {
            super.add(index, element);
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (c == null) {
            throw new UnsupportedOperationException("Collection should not be null");
        }
        boolean addedAll = false;
        removeAll(c);
        addedAll = super.addAll(index, c);
        return addedAll;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            throw new UnsupportedOperationException("Collection should not be null");
        }
        removeAll(c);
        boolean added = super.addAll(c);
        System.out.println(this);
        return added;
    }
}
