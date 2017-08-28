package scrape.sources;

import Enterprise.data.intface.Sourceable;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.*;

/**
 * This class extends {@link SimpleListProperty} mostly for presenting purposes.
 * Another use is, that it allows to add listeners to this {@code SourceList}
 * for providing flags for updating the database.
 * // TODO: 26.08.2017 maybe use a own pool
 */
public class SourceList extends SimpleListProperty<Source> implements Comparable<SourceList> {

    private static final Set<Source> globalSources = new HashSet<>();
    private static final List<Source> deletedGlobalSources = new ArrayList<>();

    private final List<Source> deletedSources = new ArrayList<>();
    private final List<Source> addedSources = new ArrayList<>();

    private final BooleanProperty listChanged = new SimpleBooleanProperty();

    /**
     * The constructor of this {@code SourceList}.
     */
    public SourceList() {
        super(FXCollections.observableArrayList());
        addInvalidListener();
    }

    /**
     * Gets the deleted Sources of this instance
     * and wraps them in a unmodifiable List.
     *
     * @return list of {@code Sources}
     */
    public List<Source> getDeletedSources() {
        return Collections.unmodifiableList(deletedSources);
    }

    /**
     * Clears the list of deleted {@code Sources} of this instance.
     */
    public void clearLocalDeleted() {
        deletedSources.clear();
    }

    /**
     * Gets a list of deleted {@code Sources} of this class.
     *
     * @return a unmodifiable list of  deleted Sources
     */
    public static List<Source> getDeletedGlobalSources() {
        return Collections.unmodifiableList(deletedGlobalSources);
    }

    /**
     * Clears the list of deleted Sources of this class.
     */
    public static void clearGlobalDeleted() {
        deletedGlobalSources.clear();
    }

    /**
     * Sets internal Update flag to true, if list changed.
     */
    private void addInvalidListener() {
        this.addListener((InvalidationListener) observable -> listChanged.set(true));
    }

    /**
     * Gets the newly added Sources of this {@code SourceList}.
     *
     * @return {@code addedSources} wrapped in an unmodifiable list
     */
    public List<Source> getAddedSources() {
        return Collections.unmodifiableList(addedSources);
    }

    /**
     * Clears the list of newly added {@link Source}s of this {@code SourceList}.
     */
    public void clearAddedSources() {
        addedSources.clear();
    }

    /**
     * Sets this {@code SourceList} and its elements updated.
     */
    public void setUpdated() {
        listChanged.set(false);
        this.forEach(Source::setUpdated);
    }

    /**
     * Checks if this list has changed, since it was last
     * set updated or created.
     *
     * @return true if it has changed
     */
    public boolean isListChanged() {
        return listChanged.get();
    }

    /**
     * Returns the {@code listChanged} {@code Property} of this
     * {@code SourceList}. Used for binding to other
     * {@code updatedProperties}.
     *
     * @return listChanged BooleanProperty
     */
    public BooleanProperty listChangedProperty() {
        return listChanged;
    }

    /**
     * // TODO: 26.08.2017 do the doc
     * @param source
     * @return
     */
    @Override
    public boolean add(Source source) {
        boolean added = false;

        if (source != null) {
            if (!globalSources.contains(source)) {
                if (!this.contains(source)) {
                    if (globalSources.add(source) && super.add(source)) {
                        added = true;
                        if (source.isNewEntry()) {
                            addedSources.add(source);
                        }
                    } else {
                        throw new IllegalStateException("globale und lokale Liste könnten inkongruent sein!");
                    }
                } else {
                    added = processLocalDuplicates(source);
                }
            } else {
                if (!this.contains(source)) {
                    added = processGlobalDuplicates(source);
                } else {
                    added = processLocalDuplicates(source);
                }
            }
        }
        return added;
    }

    /**
     * Checks if this {@code SourceList} contains
     * the parameter. If it is true, it will add the {@code CreationEntries}
     * of the parameter to the equal source of this {@code SourceList}
     * and add the reference of the local equal to the {@link Sourceable}
     * in the list of sourceables of the parameter.
     *
     * @param source source to process
     * @return true if it could be processed
     */
    private boolean processLocalDuplicates(Source source) {
        boolean processed = false;

        for (Source collectionSource : this) {
            if (collectionSource.equals(source) && !(source == collectionSource)) {

                collectionSource.getSourceables().addAll(source.getSourceables());

                for (Sourceable sourceable : source.getSourceables()) {
                    sourceable.getSourceList().add(collectionSource);
                }

                processed = true;
            }
        }
        return processed;
    }

    /**
     * Checks if an equal but not the same {@code Source} object
     * exists in the list of Sources of this class.
     * If true, it stores all references of {@code Sourceables}
     * from the parameter in the one contained in the class list (globalSource),
     * adds a reference of the globalSource to the stored {@code Sourceables}
     * and this {@code SourceList}.
     * <p>
     * If the parameter and the globalSource object are the same, it
     * stores the a reference in this {@code SourceList}.
     * </p>
     *
     * @param source {@code Source} to check and add
     * @return true if the parameter was added to this {@code SourceList}
     */
    private boolean processGlobalDuplicates(Source source) {
        boolean processed = false;

        for (Source globalSource : globalSources) {
            if (globalSource.equals(source)) {

                if (source != globalSource) {
                    globalSource.getSourceables().addAll(source.getSourceables());

                    for (Sourceable sourceable : source.getSourceables()) {
                        sourceable.getSourceList().add(globalSource);
                    }

                    addedSources.add(globalSource);
                    processed = super.add(globalSource);
                } else {
                    addedSources.add(source);
                    processed = super.add(source);
                }
            }
        }
        return processed;
    }

    @Override
    public boolean addAll(Collection<? extends Source> c) {
        removeAll(c);
        boolean added = super.addAll(c);
        System.out.println(this);
        return added;
    }

    @Override
    public boolean remove(Object obj) {
        if (obj instanceof Source) {
            deletedSources.add((Source) obj);
            if (((Source) obj).getSourceables().isEmpty()) {
                deletedGlobalSources.add((Source) obj);
                globalSources.remove(obj);
            }
        }
        return super.remove(obj);
    }

    @Override
    public int compareTo(SourceList o) {
        return this.size() - o.size();
    }
}
