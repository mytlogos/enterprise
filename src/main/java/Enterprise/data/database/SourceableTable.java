package Enterprise.data.database;

import Enterprise.data.impl.SourceableImpl;
import Enterprise.data.intface.Sourceable;
import scrape.sources.SourceList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Enterprise.data.database.DataColumn.Modifiers.NOT_NULL;
import static Enterprise.data.database.DataColumn.Type.TEXT;

/**
 * DAO class of {@link Sourceable}.
 */
public class SourceableTable extends AbstractDataTable<Sourceable> {

    private static final DataColumn translatorC = new DataColumn("TRANSLATOR", TEXT, NOT_NULL);

    private static SourceableTable INSTANCE;


    static {
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
        init();
    }

    /**
     * Returns a static Instance of this {@code SourceableTable}.
     *
     * @return instance - Instance of this {@code SourceableTable}
     * @see #SourceableTable()
     */
    public static SourceableTable getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException();
        } else {
            return INSTANCE;
        }
    }

    @Override
    protected String createString() {
        return createDataTableHelper(getIdColumn(), translatorC);
        /*return "CREATE TABLE IF NOT EXISTS " +
                getTableName() +
                "(" + getTableId() + " " + INTEGER + " PRIMARY KEY NOT NULL UNIQUE" +
                "," + translatorC + " " + TEXT + " NOT NULL" +
                ")";*/
    }

    @Override
    protected void setInsertData(Sourceable entry, PreparedStatement stmt) throws SQLException {
        setIntNull(stmt, getIdColumn());
        setString(stmt, translatorC, entry.getTranslator());
//        stmt.setString(2,entry.getTranslator());
    }

    @Override
    protected Sourceable getData(ResultSet rs) throws SQLException {
        Sourceable entry;
        int id = rs.getInt(getTableId());
        // TODO: 23.08.2017 look at this more, maybe other solution,
        // SubRelationTable gives the whole ResultSet with the whole data to this method
        SourceList sources = (SourceList) EntrySourceTable.getInstance().getSources(id);
        String tl = getString(rs, translatorC);

        entry = new SourceableImpl(id, sources, tl);
//        sources.setSourceable(entry);
        entry.setEntryOld();
        return entry;
    }

}
