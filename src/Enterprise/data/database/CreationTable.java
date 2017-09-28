package Enterprise.data.database;

import Enterprise.data.impl.CreationImpl;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.DataTable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static Enterprise.data.database.DataColumn.Modifiers.NOT_NULL;
import static Enterprise.data.database.DataColumn.Type.INTEGER;
import static Enterprise.data.database.DataColumn.Type.TEXT;

/**
 * DAO class responsible for the table holding the data of {@code Creation}entries.
 */
public class CreationTable extends AbstractDataTable<Creation> implements DataTable<Creation> {
    private static final DataColumn titleC = new DataColumn("TITLE", TEXT, NOT_NULL);
    private static final DataColumn seriesC = new DataColumn("SERIES", TEXT, NOT_NULL);
    private static final DataColumn dateLastPortionC = new DataColumn("DATELASTPORTION", TEXT, NOT_NULL);
    private static final DataColumn numPortionC = new DataColumn("NUMPORTION", INTEGER, NOT_NULL);
    private static final DataColumn coverPathC = new DataColumn("COVERPATH", TEXT, NOT_NULL);
    private static final DataColumn workStatusC = new DataColumn("WORKSTATUS", TEXT, NOT_NULL);

    private static CreationTable INSTANCE;


    static {
        try {
            INSTANCE = new CreationTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The constructor of {@code CreationTable}.
     *
     * @throws SQLException if there was an error in establishing a connection or creating the table
     */
    private CreationTable() throws SQLException {
        super("CREATIONTABLE", "CREATION_ID");
        init();
    }

    /**
     * Static getInstance Method. Gets the static Instance of this {@code CreationTable}.
     * @return instance - a static instance of this {@code CreationTable}
     */
    public static CreationTable getInstance(){
        return INSTANCE;
    }

    @Override
    protected String createString() {
        return createDataTableHelper(getIdColumn(), titleC, seriesC, dateLastPortionC, numPortionC, coverPathC, workStatusC);
        /*return "CREATE TABLE IF NOT EXISTS " +
                getTableName() +
                "(" + getTableId() + " " + INTEGER + " PRIMARY KEY NOT NULL UNIQUE" +
                "," + titleC + " " + TEXT + " NOT NULL" +
                "," + seriesC + " " + TEXT + " NOT NULL" +
                "," + dateLastPortionC + " " + TEXT + " NOT NULL" +
                "," + numPortionC + " " + INTEGER + " NOT NULL" +
                "," + coverPathC + " " + TEXT + " NOT NULL" +
                "," + workStatusC + " " + TEXT + " NOT NULL" +
                ")";*/
    }

    @Override
    protected void setInsertData(Creation entry, PreparedStatement stmt) throws SQLException {
        // TODO: 23.08.2017 check the sql statement of stmt if it is an INSERT operation

        setIntNull(stmt, getIdColumn());
        setString(stmt, titleC, entry.getTitle());
        setString(stmt, seriesC, entry.getSeries());
        setString(stmt, dateLastPortionC, entry.getDateLastPortion());
        setString(stmt, coverPathC, entry.getCoverPath());
        setString(stmt, workStatusC, entry.getWorkStatus());
        setInt(stmt, numPortionC, entry.getNumPortion());

        /*stmt.setNull(1,Types.INTEGER);
        stmt.setString(2,entry.getTitle());
        stmt.setString(3,entry.getSeries());
        stmt.setString(4,entry.getDateLastPortion());
        stmt.setInt(5,entry.getNumPortion());
        stmt.setString(6,entry.getCoverPath());
        stmt.setString(7,entry.getWorkStatus());*/
    }

    @Override
    protected Creation getData(ResultSet rs) throws SQLException {
        Creation entry;
        int authorId = getInt(rs, getIdColumn());
        String title = getString(rs, titleC);
        String series = getString(rs, seriesC);
        String dateLastPortion = getString(rs, dateLastPortionC);
        int numPortion = getInt(rs, numPortionC);
        String coverPath = getString(rs, coverPathC);
        String workStatus = getString(rs, workStatusC);

        entry = new CreationImpl.CreationImplBuilder(title).
                setId(authorId).
                setSeries(series).
                setCoverPath(coverPath).
                setNumPortion(numPortion).
                setDateLastPortion(dateLastPortion).
                setWorkStatus(workStatus).
                build();

        entry.setEntryOld();
        return entry;
    }
}
