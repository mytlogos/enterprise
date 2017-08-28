package Enterprise.data;

import Enterprise.data.intface.CreationEntry;
import Enterprise.misc.SetList;

import java.util.Collections;
import java.util.List;

/**
 * Container singleton holding entries, which lead to operations on the database (adding, deleting, updating)
 */
public class OpEntryCarrier {
    private static OpEntryCarrier instance = new OpEntryCarrier();

    private List<CreationEntry> updateEntries = new SetList<>();
    private List<CreationEntry> newEntries = new SetList<>();
    private static List<CreationEntry> deleted = new SetList<>();

    /**
     * Gets the Singleton of this {@code OpEntryCarrier}
     *
     * @return instance - the singleton instance of this {@code OpEntryCarrier}
     */
    public static OpEntryCarrier getInstance() {
        return instance;
    }

    /**
     * The singleton constructor of this {@code OpEntryCarrier},
     * is private to allow only this class to instantiate one object in the {@code instance}-Field
     */
    private OpEntryCarrier() {
        if (instance != null) {
            throw new IllegalStateException("Is already instantiated");
        }
    }

    /**
     * Gets an unmodifiable List of new Entries, to prevent illegal operations
     *
     * @return newEntries - an unmodifiable list
     */
    public List<CreationEntry> getNewEntries() {
        return Collections.unmodifiableList(newEntries);
    }

    /**
     * adds a {@link CreationEntry} to the newEntries list
     *
     * @param entry entry to be added
     * @return true, if added
     * @throws NullPointerException if entry is null
     */
    public boolean addNewEntry(CreationEntry entry) {
        if (entry == null) {
            throw new NullPointerException("entry is null");
        }
        return newEntries.add(entry);
    }

    /**
     * removes a {@link CreationEntry} from the newEntries list
     *
     * @param entry entry to be removed
     * @return true, if removed
     * @throws NullPointerException if entry is null
     */
    public boolean removeNewEntry(CreationEntry entry) {
        if (entry == null) {
            throw new NullPointerException("entry is null");
        }
        return newEntries.remove(entry);
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

    /**
     * clears the {@code updateEntries} list
     */
    public void clearUpdateEntries() {
        updateEntries.clear();
    }

    /**
     * Returns an unmodifiable List of {@code CreationEntries}, where the flag {@code dead} is true.
     *
     * @return deleted - a List of dead Entries
     */
    public List<CreationEntry> getDeleted() {
        return Collections.unmodifiableList(deleted);
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
     * adds a {@code CreationEntry} to the list of deleted Entries
     *
     * @param entry entry to be added
     * @return - {@code true} if operation was successful
     */
    public boolean addDeleted(CreationEntry entry) {
        return deleted.add(entry);
    }

}
