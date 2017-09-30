package Enterprise.data.database;

import Enterprise.data.intface.Entry;
import Enterprise.data.intface.Sourceable;
import Enterprise.misc.SetList;
import scrape.sources.Source;
import scrape.sources.SourceList;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import static Enterprise.data.database.DataColumn.Modifiers.NOT_NULL;
import static Enterprise.data.database.DataColumn.Type.INTEGER;

/**
 * This class represents a table holding the foreign keys of {@code Sourceable} and {@code Source}.
 * Every Row represents a relationship between a {@code Sourceable} and a {@code Source},
 * this can be described as a many to many relationship.
 */
public class EntrySourceTable extends AbstractSubRelation<Sourceable> {
    private static final DataColumn sourceId = new DataColumn("SOURCE_ID", INTEGER, NOT_NULL);
    private static final DataColumn sourceableId = new DataColumn("SOURCEABLE_ID", INTEGER, NOT_NULL);

    private static EntrySourceTable INSTANCE;

    static {
        try {
            INSTANCE = new EntrySourceTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The constructor of {@code EntrySourceTable}.
     *
     * @throws SQLException see {@link AbstractTable#AbstractTable(String)}
     */
    private EntrySourceTable() throws SQLException {
        super("ENTRYSOURCETABLE", sourceableId.getName());
        init();
    }

    /**
     * Returns an instance of this {@code EntrySourceTable}.
     *
     * @return instance - a new instance of this class
     */
    public static EntrySourceTable getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException();
        } else {
            return INSTANCE;
        }
    }

    @Override
    protected String createString() {
        return createRelationTableHelper(sourceableId, sourceId);
        /*return "CREATE TABLE IF NOT EXISTS " +
                getTableName() +
                "(" + sourceableId + " " + IdType + " NOT NULL" +
                "," + sourceId + " " + IdType + " NOT NULL" +
                ")";*/
    }

    @Override
    public boolean insert(Sourceable entry) {
        return Connections.getConnection(connection -> insert(entry, connection));
    }

    @Override
    public boolean insert(Sourceable entry, Connection connection) throws SQLException {
        boolean inserted = false;
        validate(entry, connection);

        List<Source> sources = entry.getSourceList().getAddedSources();
        insertNewParts(entry, sources, connection);
        if (!entry.getSourceList().isEmpty()) {
            try (PreparedStatement stmt = connection.prepareStatement(insert)) {
                //inserts new sources and Sourceables

                for (Source source : sources) {
                    setData(entry, stmt, source);
                    stmt.addBatch();
                }

                // TODO: 15.07.2017 do sth about execute/executeUpdate
                if (stmt.executeBatch().length == sources.size()) {
                    inserted = true;
                    entry.getSourceList().clearAddedSources();
                }
            }
        }
        return inserted;
    }

    @Override
    public boolean insert(Collection<? extends Sourceable> entries) {
        return Connections.getConnection(connection -> insert(entries, connection));
    }

    @Override
    public boolean insert(Collection<? extends Sourceable> entries, Connection connection) throws SQLException {
        boolean inserted = false;
        validate(entries, connection);

        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            int added = 0;
            for (Sourceable entry : entries) {
                List<Source> sources = entry.getSourceList().getAddedSources();
                insertNewParts(entry, sources, connection);

                for (Source source : sources) {
                    setData(entry, stmt, source);
                    stmt.addBatch();
                }
                added = +sources.size();
            }
            // TODO: 15.07.2017 do sth about execute/executeUpdate
            if (stmt.executeBatch().length == added) {
                inserted = true;
                entries.forEach(sourceable -> sourceable.getSourceList().clearAddedSources());
            }
        }
        //inserts new sources and Sourceables
        return inserted;
    }

    @Override
    public List<Sourceable> getEntries() {
        return Connections.getConnection(this::getEntries);
    }

