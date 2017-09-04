package Enterprise.data.database;

import Enterprise.data.ReflectUpdate;
import Enterprise.data.intface.DataBase;
import Enterprise.data.intface.DataTable;
import Enterprise.misc.SetList;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * The {@code AbstractDataTable} class represents a table manager of user data in the database,
 * in contrast to {@link AbstractSubRelation}, which manages tables
 * where the data consists of foreign keys of the different tables,
 * which are managed by this class. To be exact this class provides general methods
 * to manage a table, one needs to implement the abstract methods in this class,
 * to manage a specific table.
 *
 */
public abstract class AbstractDataTable<E extends DataBase> extends AbstractTable<E> implements DataTable<E> {
    protected final String tableId;

    /**
     * The constructor of this {@code AbstractDataTable}.
     * Sets the name of an specific table, which should be provided
     * from a subclass constructor invocation of this constructor.
     * Attempts to create the table with the provided name, in case it does not exist.
     *
     * @param tableName name of the table
     * @param tableId name of the id column
     * @throws SQLException if the table could not be constructed
     */
    protected AbstractDataTable(String tableName, String tableId) throws SQLException {
        super(tableName);
        this.tableId = tableId;
        createTable();
    }

    @Override
    public boolean updateEntry(E entry) {
        return Connections.getConnection((connection) -> updateEntry(entry, connection));
    }

