package enterprise.data.intface;

/**
 * Represents an Entry.
 * It can be dead or alive, depending on the situation.
 */
public interface Entry {
    /**
     * Sets the {@code dead} Flag to {@code true}.
     */
    void setDead();

    /**
     * Sets the {@code dead} Flag to {@code false}.
     */
    void setAlive();

    /**
     * Sets the {@code new} Flag to {@code true}.
     */
    void setEntryNew();

    /**
     * Sets the {@code new} Flag to {@code false}.
     */
    void setEntryOld();

    /**
     * @return dead - returns true, if this {@code Entry} is dead.
     */
    boolean isDead();

    /**
     * @return newEntry - returns true, if this {@code Entry} is a new Entry.
     */
    boolean isNewEntry();

    /**
     * Sets the settings of the Entry, because it was created
     * from an existing source (database) not user input.
     */
    void fromDataBase();
}
