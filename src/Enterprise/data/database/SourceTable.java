package Enterprise.data.database;

import scrape.sources.Source;

import java.net.URISyntaxException;
import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * DAO class of {@link Source}.
 */
class SourceTable extends AbstractDataTable<Source> {
    private static final String sourceUrl = "SOURCEURL";
    private static final String sourceType = "SOURCETYPE";

    private static SourceTable INSTANCE;

    static {
        try {
            INSTANCE = new SourceTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The constructor of {@code SourceTable}.
     *
     * @throws SQLException if there was an error in establishing a connection or creating the table
     */
    private SourceTable() throws SQLException {
        super("SOURCETABLE", "SOURCE_ID");
    }

    /**
     * Returns a static Instance of this {@code SourceTable}.
     *
     * @return instance - Instance of this {@code SourceTable}
     * @see #SourceTable()
     */
    static SourceTable getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException();
        } else {
            return INSTANCE;
        }
    }

    @Override
    protected String createString() {
        return "CREATE TABLE IF NOT EXISTS " +
                getTableName() +
                "(" + tableId + " " + INTEGER + " PRIMARY KEY NOT NULL" +
                "," + sourceUrl + " " + TEXT + " NOT NULL" +
                "," + sourceType + " " + TEXT + " NOT NULL" +
                ")";
    }

    @Override
    void setInsertData(Source entry, PreparedStatement stmt) throws SQLException {
        String url = entry.getUrl();
        String sourceType = entry.getSourceType().name();

        stmt.setNull(1,Types.INTEGER);
        stmt.setString(2,url);
        stmt.setString(3,sourceType);
    }

    @Override
    Source getData(ResultSet rs) throws SQLException {
        Source entry = null;
        int id = rs.getInt(tableId);
        String url = rs.getString(sourceUrl);
        String type = rs.getString(sourceType);

        Source.SourceType sourceType = Source.SourceType.valueOf(type);

        try {
            entry = Source.createSource(id, url, sourceType);
            entry.setEntryOld();
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "corrupt data: URL: "
                    + url + " SourceType: " +
                    sourceType + " Id: " + id,e);
            e.printStackTrace();
        }
        return entry;
    }

    @Override
    String getInsert() {
        return "insert into " + getTableName() + " values(?,?,?)";
    }


    // TODO: 24.08.2017 think about this, maybe generalize to AbstractDataTable
    /**
     * Deletes a {@code Collection} of {@code Source} with the given connection in the database.
     *
     * @param entries  {@code Collection} to be deleted
     * @param connection {@code Connection} to be used for this transaction
     * @return integers return an integer {@code Array} of affected Rows per Statement
     * @throws SQLException if {@code Collection} could not be deleted
     */
    boolean deleteSources(Collection<Source> entries, Connection connection) throws SQLException {
        validate(entries, connection);

        int[] deleted;
        String delete = "Delete from " + getTableName() + " where " + tableId + "= ?";

        try (PreparedStatement statement = connection.prepareStatement(delete)) {

            for (Source entry : entries) {
                setDeleteData(statement, entry);
                statement.addBatch();

                System.out.println("Entry mit ID " + entry.getId() + " wurde gelöscht!");
            }
            deleted = statement.executeBatch();
        }
        if (deleted.length == entries.size()) {
            return true;
        } else {
            throw new SQLException("could not delete all");
        }
    }

    /**
     * Checks if entry is dead and sets the parameter of a {@code PreparedStatement}
     * of an DELETE operation, if it is true.
     *
     * @param statement {@code PreparedStatement} to operate on
     * @param entry {@link Enterprise.data.intface.Entry} which shall be deleted
     * @throws SQLException if there is an problem with the {@code PreparedStatement}
     */
    private void setDeleteData(PreparedStatement statement, Source entry) throws SQLException {
        if (entry.isDead()) {
            statement.setInt(1, entry.getId());
        }
    }

    @Override @Deprecated
    final protected Set<String> updateStrings(Collection<? extends Source> entries) {
        return new HashSet<>();
    }

    @Override @Deprecated
    final public boolean updateEntry(Source entry, Connection connection) {
        throw new IllegalAccessError();
    }

    @Override @Deprecated
    final public boolean updateEntries(Collection<? extends Source> entries, Connection connection) {
        throw new IllegalAccessError();
    }
}