    /**
     * Gets the data in this table.
     *
     * @param connection {@code Connection} to be used
     * @return sourceable - {@code List} of {@code Sourceable}
     * @throws SQLException if an error occurred in the database
     */
    public List<Sourceable> getEntries(Connection connection) throws SQLException {
        List<Sourceable> entrySet = new SetList<>();

        validate(connection);

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(getAll());
            if (!rs.isClosed()) {
                while (rs.next()) {
                    int srceableId = getInt(rs, sourceableId);
                    Sourceable entry = SourceableTable.getInstance().getEntry(srceableId);
                    entrySet.add(entry);
                }
            } else {
                System.out.println("Es scheint das keine Einträge in " + getTableName() + " vorhanden sind!");
                logger.log(Level.INFO, "no entries in " + getTableName());
            }
        }
        return entrySet;
    }

    /**
     * Deletes a row with the id from a {@code Source}.
     *
     * @param entry id of entry to be deleted from this table
     * @return true if the DELETE operation was successful
     * @see #delete(Source, Connection)
     */
    public boolean delete(Source entry) {
        return Connections.getConnection(connection -> delete(entry, connection));
    }

    /**
     * Deletes rows with the Id of this {@code Source}.
     *
     * @param entry      {@code Source} to be deleted from this table
     * @param connection {@code Connection} to be used
     * @return true if successful
     * @throws SQLException if an error occurred
     */
    public boolean delete(Source entry, Connection connection) throws SQLException {
        String delete = getDeleteSource(entry);

        validate(entry, connection);
        // TODO: 24.08.2017 delete the rest too (sourceables, source from sourcetable)
        try (PreparedStatement statement = connection.prepareStatement(delete)) {
            if (statement.executeUpdate() == 1) {
                System.out.println("Source mit ID " + entry.getId() + " wurde gelöscht!");
            } else {
                throw new SQLException("inconsistent data");
            }
        }
        return true;
    }

    /**
     * Updates the relations of the {@code Sourceable} and his {@code sources}.
     * This is accomplished by deleting the corresponding rows with their id´s.
     * Also deletes dead {@link Source}s.
     *
     * @param entries {@code Collection} of {@code Sourceable} to delete
     * @return updated - integer {@code Array} holding the number of affected rows per statements per position
     * @throws SQLException if an error occurred in the database
     */
    public boolean updateEntries(Collection<Sourceable> entries) throws SQLException {
        List<Source> deletedSources = deleteSources(entries);

        boolean deleted = false;
        boolean inserted = Connections.getConnection(connection -> insert(entries, connection));

        try (Connection connection = Connections.connection()) {
            if (!entries.isEmpty()) {
                deleted = SourceTable.getInstance().deleteSources(deletedSources, connection);
            }
        }

        boolean deleteUpdate = isUpdated(deletedSources, deleted);
        boolean insertUpdate = isUpdated(entries, inserted);

        if (deleteUpdate || insertUpdate) {
            return true;
        } else if (entries.isEmpty()) {
            return false;
        } else {
            throw new SQLException("incongruent data : "
                    + "\ndeletedSources: " + !deletedSources.isEmpty() + " deleted: " + deleted
                    + "\nentries available: " + !entries.isEmpty()
            );
        }
    }

    private List<Source> deleteSources(Collection<Sourceable> entries) throws SQLException {
        List<Source> deletedSources = new ArrayList<>();

        entries.forEach(sourceable ->
                sourceable.getSourceList().
                        stream().
                        filter(Source::isDead).
                        forEach(deletedSources::add));

        return deletedSources;
    }

    private boolean isUpdated(Collection<? extends Entry> list, boolean bool) throws SQLException {
        boolean updated;
        if (!list.isEmpty() == bool) {
            updated = !list.isEmpty();
        } else {
            throw new SQLException("incongruous data");
        }
        return updated;
    }

    @Override
    public boolean delete(Sourceable entry) {
        return Connections.getConnection(connection -> delete(entry, connection));
    }

    @Override
    public boolean delete(Sourceable entry, Connection connection) throws SQLException {
        boolean deleted = false;
        validate(entry, connection);

        List<Source> deadSources = entry.getSourceList().getDeletedSources();
        boolean deadRemoved = removeDead(entry, connection);
        if (!deadSources.isEmpty()) {
            try (PreparedStatement statement = connection.prepareStatement(getDelete())) {
                for (Source source : deadSources) {
                    setData(entry, statement, source);
                    statement.addBatch();
                }

                // TODO: 24.08.2017 do sth about this
                if (statement.executeBatch().length == deadSources.size()) {
                    deleted = true;
                }
            }
            if (deleted == deadRemoved) {
                return true;
            } else {
                throw new SQLException("inconsistent data");
            }
        } else {
            return deadRemoved;
        }
    }

    @Override
    public boolean delete(Collection<? extends Sourceable> entries, Connection connection) throws SQLException {
        boolean deleted = false;

        validate(entries, connection);

        try (PreparedStatement statement = connection.prepareStatement(getDelete())) {

            int dead = 0;
            for (Sourceable entry : entries) {
                List<Source> deadSources = entry.getSourceList().getDeletedSources();
                for (Source source : deadSources) {
                    setData(entry, statement, source);
                    statement.addBatch();
                }
                dead = +deadSources.size();
            }

            // TODO: 24.08.2017 do sth about this
            if (statement.executeBatch().length == dead) {
                deleted = true;
            }
        }
        boolean deadRemoved = removeDead(entries, connection);

        if (deleted == deadRemoved) {
            return true;
        } else {
            throw new SQLException("inconsistent data");
        }
    }

    @Override
    Source getData(Connection connection, ResultSet rs) throws SQLException {
        int srceId = rs.getInt(sourceId.getName());
        Source source = SourceTable.getInstance().getEntry(srceId, connection);

        source.setUpdated();
        return source;
    }

    boolean removeDead(Sourceable entry, Connection connection) throws SQLException {
        boolean sourceableDelete = false;
        boolean sourceDelete;

        validate(entry, connection);

        if (entry.isDead()) {
            sourceableDelete = SourceableTable.getInstance().delete(entry, connection);
        }

        List<Source> globalSources = SourceList.getDeletedGlobalSources();
        sourceDelete = SourceTable.getInstance().delete(globalSources, connection);

        return globalSources.isEmpty() ? sourceableDelete : sourceableDelete && sourceDelete;

    }

    boolean removeDead(Collection<? extends Sourceable> entries, Connection connection) throws SQLException {
        List<Source> globalSources = SourceList.getDeletedGlobalSources();

        boolean sourceableDelete = SourceableTable.getInstance().delete(entries, connection);
        boolean sourceDelete = SourceTable.getInstance().delete(globalSources, connection);

        return sourceableDelete && sourceDelete;
    }

    @Override
    String getDelete() {
        return "Delete from " + getTableName() + " where " + sourceId + " = ? and " + sourceableId + " = ?";
    }

    /**
     * Gets the sources of the given {@link Sourceable} id.
     *
     * @param id id of the {@code Sourceable}
     * @return sources - a {@link SourceList}
     */
    List<Source> getSources(int id) {
        return Connections.getConnection(connection -> getSources(id, connection));
    }

    /**
     * Gets the sources of the given {@link Sourceable} id.
     *
     * @param id         id of the {@code Sourceable}
     * @param connection {@code Connection} to be used for this transaction
     * @return sources - a {@link SourceList}
     */
    private SourceList getSources(int id, Connection connection) throws SQLException {
        String selectSpecific = getFromMainId(id);

        SourceList sourceList = new SourceList();

        validate(connection);

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(selectSpecific);

            if (!rs.isClosed()) {
                while (rs.next()) {

                    Source source = getData(connection, rs);
                    sourceList.add(source);
                }
            } else {
                System.out.println("Entry konnte nicht gefunden werden!");
                logger.log(Level.INFO, "no entries found");
            }
        }
        return sourceList;
    }

    /**
     * SQL statement of a DELETE operation.
     *
     * @param entry {@code Source} to delete from this table
     * @return string - the complete SQL statement
     */
    private String getDeleteSource(Source entry) {
        return "Delete from " + getTableName() + " where " + sourceId + "=" + entry.getId();
    }

    /**
     * Sets the parameter of the {@code PreparedStatement} for either an INSERT or DELETE operation.
     *
     * @param entry     {@code Sourceable} with the Id
     * @param statement {@code PreparedStatement} to operate on
     * @param source    {@code Source} with the Id
     * @throws SQLException if the parameters cant be set when
     *                      for example the {@code PreparedStatement} is closed
     */
    private void setData(Sourceable entry, PreparedStatement statement, Source source) throws SQLException {
        statement.setInt(1, entry.getId());
        statement.setInt(2, source.getId());
    }


    /**
     * Inserts new Parts of the {@code Sourceable} Component. New {@code sources} or itself.
     *
     * @param entries    {@code Collection} of {@code Entry}s to be checked and inserted
     * @param sourceList sources to be checked and inserted
     * @param connection connection to be used for this transaction
     * @return true if successful
     * @throws SQLException if an error occured in the database
     */
    private void insertNewParts(Sourceable entries, Collection<Source> sourceList, Connection connection) throws SQLException {
        SourceTable.getInstance().insert(sourceList, connection);
        SourceableTable.getInstance().insert(entries, connection);
    }
}
