package Enterprise.data.database;

import scrape.sources.Source;

import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

/**
 * DAO class of {@link Source}.
 */
class SourceTable extends AbstractDataTable<Source> {
    private static final String sourceUrl = "SOURCEURL";
    private static final String sourceType = "SOURCETYPE";

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
     * @throws SQLException if class could not be instantiated
     * @see #SourceTable()
     */
    static SourceTable getInstance() throws SQLException {
        return new SourceTable();
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
            entry = new Source(url, sourceType, id);
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
    void queryIdData(Source entry, PreparedStatement stmt) throws SQLException {
        String sourceUrl = entry.getUrl();
        String type = entry.getSourceType().name();

        stmt.setString(2,sourceUrl);
        stmt.setString(3,type);
    }

    @Override
    String getRowQuery() {
        return "Select " + tableId + " from " + getTableName() + " where "
                + sourceUrl + " = ? AND"
                + sourceType + " = ?";
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
    int[] deleteSources(Collection<Source> entries, Connection connection) throws SQLException {
        int[] deleted;
            if (entries == null || connection == null || connection.isClosed()) {
                throw new IllegalArgumentException();
            }
            String delete = "Delete from " + getTableName() + " where " + tableId + "= ?";
            try (PreparedStatement statement = connection.prepareStatement(delete)) {

                for (Source entry : entries) {
                    setDeleteData(statement, entry);
                    statement.addBatch();
                    System.out.println("Entry mit ID " + entry.getId() + " wurde gelöscht!");
                    logger.log(Level.INFO, "entry with id " + entry.getId() + " was deleted.");
                }
                deleted = statement.executeBatch();
            }
        return deleted;
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
    final public int[] updateEntry(Source entry, Connection connection) {
        return new int[0];
    }

    @Override @Deprecated
    final public int[] updateEntries(Collection<? extends Source> entries, Connection connection) {
        return new int[0];
    }
}
