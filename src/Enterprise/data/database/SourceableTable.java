package Enterprise.data.database;

import Enterprise.data.impl.SimpleSourceable;
import Enterprise.data.intface.Sourceable;
import scrape.sources.SourceList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * DAO class of {@link Sourceable}.
 */
public class SourceableTable extends AbstractDataTable<Sourceable> {

    private static final String translatorC = "TRANSLATOR";

    private static SourceableTable INSTANCE;

    static  {
        try {
            INSTANCE = new SourceableTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The constructor of this {@code SourceableTable}.
     *
     * @throws SQLException if there was an error in establishing a connection or creating the table
     */
    private SourceableTable() throws SQLException {
        super("SOURCEABLETABLE", "SOURCEABLE_ID");
    }

    /**
     * Returns a static Instance of this {@code SourceableTable}.
     *
     * @return instance - Instance of this {@code SourceableTable}
     * @throws SQLException if class could not be instantiated
     * @see #SourceableTable()
     */
    static SourceableTable getInstance() throws SQLException {
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
                "(" + tableId + " " + INTEGER + " PRIMARY KEY NOT NULL UNIQUE" +
                "," + translatorC + " " + TEXT + " NOT NULL" +
                ")";
    }

    @Override
    void setInsertData(Sourceable entry, PreparedStatement stmt) throws SQLException {
        stmt.setNull(1,Types.INTEGER);
        stmt.setString(2,entry.getTranslator());
    }

    @Override
    Sourceable getData(ResultSet rs) throws SQLException {
        Sourceable entry;
        int id = rs.getInt(tableId);
        // TODO: 23.08.2017 look at this more, maybe other solution,
        // SubRelationTable gives the whole ResultSet with the whole data to this method
        SourceList sources = (SourceList) EntrySourceTable.getInstance().getSources(id);
        String tl = rs.getString(translatorC);

        entry = new SimpleSourceable(id,sources,tl);
        entry.setEntryOld();
        return entry;
    }

    @Override
    void queryIdData(Sourceable entry, PreparedStatement stmt) throws SQLException {

    }

    @Override
    String getRowQuery() {
        // TODO: 27.08.2017 look for solution, there are no overall unique translators
        /*
         * Solution 1:
         * Get all rows that have data equivalent to the inserted entry, with their generated id´s
         * compare with all existing id´s of Sourceables, if the retrieved id does not exist,
         * assign it to the inserted data
         *
         * problem: if several sourceables with same translator are inserted
         *
         * Solution 2:
         * identify all other unique parts of the CreationEntry and get the id from the relationTable
         *
         * problem: cases where other parts could not be identified as unique
         *
         * Solution 3:
         * throw speed away and do not do batch updates, only single inserts
         * and get the id from statement.generatedKeys
         *
         * Solution 4:
         * throw database generated id´s in the wind and
         * get them via timeStamp generated id´s, possible with nano resolution, making them unique
         * at every time.
         * or use a uuid generator
         */
        return "Select " + tableId + " from " + getTableName() + " where "
                + translatorC + " = ?";
    }

    @Override
    String getInsert() {
        return "insert into " + getTableName() + " values(?,?)";
    }
}
