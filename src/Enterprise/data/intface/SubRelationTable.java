package Enterprise.data.intface;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Interface which represents a table holding foreign keys of other tables, marking their relationships.
 */
public interface SubRelationTable<E extends DataBase> extends Table<E> {
    /**
     * Inserts an {@link Entry} with the given Connection.
     * This method is used for transaction, with manual commit.
     *
     * @param entry {@code Entry} to insert
     * @param connection {@code Connection} for this {@code Transaction}
     * @return true if successful
     * @throws SQLException if an error occurred during this transaction
     */
    boolean insert(E entry, Connection connection) throws SQLException;

    /**
     * Inserts a {@code Collection} of {@link Entry}s with the given Connection.
     * This method is used for transaction, with manual commit.
     *
     * @param entries {@code Collection} of {@code Entry}s to insert
     * @param connection {@code Connection} for this {@code Transaction}
     * @return true if successful
     * @throws SQLException if an error occurred during this transaction
     */
    boolean insert(Collection<? extends E> entries, Connection connection) throws SQLException;

    /**
     * Deletes a {@code Collection} of {@link Entry}s with the given Connection.
     * This method is used for transaction, with manual commit.
     *
     * @param entries {@code Collection} of {@code Entry}s to delete
     * @param connection {@code Connection} for this {@code Transaction}
     * @return true if successful
     * @throws SQLException if an error occurred during this transaction
     */
    boolean delete(Collection<? extends E> entries, Connection connection) throws SQLException;

    /**
     * Deletes an {@link Entry} with the given Connection.
     * This method is used for transaction, with manual commit.
     *
     * @param entry {@code Entry} to insert
     * @param connection {@code Connection} for this {@code Transaction}
     * @return true if successful
     * @throws SQLException if an error occurred during this transaction
     */
    boolean delete(Sourceable entry, Connection connection) throws SQLException;
}
