package Enterprise.misc;

import Enterprise.data.intface.CreationEntry;

/**
 * Data transfer class.
 * Transfers {@link CreationEntry}s between {@link Enterprise.gui.controller.Controller}.
 * It does not prevent transfer of null references.
 */
public class EntrySingleton {
    private static final EntrySingleton ourInstance = new EntrySingleton();
    private CreationEntry entry;

    /**
     * The constructor of {@code EntrySingleton}.
     */
    private EntrySingleton() {
        if (ourInstance != null) {
            throw new IllegalStateException();
        }
    }

    /**
     * Gets the singleton instance {@code ourInstance}.
     *
     * @return instance
     */
    public static EntrySingleton getInstance() {
        return ourInstance;
    }

    /**
     * Gets the {@link CreationEntry}.
     *
     * @return creationEntry
     */
    public CreationEntry getEntry() {
        return entry;
    }

    /**
     * Sets the {@link CreationEntry} to transfer.
     *
     * @param entry entry to transfer
     */
    public void setEntry(CreationEntry entry) {
        this.entry = entry;
    }
}