    @Override
    public boolean updateEntry(E entry, Connection connection) throws SQLException {
        validate(entry, connection);
        boolean updated = false;
        ReflectUpdate classSpy = new ReflectUpdate();
        Set<String> statements = classSpy.updateStrings(entry, getTableName(), tableId);

        int[] affected;
        try (Statement stmt = connection.createStatement()) {
            for (String sql : statements) {
                stmt.addBatch(sql);
            }
            affected = stmt.executeBatch();
            if (affected.length == statements.size()) {
                entry.setUpdated();
                updated = true;
            } else {
                logger.log(Level.SEVERE, "a problem while updating occurred: " +
                        "statments: " + statements.size() +
                        "affected: " + affected.length);
                throw new IllegalStateException("Beinflusste Spalten beim " + getTableName() + " stimmen nicht überein!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updated;
    }

    @Override
    public boolean updateEntries(Collection<? extends E> entries) {
        return Connections.getConnection(connection -> updateEntries(entries, connection));
    }

    @Override
    public boolean updateEntries(Collection<? extends E> entries, Connection connection) {
        Set<String> statements = updateStrings(entries);
        int[] affected;
        boolean updated = false;
        try {
            validate(entries, connection);

            try (Statement stmt = connection.createStatement()) {
                for (String sql : statements) {
                    stmt.addBatch(sql);
                }
                affected = stmt.executeBatch();

                if (affected.length == statements.size()) {
                    entries.forEach(E::setUpdated);
                    updated = true;
                } else {
                    throw new IllegalStateException("Beinflusste Spalten beim " + getTableName() + " stimmen nicht überein!");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "error occurred while updating " + getTableName(), e);
            e.printStackTrace();
        }
        return updated;
    }

    @Override
    public boolean insert(E entry) {
        return Connections.getConnection(connection -> insert(entry, connection));
    }
    @Override
    public boolean insert(E entry, Connection connection) {
        boolean inserted = false;
        if (!entry.isNewEntry()) {
            return false;
        }
        try {
            validate(entry, connection);
            try(PreparedStatement stmt = connection.prepareStatement(getInsert())) {
                setInsertData(entry, stmt);
                int updated = stmt.executeUpdate();

                ResultSet set = stmt.getGeneratedKeys();

                //sets the id of the entry
                if (set != null && set.next()) {
                    entry.setId(set.getInt(1), this);
                } else {
                    System.out.println("could not set id");
                }

                // TODO: 15.07.2017 do sth about execute/executeUpdate
                if (updated > 0) {
                    inserted = true;
                }
                entry.setEntryOld();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "error occurred while inserting", e);
            e.printStackTrace();
        }
        return inserted;
    }

    @Override
    public boolean insert(Collection<? extends E> entries) {
        return Connections.getConnection(connection -> insert(entries, connection));
    }

    @Override
    public boolean insert(Collection<? extends E> entries, Connection connection) throws SQLException {
        validate(entries, connection);

        int toInsert = 0;
        int inserted = 0;

        for (E entry : entries) {
            if (entry.isNewEntry()) {
                toInsert++;
                if (insert(entry, connection)) {
                    inserted++;
                }
            }
        }
        return toInsert == inserted;
    }

    @Override
    public List<E> getEntries() {
        return Connections.getConnection(this::getEntries);
    }

    @Override
    public List<E> getEntries(Connection connection) {
        E entry;
        List<E> entries = new SetList<>();

        try {
            validate(connection);

            try(Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(getAll());
                if (!rs.isClosed()) {
                    while (rs.next()) {

                        entry = getData(rs);
                        entries.add(entry);
                    }
                } else {
                    System.out.println("Es scheint das keine Einträge in " + getTableName() + "vorhanden sind!");
                    logger.log(Level.INFO, "no entries in " + getTableName());
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "error in the connection", e);
            e.printStackTrace();
        }
        return entries;
    }

    @Override
    public E getEntry(int id) {
        return Connections.getConnection(connection -> getEntry(id, connection));
    }

    @Override
    public E getEntry(int id, Connection connection) {
        E entry = null;
        try {
            validate(connection);

            try(Statement stmt = connection.createStatement()) {
                String getString = getFromId(id);
                ResultSet rs = stmt.executeQuery(getString);
                if (!rs.isClosed() && rs.next()) {
                    entry = getData(rs);

                } else {
                    System.out.println("Es scheint das keine Einträge in " + getTableName() + "vorhanden sind!");
                    logger.log(Level.INFO, "no entries in " + getTableName() +
                                                " with SQL statement: " + getString);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "error occurred while getting data", e);
            e.printStackTrace();
        }
        return entry;
    }

    @Override
    public boolean delete(E entry) {
        return Connections.getConnection(connection -> delete(entry, connection));
    }

    @Override
    public boolean delete(E entry, Connection connection) throws SQLException {
        boolean deleted = false;
        validate(entry, connection);

        String delete = getDelete(entry);
        try (PreparedStatement statement = connection.prepareStatement(delete)) {
            if (statement.executeUpdate() == 1) {
                deleted = true;
            }
            logger.log(Level.INFO, entry.getClass().getSimpleName() + " with id " + entry.getId() + " was deleted.");
        }
        return deleted;
    }

    @Override
    public boolean delete(Collection<? extends E> deletedEntries) {
        boolean deleted = false;
        for (E e : deletedEntries) {
            deleted = delete(e);
        }
        return deleted;
    }

    /**
     * // TODO: 27.08.2017 do the doc
     * @param deletedEntries
     * @param connection
     * @return
     * @throws SQLException
     */
    boolean delete(Collection<? extends E> deletedEntries, Connection connection) throws SQLException {
        validate(deletedEntries, connection);

        boolean deleted = false;
        for (E e : deletedEntries) {
            deleted = delete(e, connection);
        }
        return deleted;
    }

    /**
     * Gets the SQL Statement for an UPDATE operation for the {@code Collection} of {@code Entry}s.
     *
     * @param entries {@code Collection} to be updated
     * @return statements - {@code Set} of complete SQL statements
     * @see ReflectUpdate#updateStrings(DataBase, String, String)
     */
    Set<String> updateStrings(Collection<? extends E> entries) {
        Set<String> statements = new HashSet<>();
        ReflectUpdate classSpy = new ReflectUpdate();

        for (E entry : entries) {
            statements.addAll(classSpy.updateStrings(entry, getTableName(), tableId));
        }

        return statements;
    }

    /**
     * Gets the SQL statement for a SELECT operation. Selects only one specific row from the table.
     *
     * @param id Id of the {@link Enterprise.data.intface.Entry}, which will be constructed
     * @return string - the complete SQL statement
     */
    private String getFromId(int id) {
        return "Select * from " + getTableName() + " where " + tableId + " = " + id;
    }

    /**
     * Gets the SQL statement for a DELETE operation. Deletes only one row from the table.
     *
     * @param entry {@link Enterprise.data.intface.Entry} which shall be removed from the table
     * @return string - the complete SQL statement
     */
    private String getDelete(E entry) {
        return "Delete from " + getTableName() + " where " + tableId + "=" + entry.getId();
    }

    /**
     * Sets the parameters of the {@code PreparedStatement} of an {@code INSERT} operation.
     *
     * @param entry {@link Enterprise.data.intface.Entry} to be inserted
     * @param stmt {@code PreparedStatement} to operate on
     * @throws SQLException if there is a problem with the {@code PreparedStatement},
     *                      if for example, it is closed
     */
    protected abstract void setInsertData(E entry, PreparedStatement stmt) throws SQLException;

    /**
     * Constructs an {@code Entry} from the given {@code ResultSet}.
     *
     * @param rs {@code ResultSet} queried from the database
     * @return entry constructed {@code Entry}
     * @throws SQLException if the {@code ResultSet} is closed,
     *                      the {@code Cursor} was not moved or
     *                      the given column does not exist
     */
    protected abstract E getData(ResultSet rs) throws SQLException;
}
