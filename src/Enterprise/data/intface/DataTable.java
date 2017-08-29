package Enterprise.data.intface;

import Enterprise.data.ClassSpy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * DataTable interface which represents an Data Model Object extending from {@code DataBase},
 * which will be saved into the table.
 */
public interface DataTable<E extends DataBase> extends Table<E> {
    /**
     * Inserts an generic entry into the database.
     * Uses the given {@code Connection}.
     * <p>
     * Required to make a {@code Transaction} across multiple {@code DAOs} with manual {@code Commit}.
     * </p>
     *
     * @param entry entry which will be added into the database
     * @param connection {@code Connection} which will be used to transfer data
     * @return true if this operation was successful
     */
    boolean insert(E entry, Connection connection);

    /**
     * Inserts a collection of generic Entries into the Database.
     * Uses the given {@code Connection}.
     * <p>
     * Required to make a {@code Transaction} across multiple {@code DAOs} with manual {@code Commit}.
     * </p>
     *
     * @param entries collection which will be inserted into the table
     * @param connection {@code Connection} which will be used to transfer data
     * @return true if whole collection could be
     */
    boolean insert(Collection<? extends E> entries, Connection connection) throws SQLException;

    /**
     * Updates an {@code Entry} from the database.
     * For more information see {@link #updateEntry(DataBase, Connection)}.
     *
     * @param entry {@link Entry} to update
     * @return updated - integer {@code Array} holding the number of affected rows per statements per position
     */
    boolean updateEntry(E entry);

    /**
     * Updates the database with the data of the given {@link Enterprise.data.intface.Entry}.
     * For this operation, this method uses {@code Reflection}
     * to get the necessary data from the different classes.
     * To succeed with the {@code Reflection}, several restraints one the fields and methods are necessary.
     * This method uses the {@link ClassSpy} for {@code Reflection}, which looks, in this case,
     * for fields who are annotated with {@link Enterprise.misc.SQL}.
     * At the moment these fields need to have a data type
     * which derives from {@link javafx.beans.property.Property}.
     *
     * @param entry {@code Entry} to be updated
     * @param connection {@code Connection} used for this {@code Transaction}
     * @return updated - integer {@code Array} holding the number of affected rows per statements per position
     * @see ClassSpy
     */
    boolean updateEntry(E entry, Connection connection) throws SQLException;

    /**
     * Updates the database with the data of the given {@code Collection} of {@link Enterprise.data.intface.Entry}s.
     * For more information see {@link #updateEntries(Collection, Connection)}.
     *
     * @param entries iterable Collection, with Entries extending Database, or Entry
     * @return return an integer Array of affected Rows per Statement
     */
    boolean updateEntries(Collection<? extends E> entries);

    /***
     * Updates the database with the data of the given {@code Collection} of {@link Enterprise.data.intface.Entry}s.
     * For this operation, this method uses {@code Reflection}
     * to get the necessary data from the different classes.
     * To succeed with the {@code Reflection}, several restraints one the fields and methods are necessary.
     * This method uses the {@link ClassSpy} for {@code Reflection}, which looks, in this case,
     * for fields who are annotated with {@link Enterprise.misc.SQL}.
     * At the moment these fields need to have a data type
     * which derives from {@link javafx.beans.property.Property}.
     *
     * @param entries iterable Collection, with Entries extending Database, or Entry
     * @param connection Connection, which should be used
     * @return return an integer Array of affected Rows per Statement
     */
    boolean updateEntries(Collection<? extends E> entries, Connection connection);

    /**
     * Gets the entries from the table through the given {@code Connection}.
     *
     * @param connection {@code Connection} to the database
     * @return entries - a {@code Collection} of {@code Entry}s
     */
    List<E> getEntries(Connection connection);

    /**
     * Gets an Object from the table with the given Id.
     *
     * @param id database Id
     * @param connection {@code Connection} which will be used to transfer data
     * @return entry - object constructed from one row of the table
     */
    E getEntry(int id, Connection connection);

    /**
     * Gets an Object from the table with the given Id.
     *
     * @param id database Id
     * @return entry - object constructed from one row of the table
     */
    E getEntry(int id);

    /**
     * Deletes a row in the table. Uses the given {@code Connection}.
     * <p>
     * Required to make a {@code Transaction} across multiple {@code DAOs} with manual {@code Commit}.
     * </p>
     *
     * @param entry entry to be deleted from the table
     * @param connection {@code Connection} which will be used to transfer data
     */
    boolean delete(E entry, Connection connection) throws SQLException;

    /**
     * Removes the Entries provided in the {@code Collection} from the database.
     *
     * @param deletedEntries {@code Collection} of {@code Entry}s.
     * @return true, if all elements were removed
     */
    boolean delete(Collection<? extends E> deletedEntries);
}
