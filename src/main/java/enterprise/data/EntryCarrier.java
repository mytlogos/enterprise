package enterprise.data;

import enterprise.data.intface.*;
import scrape.sources.Source;
import tools.SetList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Container singleton holding entries, which lead to operations on the database (adding, deleting, updating)
 */
public class EntryCarrier {
    private static final EntryCarrier instance = new EntryCarrier();

    private final List<DataEntry> deleted = new SetList<>();
    private final List<Creation> updateCreations = new SetList<>();
    private final List<User> updateUsers = new SetList<>();
    private final List<Creator> updateCreators = new SetList<>();
    private final List<Sourceable> updateSourceable = new SetList<>();
    private final List<Source> updateSources = new SetList<>();
    private final List<CreationEntry> updateEntries = new SetList<>();
    private final List<DataEntry> newEntries = new SetList<>();

    /**
     * The singleton constructor of this {@code EntryCarrier},
     * is private to allow only this class to instantiate one object in the {@code instance}-Field
     */
    private EntryCarrier() {
        if (instance != null) {
            throw new IllegalStateException("Is already instantiated");
        }
    }

    /**
     * Gets the Singleton of this {@code EntryCarrier}
     *
     * @return instance - the singleton instance of this {@code EntryCarrier}
     */
    public static EntryCarrier getInstance() {
        return instance;
    }

    /**
     * Gets an unmodifiable List of new Entries, to prevent illegal operations
     *
     * @return newEntries - an unmodifiable list
     */
    public List<DataEntry> getNewEntries() {
        return Collections.unmodifiableList(newEntries);
    }

    public List<Creation> getUpdateCreations() {
        return Collections.unmodifiableList(updateCreations);
    }

    public List<User> getUpdateUsers() {
        return Collections.unmodifiableList(updateUsers);
    }

    public List<Creator> getUpdateCreators() {
        return Collections.unmodifiableList(updateCreators);
    }

    public List<Sourceable> getUpdateSourceable() {
        return Collections.unmodifiableList(updateSourceable);
    }

    public List<Source> getUpdateSources() {
        return Collections.unmodifiableList(updateSources);
    }

    /**
     * Returns an unmodifiable List of {@code CreationEntries}, where the flag {@code dead} is true.
     *
     * @return deleted - a List of dead Entries
     */
    public List<DataEntry> getDeleted() {
        return Collections.unmodifiableList(deleted);
    }

    /**
     * clears the newEntries list
     */
    public void clearNewEntries() {
        newEntries.clear();
    }

    /**
     * Gets an unmodifiable List of {@code updateEntries}, to prevent illegal operations
     *
     * @return updateEntries - an unmodifiable list
     */
    public List<CreationEntry> getUpdateEntries() {
        return Collections.unmodifiableList(updateEntries);
    }

    /**
     * adds a {@link CreationEntry} to the {@code updateEntries} list
     *
     * @param entry entry to be added
     * @return true, if added
     * @throws NullPointerException if entry is null
     */
    public boolean addUpdateEntry(CreationEntry entry) {
        if (entry == null) {
            throw new NullPointerException("entry is null");
        }
        return updateEntries.add(entry);
    }

    /**
     * adds a {@link CreationEntry} to the newEntries list
     *
     * @param entry entry to be added
     * @return true, if added
     * @throws NullPointerException if entry is null
     */
    public void addNewEntry(CreationEntry entry) {
        if (entry == null) {
            throw new NullPointerException("entry is null");
        }
        newEntries.add(entry);
    }

    public boolean addUpdate(Creation creation) {
        if (creation == null) {
            throw new NullPointerException("entry is null");
        }
        return updateCreations.add(creation);
    }

    public boolean addUpdate(Creator creator) {
        if (creator == null) {
            throw new NullPointerException("entry is null");
        }
        return updateCreators.add(creator);
    }

    public boolean addUpdate(User user) {
        if (user == null) {
            throw new NullPointerException("entry is null");
        }
        return updateUsers.add(user);
    }

    public boolean addUpdate(Sourceable creation) {
        if (creation == null) {
            throw new NullPointerException("entry is null");
        }
        return updateSourceable.add(creation);
    }

    public boolean addUpdate(Source source) {
        if (source == null) {
            throw new NullPointerException("entry is null");
        }
        return updateSources.add(source);
    }

    /**
     * adds a {@code CreationEntry} to the list of deleted Entries
     *
     * @param entry entry to be added
     * @return - {@code true} if operation was successful
     */
    public boolean addDeleted(CreationEntry entry) {
        return deleted.add(entry);
    }

    /**
     * clears the {@code updateEntries} list
     */
    public void clearUpdateEntries() {
        updateEntries.clear();
    }

    public void clearUpdateCreations() {
        updateCreations.clear();
    }

    public void clearUpdateCreators() {
        updateCreators.clear();
    }

    public void clearUpdateUsers() {
        updateUsers.clear();
    }

    public void clearUpdateSourceables() {
        updateSourceable.clear();
    }

    public void clearUpdateSources() {
        updateSources.clear();
    }

    /**
     * Clears the {@code deleted} list
     */
    public void clearDeleted() {
        deleted.clear();
    }

    /**
     * remove a {@code CreationEntry} from the list of deleted Entries
     *
     * @param entry entry to be removed
     * @return - {@code true} if operation was successful
     */
    public boolean removeDeleted(CreationEntry entry) {
        return deleted.remove(entry);
    }

    /**
     * removes a {@link CreationEntry} from the {@code updateEntries} list
     *
     * @param entry entry to be removed
     * @return true, if removed
     * @throws NullPointerException if entry is null
     */
    public boolean removeUpdateEntry(CreationEntry entry) {
        if (entry == null) {
            throw new NullPointerException("entry is null");
        }
        return updateEntries.remove(entry);
    }

    public boolean removeUpdate(Creation creation) {
        if (creation == null) {
            throw new NullPointerException("entry is null");
        }
        return updateCreations.remove(creation);
    }

    public boolean removeUpdate(Creator creator) {
        if (creator == null) {
            throw new NullPointerException("entry is null");
        }
        return updateCreators.remove(creator);
    }

    public boolean removeUpdate(User user) {
        if (user == null) {
            throw new NullPointerException("entry is null");
        }
        return updateUsers.remove(user);
    }

    public boolean removeUpdate(Sourceable creation) {
        if (creation == null) {
            throw new NullPointerException("entry is null");
        }
        return updateSourceable.remove(creation);
    }

    public boolean removeUpdate(Source source) {
        if (source == null) {
            throw new NullPointerException("entry is null");
        }
        return updateSources.remove(source);
    }

    /**
     * removes a {@link CreationEntry} from the newEntries list
     *
     * @param entry entry to be removed
     * @return true, if removed
     * @throws NullPointerException if entry is null
     */
    public boolean removeNewEntry(DataEntry entry) {
        if (entry == null) {
            throw new NullPointerException("entry is null");
        }
        return newEntries.remove(entry);
    }

    public void removeNewEntries(Collection<DataEntry> entries) {
        newEntries.removeAll(entries);
    }

    public void removeDeleted(Collection<DataEntry> entries) {
        deleted.removeAll(entries);
    }
}
