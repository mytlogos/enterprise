package Enterprise.data.intface;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Table interface for basic database operations
 */
public interface Table<E extends DataBase> {
    /**
     * Creates a new Table in the underlying Database, in case it does not exist.
     *
     * @return true if table was created, false if it exists already or could not be created
     * @throws SQLException if there was an error in creating the table
     */
    boolean createTable() throws SQLException;

    /**
     * Checks if the specified table of the class exists already
     *
     * @return true if the table exists
     * @throws SQLException if there was an error while checking with the database
     */
    boolean tableExists() throws SQLException;

    /**
     * Inserts an generic entry into the database.
     *
     * @param entry entry which will be added into the database
     * @return true if this operation was successful
     */
    boolean insert(E entry);

    /**
     * Inserts a collection of generic Entries into the Database.
     *
     * @param entries collection which will be inserted into the table
     * @return true if whole collection could be
     */
    boolean insert(Collection<? extends E> entries);

    /**
     * Gets all Entries of the table.
     *
     * @return entries - list of database-objects
     */
    List<E> getEntries();

    /**
     * Deletes all data in the table, can not be undone.
     */
    void deleteAll();

    /**
     * Deletes a row in the table.
     *
     * @param entry entry to be deleted from the table
     */
    boolean delete(E entry);
}
